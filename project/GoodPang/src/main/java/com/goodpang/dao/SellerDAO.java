package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.SellerDTO;
import com.goodpang.util.ConnectionProvider;

public class SellerDAO {

    // 판매자 회원가입 (vendor-signup.jsp 1단계 항목만 채워서 INSERT, 승인상태는 '입점 대기'로 시작)
    public int insertSeller(SellerDTO dto) {

        String sql = """
            INSERT INTO SELLER (
                SELLER_NO,
                EMAIL,
                SELLER_PW,
                MANAGER_NAME,
                PHONE,
                BUSINESS_NO,
                BUSINESS_TYPE,
                CEO_NAME,
                STORE_NAME,
                APPROVAL_STATUS,
                CREATED_DATE,
                UPDATED_DATE
            )
            VALUES (
                SEQ_SELLER.NEXTVAL,
                ?, ?, ?, ?,
                ?, ?, ?, ?,
                '입점 대기',
                SYSDATE, SYSDATE
            )
            """;

        int rowCount = 0;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, dto.getEmail());
            pstmt.setString(2, dto.getSellerPw());
            pstmt.setString(3, dto.getManagerName());
            pstmt.setString(4, dto.getPhone());
            pstmt.setString(5, dto.getBusinessNo());
            pstmt.setString(6, dto.getBusinessType());
            pstmt.setString(7, dto.getCeoName());
            pstmt.setString(8, dto.getStoreName());

            rowCount = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowCount;
    }

    public boolean existsByEmail(String email) {

        String sql = """
            SELECT COUNT(*)
            FROM SELLER
            WHERE EMAIL = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByBusinessNo(String businessNo) {

        String sql = """
            SELECT COUNT(*)
            FROM SELLER
            WHERE BUSINESS_NO = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, businessNo);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public SellerDTO findByEmail(String email) {

        SellerDTO dto = null;

        String sql = """
            SELECT
                SELLER_NO, EMAIL, SELLER_PW, MANAGER_NAME, PHONE,
                BUSINESS_NO, BUSINESS_TYPE, CEO_NAME, STORE_NAME,
                APPROVAL_STATUS, REJECT_REASON
            FROM SELLER
            WHERE EMAIL = ?
            """;

        try (
            Connection conn = ConnectionProvider.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    dto = new SellerDTO();

                    dto.setSellerNo(rs.getInt("SELLER_NO"));
                    dto.setEmail(rs.getString("EMAIL"));
                    dto.setSellerPw(rs.getString("SELLER_PW"));
                    dto.setManagerName(rs.getString("MANAGER_NAME"));
                    dto.setPhone(rs.getString("PHONE"));
                    dto.setBusinessNo(rs.getString("BUSINESS_NO"));
                    dto.setBusinessType(rs.getString("BUSINESS_TYPE"));
                    dto.setCeoName(rs.getString("CEO_NAME"));
                    dto.setStoreName(rs.getString("STORE_NAME"));
                    dto.setApprovalStatus(rs.getString("APPROVAL_STATUS"));
                    dto.setRejectReason(rs.getString("REJECT_REASON"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
