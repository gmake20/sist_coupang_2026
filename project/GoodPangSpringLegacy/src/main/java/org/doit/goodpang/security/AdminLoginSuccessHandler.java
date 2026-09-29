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
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/*
 * 관리자 로그인 성공 처리(security-context.xml의 관리자 전용 <http> form-login에서 사용).
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

	// 부모 클래스와 같은 저장소(세션의 SPRING_SECURITY_SAVED_REQUEST)를 읽고 지우기 위해 같은 종류로 하나 둔다
	private final RequestCache requestCache = new HttpSessionRequestCache();

	public AdminLoginSuccessHandler(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
		setDefaultTargetUrl("/admin/dashboard.htm");
		setRequestCache(requestCache);
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

		discardUnusableSavedRequest(request, response);

		super.onAuthenticationSuccess(request, response, authentication);
	}

	/*
	 * 로그인 전에 기억해 둔 주소(saved request)가 실제로 돌아갈 만한 관리자 화면이 아니면 버린다 → 대시보드로 이동.
	 *
	 * Security는 로그인 안 한 상태로 /admin/** 에 들어오면 그 주소를 무조건 기억해 둔다.
	 * 그래서 기존 GoodPang 주소(/admin/login, /admin/notices 처럼 .htm 없는 주소 - 즐겨찾기/브라우저 자동완성)로
	 * 한 번 들어왔다가 로그인하면, 로그인 성공 후 그 옛 주소로 보내져서 404가 났다.
	 * (한 번 쓰고 나면 지워지므로 두 번째 로그인부터는 정상 → "처음에만 404")
	 *
	 * 돌아가도 되는 주소: /admin/ 아래의 .htm 화면 중 로그인 화면이 아닌 것.
	 * 세션을 두 로그인이 같이 쓰기 때문에, 쇼핑몰 쪽에서 기억된 주소(/order/... 등)가 넘어오는 경우도 여기서 걸러진다.
	 */
	private void discardUnusableSavedRequest(HttpServletRequest request, HttpServletResponse response) {

		SavedRequest savedRequest = requestCache.getRequest(request, response);

		if (savedRequest == null) {
			return;
		}

		String path;
		try {
			path = new java.net.URI(savedRequest.getRedirectUrl()).getPath();
		} catch (java.net.URISyntaxException e) {
			path = null;
		}

		String adminPrefix = request.getContextPath() + "/admin/";
		boolean usable = path != null
				&& path.startsWith(adminPrefix)
				&& path.endsWith(".htm")
				&& !path.equals(adminPrefix + "login.htm");

		if (!usable) {
			requestCache.removeRequest(request, response);
		}
	}
}
