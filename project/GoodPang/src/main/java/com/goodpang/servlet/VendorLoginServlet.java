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

		// 입점심사 상태(입점 대기/심사 중/승인/반려)와 무관하게 로그인은 허용하고,
		// 상태별 안내는 대시보드(vendor_dashboard.jsp)에서 분기 처리한다.
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
