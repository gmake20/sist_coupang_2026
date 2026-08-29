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

    // 특정 주문번호(ORDER_NO)에 해당하는 주문 상세 상품 및 배송/결제 정보 조회
    public List<OrderDetailDTO> getOrderDetailList(int orderNo) {
        List<OrderDetailDTO> list = new ArrayList<>();

        // ORDERS, ORDER_DETAIL, PRODUCT, MEMBER, ORDER_ADDRESS, PAYMENT 6개 테이블 조인
        String sql = "SELECT " +
                     "    od.ORDER_DETAIL_NO, " +
                     "    od.ORDER_NO, " +
                     "    o.MEMBER_NO, " +
                     "    o.ORDER_STATUS, " +
                     "    o.ORDER_DATE, " +
                     "    o.TOTAL_PRICE, " +
                     "    p.PRODUCT_NO, " +
                     "    p.PRODUCT_NAME, " +
                     "    od.ORDER_QTY AS QUANTITY, " +
                     "    o.DELIVERY_FEE, " +
                     "    pay.PAYMENT_METHOD, " +
                     "    oa.REQUEST_MSG, " +
                     "    oa.ADDRESS, " +
                     "    oa.DETAIL_ADDRESS, " +
                     "    m.MEMBER_NAME, " +
                     "    m.PHONE, " +
                     "    po.OPTION1_TYPE, " +
                     "    po.OPTION1_VALUE, " +
                     "    po.OPTION2_TYPE, " +
                     "    po.OPTION2_VALUE " +
                     "FROM ORDERS o " +
                     "JOIN ORDER_DETAIL od ON o.ORDER_NO = od.ORDER_NO " +
                     "JOIN PRODUCT p ON od.PRODUCT_NO = p.PRODUCT_NO " +
                     "JOIN MEMBER m ON o.MEMBER_NO = m.MEMBER_NO " +
                     "LEFT JOIN ORDER_ADDRESS oa ON o.ORDER_ADDRESS_NO = oa.ORDER_ADDRESS_NO " +
                     "LEFT JOIN PAYMENT pay ON o.ORDER_NO = pay.ORDER_NO " +
                     "LEFT JOIN PRODUCT_OPTION po ON od.OPTION_ID = po.OPTION_ID " +
                     "WHERE o.ORDER_NO = ?";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetailDTO dto = new OrderDetailDTO();

                    // PK / FK 및 주문 기본 정보
                    dto.setOrderDetailNo(rs.getLong("ORDER_DETAIL_NO"));
                    dto.setOrderNo(rs.getInt("ORDER_NO"));
                    dto.setMemberNo(rs.getInt("MEMBER_NO"));
                    dto.setOrderStatus(rs.getString("ORDER_STATUS"));
                    dto.setOrderDate(rs.getTimestamp("ORDER_DATE"));
                    dto.setTotalPrice(rs.getInt("TOTAL_PRICE"));

                    // 상품 정보 (ORDER_QTY -> quantity 별칭 매핑)
                    dto.setProductNo(rs.getLong("PRODUCT_NO"));
                    dto.setProductName(rs.getString("PRODUCT_NAME"));
                    dto.setQuantity(rs.getInt("QUANTITY"));
                    dto.setDeliveryFee(rs.getInt("DELIVERY_FEE"));

                    // 결제 및 배송지 정보
                    dto.setPaymentMethod(rs.getString("PAYMENT_METHOD"));
                    dto.setRequestMsg(rs.getString("REQUEST_MSG"));
                    dto.setAddress(rs.getString("ADDRESS"));
                    dto.setDetailAddress(rs.getString("DETAIL_ADDRESS"));
                    dto.setMemberName(rs.getString("MEMBER_NAME"));
                    dto.setPhone(rs.getString("PHONE"));

                    // 옵션 정보 (NULL 허용)
                    dto.setOption1Type(rs.getString("OPTION1_TYPE"));
                    dto.setOption1Value(rs.getString("OPTION1_VALUE"));
                    dto.setOption2Type(rs.getString("OPTION2_TYPE"));
                    dto.setOption2Value(rs.getString("OPTION2_VALUE"));

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
    
    public List<OrderDetailDTO> getOrderDetailListv2(int orderNo) {

        List<OrderDetailDTO> list = new ArrayList<>();

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

                    o.DELIVERY_FEE,

                    pay.PAYMENT_METHOD,

                    /* 카드사 이름 */
                    CASE pm.CARD_COMPANY
                        WHEN 'BC'      THEN '비씨카드'
                        WHEN 'SHINHAN' THEN '신한카드'
                        WHEN 'KB'      THEN 'KB국민카드'
                        WHEN 'SAMSUNG' THEN '삼성카드'
                        WHEN 'HYUNDAI' THEN '현대카드'
                        WHEN 'LOTTE'   THEN '롯데카드'
                        WHEN 'HANA'    THEN '하나카드'
                        WHEN 'WOORI'   THEN '우리카드'
                        WHEN 'NH'      THEN 'NH농협카드'
                        ELSE pm.CARD_COMPANY
                    END AS CARD_COMPANY_NAME,

                    /* 은행 이름 */
                    CASE pm.BANK_CODE
                        WHEN 'SHINHAN' THEN '신한은행'
                        WHEN 'KB'      THEN 'KB국민은행'
                        WHEN 'WOORI'   THEN '우리은행'
                        WHEN 'NH'      THEN 'NH농협은행'
                        WHEN 'HANA'    THEN '하나은행'
                        WHEN 'KAKAO'   THEN '카카오뱅크'
                        WHEN 'TOSS'    THEN '토스뱅크'
                        ELSE pm.BANK_CODE
                    END AS BANK_NAME,

                    oa.REQUEST_MSG,
                    oa.ADDRESS,
                    oa.DETAIL_ADDRESS,

                    m.MEMBER_NAME,
                    m.PHONE,

                    po.OPTION1_TYPE,
                    po.OPTION1_VALUE,
                    po.OPTION2_TYPE,
                    po.OPTION2_VALUE

                FROM ORDERS o

                JOIN ORDER_DETAIL od
                    ON o.ORDER_NO = od.ORDER_NO

                JOIN PRODUCT p
                    ON od.PRODUCT_NO = p.PRODUCT_NO

                JOIN MEMBER m
                    ON o.MEMBER_NO = m.MEMBER_NO

                LEFT JOIN ORDER_ADDRESS oa
                    ON o.ORDER_ADDRESS_NO = oa.ORDER_ADDRESS_NO

                LEFT JOIN PAYMENT pay
                    ON o.ORDER_NO = pay.ORDER_NO

                LEFT JOIN PAYMENT_METHOD pm
                    ON pay.PAYMENT_METHOD_NO = pm.PAYMENT_METHOD_NO

                LEFT JOIN PRODUCT_OPTION po
                    ON od.OPTION_ID = po.OPTION_ID

                WHERE o.ORDER_NO = ?
                """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, orderNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    OrderDetailDTO dto = new OrderDetailDTO();

                    // 주문 기본 정보
                    dto.setOrderDetailNo(
                            rs.getLong("ORDER_DETAIL_NO")
                    );

                    dto.setOrderNo(
                            rs.getInt("ORDER_NO")
                    );

                    dto.setMemberNo(
                            rs.getInt("MEMBER_NO")
                    );

                    dto.setOrderStatus(
                            rs.getString("ORDER_STATUS")
                    );

                    dto.setOrderDate(
                            rs.getTimestamp("ORDER_DATE")
                    );

                    dto.setTotalPrice(
                            rs.getInt("TOTAL_PRICE")
                    );


                    // 상품 정보
                    dto.setProductNo(
                            rs.getLong("PRODUCT_NO")
                    );

                    dto.setProductName(
                            rs.getString("PRODUCT_NAME")
                    );

                    dto.setQuantity(
                            rs.getInt("QUANTITY")
                    );

                    dto.setDeliveryFee(
                            rs.getInt("DELIVERY_FEE")
                    );


                    // 결제 정보
                    dto.setPaymentMethod(
                            rs.getString("PAYMENT_METHOD")
                    );

                    dto.setCardCompanyName(
                            rs.getString("CARD_COMPANY_NAME")
                    );

                    dto.setBankName(
                            rs.getString("BANK_NAME")
                    );


                    // 배송지 정보
                    dto.setRequestMsg(
                            rs.getString("REQUEST_MSG")
                    );

                    dto.setAddress(
                            rs.getString("ADDRESS")
                    );

                    dto.setDetailAddress(
                            rs.getString("DETAIL_ADDRESS")
                    );

                    dto.setMemberName(
                            rs.getString("MEMBER_NAME")
                    );

                    dto.setPhone(
                            rs.getString("PHONE")
                    );


                    // 상품 옵션
                    dto.setOption1Type(
                            rs.getString("OPTION1_TYPE")
                    );

                    dto.setOption1Value(
                            rs.getString("OPTION1_VALUE")
                    );

                    dto.setOption2Type(
                            rs.getString("OPTION2_TYPE")
                    );

                    dto.setOption2Value(
                            rs.getString("OPTION2_VALUE")
                    );


                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}