package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.CheckoutDTO;
import com.goodpang.dto.CheckoutItemDTO;
import com.goodpang.util.ConnectionProvider;

public class CheckoutDAO {

	public CheckoutDTO getCheckout(
	        int checkoutNo,
	        int memberNo) {

	    CheckoutDTO dto = null;

	    String sql = """
	        SELECT
	            CHECKOUT_NO,
	            MEMBER_NO,
	            PRODUCT_AMOUNT,
	            INSTANT_DISCOUNT,
	            COUPON_DISCOUNT,
	            CASH_USED,
	            DELIVERY_FEE,
	            TOTAL_PRICE
	        FROM CHECKOUT
	        WHERE CHECKOUT_NO = ?
	          AND MEMBER_NO = ?
	        """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql);
	    ) {

	        pstmt.setInt(1, checkoutNo);
	        pstmt.setInt(2, memberNo);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                dto = new CheckoutDTO();

	                dto.setCheckoutNo(
	                        rs.getInt("CHECKOUT_NO")
	                );

	                dto.setMemberNo(
	                        rs.getInt("MEMBER_NO")
	                );

	                dto.setProductAmount(
	                        rs.getInt("PRODUCT_AMOUNT")
	                );

	                dto.setInstantDiscount(
	                        rs.getInt("INSTANT_DISCOUNT")
	                );

	                dto.setCouponDiscount(
	                        rs.getInt("COUPON_DISCOUNT")
	                );

	                dto.setCashUsed(
	                        rs.getInt("CASH_USED")
	                );

	                dto.setDeliveryFee(
	                        rs.getInt("DELIVERY_FEE")
	                );

	                dto.setTotalPrice(
	                        rs.getInt("TOTAL_PRICE")
	                );
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return dto;
	}
	
	public List<CheckoutItemDTO> getCheckoutItems(
	        int checkoutNo) {

	    List<CheckoutItemDTO> list =
	            new ArrayList<>();

	    String sql = """
	        SELECT
	            CHECKOUT_ITEM_NO,
	            CHECKOUT_NO,
	            PRODUCT_NO,
	            OPTION_ID,
	            ORDER_QTY,
	            PRICE
	        FROM CHECKOUT_ITEM
	        WHERE CHECKOUT_NO = ?
	        ORDER BY CHECKOUT_ITEM_NO
	        """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql);
	    ) {

	        pstmt.setInt(1, checkoutNo);

	        try (ResultSet rs =
	                     pstmt.executeQuery()) {

	            while (rs.next()) {

	                CheckoutItemDTO dto =
	                        new CheckoutItemDTO();

	                dto.setCheckoutItemNo(
	                        rs.getInt(
	                                "CHECKOUT_ITEM_NO"
	                        )
	                );

	                dto.setCheckoutNo(
	                        rs.getInt(
	                                "CHECKOUT_NO"
	                        )
	                );

	                dto.setProductNo(
	                        rs.getInt(
	                                "PRODUCT_NO"
	                        )
	                );

	                int optionId =
	                        rs.getInt(
	                                "OPTION_ID"
	                        );

	                if (!rs.wasNull()) {
	                    dto.setOptionId(
	                            optionId
	                    );
	                }

	                dto.setOrderQty(
	                        rs.getInt(
	                                "ORDER_QTY"
	                        )
	                );

	                dto.setPrice(
	                        rs.getInt(
	                                "PRICE"
	                        )
	                );

	                list.add(dto);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public List<CheckoutItemDTO> getCheckoutItemsPRODUCT(
	        int checkoutNo) {

	    List<CheckoutItemDTO> list =
	            new ArrayList<>();

	    String sql = """
		        SELECT
		            ci.CHECKOUT_ITEM_NO,
		            ci.CHECKOUT_NO,
		            ci.PRODUCT_NO,
		            ci.OPTION_ID,
		            ci.ORDER_QTY,
		            ci.PRICE,
		            p.PRODUCT_NAME,
		            p.PRODUCT_IMAGE
		        FROM CHECKOUT_ITEM ci
		        JOIN PRODUCT p
		          ON ci.PRODUCT_NO = p.PRODUCT_NO
		        WHERE ci.CHECKOUT_NO = ?
		        ORDER BY ci.CHECKOUT_ITEM_NO
		        """;

	    try (
	        Connection conn =
	                ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	                conn.prepareStatement(sql);
	    ) {

	        pstmt.setInt(
	                1,
	                checkoutNo
	        );

	        try (ResultSet rs =
	                     pstmt.executeQuery()) {

	            while (rs.next()) {

	                CheckoutItemDTO dto =
	                        new CheckoutItemDTO();

	                dto.setCheckoutItemNo(
	                        rs.getInt(
	                                "CHECKOUT_ITEM_NO"
	                        )
	                );

	                dto.setCheckoutNo(
	                        rs.getInt(
	                                "CHECKOUT_NO"
	                        )
	                );

	                dto.setProductNo(
	                        rs.getInt(
	                                "PRODUCT_NO"
	                        )
	                );

	                int optionId =
	                        rs.getInt(
	                                "OPTION_ID"
	                        );

	                if (!rs.wasNull()) {
	                    dto.setOptionId(
	                            optionId
	                    );
	                }

	                dto.setOrderQty(
	                        rs.getInt(
	                                "ORDER_QTY"
	                        )
	                );

	                dto.setPrice(
	                        rs.getInt(
	                                "PRICE"
	                        )
	                );

	                dto.setProductName(
	                        rs.getString(
	                                "PRODUCT_NAME"
	                        )
	                );

					/*
					 * dto.setProductImage( rs.getString( "PRODUCT_IMAGE" ) );
					 */

	                list.add(dto);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public int getProductPrice(
	        Connection conn,
	        int productNo)
	        throws Exception {

	    String sql = """
	            SELECT PRODUCT_PRICE
	            FROM PRODUCT
	            WHERE PRODUCT_NO = ?
	            """;

	    try (
	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(
	                1,
	                productNo
	        );

	        try (
	            ResultSet rs =
	                    pstmt.executeQuery()
	        ) {

	            if (rs.next()) {

	                return rs.getInt(
	                        "PRODUCT_PRICE"
	                );
	            }
	        }
	    }

	    return -1;
	}
	
	public int insertCheckout(
	        Connection conn,
	        int memberNo,
	        int productAmount,
	        int instantDiscount,
	        int couponDiscount,
	        int cashUsed,
	        int deliveryFee,
	        int totalPrice)
	        throws Exception {

	    int checkoutNo;

	    String seqSql = """
	            SELECT SEQ_CHECKOUT_NO.NEXTVAL
	            FROM DUAL
	            """;

	    try (
	        PreparedStatement pstmt =
	                conn.prepareStatement(seqSql);

	        ResultSet rs =
	                pstmt.executeQuery()
	    ) {

	        if (!rs.next()) {

	            throw new Exception(
	                    "CHECKOUT_NO 생성 실패"
	            );
	        }

	        checkoutNo =
	                rs.getInt(1);
	    }

	    String sql = """
	            INSERT INTO CHECKOUT (
	                CHECKOUT_NO,
	                MEMBER_NO,
	                CREATED_AT,
	                PRODUCT_AMOUNT,
	                INSTANT_DISCOUNT,
	                COUPON_DISCOUNT,
	                CASH_USED,
	                DELIVERY_FEE,
	                TOTAL_PRICE
	            )
	            VALUES (
	                ?,
	                ?,
	                SYSDATE,
	                ?,
	                ?,
	                ?,
	                ?,
	                ?,
	                ?
	            )
	            """;

	    try (
	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(
	                1,
	                checkoutNo
	        );

	        pstmt.setInt(
	                2,
	                memberNo
	        );

	        pstmt.setInt(
	                3,
	                productAmount
	        );

	        pstmt.setInt(
	                4,
	                instantDiscount
	        );

	        pstmt.setInt(
	                5,
	                couponDiscount
	        );

	        pstmt.setInt(
	                6,
	                cashUsed
	        );

	        pstmt.setInt(
	                7,
	                deliveryFee
	        );

	        pstmt.setInt(
	                8,
	                totalPrice
	        );


	        int result =
	                pstmt.executeUpdate();


	        if (result != 1) {

	            throw new Exception(
	                    "CHECKOUT INSERT 실패"
	            );
	        }
	    }
	    return checkoutNo;
	}
	
	public int insertCheckoutItem(
	        Connection conn,
	        int checkoutNo,
	        int productNo,
	        Integer optionId,
	        int quantity,
	        int price)
	        throws Exception {

	    String sql = """
	            INSERT INTO CHECKOUT_ITEM (
	                CHECKOUT_ITEM_NO,
	                CHECKOUT_NO,
	                PRODUCT_NO,
	                OPTION_ID,
	                ORDER_QTY,
	                PRICE
	            )
	            VALUES (
	                SEQ_CHECKOUT_ITEM_NO.NEXTVAL,
	                ?,
	                ?,
	                ?,
	                ?,
	                ?
	            )
	            """;

	    try (
	        PreparedStatement pstmt =
	                conn.prepareStatement(sql)
	    ) {

	        pstmt.setInt(
	                1,
	                checkoutNo
	        );

	        pstmt.setInt(
	                2,
	                productNo
	        );

	        if (optionId == null) {

	            pstmt.setNull(
	                    3,
	                    java.sql.Types.NUMERIC
	            );

	        } else {

	            pstmt.setInt(
	                    3,
	                    optionId
	            );
	        }

	        pstmt.setInt(
	                4,
	                quantity
	        );

	        pstmt.setInt(
	                5,
	                price
	        );
	        return pstmt.executeUpdate();
	    }
	}
}
