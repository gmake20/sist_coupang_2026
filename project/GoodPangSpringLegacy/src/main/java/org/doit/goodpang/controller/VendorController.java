package org.doit.goodpang.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.SellerDTO;
import org.doit.goodpang.mapper.VendorMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
	// private final MemberShipService memberShipService;

	// [2]
	@GetMapping(value = "/login.htm")
	public ModelAndView noticeDetail(@RequestParam(value = "seq", defaultValue = "1") String seq)
			throws ClassNotFoundException, SQLException {
		System.out.println("VendorController.login()...");

		// [2]
		// NoticeVO noticeVO = this.noticeDao.getNotice(seq);
		ModelAndView mav = new ModelAndView("vendor.login");
		// mav.addObject("noticeVO", noticeVO);

		return mav;
	}

	@PostMapping(value = "/login.htm")
	public ModelAndView login(
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "password", defaultValue = "") String password,
			HttpSession session

	) throws ClassNotFoundException, SQLException {
		System.out.println("VendorController.login()...");

		SellerDTO seller = vendorMapper.findByEmail(email);

		if (seller == null || !BCrypt.checkpw(password, seller.getSellerPw())) {

			String error = URLEncoder.encode("아이디 또는 비밀번호가 올바르지 않습니다.", StandardCharsets.UTF_8);
			return new ModelAndView("redirect:/vendor/login.htm?error=" + error);
		}

		if ("정지".equals(seller.getApprovalStatus())) {

			String reason = seller.getRejectReason();
			String error = URLEncoder.encode("정지된 계정입니다. 문의사항은 고객센터로 연락 주시기 바랍니다."
					+ (reason != null && !reason.isBlank() ? " (사유: " + reason + ")" : ""),
					StandardCharsets.UTF_8);
			return new ModelAndView("redirect:/vendor/login.htm?error=" + error);
		}

		if ("탈퇴".equals(seller.getApprovalStatus())) {

			String error = URLEncoder.encode("탈퇴한 계정입니다.", StandardCharsets.UTF_8);
			return new ModelAndView("redirect:/vendor/login.htm?error=" + error);

		}

		session.setAttribute("loginSeller", seller);
		session.setAttribute("sellerNo", seller.getSellerNo());
		session.setAttribute("storeName", seller.getStoreName());

		session.setMaxInactiveInterval(30 * 60);

		String redirectUrl = (String) session.getAttribute("vendorRedirectAfterLogin");

		session.removeAttribute("vendorRedirectAfterLogin");

		if (redirectUrl != null && !redirectUrl.isBlank()) {
			return new ModelAndView("redirect:" + redirectUrl);
		}
		return new ModelAndView("redirect:/vendor/dashboard.htm");
	}

	@GetMapping(value = "/dashboard.htm")
	public ModelAndView dashboard() {
		return new ModelAndView("vendor.dashboard");
	}

}
