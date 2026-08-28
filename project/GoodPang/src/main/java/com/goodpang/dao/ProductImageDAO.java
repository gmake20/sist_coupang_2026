package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.ProductImageDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductImageDAO {

    // 상품 하나의 PRODUCT_IMAGE 를 전부 가져옴 (옵션 사진 + 상세설명 사진 다 섞여서 나옴).
    // OPTION_ID 로 옵션 사진/상세설명 사진을 나누는 건 ProductServlet 쪽에서 함.
    public List<ProductImageDTO> selectImagesByProductNo(int productNo) {

        List<ProductImageDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    IMAGE_NO,
                    PRODUCT_NO,
                    OPTION_ID,
                    IMAGE_PURPOSE,
                    IMAGE_ORDER,
                    IMAGE_URL
                FROM PRODUCT_IMAGE
                WHERE PRODUCT_NO = ?
                ORDER BY OPTION_ID, IMAGE_ORDER
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ProductImageDTO dto = new ProductImageDTO();

                    dto.setImageNo(rs.getInt("IMAGE_NO"));
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));

                    int optionId = rs.getInt("OPTION_ID");
                    dto.setOptionId(rs.wasNull() ? null : optionId);   // 상세설명 사진은 OPTION_ID 가 null

                    dto.setImagePurpose(rs.getString("IMAGE_PURPOSE"));
                    dto.setImageOrder(rs.getInt("IMAGE_ORDER"));
                    dto.setImageUrl(rs.getString("IMAGE_URL"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
