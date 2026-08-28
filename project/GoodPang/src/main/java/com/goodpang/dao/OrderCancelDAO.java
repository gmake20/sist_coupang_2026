package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.DBConn;

public class OrderCancelDAO {
	

	    // [기존 cancelOrder 메서드는 그대로 유지]

	    /**
	     * 특정 회원의 취소/반품 내역 목록 조회
	     * @throws NamingException 
	     */
	    public List<OrderDetailDTO> getCancelHistoryList(int memberNo) throws NamingException {
	        List<OrderDetailDTO> list = new ArrayList<>();

	        String sql = """
	            SELECT 
	                o.order_no,
	                o.order_date,
	                o.order_status,
	                o.delivery_fee,
	                od.order_detail_no,
	                od.order_qty AS quantity,
	                od.price AS total_price,
	                p.product_no,
	                p.product_name,
	                po.option1_type, po.option1_value,
	                po.option2_type, po.option2_value,
	                pr.return_no,
	                pr.request_date,
	                pr.return_reason,
	                pr.refund_amount,
	                pr.expected_cancel_date
	            FROM ORDERS o
	            JOIN ORDER_DETAIL od ON o.order_no = od.order_no
	            JOIN PRODUCT p ON od.product_no = p.product_no
	            LEFT JOIN PRODUCT_OPTION po ON od.option_id = po.option_id
	            JOIN PRODUCT_RETURN pr ON od.order_detail_no = pr.order_detail_no
	            WHERE o.member_no = ?
	            ORDER BY pr.request_date DESC
	            """;

	        try (Connection conn = ConnectionProvider.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setInt(1, memberNo);

	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    OrderDetailDTO dto = new OrderDetailDTO();

	                    // 주문 정보
	                    dto.setOrderNo(rs.getInt("order_no"));
	                    dto.setOrderDate(rs.getTimestamp("order_date"));
	                    dto.setOrderStatus(rs.getString("order_status"));
	                    dto.setDeliveryFee(rs.getInt("delivery_fee"));

	                    // 주문 상세 및 상품 정보
	                    dto.setOrderDetailNo(rs.getLong("order_detail_no"));
	                    dto.setQuantity(rs.getInt("quantity"));
	                    dto.setTotalPrice(rs.getInt("total_price"));
	                    dto.setProductNo(rs.getLong("product_no"));
	                    dto.setProductName(rs.getString("product_name"));

	                    // 옵션 정보
	                    dto.setOption1Type(rs.getString("option1_type"));
	                    dto.setOption1Value(rs.getString("option1_value"));
	                    dto.setOption2Type(rs.getString("option2_type"));
	                    dto.setOption2Value(rs.getString("option2_value"));

	                    // 취소/반품(PRODUCT_RETURN) 정보
	                    dto.setReturnNo(rs.getLong("return_no"));
	                    dto.setRequestDate(rs.getTimestamp("request_date"));
	                    dto.setReturnReason(rs.getString("return_reason"));
	                    dto.setRefundAmount(rs.getInt("refund_amount"));
	                    dto.setExpectedCancelDate(rs.getTimestamp("expected_cancel_date"));

	                    list.add(dto);
	                }
	            }
	        } catch (SQLException e) {
	            System.err.println("[ERROR OrderCancelDAO] 취소 내역 목록 조회 실패");
	            e.printStackTrace();
	        } finally {
	            DBConn.close();
	        }

	        return list;
	    }
	

    /**
     * 주문 취소 처리
     * 1) ORDERS 테이블: order_status = '주문취소' UPDATE
     * 2) ORDER_DETAIL 조회: order_detail_no, order_qty, price 추출
     * 3) PRODUCT_RETURN 테이블: 정확한 컬럼(order_detail_no)으로 취소 내역 INSERT
     */
    public boolean cancelOrder(int orderNo, String cancelReason) {

        // 1. ORDERS 테이블 상태 변경 SQL
        String sqlOrderUpdate = """
            UPDATE ORDERS 
               SET order_status = '주문취소' 
             WHERE order_no = ?
            """;

        // 2. ORDER_DETAIL 정보 조회 SQL (order_detail_no 및 수량/금액 추출)
        String sqlSelectDetail = """
            SELECT order_detail_no, order_qty, price 
              FROM ORDER_DETAIL 
             WHERE order_no = ?
            """;

     // 기존: SEQ_PRODUCT_RETURN.NEXTVAL 대신 (SELECT NVL(MAX(return_no), 0) + 1 FROM PRODUCT_RETURN) 사용
        String sqlReturnInsert = """
            INSERT INTO PRODUCT_RETURN (
                return_no, request_date, return_qty, return_reason, return_status, refund_amount, order_detail_no
            ) VALUES (
                (SELECT NVL(MAX(return_no), 0) + 1 FROM PRODUCT_RETURN), 
                SYSDATE, ?, ?, '취소완료', ?, ?
            )
            """;

        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtReturn = null;
        ResultSet rs = null;
        boolean isSuccess = false;

        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // [Step 1] ORDERS 상태 업데이트
            pstmtOrder = conn.prepareStatement(sqlOrderUpdate);
            pstmtOrder.setInt(1, orderNo);
            int orderResult = pstmtOrder.executeUpdate();

            // [Step 2 & 3] ORDER_DETAIL 조회 후 PRODUCT_RETURN에 등록
            if (orderResult > 0) {
                pstmtSelect = conn.prepareStatement(sqlSelectDetail);
                pstmtSelect.setInt(1, orderNo);
                rs = pstmtSelect.executeQuery();

                pstmtReturn = conn.prepareStatement(sqlReturnInsert);

                int returnResultCount = 0;
                while (rs.next()) {
                    long orderDetailNo = rs.getLong("order_detail_no");
                    int returnQty = rs.getInt("order_qty");
                    int refundAmount = rs.getInt("price");

                    pstmtReturn.setInt(1, returnQty);
                    pstmtReturn.setString(2, cancelReason);
                    pstmtReturn.setInt(3, refundAmount);
                    pstmtReturn.setLong(4, orderDetailNo);

                    returnResultCount += pstmtReturn.executeUpdate();
                }

                if (returnResultCount > 0) {
                    conn.commit(); // 성공 시 DB 적용
                    isSuccess = true;
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
            }

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception e) {}
            if (pstmtOrder != null) try { pstmtOrder.close(); } catch (Exception e) {}
            if (pstmtSelect != null) try { pstmtSelect.close(); } catch (Exception e) {}
            if (pstmtReturn != null) try { pstmtReturn.close(); } catch (Exception e) {}
            DBConn.close();
        }

        return isSuccess;
    }
}