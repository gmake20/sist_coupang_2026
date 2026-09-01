package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.CategoryDTO;
import com.goodpang.dto.CategoryProductDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 카테고리 목록 페이지(/category?categoryNo=...) 조회 전용 DAO.
 * 실제 라이브 coupang.com 을 Playwright MCP 로 확인해서 설계함 — 근거는 ref/category/STRUCTURE.md.
 */
public class CategoryProductDAO {

    // 정렬 화이트리스트 — 원본은 5개(쿠팡랭킹순 포함)지만 우리는 4개만(2026-08-30 확정, 쿠팡랭킹순 제외)
    public enum Sort {
        LATEST, PRICE_ASC, PRICE_DESC, SALE_COUNT
    }

    // 상품 목록 하나의 SELECT 본문 + FROM/JOIN 은 count 조회와 겹쳐서 재사용
    private static final String BASE_FROM = """
            FROM PRODUCT P
            LEFT JOIN (
                SELECT PRODUCT_NO, PRICE, NORMAL_PRICE
                FROM (
                    SELECT PRODUCT_NO, PRICE, NORMAL_PRICE,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY PRICE ASC) AS RN
                    FROM PRODUCT_OPTION
                )
                WHERE RN = 1
            ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
            LEFT JOIN (
                SELECT PRODUCT_NO, IMAGE_URL,
                       ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                FROM PRODUCT_IMAGE
                WHERE IMAGE_PURPOSE = '대표'
            ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            LEFT JOIN (
                SELECT od.PRODUCT_NO,
                       ROUND(AVG(r.PRODUCT_RATING), 1) AS AVG_RATING,
                       COUNT(*) AS REVIEW_COUNT
                FROM REVIEW r
                JOIN ORDER_DETAIL od ON r.ORDER_DETAIL_NO = od.ORDER_DETAIL_NO
                GROUP BY od.PRODUCT_NO
            ) RV ON RV.PRODUCT_NO = P.PRODUCT_NO
            LEFT JOIN (
                SELECT od.PRODUCT_NO, SUM(od.ORDER_QTY) AS SALE_COUNT
                FROM ORDER_DETAIL od
                JOIN ORDERS o ON od.ORDER_NO = o.ORDER_NO
                WHERE o.ORDER_STATUS != '주문취소'
                GROUP BY od.PRODUCT_NO
            ) SC ON SC.PRODUCT_NO = P.PRODUCT_NO
            WHERE P.SUB_CATEGORY_NO = ?
              AND (P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) BETWEEN ? AND ?
              AND NVL(RV.AVG_RATING, 0) >= ?
              AND (? IS NULL OR EXISTS (
                    SELECT 1 FROM PRODUCT_OPTION PO2
                    WHERE PO2.PRODUCT_NO = P.PRODUCT_NO
                      AND (PO2.OPTION1_VALUE = ? OR PO2.OPTION2_VALUE = ? OR PO2.OPTION3_VALUE = ?)
                  ))
            """;

