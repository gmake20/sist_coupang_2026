package org.doit.goodpang.mapper;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.MemberVO;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMapper {

	public MemberVO read(String userid) throws ClassNotFoundException, SQLException;
	
}
