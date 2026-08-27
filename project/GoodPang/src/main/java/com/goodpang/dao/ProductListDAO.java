package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorProductListDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 상품 목록(vendor_products.jsp) 조회.
 * PRODUCT_OPTION 가격/재고를 상품 하나로 요약하고, 대표이미지 1장만 같이 가져온다.
 */
public class ProductListDAO {

    // 판매자(sellerNo)가 등록한 상품 목록 - 최근 등록순
    public List<VendorProductListDTO> findBySellerNo(int sellerNo) {

        List<VendorProductListDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                P.PRODUCT_NO,
                P.PRODUCT_NAME,
                P.SALE_METHOD,
                P.SALE_STATUS,
                C1.CATEGORY_NAME AS MAIN_CATEGORY_NAME,
                C2.CATEGORY_NAME AS MID_CATEGORY_NAME,
                C3.CATEGORY_NAME AS SUB_CATEGORY_NAME,
                NVL(OPT.MIN_PRICE, P.PRODUCT_PRICE)  AS MIN_PRICE,
                NVL(OPT.MAX_PRICE, P.PRODUCT_PRICE)  AS MAX_PRICE,
                NVL(OPT.TOTAL_QUANTITY, P.QUANTITY)  AS TOTAL_QUANTITY,
                NVL(OPT.OPTION_COUNT, 0)             AS OPTION_COUNT,
                IMG.IMAGE_URL                        AS THUMBNAIL_URL,
                P.CREATED_DATE,
                P.UPDATED_DATE
            FROM PRODUCT P
                JOIN CATEGORY C3 ON P.SUB_CATEGORY_NO = C3.CATEGORY_NO
                LEFT JOIN CATEGORY C2 ON C2.CATEGORY_NO = C3.PARENT_CATEGORY_NO
                LEFT JOIN CATEGORY C1 ON C1.CATEGORY_NO = C2.PARENT_CATEGORY_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO,
                           MIN(PRICE)    AS MIN_PRICE,
                           MAX(PRICE)    AS MAX_PRICE,
                           SUM(QUANTITY) AS TOTAL_QUANTITY,
                           COUNT(*)      AS OPTION_COUNT
                    FROM PRODUCT_OPTION
                    GROUP BY PRODUCT_NO
                ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN (
                    SELECT PRODUCT_NO, IMAGE_URL,
                           ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
                    FROM PRODUCT_IMAGE
                    WHERE IMAGE_PURPOSE = '대표'
                ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
            WHERE P.SELLER_NO = ?
            ORDER BY P.CREATED_DATE DESC
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

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

    private VendorProductListDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorProductListDTO dto = new VendorProductListDTO();

        dto.setProductNo(rs.getInt("PRODUCT_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setSaleMethod(rs.getString("SALE_METHOD"));
        dto.setSaleStatus(rs.getString("SALE_STATUS"));

        dto.setMainCategoryName(rs.getString("MAIN_CATEGORY_NAME"));
        dto.setMidCategoryName(rs.getString("MID_CATEGORY_NAME"));
        dto.setSubCategoryName(rs.getString("SUB_CATEGORY_NAME"));

        int minPrice = rs.getInt("MIN_PRICE");
        dto.setMinPrice(rs.wasNull() ? null : minPrice);

        int maxPrice = rs.getInt("MAX_PRICE");
        dto.setMaxPrice(rs.wasNull() ? null : maxPrice);

        int totalQuantity = rs.getInt("TOTAL_QUANTITY");
        dto.setTotalQuantity(rs.wasNull() ? null : totalQuantity);

        dto.setOptionCount(rs.getInt("OPTION_COUNT"));
        dto.setThumbnailUrl(rs.getString("THUMBNAIL_URL"));

        dto.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
        dto.setUpdatedDate(rs.getTimestamp("UPDATED_DATE"));

        return dto;
    }
}
