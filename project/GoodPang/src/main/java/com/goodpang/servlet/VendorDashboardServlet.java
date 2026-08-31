package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.VendorDashboardDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorDashboardStatDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 대시보드(vendor_dashboard.jsp) 화면 진입.
 */
@WebServlet("/vendor/dashboard")
public class VendorDashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		VendorDashboardDAO dao = new VendorDashboardDAO();
		VendorDashboardStatDTO dashboardStat = dao.getTodayStat(loginSeller.getSellerNo());
		request.setAttribute("dashboardStat", dashboardStat);

		request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response);
	}

}
