package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorDashboardDAO;
import com.goodpang.dao.VendorOrderListDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorDailySalesDTO;
import com.goodpang.dto.VendorDashboardStatDTO;
import com.goodpang.dto.VendorOrderStatSummaryDTO;
import com.google.gson.Gson;

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

	private static final Gson gson = new Gson();

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

		// 주문/배송 현황 패널(배송중·배송완료) - vendor-order.jsp 상단 카드와 같은 집계를 재사용
		VendorOrderListDAO orderListDAO = new VendorOrderListDAO();
		VendorOrderStatSummaryDTO orderStat = orderListDAO.countStats(loginSeller.getSellerNo());
		request.setAttribute("orderStat", orderStat);

		// 매출 현황 차트(일간) - 최근 7일 실데이터. JS의 salesData.daily 자리를 이 JSON으로 채운다.
		List<VendorDailySalesDTO> dailySales = dao.getDailySalesStat(loginSeller.getSellerNo());
		request.setAttribute("dailySalesJson", gson.toJson(dailySales));

		request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response);
	}

}
