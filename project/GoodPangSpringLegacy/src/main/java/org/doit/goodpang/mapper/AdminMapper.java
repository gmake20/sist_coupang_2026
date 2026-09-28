package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.AdminDTO;
import org.springframework.stereotype.Repository;

/*
 * 관리자(ADMIN) 계정 - 기존 GoodPang AdminDAO.
 */
@Repository
public interface AdminMapper {

	// 관리자 아이디로 1건 조회 (로그인용, 비밀번호 해시 포함). 없으면 null
	public AdminDTO findByAdminId(@Param("adminId") String adminId);

}
