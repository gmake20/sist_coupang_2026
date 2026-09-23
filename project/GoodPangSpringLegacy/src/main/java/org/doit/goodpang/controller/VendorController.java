package org.doit.goodpang.controller;

import java.sql.SQLException;

import org.doit.goodpang.domain.SellerDTO;
import org.doit.goodpang.mapper.VendorMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {
	private final VendorMapper vendorMapper;
	//private final MemberShipService memberShipService;

	
	// [2] 
	@GetMapping(value = "/login.htm")
	public ModelAndView noticeDetail(@RequestParam(value="seq",defaultValue = "1") String seq) throws ClassNotFoundException, SQLException  {
		System.out.println("VendorController.login()...");

		
		// [2]
		// NoticeVO noticeVO = this.noticeDao.getNotice(seq);
		ModelAndView mav = new ModelAndView("vendor.login");
		// mav.addObject("noticeVO", noticeVO);

		return mav;
	}
	
	@PostMapping(value = "/login.htm")
	public ModelAndView login(
			@RequestParam(value="email",defaultValue = "") String email
			,@RequestParam(value="password",defaultValue = "") String password
			
			) throws ClassNotFoundException, SQLException  {
		System.out.println("VendorController.login()...");

		SellerDTO seller = vendorMapper.findByEmail(email);
		
		/*
		request.setCharacterEncoding("UTF-8");

		String email = request.getParameter("email");
		String password = request.getParameter("password");

		SellerDAO dao = new SellerDAO();

		SellerDTO seller = dao.findByEmail(email);

		// 이메일 없음 또는 비밀번호 불일치
		if (seller == null || !BCrypt.checkpw(password, seller.getSellerPw())) {

			request.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-login.jsp")
				   .forward(request, response);
			return;
		}

		// '정지'/'탈퇴' 상태만 로그인 자체를 차단한다. 그 외 입점심사 상태(입점 대기/심사 중/승인/반려)는
		// 로그인을 허용하고, 상태별 안내는 대시보드(vendor-dashboard.jsp)에서 분기 처리한다.
		if ("정지".equals(seller.getApprovalStatus())) {

			String reason = seller.getRejectReason();
			request.setAttribute("error",
					"정지된 계정입니다. 문의사항은 고객센터로 연락 주시기 바랍니다."
					+ (reason != null && !reason.isBlank() ? " (사유: " + reason + ")" : ""));
			request.getRequestDispatcher("/WEB-INF/views/vendor-login.jsp")
				   .forward(request, response);
			return;
		}

		if ("탈퇴".equals(seller.getApprovalStatus())) {

			request.setAttribute("error", "탈퇴한 계정입니다.");
			request.getRequestDispatcher("/WEB-INF/views/vendor-login.jsp")
				   .forward(request, response);
			return;
		}

		HttpSession session = request.getSession();

		session.setAttribute("loginSeller", seller);
		session.setAttribute("sellerNo", seller.getSellerNo());
		session.setAttribute("storeName", seller.getStoreName());

		session.setMaxInactiveInterval(30 * 60);

		String redirectUrl = (String) session.getAttribute("vendorRedirectAfterLogin");

		session.removeAttribute("vendorRedirectAfterLogin");

		if (redirectUrl != null && !redirectUrl.isBlank()) {
			response.sendRedirect(redirectUrl);
		} else {
			response.sendRedirect(request.getContextPath() + "/vendor/dashboard");
		}
		
		*/
		
		// [2]
		// NoticeVO noticeVO = this.noticeDao.getNotice(seq);
		ModelAndView mav = new ModelAndView("vendor.dashboard");
		// mav.addObject("noticeVO", noticeVO);

		return mav;
	}	

}
