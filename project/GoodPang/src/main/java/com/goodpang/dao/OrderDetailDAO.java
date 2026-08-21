package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.DBConn;



public class OrderDetailDAO {

    // 특정 주문번호(ORDER_NO)에 해당하는 주문 상세 상품 목록 조회
    public List<OrderDetailDTO> getOrderDetailList(int orderNo) {
        List<OrderDetailDTO> list = new ArrayList<>();
        
        // OPTION_ID가 NULL인 상품(옵션 없는 상품)도 함께 조회하기 위해 LEFT JOIN 사용
        String sql = "SELECT od.ORDER_DETAIL_NO, " +
                     "       od.ORDER_NO, " +
                     "       od.PRODUCT_NO, " +
                     "       p.PRODUCT_NAME, " +
                     "       od.OPTION_ID, " +
                     "       opt.OPTION_NAME, " +
                     "       od.ORDER_QTY, " +
                     "       od.PRICE " +
                     "FROM ORDER_DETAIL od " +
                     "JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO " +
                     "LEFT JOIN PRODUCT_OPTION opt ON od.OPTION_ID = opt.OPTION_ID " +
                     "WHERE od.ORDER_NO = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetailDTO dto = new OrderDetailDTO();
                    
                    dto.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
                    dto.setOrderNo(rs.getInt("ORDER_NO"));
                    dto.setProductNo(rs.getInt("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setOrderQty(rs.getInt("ORDER_QTY"));
                    dto.setPrice(rs.getInt("PRICE"));

                    // OPTION_ID (NULL 허용) 예외 처리
                    int optionId = rs.getInt("OPTION_ID");
                    if (!rs.wasNull()) {
                        dto.setOptionId(optionId);
                        dto.setOptionName(rs.getString("OPTION_NAME"));
                    } else {
                        dto.setOptionId(null);
                        dto.setOptionName("선택 옵션 없음");
                    }

                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}