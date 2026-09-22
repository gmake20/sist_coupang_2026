package org.doit.goodpang.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request, 
			HttpServletResponse response,
			Authentication authentication // 인증받은 사용자 정보
			) throws IOException, ServletException {

		log.warn("😘😘😘 Login Success...");
		// 인증사용자가 가지고 있는 롤(Role) == 권한

		List<String> roleNames = new ArrayList<String>();         
		authentication.getAuthorities().forEach( auth -> {
			roleNames.add(auth.getAuthority());
		} );

		log.warn("👍 > ROLE NAMES : " + roleNames );

		if ( roleNames.contains("ROLE_ADMIN") ) {
			response.sendRedirect("/");
			return;
		} else if ( roleNames.contains("ROLE_MANAGER") ) {
			response.sendRedirect("/customer/notice.htm");
			return;
		} else if ( roleNames.contains("ROLE_USER") ) {
			response.sendRedirect("/customer/notice.htm");
			return;
		}

	}

}
