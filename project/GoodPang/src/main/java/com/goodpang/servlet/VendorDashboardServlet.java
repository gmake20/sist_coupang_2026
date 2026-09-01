package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	private static final String[] DAY_NAMES = { "일", "월", "화", "수", "목", "금", "토" };

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		LocalDate today = LocalDate.now();
		LocalDate selectedDate = parseSelectedDate(request.getParameter("date"), today);

		VendorDashboardDAO dao = new VendorDashboardDAO();
		VendorDashboardStatDTO dashboardStat = dao.getTodayStat(loginSeller.getSellerNo(), java.sql.Date.valueOf(selectedDate));
		request.setAttribute("dashboardStat", dashboardStat);

		request.setAttribute("selectedDate", selectedDate.toString());
		request.setAttribute("selectedDateLabel", formatDateLabel(selectedDate));
		request.setAttribute("dateOptions", buildDateOptions(today));

		// 주문/배송 현황 패널(배송중·배송완료) - vendor-order.jsp 상단 카드와 같은 집계를 재사용
		VendorOrderListDAO orderListDAO = new VendorOrderListDAO();
		VendorOrderStatSummaryDTO orderStat = orderListDAO.countStats(loginSeller.getSellerNo());
		request.setAttribute("orderStat", orderStat);

		// 매출 현황 차트(일간/주간/월간) - 실데이터. JS의 salesData.daily/weekly/monthly 자리를 이 JSON으로 채운다.
		List<VendorDailySalesDTO> dailySales = dao.getDailySalesStat(loginSeller.getSellerNo());
		request.setAttribute("dailySalesJson", gson.toJson(dailySales));

		List<VendorDailySalesDTO> weeklySales = dao.getWeeklySalesStat(loginSeller.getSellerNo());
		request.setAttribute("weeklySalesJson", gson.toJson(weeklySales));

		List<VendorDailySalesDTO> monthlySales = dao.getMonthlySalesStat(loginSeller.getSellerNo());
		request.setAttribute("monthlySalesJson", gson.toJson(monthlySales));

		request.getRequestDispatcher("/WEB-INF/views/vendor-dashboard.jsp").forward(request, response);
	}

	// ?date=yyyy-MM-dd 파라미터를 파싱. 형식이 잘못됐거나 미래 날짜면 오늘로 대체한다.
	private LocalDate parseSelectedDate(String dateParam, LocalDate today) {

		if (dateParam == null || dateParam.isBlank()) {
			return today;
		}

		try {
			LocalDate parsed = LocalDate.parse(dateParam.trim());
			return parsed.isAfter(today) ? today : parsed;
		} catch (DateTimeParseException e) {
			return today;
		}
	}

	// 날짜 선택 드롭다운에 보여줄 최근 3일(오늘/어제/그제) 옵션
	private List<Map<String, String>> buildDateOptions(LocalDate today) {

		List<Map<String, String>> options = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			LocalDate date = today.minusDays(i);

			Map<String, String> option = new LinkedHashMap<>();
			option.put("date", date.toString());
			option.put("label", formatDateLabel(date));

			options.add(option);
		}

		return options;
	}

	private String formatDateLabel(LocalDate date) {
		String dayName = DAY_NAMES[date.getDayOfWeek().getValue() % 7];
		return String.format("%04d.%02d.%02d (%s)", date.getYear(), date.getMonthValue(), date.getDayOfMonth(), dayName);
	}

}
