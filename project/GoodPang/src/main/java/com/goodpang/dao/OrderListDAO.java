package com.goodpang.dao;

import com.goodpang.dto.OrderItemDTO; //[cite: 1]
import com.goodpang.util.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

public class OrderListDAO {
	
	public OrderListDAO() {
        // 기본 생성자를 명시적으로 선언 (내용 비워둠)
    }

	
	/**
	 * 특정 회원의 마이페이지 주문 내역 조회 (4개 테이블 조인)
	 * @throws NamingException 
	 */
	public List<OrderItemDTO> selectMyPageOrders(int memberNo) throws NamingException {
		List<OrderItemDTO> list = new ArrayList<>();

		String sql = "SELECT "
				+ "  o.order_no, o.member_no, o.order_date, o.order_status, o.total_price AS order_total_price, "
				+ "  od.order_detail_no, od.order_qty, od.price AS item_price, "
				+ "  p.product_no, p.product_name, p.product_price, "
				+ "  po.OPTION_ID, po.OPTION1_TYPE, po.OPTION1_VALUE, po.OPTION2_TYPE, po.OPTION2_VALUE, po.PRICE AS option_price "
				+ "FROM ORDERS o "
				+ "JOIN ORDER_DETAIL od ON o.order_no = od.order_no "
				+ "JOIN PRODUCT p ON od.product_no = p.product_no "
				+ "LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID "
				+ "WHERE o.member_no = ? "
				+ "ORDER BY o.order_date DESC, od.order_detail_no ASC";

		try (Connection conn = ConnectionProvider.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, memberNo);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					OrderItemDTO dto = new OrderItemDTO();

					// ORDERS
					dto.setOrderNo(rs.getInt("order_no"));

					// 2. Null 체크 후 LocalDate로 변환
					dto.setOrderDate(rs.getTimestamp("order_date"));
				
					dto.setOrderStatus(rs.getString("order_status"));

					dto.setTotalPrice(rs.getInt("order_total_price"));

					// ORDER_DETAIL
					dto.setOrderDetailNo(rs.getLong("order_detail_no"));
					dto.setQuantity(rs.getInt("order_qty"));


					// PRODUCT
					dto.setProductNo(rs.getLong("product_no"));
					dto.setProductName(rs.getString("product_name"));


					// PRODUCT_OPTION

					dto.setOption1Type(rs.getString("OPTION1_TYPE"));
					dto.setOption1Value(rs.getString("OPTION1_VALUE"));
					dto.setOption2Type(rs.getString("OPTION2_TYPE"));
					dto.setOption2Value(rs.getString("OPTION2_VALUE"));



					list.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}