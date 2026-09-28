package org.doit.goodpang.mapper;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.SellerDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMapper {
	
	public SellerDTO findByEmail(@Param("email") String email) throws ClassNotFoundException, SQLException;;

	// 판매자 정보관리 제출 - 사업장 주소/통신판매업번호/대표카테고리/정산계좌/서류 경로. 입점 대기·반려면 '심사 중'으로 전환
	public int updateBusinessInfo(SellerDTO dto);
	/*
	 * public MemberVO getMember(@Param("id") String id) throws
	 * ClassNotFoundException, SQLException;
	 * 
	 * public int insert(MemberVO member) throws ClassNotFoundException,
	 * SQLException;
	 * 
	 * // 회원 ID(username)를 매개변수로 회원 정보를 반환하는 메서드 / UserDetailsService implement한 구현체
	 * 안에 메서드 public MemberVO read(@Param("userid") String userid) throws
	 * ClassNotFoundException, SQLException;
	 */	
}
