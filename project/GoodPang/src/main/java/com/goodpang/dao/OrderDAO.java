package com.goodpang.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.OrderItemDTO;
import com.goodpang.dto.OrderSummaryDTO;
import com.goodpang.util.DBConn;

public class OrderDAO {

    /*
     * 기본 배송지 조회
     */
    public AddressDTO getAddress(int memberNo) {

        AddressDTO address = null;

        String sql = """
                SELECT
                    RECEIVER_NAME,
                    ROAD_ADDRESS,
                    DETAIL_ADDRESS,
                    PHONE,
                    IS_DEFAULT
                FROM DELIVERY_ADDRESS
                WHERE MEMBER_NO = ?
                AND IS_DEFAULT = 1
                """;

        try (
                Connection conn = DBConn.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    address = new AddressDTO();

                    address.setName(
                            rs.getString("RECEIVER_NAME")
                    );

                    address.setRoadAddress(
                            rs.getString("ROAD_ADDRESS")
                    );

                    address.setDetailAddress(
                            rs.getString("DETAIL_ADDRESS")
                    );

                    address.setPhone(
                            rs.getString("PHONE")
                    );

                    address.setDefaultAddress(
                            rs.getBoolean("IS_DEFAULT")
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
                Connection conn = DBConn.getConnection();
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

        OrderSummaryDTO summary =
                new OrderSummaryDTO();

        String sql = """
                SELECT
                    TOTAL_PRODUCT_PRICE,
                    INSTANT_DISCOUNT,
                    COUPON_DISCOUNT,
                    DELIVERY_FEE,
                    USED_CASH,
                    REMAIN_CASH,
                    FINAL_PRICE
                FROM ORDERS
                WHERE ORDER_NO = ?
                """;

        try (
                Connection conn = DBConn.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    summary.setTotalProductPrice(
                            rs.getInt("TOTAL_PRODUCT_PRICE")
                    );

                    summary.setInstantDiscount(
                            rs.getInt("INSTANT_DISCOUNT")
                    );

                    summary.setCouponDiscount(
                            rs.getInt("COUPON_DISCOUNT")
                    );

                    summary.setDeliveryFee(
                            rs.getInt("DELIVERY_FEE")
                    );

                    summary.setUsedCash(
                            rs.getInt("USED_CASH")
                    );

                    summary.setRemainCash(
                            rs.getInt("REMAIN_CASH")
                    );

                    summary.setFinalPrice(
                            rs.getInt("FINAL_PRICE")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return summary;
    }


    /*
     * 배송 요청사항 조회
     */
    public String getDeliveryRequest(int orderNo) {

        String deliveryRequest = null;

        String sql = """
                SELECT DELIVERY_REQUEST
                FROM ORDERS
                WHERE ORDER_NO = ?
                """;

        try (
                Connection conn = DBConn.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    deliveryRequest =
                            rs.getString("DELIVERY_REQUEST");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return deliveryRequest;
    }
}