    // 목록 (60개씩 고정, page 는 1부터). color 는 null 이면 필터 안 함
    public List<CategoryProductDTO> findByCategory(
            int categoryNo, Sort sort, int minPrice, int maxPrice, int minRating, String color,
            int page, int pageSize) {

        List<CategoryProductDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    P.PRODUCT_NO,
                    P.PRODUCT_NAME,
                    IMG.IMAGE_URL AS THUMBNAIL_URL,
                    P.PRODUCT_PRICE + NVL(OPT.PRICE, 0) AS SALE_PRICE,
                    CASE WHEN OPT.NORMAL_PRICE IS NOT NULL
                         THEN P.PRODUCT_PRICE + OPT.NORMAL_PRICE END AS NORMAL_PRICE,
                    NVL(RV.AVG_RATING, 0) AS AVG_RATING,
                    NVL(RV.REVIEW_COUNT, 0) AS REVIEW_COUNT,
                    NVL(SC.SALE_COUNT, 0) AS SALE_COUNT
                """
                + BASE_FROM
                + "ORDER BY " + orderByClause(sort) + "\n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY\n";

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            int i = 1;
            pstmt.setInt(i++, categoryNo);
            pstmt.setInt(i++, minPrice);
            pstmt.setInt(i++, maxPrice);
            pstmt.setInt(i++, minRating);
            pstmt.setString(i++, color);
            pstmt.setString(i++, color);
            pstmt.setString(i++, color);
            pstmt.setString(i++, color);
            pstmt.setInt(i++, (page - 1) * pageSize);
            pstmt.setInt(i++, pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 페이지네이션용 총 개수 — 필터 조건은 findByCategory 와 반드시 같아야 함
    public int countByCategory(int categoryNo, int minPrice, int maxPrice, int minRating, String color) {

        String sql = "SELECT COUNT(*) " + BASE_FROM;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);
            pstmt.setInt(2, minPrice);
            pstmt.setInt(3, maxPrice);
            pstmt.setInt(4, minRating);
            pstmt.setString(5, color);
            pstmt.setString(6, color);
            pstmt.setString(7, color);
            pstmt.setString(8, color);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
     * 색상 필터 항목 — 하드코딩 안 하고 실제 등록된 값만 보여줌(2026-08-30 확정).
     * OPTION1~3_TYPE 중 아무 축이나 '색상'으로 등록될 수 있어서 세 축을 UNION 으로 합침.
     * (처음엔 LATERAL JOIN 으로 짰다가 Oracle 버전 호환이 불확실해서 이 방식으로 바꿈)
     */
    public List<String> findColorOptionsByCategory(int categoryNo) {

        List<String> colors = new ArrayList<>();

        String sql = """
                SELECT DISTINCT OPTION_VALUE FROM (
                    SELECT PO.OPTION1_VALUE AS OPTION_VALUE
                    FROM PRODUCT_OPTION PO
                    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                    WHERE P.SUB_CATEGORY_NO = ? AND PO.OPTION1_TYPE = '색상'
                    UNION
                    SELECT PO.OPTION2_VALUE
                    FROM PRODUCT_OPTION PO
                    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                    WHERE P.SUB_CATEGORY_NO = ? AND PO.OPTION2_TYPE = '색상'
                    UNION
                    SELECT PO.OPTION3_VALUE
                    FROM PRODUCT_OPTION PO
                    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
                    WHERE P.SUB_CATEGORY_NO = ? AND PO.OPTION3_TYPE = '색상'
                )
                WHERE OPTION_VALUE IS NOT NULL
                ORDER BY OPTION_VALUE
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);
            pstmt.setInt(2, categoryNo);
            pstmt.setInt(3, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    colors.add(rs.getString("OPTION_VALUE"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return colors;
    }

    // 브레드크럼 — [대분류, 중분류, 소분류] 순서로 3칸 고정. CATEGORY 가 자기참조 트리라 3번 자기조인.
    public CategoryDTO[] findBreadcrumb(int categoryNo) {

        String sql = """
                SELECT
                    SC.CATEGORY_NO   AS SUB_NO,   SC.CATEGORY_NAME   AS SUB_NAME,
                    MC.CATEGORY_NO   AS MID_NO,   MC.CATEGORY_NAME   AS MID_NAME,
                    MAINC.CATEGORY_NO AS MAIN_NO, MAINC.CATEGORY_NAME AS MAIN_NAME
                FROM CATEGORY SC
                JOIN CATEGORY MC    ON SC.PARENT_CATEGORY_NO = MC.CATEGORY_NO
                JOIN CATEGORY MAINC ON MC.PARENT_CATEGORY_NO = MAINC.CATEGORY_NO
                WHERE SC.CATEGORY_NO = ?
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long mainNo = rs.getLong("MAIN_NO");
                    CategoryDTO main = new CategoryDTO(mainNo, rs.getString("MAIN_NAME"), null, 1, "./pds/category_" + mainNo + ".png");
                    CategoryDTO mid = new CategoryDTO(rs.getLong("MID_NO"), rs.getString("MID_NAME"), mainNo, 2, null);
                    CategoryDTO sub = new CategoryDTO(rs.getLong("SUB_NO"), rs.getString("SUB_NAME"), rs.getLong("MID_NO"), 3, null);
                    return new CategoryDTO[] { main, mid, sub };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CategoryDTO[0];
    }

    // 형제 카테고리 목록 — 필터 사이드바의 "카테고리" 그룹(예: 티셔츠와 같은 레벨의 맨투맨/셔츠/바지 등)
    public List<CategoryDTO> findSiblingCategories(int categoryNo) {

        List<CategoryDTO> list = new ArrayList<>();

        String sql = """
                SELECT SIB.CATEGORY_NO, SIB.CATEGORY_NAME, SIB.PARENT_CATEGORY_NO, SIB.CATEGORY_LEVEL
                FROM CATEGORY SELF
                JOIN CATEGORY SIB ON SIB.PARENT_CATEGORY_NO = SELF.PARENT_CATEGORY_NO
                WHERE SELF.CATEGORY_NO = ?
                ORDER BY SIB.CATEGORY_NO
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, categoryNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long categoryNoResult = rs.getLong("CATEGORY_NO");
                    int categoryLevel = rs.getInt("CATEGORY_LEVEL");
                    list.add(new CategoryDTO(
                        categoryNoResult,
                        rs.getString("CATEGORY_NAME"),
                        rs.getLong("PARENT_CATEGORY_NO"),
                        categoryLevel,
                        categoryLevel == 1 ? "./pds/category_" + categoryNoResult + ".png" : null
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // SQL 인젝션 방지 — 사용자 입력을 ORDER BY 에 직접 이어붙이지 않고 화이트리스트에서만 고름
    private String orderByClause(Sort sort) {
        return switch (sort) {
            case PRICE_ASC  -> "(P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) ASC";
            case PRICE_DESC -> "(P.PRODUCT_PRICE + NVL(OPT.PRICE, 0)) DESC";
            case SALE_COUNT -> "NVL(SC.SALE_COUNT, 0) DESC";
            case LATEST     -> "P.CREATED_DATE DESC";
        };
    }

    private CategoryProductDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        CategoryProductDTO dto = new CategoryProductDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

        dto.setSalePrice(rs.getInt("SALE_PRICE"));

        int normalPrice = rs.getInt("NORMAL_PRICE");
        dto.setNormalPrice(rs.wasNull() ? null : normalPrice);

        if (dto.getNormalPrice() != null && dto.getNormalPrice() > dto.getSalePrice()) {
            dto.setDiscountRate((int) Math.round((1 - (double) dto.getSalePrice() / dto.getNormalPrice()) * 100));
        }

        dto.setAvgRating(rs.getDouble("AVG_RATING"));
        dto.setReviewCount(rs.getInt("REVIEW_COUNT"));
        dto.setSaleCount(rs.getInt("SALE_COUNT"));

        // 적립 — 실제 적립 정책 테이블이 없어서 판매가 1%로 임의 계산(2026-09-01 사용자 요청)
        dto.setCashReward((int) Math.floor(dto.getSalePrice() * 0.01));

        return dto;
    }
}
