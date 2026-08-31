package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.VendorDashboardStatDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자센터 대시보드(vendor-dashboard.jsp) 상단 KPI 카드 집계.
 */
public class VendorDashboardDAO {

    // 오늘/어제 주문수·매출 - 이 판매자 상품이 포함된 주문(ORDER_DETAIL) 기준, 주문취소 건 제외
    public VendorDashboardStatDTO getTodayStat(int sellerNo) {

        VendorDashboardStatDTO dto = new VendorDashboardStatDTO();

        String sql = """
            SELECT
                COUNT(DISTINCT CASE WHEN TRUNC(O.ORDER_DATE) = TRUNC(SYSDATE)
                                     THEN O.ORDER_NO END) AS TODAY_ORDER_COUNT,
                NVL(SUM(CASE WHEN TRUNC(O.ORDER_DATE) = TRUNC(SYSDATE)
                              THEN OD.PRICE * OD.ORDER_QTY END), 0) AS TODAY_SALES,
                COUNT(DISTINCT CASE WHEN TRUNC(O.ORDER_DATE) = TRUNC(SYSDATE) - 1
                                     THEN O.ORDER_NO END) AS YESTERDAY_ORDER_COUNT,
                NVL(SUM(CASE WHEN TRUNC(O.ORDER_DATE) = TRUNC(SYSDATE) - 1
                              THEN OD.PRICE * OD.ORDER_QTY END), 0) AS YESTERDAY_SALES
            FROM ORDER_DETAIL OD
                JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
            WHERE P.SELLER_NO = ?
              AND O.ORDER_STATUS != '주문취소'
              AND TRUNC(O.ORDER_DATE) IN (TRUNC(SYSDATE), TRUNC(SYSDATE) - 1)
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto.setTodayOrderCount(rs.getInt("TODAY_ORDER_COUNT"));
                    dto.setTodaySales(rs.getLong("TODAY_SALES"));
                    dto.setYesterdayOrderCount(rs.getInt("YESTERDAY_ORDER_COUNT"));
                    dto.setYesterdaySales(rs.getLong("YESTERDAY_SALES"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
