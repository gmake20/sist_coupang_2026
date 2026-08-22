package com.goodpang.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.OrderCompleteDTO;
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
									rs.getString("ADDRESS_DEFAULT")
							);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return address;
	}

	public List<AddressDTO> getAddressList(
			int memberNo) {

		List<AddressDTO> list =
				new ArrayList<>();

		String sql = """
				SELECT
				    ADDRESS_NO,
				    RECEIVER_NAME,
				    ADDRESS,
				    DETAIL_ADDRESS,
				    TEL,
				    REQUEST_MSG,
				    ADDRESS_DEFAULT
				FROM DELIVERY_ADDRESS
				WHERE MEMBER_NO = ?
				ORDER BY
				    ADDRESS_DEFAULT DESC,
				    ADDRESS_NO DESC
				""";

		try (
				Connection conn =
				ConnectionProvider.getConnection();

				PreparedStatement pstmt =
						conn.prepareStatement(sql)
				) {

			pstmt.setInt(
					1,
					memberNo
					);

			try (
					ResultSet rs =
					pstmt.executeQuery()
					) {

				while (rs.next()) {

					AddressDTO dto =
							new AddressDTO();

					dto.setAddressNo(
							rs.getInt(
									"ADDRESS_NO"
									)
							);

					dto.setReceiverName(
							rs.getString(
									"RECEIVER_NAME"
									)
							);

					dto.setAddress(
							rs.getString(
									"ADDRESS"
									)
							);

					dto.setDetailAddress(
							rs.getString(
									"DETAIL_ADDRESS"
									)
							);

					dto.setTel(
							rs.getString(
									"TEL"
									)
							);

					dto.setRequestMsg(
							rs.getString(
									"REQUEST_MSG"
									)
							);

					dto.setAddressDefault(
									rs.getString("ADDRESS_DEFAULT")
							);

					list.add(dto);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
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
				    NVL(o.product_amount, 0) AS total_product_price,
				    NVL(o.delivery_fee, 0) AS delivery_fee,
				    NVL(o.instant_discount, 0) AS instant_discount,
				    NVL(o.coupon_discount, 0) AS coupon_discount,
				    NVL(o.cash_used, 0) AS cash_used,
				    NVL(o.total_price, 0) AS final_price
				FROM orders o
				WHERE o.order_no = ?
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
					summary.setInstantDiscount(rs.getInt("instant_discount"));
					summary.setCouponDiscount(rs.getInt("coupon_discount"));
					summary.setCashUsed(rs.getInt("cash_used"));

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return summary;
	}

	public OrderCompleteDTO getOrderComplete(int orderNo) {

		OrderCompleteDTO dto = null;

		String sql = """
				SELECT
				    o.ORDER_NO,
				    o.MEMBER_NO,
				    o.ORDER_DATE,
				    o.DELIVERY_FEE,
				    o.TOTAL_PRICE,
				    o.ORDER_STATUS,
				    o.PRODUCT_AMOUNT,
				    o.INSTANT_DISCOUNT,
				    o.COUPON_DISCOUNT,
				    o.CASH_USED,

				    d.RECEIVER_NAME,
				    d.TEL,
				    d.ZIPCODE,
				    d.ADDRESS,
				    d.DETAIL_ADDRESS,
				    d.REQUEST_MSG

				FROM ORDERS o

				JOIN ORDER_DELIVERY d
				  ON o.ORDER_NO = d.ORDER_NO

				WHERE o.ORDER_NO = ?
				""";

		try (
				Connection conn = ConnectionProvider.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)
				) {

			/*
			 * ORDER_NO
			 */
			pstmt.setInt(1, orderNo);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					dto = new OrderCompleteDTO();

					dto.setReceiverName(
							rs.getString("RECEIVER_NAME")
							);

					dto.setReceiverPhone(
							rs.getString("TEL")
							);

					dto.setZipcode(
							rs.getString("ZIPCODE")
							);

					String address =
							rs.getString("ADDRESS");

					String detailAddress =
							rs.getString("DETAIL_ADDRESS");


					if (address == null) {
						address = "";
					}

					if (detailAddress != null
							&& !detailAddress.isBlank()) {

						address += " " + detailAddress;
					}

					dto.setAddress(address);
					dto.setRequestMsg(
							rs.getString("REQUEST_MSG")
							);

					if (rs.getDate("ORDER_DATE") != null) {

						dto.setArrivalDate(
								rs.getDate("ORDER_DATE")
								.toLocalDate()
								.plusDays(1)
								.toString()
								);
					}


					/*

					 * 현재 ORDERS / ORDER_DELIVERY에는
					 * 판매자 정보가 없으므로 임시값
					 */
					dto.setSellerName(
							"주식회사 회사이름"
							);
					dto.setOrderAmount(
							rs.getInt("PRODUCT_AMOUNT")
							);

					int discountAmount =
							rs.getInt("INSTANT_DISCOUNT")
							+
							rs.getInt("COUPON_DISCOUNT");

					dto.setDiscountAmount(
							discountAmount
							);

					dto.setShippingFee(
							rs.getInt("DELIVERY_FEE")
							);

					dto.setPaymentAmount(
							rs.getInt("TOTAL_PRICE")
							);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dto;
	}
	
	public int insertOrderDelivery(
            Connection conn,
            int orderNo,
            int addressNo) throws Exception {

        String sql = """
                INSERT INTO ORDER_DELIVERY (
                    ORDER_NO,
                    RECEIVER_NAME,
                    TEL,
                    ZIPCODE,
                    ADDRESS,
                    DETAIL_ADDRESS,
                    REQUEST_MSG
                )
                SELECT
                    ?,
                    RECEIVER_NAME,
                    TEL,
                    ZIPCODE,
                    ADDRESS,
                    DETAIL_ADDRESS,
                    REQUEST_MSG
                FROM DELIVERY_ADDRESS
                WHERE ADDRESS_NO = ?
                """;

        try (
            PreparedStatement pstmt =
                conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, addressNo);

            return pstmt.executeUpdate();
        }
    }
}