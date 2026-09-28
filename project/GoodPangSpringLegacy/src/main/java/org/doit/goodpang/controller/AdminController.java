package org.doit.goodpang.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.AdminDTO;
import org.doit.goodpang.mapper.AdminMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminMapper adminMapper;

	// 관리자 로그인 화면 - 판매자 로그인처럼 레이아웃 없는 단독 화면 (tiles.xml admin.login)
	@GetMapping(value = "/login.htm")
	public ModelAndView login() {
		return new ModelAndView("admin.login");
	}

	/*
	 * 관리자 로그인 처리 (기존 AdminLoginServlet.doPost).
	 * 아이디가 없거나 비밀번호가 틀리면 같은 화면에 error를 보여주고,
	 * 성공하면 세션에 loginAdmin/adminNo/adminName을 넣고 대시보드(또는 로그인 전에 가려던 화면)로 보낸다.
	 */
	@PostMapping(value = "/login.htm")
	public ModelAndView login(
			@RequestParam(value = "adminId", required = false) String adminId,
			@RequestParam(value = "password", required = false) String password,
			HttpSession session) {

		AdminDTO admin = (adminId == null || adminId.isBlank()) ? null : adminMapper.findByAdminId(adminId);

		// 아이디 없음 또는 비밀번호 불일치
		if (admin == null || password == null || !BCrypt.checkpw(password, admin.getAdminPw())) {
			ModelAndView mav = new ModelAndView("admin.login");
			mav.addObject("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
			return mav;
		}

		session.setAttribute("loginAdmin", admin);
		session.setAttribute("adminNo", admin.getAdminNo());
		session.setAttribute("adminName", admin.getAdminName());

		session.setMaxInactiveInterval(30 * 60);

		String redirectUrl = (String) session.getAttribute("adminRedirectAfterLogin");

		session.removeAttribute("adminRedirectAfterLogin");

		if (redirectUrl != null && !redirectUrl.isBlank()) {
			return new ModelAndView("redirect:" + redirectUrl);
		}

		return new ModelAndView("redirect:/admin/dashboard.htm");
	}

	/*
	 * 관리자 로그아웃 - 세션을 통째로 무효화하고 관리자 로그인 화면으로 (기존 AdminLogoutServlet과 동일).
	 * 링크(GET)로 불러도, 기존 서블릿처럼 POST로 와도 같은 처리를 한다.
	 * getSession(false): 세션이 없으면 새로 만들지 않도록 HttpSession 대신 request에서 꺼낸다.
	 */
	@RequestMapping(value = "/logout.htm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView logout(HttpServletRequest request) {

		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		return new ModelAndView("redirect:/admin/login.htm");
	}

	// 관리자 대시보드 - 각 관리 메뉴로 가는 카드 목록 (기존 AdminDashboardServlet)
	@GetMapping(value = "/dashboard.htm")
	public ModelAndView dashboard(HttpServletRequest request) {

		ModelAndView loginRedirect = requireAdminLogin(request);
		if (loginRedirect != null) {
			return loginRedirect;
		}

		return new ModelAndView("admin.dashboard");
	}

	/*
	 * 관리자 로그인 확인 (기존 AdminAuthFilter가 /admin/* 전체에 하던 일).
	 * 로그인돼 있으면 null, 아니면 지금 가려던 주소를 세션(adminRedirectAfterLogin)에 기억해 두고
	 * 로그인 화면으로 보내는 ModelAndView를 돌려준다 - 로그인 성공 후 그 주소로 돌아온다(login POST 참고).
	 * 관리자 화면 메서드마다 맨 앞에서 호출한다.
	 */
	private ModelAndView requireAdminLogin(HttpServletRequest request) {

		HttpSession session = request.getSession(false);

		if (session != null && session.getAttribute("loginAdmin") != null) {
			return null;
		}

		// redirect: 뒤에는 contextPath가 자동으로 붙으므로, 기억할 주소에서는 contextPath를 뺀다
		String redirectUrl = request.getRequestURI().substring(request.getContextPath().length());
		String queryString = request.getQueryString();

		if (queryString != null && !queryString.isBlank()) {
			redirectUrl += "?" + queryString;
		}

		request.getSession().setAttribute("adminRedirectAfterLogin", redirectUrl);

		return new ModelAndView("redirect:/admin/login.htm");
	}

}
