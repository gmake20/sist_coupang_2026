package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorSettlementDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 정산관리(vendor-settlement.jsp) 조회.
 * 배송완료된 주문을, 그 주문의 DELIVERY_END_DATE(배송완료일) 기준 1주 단위(월요일 시작)로
 * 묶어서 매출액을 집계한다. 수수료율/정산주기는 VendorSettlementDTO 상단 주석 참고 - 실제
 * 값이 아니라 가정치다.
 */
public class VendorSettlementDAO {

    public List<VendorSettlementDTO> findBySellerNo(int sellerNo) {

        List<VendorSettlementDTO> list = new ArrayList<>();

        String sql = """
            SELECT
                TRUNC(D.DELIVERY_END_DATE, 'IW') AS PERIOD_START,
                COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT,
                SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
            WHERE P.SELLER_NO = ?
              AND O.ORDER_STATUS = '배송완료'
              AND D.DELIVERY_END_DATE IS NOT NULL
            GROUP BY TRUNC(D.DELIVERY_END_DATE, 'IW')
            ORDER BY PERIOD_START DESC
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

    private VendorSettlementDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorSettlementDTO dto = new VendorSettlementDTO();

        dto.setPeriodStart(rs.getDate("PERIOD_START").toLocalDate());
        dto.setOrderCount(rs.getInt("ORDER_COUNT"));
        dto.setSalesAmount(rs.getLong("SALES_AMOUNT"));

        return dto;
    }
}
