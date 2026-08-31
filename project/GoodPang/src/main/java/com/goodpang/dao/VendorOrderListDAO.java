package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorOrderListDTO;
import com.goodpang.dto.VendorOrderStatSummaryDTO;
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

    /*
     * 주문/배송 관리 화면 상단 통계 카드 + 우측 배송현황 도넛용 집계.
     * ORDER_STATUS로 실제 존재하는 상태(결제완료/배송중/배송완료)만 센다 - 신규주문/결제대기/
     * 상품준비중/취소반품교환에 대응하는 상태값은 아직 시스템에 없어서 집계 대상에서 제외.
     * DELIVERY_END_DATE 기준으로 "오늘" 배송완료된 건수를 판단한다(AdminDeliveryDAO가
     * 배송완료 처리할 때 그 컬럼에 SYSDATE를 넣음).
     */
    public VendorOrderStatSummaryDTO countStats(int sellerNo) {

        VendorOrderStatSummaryDTO dto = new VendorOrderStatSummaryDTO();

        String sql = """
            SELECT
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '결제완료' THEN O.ORDER_NO END) AS WAITING_COUNT,
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송중' THEN O.ORDER_NO END) AS SHIPPING_COUNT,
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송완료' THEN O.ORDER_NO END) AS DELIVERED_COUNT,
                COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송완료'
                                      AND D.DELIVERY_END_DATE IS NOT NULL
                                      AND TRUNC(D.DELIVERY_END_DATE) = TRUNC(SYSDATE)
                                 THEN O.ORDER_NO END) AS DELIVERED_TODAY_COUNT
            FROM ORDERS O
                JOIN ORDER_DETAIL OD ON OD.ORDER_NO = O.ORDER_NO
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                LEFT JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
            WHERE P.SELLER_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    dto.setWaitingCount(rs.getInt("WAITING_COUNT"));
                    dto.setShippingCount(rs.getInt("SHIPPING_COUNT"));
                    dto.setDeliveredCount(rs.getInt("DELIVERED_COUNT"));
                    dto.setDeliveredTodayCount(rs.getInt("DELIVERED_TODAY_COUNT"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    /*
     * 결제완료 -> 배송중 전환 + DELIVERY 행 생성(송장번호 저장), 하나의 트랜잭션으로 처리.
     * ORDERS에는 seller_no가 없어서, 이 주문에 이 판매자의 상품이 실제로 포함돼 있는지
     * ORDER_DETAIL/PRODUCT로 확인한 뒤에만 처리한다.
     */
    public boolean shipOrder(int orderNo, int sellerNo, String invoiceNo) {

        try (Connection conn = ConnectionProvider.getConnection()) {

            conn.setAutoCommit(false);

            try {
                String deliveryServiceCode = findDeliveryServiceCode(conn, orderNo, sellerNo);

                if (deliveryServiceCode == null) {
                    conn.rollback();
                    return false;
                }

                insertDelivery(conn, orderNo, deliveryServiceCode, invoiceNo);

                boolean updated = updateStatusToShipping(conn, orderNo, sellerNo);

                if (!updated) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 이 판매자가 이 주문에 등록한 상품 중 하나의 택배사 코드를 대표로 사용
    private String findDeliveryServiceCode(Connection conn, int orderNo, int sellerNo) throws Exception {

        String sql = """
            SELECT P.DELIVERY_SERVICE_CODE
            FROM ORDER_DETAIL OD
                JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
            WHERE OD.ORDER_NO = ?
              AND P.SELLER_NO = ?
              AND ROWNUM = 1
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, sellerNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("DELIVERY_SERVICE_CODE") : null;
            }
        }
    }

    private void insertDelivery(Connection conn, int orderNo, String deliveryServiceCode, String invoiceNo) throws Exception {

        String sql = """
            INSERT INTO DELIVERY (
                DELIVERY_NO, ORDER_NO, DELIVERY_SERVICE_CODE, INVOICE_NO,
                DELIVERY_STATUS, DELIVERY_START_DATE, DELIVERY_END_DATE,
                CREATED_DATE, UPDATED_DATE
            ) VALUES (
                SEQ_DELIVERY.NEXTVAL, ?, ?, ?,
                '배송중', SYSDATE, NULL,
                SYSDATE, SYSDATE
            )
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.setString(2, deliveryServiceCode);
            pstmt.setString(3, invoiceNo);

            pstmt.executeUpdate();
        }
    }

    private boolean updateStatusToShipping(Connection conn, int orderNo, int sellerNo) throws Exception {

        String sql = """
            UPDATE ORDERS
            SET ORDER_STATUS = '배송중'
            WHERE ORDER_NO = ?
              AND ORDER_STATUS = '결제완료'
              AND EXISTS (
                    SELECT 1
                    FROM ORDER_DETAIL OD
                        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
                    WHERE OD.ORDER_NO = ORDERS.ORDER_NO
                      AND P.SELLER_NO = ?
              )
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderNo);
            pstmt.setInt(2, sellerNo);

            return pstmt.executeUpdate() == 1;
        }
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
