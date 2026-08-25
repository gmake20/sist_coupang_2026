package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.ProductDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductDAO {

    // 상품 하나 조회 (PRODUCT + SELLER + 카테고리 3단 JOIN)
    // ▶ 옵션(PRODUCT_OPTION)·리뷰(REVIEW)는 이번 범위에 안 넣음 — product.jsp 에 계속 하드코딩
    public ProductDTO selectProduct(int productNo) {

        String sql = """
            SELECT
                P.PRODUCT_NO,
                P.PRODUCT_NAME,
                P.PRODUCT_DESC,
                P.PRODUCT_PRICE,
                P.QUANTITY,
                P.SELLER_NO,
                S.STORE_NAME,
                P.SUB_CATEGORY_NO,
                SC.SUB_CATEGORY_NAME,
                MC.MID_CATEGORY_NAME,
                MAINC.MAIN_CATEGORY_NAME
            FROM PRODUCT P
            JOIN SELLER S
                ON P.SELLER_NO = S.SELLER_NO
            JOIN SUB_CATEGORY SC
                ON P.SUB_CATEGORY_NO = SC.SUB_CATEGORY_NO
            JOIN MID_CATEGORY MC
                ON SC.MID_CATEGORY_NO = MC.MID_CATEGORY_NO
            JOIN MAIN_CATEGORY MAINC
                ON MC.MAIN_CATEGORY_NO = MAINC.MAIN_CATEGORY_NO
            WHERE P.PRODUCT_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    ProductDTO dto = new ProductDTO();

                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setProductDesc(rs.getString("PRODUCT_DESC"));
                    dto.setProductPrice(rs.getInt("PRODUCT_PRICE"));
                    dto.setQuantity(rs.getInt("QUANTITY"));

                    dto.setSellerNo(rs.getInt("SELLER_NO"));
                    dto.setStoreName(rs.getString("STORE_NAME"));

                    dto.setSubCategoryNo(rs.getInt("SUB_CATEGORY_NO"));
                    dto.setSubCategoryName(rs.getString("SUB_CATEGORY_NAME"));
                    dto.setMidCategoryName(rs.getString("MID_CATEGORY_NAME"));
                    dto.setMainCategoryName(rs.getString("MAIN_CATEGORY_NAME"));

                    return dto;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
