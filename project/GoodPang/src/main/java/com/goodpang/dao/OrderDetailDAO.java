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

   
    public List<OrderDetailDTO> getOrderDetailList(
            int orderNo,
            int memberNo) {

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

                    od.OPTION_ID,
                    od.ORDER_QTY AS QUANTITY,
                    od.PRICE AS ITEM_PRICE,

                    o.DELIVERY_FEE,
                    pay.PAYMENT_METHOD,

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
                    po.OPTION2_VALUE,

                    (
                        SELECT pi.IMAGE_URL
                        FROM PRODUCT_IMAGE pi
                        WHERE pi.PRODUCT_NO = p.PRODUCT_NO
                        ORDER BY
                            CASE
                                WHEN pi.OPTION_ID = od.OPTION_ID
                                     AND pi.IMAGE_PURPOSE = '대표' THEN 0
                                WHEN pi.OPTION_ID = od.OPTION_ID THEN 1
                                WHEN pi.IMAGE_PURPOSE = '대표' THEN 2
                                ELSE 3
                            END,
                            pi.IMAGE_NO ASC
                        FETCH FIRST 1 ROW ONLY
                    ) AS IMAGE_URL

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
                  AND o.MEMBER_NO = ?

                ORDER BY od.ORDER_DETAIL_NO ASC
                """;

        try (
            Connection conn =
                    ConnectionProvider.getConnection();

            PreparedStatement pstmt =
                    conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, memberNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    OrderDetailDTO dto =
                            new OrderDetailDTO();

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

                    dto.setProductNo(
                            rs.getLong("PRODUCT_NO")
                    );

                    dto.setProductName(
                            rs.getString("PRODUCT_NAME")
                    );

                    dto.setQuantity(
                            rs.getInt("QUANTITY")
                    );

                    dto.setItemPrice(
                            rs.getInt("ITEM_PRICE")
                    );

                    dto.setDeliveryFee(
                            rs.getInt("DELIVERY_FEE")
                    );

                    dto.setPaymentMethod(
                            rs.getString("PAYMENT_METHOD")
                    );

                    dto.setCardCompanyName(
                            rs.getString("CARD_COMPANY_NAME")
                    );

                    dto.setBankName(
                            rs.getString("BANK_NAME")
                    );

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

                    dto.setOptionId(
                            rs.getInt("OPTION_ID")
                    );

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

                    dto.setImageUrl(
                            rs.getString("IMAGE_URL")
                    );

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "주문 상세 조회 실패",
                    e
            );
        }

        return list;
    }
}