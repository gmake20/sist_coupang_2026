package org.doit.goodpang.mapper;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.MemberVO;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMapper {

	public MemberVO read(String userid) throws ClassNotFoundException, SQLException;
	
	int insert(MemberVO memberVO);
	
    MemberVO getMember(
            @Param("memberNo") Long memberNo
    );
	
    int updateEmail(
            @Param("memberNo") Long memberNo,
            @Param("newEmail") String newEmail
    );

    int updatePhone(
            @Param("memberNo") Long memberNo,
            @Param("newPhone") String newPhone
    );
    
    int updatePassword(
            @Param("memberNo") Long memberNo,
            @Param("encodedPassword") String encodedPassword
    );
}
