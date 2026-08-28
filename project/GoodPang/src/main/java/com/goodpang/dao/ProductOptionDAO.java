package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.util.ConnectionProvider;

public class ProductOptionDAO {

    // 상품 하나의 옵션 목록 (PRICE 는 NVL 로 0 처리)
    public List<ProductOptionDTO> selectOptionsByProductNo(int productNo) {

        List<ProductOptionDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    OPTION_ID,
                    OPTION1_TYPE,
                    OPTION1_VALUE,
                    OPTION2_TYPE,
                    OPTION2_VALUE,
                    OPTION3_TYPE,
                    OPTION3_VALUE,
                    NVL(PRICE, 0) AS PRICE,
                    QUANTITY,
                    STATUS
                FROM PRODUCT_OPTION
                WHERE PRODUCT_NO = ?
                ORDER BY OPTION_ID
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {

            pstmt.setInt(1, productNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    ProductOptionDTO dto = new ProductOptionDTO();

                    dto.setOptionId(rs.getInt("OPTION_ID"));

                    dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                    dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                    dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                    dto.setOption2Value(rs.getString("OPTION2_VALUE"));
                    dto.setOption3Type(rs.getString("OPTION3_TYPE"));
                    dto.setOption3Value(rs.getString("OPTION3_VALUE"));

                    dto.setPrice(rs.getInt("PRICE"));
                    dto.setQuantity(rs.getInt("QUANTITY"));
                    dto.setStatus(rs.getString("STATUS"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
