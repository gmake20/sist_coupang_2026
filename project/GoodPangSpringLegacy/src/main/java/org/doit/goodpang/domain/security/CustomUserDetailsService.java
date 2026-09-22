package org.doit.goodpang.domain.security;

import java.sql.SQLException;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.mapper.MemberMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.warn("😍😍 MemberMapper loadUserByUsername :" + username);
		
		MemberVO memberVO = null;
		try {
			memberVO = this.memberMapper.read(username);
		} catch (ClassNotFoundException  | SQLException e) {
		System.out.println("🚨🚨 MemberMapper loadUserByUsername");
			e.printStackTrace();
		} 
		
		return memberVO == null ? null : new CustomerUser(memberVO);
	}
	
	
}//class








