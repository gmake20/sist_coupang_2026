package org.doit.goodpang.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.AdminDTO;
import org.doit.goodpang.mapper.AdminMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/*
 * 관리자 로그인 성공 처리 (security-context.xml의 관리자 전용 <http> form-login에서 사용).
 *
 * 인증 자체(아이디/비밀번호/ROLE_ADMIN)는 Spring Security의 jdbc-user-service가 ADMIN 테이블로 처리하고,
 * 여기서는 기존 AdminLoginServlet이 세션에 넣던 값(loginAdmin/adminNo/adminName)을 그대로 채워준다 -
 * 관리자 화면(JSP의 sessionScope.adminName, 공지 등록 시 adminNo 등)이 이 값을 계속 쓰기 때문.
 *
 * 이동할 곳은 부모 클래스(SavedRequestAwareAuthenticationSuccessHandler)가 정한다:
 * 로그인 전에 가려던 관리자 화면이 있으면 그곳으로, 없으면 defaultTargetUrl(/admin/dashboard.htm)로.
 * (기존 AdminAuthFilter + adminRedirectAfterLogin 세션 값이 하던 일)
 */
public class AdminLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final AdminMapper adminMapper;

	public AdminLoginSuccessHandler(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
		setDefaultTargetUrl("/admin/dashboard.htm");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		AdminDTO admin = adminMapper.findByAdminId(authentication.getName());

		if (admin != null) {
			admin.setAdminPw(null); // 세션에는 비밀번호 해시를 남기지 않음

			HttpSession session = request.getSession();
			session.setAttribute("loginAdmin", admin);
			session.setAttribute("adminNo", admin.getAdminNo());
			session.setAttribute("adminName", admin.getAdminName());
			session.setMaxInactiveInterval(30 * 60);
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
