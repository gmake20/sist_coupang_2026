package org.doit.goodpang.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.domain.SellerDTO;
import org.doit.goodpang.domain.VendorDailySalesDTO;
import org.doit.goodpang.domain.VendorDailyTrafficDTO;
import org.doit.goodpang.domain.VendorDashboardStatDTO;
import org.doit.goodpang.domain.VendorOrderStatSummaryDTO;
import org.doit.goodpang.domain.VendorProductListDTO;
import org.doit.goodpang.mapper.VendorDashboardMapper;
import org.doit.goodpang.mapper.VendorMapper;
import org.doit.goodpang.mapper.VendorProductMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {
	private final VendorMapper vendorMapper;
	private final VendorDashboardMapper vendorDashboardMapper;
	private final VendorProductMapper vendorProductMapper;
	// private final MemberShipService memberShipService;

	// 차트 데이터를 JS에 넘길 JSON 변환용 (기존 서블릿의 Gson 대신 pom.xml에 이미 있는 Jackson 사용)
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final int PAGE_SIZE = 20; // 상품 목록 한 페이지 행 수
	private static final String[] DAY_NAMES = { "일", "월", "화", "수", "목", "금", "토" };

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
	public ModelAndView dashboard(
			@RequestParam(value = "date", required = false) String date,
			HttpSession session) throws JsonProcessingException {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		int sellerNo = loginSeller.getSellerNo();

		LocalDate today = LocalDate.now();
		LocalDate selectedDate = parseSelectedDate(date, today);

		java.sql.Date targetSqlDate = java.sql.Date.valueOf(selectedDate);

		ModelAndView mav = new ModelAndView("vendor.dashboard");
		mav.addObject("menu", "dashboard"); // 사이드바 활성 메뉴

		VendorDashboardStatDTO dashboardStat = vendorDashboardMapper.getTodayStat(sellerNo, targetSqlDate);
		mav.addObject("dashboardStat", dashboardStat);

		mav.addObject("selectedDate", selectedDate.toString());
		mav.addObject("selectedDateLabel", formatDateLabel(selectedDate));
		mav.addObject("dateOptions", buildDateOptions(today));

		// KPI 카드 라벨 - 오늘을 보고 있으면 "오늘"/"어제", 과거 날짜를 보고 있으면 그 날짜/전날을 "M/d"로 표시
		boolean isToday = selectedDate.equals(today);
		LocalDate compareDate = selectedDate.minusDays(1);
		mav.addObject("todayLabel", isToday ? "오늘" : formatShortDate(selectedDate));
		mav.addObject("compareLabel", (isToday ? "어제" : formatShortDate(compareDate)) + " 대비");

		// 주문/배송 현황 패널(배송중·배송완료) - vendor-order.jsp 상단 카드와 같은 집계
		VendorOrderStatSummaryDTO orderStat = vendorDashboardMapper.countOrderStats(sellerNo);
		mav.addObject("orderStat", orderStat);

		// 매출 현황 차트(일간/주간/월간) - 실데이터. JS의 salesData.daily/weekly/monthly 자리를 이 JSON으로
		// 채운다.
		// 선택된 날짜가 속한 구간까지 포함해서 최근 7일/5주/5개월을 보여준다.
		List<VendorDailySalesDTO> dailySales = vendorDashboardMapper.getDailySalesStat(sellerNo, targetSqlDate);
		mav.addObject("dailySalesJson", objectMapper.writeValueAsString(dailySales));

		List<VendorDailySalesDTO> weeklySales = vendorDashboardMapper.getWeeklySalesStat(sellerNo, targetSqlDate);
		mav.addObject("weeklySalesJson", objectMapper.writeValueAsString(weeklySales));

		List<VendorDailySalesDTO> monthlySales = vendorDashboardMapper.getMonthlySalesStat(sellerNo, targetSqlDate);
		mav.addObject("monthlySalesJson", objectMapper.writeValueAsString(monthlySales));

		// KPI 카드 스파크라인용 - 기준일 포함 최근 7일 방문자수/상품노출수 추이
		List<VendorDailyTrafficDTO> dailyTraffic = vendorDashboardMapper.getDailyTrafficStat(sellerNo, targetSqlDate);
		mav.addObject("dailyTrafficJson", objectMapper.writeValueAsString(dailyTraffic));

		// 공지사항 위젯 - 최신 5건만, "더보기"는 /vendor/notice 전체 목록으로 연결
		List<NoticeDTO> recentNotices = vendorDashboardMapper.findRecentNotices(5);
		mav.addObject("recentNotices", recentNotices);

		return mav;
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
		return String.format("%04d.%02d.%02d (%s)", date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
				dayName);
	}

	private String formatShortDate(LocalDate date) {
		return date.getMonthValue() + "/" + date.getDayOfMonth();
	}

	@GetMapping(value = "/product.htm")
	public ModelAndView product(
			@RequestParam(value = "view", required = false) String view,
			@RequestParam(value = "page", required = false) String pageParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		boolean hiddenView = "hidden".equals(view);
		int page = parsePage(pageParam);

		int sellerNo = loginSeller.getSellerNo();
		String displayYn = hiddenView ? "N" : "Y";

		List<VendorProductListDTO> productList = vendorProductMapper.findBySellerNo(sellerNo, displayYn,
				(page - 1) * PAGE_SIZE, PAGE_SIZE);

		// 통계 카드(전체/판매중/품절/판매중지/승인대기)는 현재 페이지가 아니라 이 탭(노출/숨김)
		// 전체 상품 기준이어야 하므로, 화면에 뿌리는 productList와 별개로 집계 쿼리를 따로 돌린다.
		int totalCount = vendorProductMapper.countBySellerNo(sellerNo, displayYn, null);
		int saleCount = vendorProductMapper.countBySellerNo(sellerNo, displayYn, "판매 중");
		int soldOutCount = vendorProductMapper.countBySellerNo(sellerNo, displayYn, "품절");
		int stoppedCount = vendorProductMapper.countBySellerNo(sellerNo, displayYn, "판매 중지");
		int pendingCount = vendorProductMapper.countBySellerNo(sellerNo, displayYn, "승인 대기");
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		ModelAndView mav = new ModelAndView("vendor.product");
		mav.addObject("menu", "products"); // 사이드바 활성 메뉴

		mav.addObject("productList", productList);
		mav.addObject("hiddenView", hiddenView);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);
		mav.addObject("saleCount", saleCount);
		mav.addObject("soldOutCount", soldOutCount);
		mav.addObject("stoppedCount", stoppedCount);
		mav.addObject("pendingCount", pendingCount);

		return mav;
	}

	// ?page= 파라미터 파싱. 없거나 숫자가 아니면 1페이지
	private int parsePage(String pageParam) {
		try {
			return Math.max(1, Integer.parseInt(pageParam));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	@GetMapping(value = "/product_write.htm")
	public ModelAndView product_write() {
		ModelAndView mav = new ModelAndView("vendor.product_write");
		return mav;

	}

}
