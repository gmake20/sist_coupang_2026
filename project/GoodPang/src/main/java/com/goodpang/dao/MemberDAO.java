package com.goodpang.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.goodpang.dto.MemberDTO;
import com.goodpang.util.ConnectionProvider;

public class MemberDAO {

    // 회원가입
	public int insertMember(MemberDTO dto) {

	    String sql = """
	        INSERT INTO MEMBER (
	            MEMBER_NO,
	            MEMBER_ID,
	            MEMBER_PW,
	            MEMBER_NAME,
	            PHONE,
	            EMAIL,
	            RANK
	        )
	        VALUES (
	            SEQ_MEMBER_NO.NEXTVAL,
	            ?,
	            ?,
	            ?,
	            ?,
	            ?,
	            ?
	        )
	        """;

	    int rowCount = 0;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, dto.getMemberId());
	        pstmt.setString(2, dto.getMemberPw());
	        pstmt.setString(3, dto.getMemberName());
	        pstmt.setString(4, dto.getPhone());
	        pstmt.setString(5, dto.getEmail());
	        pstmt.setString(6, dto.getRank());

	        rowCount = pstmt.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return rowCount;
	}
	
	public boolean existsByEmail(String email) {

	    String sql = """
	        SELECT COUNT(*)
	        FROM MEMBER
	        WHERE EMAIL = ?
	        """;

	    try (
	        Connection conn =
	            ConnectionProvider.getConnection();

	        PreparedStatement pstmt =
	            conn.prepareStatement(sql);
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
	
	public MemberDTO login(String memberId) {

	    String sql = """
	        SELECT
	            MEMBER_NO,
	            MEMBER_ID,
	            MEMBER_PW,
	            MEMBER_NAME,
	            PHONE,
	            EMAIL,
	            RANK
	        FROM MEMBER
	        WHERE MEMBER_ID = ?
	        """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, memberId);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                MemberDTO dto = new MemberDTO();

	                dto.setMemberNo(
	                    rs.getInt("MEMBER_NO")
	                );

	                dto.setMemberId(
	                    rs.getString("MEMBER_ID")
	                );

	                dto.setMemberPw(
	                    rs.getString("MEMBER_PW")
	                );

	                dto.setMemberName(
	                    rs.getString("MEMBER_NAME")
	                );

	                dto.setPhone(
	                    rs.getString("PHONE")
	                );

	                dto.setEmail(
	                    rs.getString("EMAIL")
	                );

	                dto.setRank(
	                    rs.getString("RANK")
	                );

	                return dto;
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	public boolean existsByMemberId(String memberId) {

	    String sql = """
	        SELECT COUNT(*)
	        FROM MEMBER
	        WHERE MEMBER_ID = ?
	        """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	    ) {

	        pstmt.setString(1, memberId);

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
	
	public MemberDTO findByEmail(String email) {

	    MemberDTO dto = null;

	    String sql = """
	            SELECT
	                MEMBER_NO,
	                MEMBER_ID,
	                MEMBER_PW,
	                MEMBER_NAME,
	                PHONE,
	                EMAIL,
	                RANK
	            FROM MEMBER
	            WHERE EMAIL = ?
	            """;

	    try (
	        Connection conn = ConnectionProvider.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {

	        pstmt.setString(1, email);

	        try (ResultSet rs = pstmt.executeQuery()) {

	            if (rs.next()) {

	                dto = new MemberDTO();

	                dto.setMemberNo(
	                    rs.getInt("MEMBER_NO")
	                );

	                dto.setMemberId(
	                    rs.getString("MEMBER_ID")
	                );

	                dto.setMemberPw(
	                    rs.getString("MEMBER_PW")
	                );

	                dto.setMemberName(
	                    rs.getString("MEMBER_NAME")
	                );

	                dto.setPhone(
	                    rs.getString("PHONE")
	                );

	                dto.setEmail(
	                    rs.getString("EMAIL")
	                );

	                dto.setRank(
	                    rs.getString("RANK")
	                );
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return dto;
	}
	
	
}