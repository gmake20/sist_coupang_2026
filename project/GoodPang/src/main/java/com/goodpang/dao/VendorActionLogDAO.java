package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.goodpang.dto.VendorActionLogDTO;
import com.goodpang.util.ConnectionProvider;

/*
 * 판매자가 판매자센터에서 수행한 액션(상품 등록/노출전환/판매중지/옵션수정/배송처리 등)을
 * VENDOR_ACTION_LOG에 남긴다. target_no는 target_type(PRODUCT/PRODUCT_OPTION/ORDERS 등)에
 * 따라 다른 테이블의 PK를 가리키는 폴리모픽 연관이라 FK가 없다 - docs/vendor_action_log_table_create.sql 참고.
 */
public class VendorActionLogDAO {

    public void log(int sellerNo, String actionType, String targetType, int targetNo, String detail) {

        String sql = """
            INSERT INTO VENDOR_ACTION_LOG (
                ACTION_LOG_NO, SELLER_NO, ACTION_TYPE, TARGET_TYPE, TARGET_NO, DETAIL, ACTION_DATE
            ) VALUES (
                SEQ_VENDOR_ACTION_LOG.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE
            )
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, sellerNo);
            pstmt.setString(2, actionType);
            pstmt.setString(3, targetType);
            pstmt.setInt(4, targetNo);
            pstmt.setString(5, detail);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 전체 판매자의 작업 로그 - 최신순. page는 1부터. 조회는 관리자 전용(판매자센터에는 노출 안 함)
    public List<VendorActionLogDTO> findAll(int page, int pageSize) {

        List<VendorActionLogDTO> list = new ArrayList<>();

        String sql = """
            SELECT L.ACTION_LOG_NO, L.SELLER_NO, S.STORE_NAME,
                   L.ACTION_TYPE, L.TARGET_TYPE, L.TARGET_NO, L.DETAIL, L.ACTION_DATE
            FROM VENDOR_ACTION_LOG L
                JOIN SELLER S ON L.SELLER_NO = S.SELLER_NO
            ORDER BY L.ACTION_LOG_NO DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, (page - 1) * pageSize);
            pstmt.setInt(2, pageSize);

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

    // 페이지네이션용 총 개수
    public int countAll() {

        String sql = "SELECT COUNT(*) FROM VENDOR_ACTION_LOG";

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private VendorActionLogDTO mapRow(ResultSet rs) throws java.sql.SQLException {

        VendorActionLogDTO dto = new VendorActionLogDTO();
        dto.setActionLogNo(rs.getInt("ACTION_LOG_NO"));
        dto.setSellerNo(rs.getInt("SELLER_NO"));
        dto.setStoreName(rs.getString("STORE_NAME"));
        dto.setActionType(rs.getString("ACTION_TYPE"));
        dto.setTargetType(rs.getString("TARGET_TYPE"));
        dto.setTargetNo(rs.getInt("TARGET_NO"));
        dto.setDetail(rs.getString("DETAIL"));
        dto.setActionDate(rs.getTimestamp("ACTION_DATE"));
        return dto;
    }
}
