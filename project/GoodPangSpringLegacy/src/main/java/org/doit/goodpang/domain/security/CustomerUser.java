package org.doit.goodpang.domain.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.doit.goodpang.domain.MemberVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CustomerUser extends User {
	
	// 필드 선언 (인증된 사용자 정보.. )
	private MemberVO member_x;

	public CustomerUser(
			String username
			, String password
			, boolean enabled
			, boolean accountNonExpired
			, boolean credentialsNonExpired
			, boolean accountNonLocked
			, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		
		
	}

	public CustomerUser(MemberVO memberVO) {
		super(
				memberVO.getId(), memberVO.getPwd()
	            // List<AuthVO>     -> Collection<? extends GrantedAuthority>  
	            , memberVO.getAuthList()
	                       .stream()
	                       .map(
	                       auth->new SimpleGrantedAuthority(auth.getAuthority())
	                       )
	                        .collect(Collectors.toList())
	            );
		
	this.member_x = memberVO;	
	}
	
	
}




