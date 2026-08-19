package com.goodpang.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.OrderItemDTO;
import com.goodpang.dto.OrderSummaryDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.DBConn;

public class OrderDAO {

    /*
     * 기본 배송지 조회
     */
	public AddressDTO getAddress(int memberNo) {

	    AddressDTO address = null;

	    String sql = """
	            SELECT
	                ADDRESS_NO,
	                MEMBER_NO,
	                RECEIVER_NAME,
	                TEL,
	                ZIPCODE,
	                ADDRESS,
	                DETAIL_ADDRESS,
	                REQUEST_MSG,
	                ADDRESS_DEFAULT
	            FROM DELIVERY_ADDRESS
	            WHERE MEMBER_NO = ?
	              AND ADDRESS_DEFAULT = 'Y'
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(1, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                address = new AddressDTO();

	                address.setAddressNo(
	                    rs.getInt("ADDRESS_NO")
	                );

	                address.setMemberNo(
	                    rs.getInt("MEMBER_NO")
	                );

	                address.setReceiverName(
	                    rs.getString("RECEIVER_NAME")
	                );

	                address.setTel(
	                    rs.getString("TEL")
	                );

	                address.setZipcode(
	                    rs.getString("ZIPCODE")
	                );

	                address.setAddress(
	                    rs.getString("ADDRESS")
	                );

	                address.setDetailAddress(
	                    rs.getString("DETAIL_ADDRESS")
	                );

	                address.setRequestMsg(
	                    rs.getString("REQUEST_MSG")
	                );

	                address.setAddressDefault(
	                    "Y".equals(
	                        rs.getString("ADDRESS_DEFAULT")
	                    )
	                );
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return address;
	}

    /*
     * 주문 상품 조회
     */
    public List<OrderItemDTO> getOrderItems(int orderNo) {

        List<OrderItemDTO> orderItems =
                new ArrayList<>();

        String sql = """
                SELECT
                    P.PRODUCT_NAME,
                    OI.OPTION_NAME,
                    OI.SALE_PRICE,
                    OI.QUANTITY,
                    OI.FREE_DELIVERY
                FROM ORDER_ITEM OI
                JOIN PRODUCT P
                  ON OI.PRODUCT_NO = P.PRODUCT_NO
                WHERE OI.ORDER_NO = ?
                ORDER BY OI.ORDER_ITEM_NO
                """;

        try (
                Connection conn = ConnectionProvider.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    OrderItemDTO item =
                            new OrderItemDTO();

                    item.setProductName(
                            rs.getString("PRODUCT_NAME")
                    );

                    item.setOptionName(
                            rs.getString("OPTION_NAME")
                    );

                    item.setSalePrice(
                            rs.getInt("SALE_PRICE")
                    );

                    item.setQuantity(
                            rs.getInt("QUANTITY")
                    );

                    item.setFreeDelivery(
                            rs.getBoolean("FREE_DELIVERY")
                    );

                    orderItems.add(item);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderItems;
    }


    /*
     * 주문 금액 조회
     */
    public OrderSummaryDTO getOrderSummary(int orderNo) {

        OrderSummaryDTO summary = null;

        String sql = """
                SELECT
                    o.order_no,

                    NVL(
                        SUM(od.price * od.order_qty),
                        0
                    ) AS total_product_price,

                    NVL(
                        o.delivery_fee,
                        0
                    ) AS delivery_fee,

                    NVL(
                        SUM(od.price * od.order_qty),
                        0
                    )
                    +
                    NVL(
                        o.delivery_fee,
                        0
                    ) AS final_price

                FROM orders o

                LEFT JOIN order_detail od
                       ON o.order_no = od.order_no

                WHERE o.order_no = ?

                GROUP BY
                    o.order_no,
                    o.delivery_fee
                """;

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    summary =
                            new OrderSummaryDTO();

                    summary.setTotalProductPrice(
                            rs.getInt(
                                    "total_product_price"
                            )
                    );

                    summary.setDeliveryFee(
                            rs.getInt(
                                    "delivery_fee"
                            )
                    );

                    summary.setFinalPrice(
                            rs.getInt(
                                    "final_price"
                            )
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return summary;
    }
}