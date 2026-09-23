package org.doit.goodpang.mapper;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.SellerDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorMapper {
	
	public SellerDTO findByEmail(@Param("email") String email) throws ClassNotFoundException, SQLException;;
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
