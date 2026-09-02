package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.DBConn;

public class OrderDetailDAO {

    /**
     * 특정 주문번호(ORDER_NO)에 해당하는 주문 상세 정보 조회 (이미지 URL 포함)
     */
    public List<OrderDetailDTO> getOrderDetailList(int orderNo , int memberNo) {
        List<OrderDetailDTO> list = new ArrayList<>();
        
        // 주소창에 orderNo = 다른 사람 주문 번호 넣으면 조회 가능해서 AND o.MEMBER_NO = ? 이거 한줄 추가함 26/09/02
        // ORDERS, ORDER_DETAIL, PRODUCT, MEMBER, ORDER_ADDRESS, PAYMENT, PRODUCT_OPTION + PRODUCT_IMAGE 조인
        String sql = """
            SELECT 
                od.ORDER_DETAIL_NO, 
                od.ORDER_NO, 
                o.MEMBER_NO, 
                o.ORDER_STATUS, 
                o.ORDER_DATE, 
                o.TOTAL_PRICE, 
                p.PRODUCT_NO, 
                p.PRODUCT_NAME, 
                od.ORDER_QTY AS QUANTITY, 
                od.PRICE AS ITEM_PRICE,
                o.DELIVERY_FEE, 
                pay.PAYMENT_METHOD, 
                oa.REQUEST_MSG, 
                oa.ADDRESS, 
                oa.DETAIL_ADDRESS, 
                m.MEMBER_NAME, 
                m.PHONE, 
                po.OPTION1_TYPE, 
                po.OPTION1_VALUE, 
                po.OPTION2_TYPE, 
                po.OPTION2_VALUE,
                img.IMAGE_URL -- ★ 대표 이미지 컬럼 추가
            FROM ORDERS o 
            JOIN ORDER_DETAIL od ON o.ORDER_NO = od.ORDER_NO 
            JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO 
            JOIN MEMBER m ON o.MEMBER_NO = m.MEMBER_NO 
            LEFT JOIN ORDER_ADDRESS oa ON o.ORDER_ADDRESS_NO = oa.ORDER_ADDRESS_NO 
            LEFT JOIN PAYMENT pay ON o.ORDER_NO = pay.ORDER_NO 
            LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID 
            LEFT JOIN (
                -- ★ 대표 이미지 1건만 안전하게 가져오는 서브쿼리 조인
                SELECT product_no, image_url 
                FROM (
                    SELECT product_no, image_url, 
                           ROW_NUMBER() OVER(PARTITION BY product_no ORDER BY image_no ASC) as rn 
                    FROM PRODUCT_IMAGE 
                    WHERE image_purpose = '대표'
                ) WHERE rn = 1
            ) img ON p.PRODUCT_NO = img.PRODUCT_NO
            WHERE o.ORDER_NO = ?
            AND o.MEMBER_NO = ?
            ORDER BY od.ORDER_DETAIL_NO ASC
            """;

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, memberNo); // 추가 - 다른 사람 주문번호 조회 방지

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetailDTO dto = new OrderDetailDTO();

                    // PK 및 주문 기본 정보
                    dto.setOrderDetailNo(rs.getLong("ORDER_DETAIL_NO"));
                    dto.setOrderNo(rs.getInt("ORDER_NO"));
                    dto.setMemberNo(rs.getInt("MEMBER_NO"));
                    dto.setOrderStatus(rs.getString("ORDER_STATUS"));
                    dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));
                    dto.setTotalPrice(rs.getInt("TOTAL_PRICE"));

                    // 상품 정보
                    dto.setProductNo(rs.getLong("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setQuantity(rs.getInt("QUANTITY"));
                    dto.setItemPrice(rs.getInt("ITEM_PRICE"));
                    dto.setDeliveryFee(rs.getInt("DELIVERY_FEE"));

                    // 결제 및 배송지 정보
                    dto.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
                    dto.setRequestMsg(rs.getString("REQUEST_MSG"));
                    dto.setAddress(rs.getString("ADDRESS"));
                    dto.setDetailAddress(rs.getString("DETAIL_ADDRESS"));
                    dto.setMemberName(rs.getString("MEMBER_NAME"));
                    dto.setPhone(rs.getString("PHONE"));

                    // 옵션 정보
                    dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                    dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                    dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                    dto.setOption2Value(rs.getString("OPTION2_VALUE"));

                    // ★ 대표 이미지 URL 바인딩
                    dto.setImageUrl(rs.getString("IMAGE_URL"));

                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConn.close();
        }

        return list;
    }
}