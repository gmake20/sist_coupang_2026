package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorOrderListDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 주문 목록(vendor_orders.jsp) 조회.
 * 판매자의 상품이 포함된 주문 라인(ORDER_DETAIL)을 기준으로 한 건씩 보여준다.
 * 배송상태(출고대기/배송중 등)는 별도 컬럼이 없어서 ORDER_STATUS만 다룬다.
 */
public class VendorOrderListDAO {

    // 판매자(sellerNo)의 상품이 포함된 주문 목록 - 최근 주문순
    public List<VendorOrderListDTO> findBySellerNo(int sellerNo) {

        List<VendorOrderListDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                O.ORDER_NO,
                OD.ORDER_DETAIL_NO,
                P.PRODUCT_NAME,
                PO.OPTION1_VALUE,
                PO.OPTION2_VALUE,
                PO.OPTION3_VALUE,
                OD.ORDER_QTY,
                OD.PRICE,
                O.ORDER_STATUS,
                O.ORDER_DATE,
                M.MEMBER_NAME,
                M.PHONE
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
                LEFT JOIN PRODUCT_OPTION PO ON OD.OPTION_ID = PO.OPTION_ID
            WHERE P.SELLER_NO = ?
            ORDER BY O.ORDER_DATE DESC, O.ORDER_NO DESC
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

    private VendorOrderListDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorOrderListDTO dto = new VendorOrderListDTO();

        dto.setOrderNo(rs.getInt("ORDER_NO"));
        dto.setOrderDetailNo(rs.getInt("ORDER_DETAIL_NO"));
        dto.setProductName(rs.getString("PRODUCT_NAME"));
        dto.setOptionLabel(buildOptionLabel(rs.getString("OPTION1_VALUE"), rs.getString("OPTION2_VALUE"), rs.getString("OPTION3_VALUE")));
        dto.setOrderQty(rs.getInt("ORDER_QTY"));
        dto.setPrice(rs.getInt("PRICE"));
        dto.setOrderStatus(rs.getString("ORDER_STATUS"));
        dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));
        dto.setBuyerName(rs.getString("MEMBER_NAME"));
        dto.setBuyerPhone(rs.getString("PHONE"));

        return dto;
    }

    private String buildOptionLabel(String option1Value, String option2Value, String option3Value) {

        StringBuilder sb = new StringBuilder();

        for (String value : new String[] { option1Value, option2Value, option3Value }) {
            if (value != null && !value.isBlank()) {
                if (sb.length() > 0) {
                    sb.append(" / ");
                }
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
