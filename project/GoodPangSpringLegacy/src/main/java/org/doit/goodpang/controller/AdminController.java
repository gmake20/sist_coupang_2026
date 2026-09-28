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

}
