package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.SellerDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/vendor/login")
public class VendorLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public VendorLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/vendor-login.jsp")
		   .forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

		// 입점심사 상태 확인 - 승인된 판매자만 로그인 허용
		String approvalStatus = seller.getApprovalStatus();

		if (!"승인".equals(approvalStatus)) {

			String message;

			switch (approvalStatus) {
				case "입점 대기":
					message = "아직 입점 절차가 완료되지 않았습니다. 사업자 정보 및 서류 제출을 완료해주세요.";
					break;
				case "심사 중":
					message = "입점 심사가 진행 중입니다. 승인 완료 후 로그인하실 수 있습니다.";
					break;
				case "반려":
					message = "입점 신청이 반려되었습니다."
							+ (seller.getRejectReason() != null ? " 사유: " + seller.getRejectReason() : "");
					break;
				default:
					message = "현재 로그인할 수 없는 상태입니다.";
			}

			request.setAttribute("error", message);
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
			response.sendRedirect(request.getContextPath() + "/vendor_dashboard.jsp");
		}
	}

}
