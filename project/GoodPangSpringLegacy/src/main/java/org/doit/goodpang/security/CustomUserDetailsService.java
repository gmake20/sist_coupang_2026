package org.doit.goodpang.security;

import java.sql.SQLException;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.mapper.MemberMapper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        MemberVO member = null;
		try {
			member = memberMapper.read(username);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (member == null) {
            throw new UsernameNotFoundException(
                "존재하지 않는 회원입니다. : " + username
            );
        }
        
        if (member.getStatus() == 0) {
            throw new DisabledException(
                    "탈퇴한 회원입니다."
            );
        }


        return new CustomUser(member);
    }
}