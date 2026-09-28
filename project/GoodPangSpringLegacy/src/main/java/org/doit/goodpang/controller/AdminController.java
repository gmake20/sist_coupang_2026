package org.doit.goodpang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

/*
 * 관리자 화면.
 *
 * 인증/권한은 Spring Security가 처리한다 (security-context.xml의 관리자 전용 <http>, SL14_SECURITY2_JDBC 방식).
 *  - 로그인 처리: 폼이 /admin/loginProcess 로 POST → jdbc-user-service가 ADMIN 테이블로 확인, ROLE_ADMIN 부여
 *                 → AdminLoginSuccessHandler가 세션에 loginAdmin/adminNo/adminName을 넣고 이동
 *  - 로그아웃:    /admin/logout.htm POST → Security LogoutFilter가 세션 무효화 후 /admin/login.htm?logout
 *  - 권한 확인:   /admin/** 는 hasRole('ADMIN') - 로그인 안 했으면 자동으로 로그인 화면으로 보내고,
 *                 로그인 후 원래 가려던 화면으로 돌려보낸다.
 * 그래서 기존 AdminLoginServlet/AdminLogoutServlet/AdminAuthFilter가 하던 일은 이 Controller에 없다.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	/*
	 * 관리자 로그인 화면 - 판매자 로그인처럼 레이아웃 없는 단독 화면 (tiles.xml admin.login).
	 * Security가 상황에 따라 파라미터를 붙여서 이 화면으로 보낸다:
	 *   ?error  로그인 실패 / ?logout  로그아웃 완료 / ?denied  관리자 권한이 없는 계정으로 접근
	 */
	@GetMapping(value = "/login.htm")
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "denied", required = false) String denied) {

		ModelAndView mav = new ModelAndView("admin.login");

		if (error != null) {
			mav.addObject("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
		} else if (denied != null) {
			mav.addObject("error", "관리자 권한이 없는 계정입니다. 관리자 계정으로 로그인해주세요.");
		} else if (logout != null) {
			mav.addObject("message", "로그아웃되었습니다.");
		}

		return mav;
	}

	// 관리자 대시보드 - 각 관리 메뉴로 가는 카드 목록 (기존 AdminDashboardServlet). 권한 확인은 Security가 함
	@GetMapping(value = "/dashboard.htm")
	public ModelAndView dashboard() {
		return new ModelAndView("admin.dashboard");
	}

}
