package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.ProductDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductDAO {

    // 상품 하나 조회 (PRODUCT + SELLER + 카테고리 3단 JOIN)
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
			        S.CEO_NAME,
			        S.BUSINESS_ADDRESS,
			        S.BUSINESS_DETAIL_ADDRESS,
			        S.EMAIL,
			        S.PHONE,
			        S.MAIL_ORDER_NO,
			        S.BUSINESS_NO,
			        P.SUB_CATEGORY_NO,
			        SC.CATEGORY_NAME    AS SUB_CATEGORY_NAME,
			        MC.CATEGORY_NAME    AS MID_CATEGORY_NAME,
			        MAINC.CATEGORY_NAME AS MAIN_CATEGORY_NAME
			        FROM PRODUCT P
			        JOIN SELLER S
			        ON P.SELLER_NO = S.SELLER_NO
			        JOIN CATEGORY SC
			        ON P.SUB_CATEGORY_NO = SC.CATEGORY_NO        
			        JOIN CATEGORY MC
			        ON SC.PARENT_CATEGORY_NO = MC.CATEGORY_NO   
			        JOIN CATEGORY MAINC
			        ON MC.PARENT_CATEGORY_NO = MAINC.CATEGORY_NO 
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
                    dto.setCeoName(rs.getString("CEO_NAME"));
                    dto.setBusinessAddress(rs.getString("BUSINESS_ADDRESS"));
                    dto.setBusinessDetailAddress(rs.getString("BUSINESS_DETAIL_ADDRESS"));
                    dto.setEmail(rs.getString("EMAIL"));
                    dto.setPhone(rs.getString("PHONE"));
                    dto.setMailOrderNo(rs.getString("MAIL_ORDER_NO"));
                    dto.setBusinessNo(rs.getString("BUSINESS_NO"));

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
