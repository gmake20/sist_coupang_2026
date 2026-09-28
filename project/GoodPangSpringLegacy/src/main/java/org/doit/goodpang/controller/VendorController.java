package org.doit.goodpang.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.domain.ProductOptionWriteDTO;
import org.doit.goodpang.domain.ProductWriteDTO;
import org.doit.goodpang.domain.SellerDTO;
import org.doit.goodpang.domain.VendorDailySalesDTO;
import org.doit.goodpang.domain.VendorDailyTrafficDTO;
import org.doit.goodpang.domain.VendorDashboardStatDTO;
import org.doit.goodpang.domain.VendorDeliveryDTO;
import org.doit.goodpang.domain.VendorOrderListDTO;
import org.doit.goodpang.domain.VendorOrderStatSummaryDTO;
import org.doit.goodpang.domain.VendorProductDetailDTO;
import org.doit.goodpang.domain.VendorProductListDTO;
import org.doit.goodpang.domain.VendorProductOptionDTO;
import org.doit.goodpang.domain.VendorProductOptionGroupDTO;
import org.doit.goodpang.domain.VendorReturnDTO;
import org.doit.goodpang.domain.VendorSettlementDTO;
import org.doit.goodpang.domain.VendorSettlementDetailDTO;
import org.doit.goodpang.domain.VendorShippingDTO;
import org.doit.goodpang.mapper.NoticeMapper;
import org.doit.goodpang.mapper.VendorActionLogMapper;
import org.doit.goodpang.mapper.VendorDashboardMapper;
import org.doit.goodpang.mapper.VendorMapper;
import org.doit.goodpang.mapper.VendorOrderMapper;
import org.doit.goodpang.mapper.VendorProductMapper;
import org.doit.goodpang.mapper.VendorSettlementMapper;
import org.doit.goodpang.service.VendorOrderService;
import org.doit.goodpang.service.VendorProductService;
import org.doit.goodpang.service.VendorOrderService.ShipResult;
import org.doit.goodpang.util.UploadPaths;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
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
	private final VendorOrderMapper vendorOrderMapper;
	private final VendorActionLogMapper vendorActionLogMapper;
	private final VendorOrderService vendorOrderService;
	private final VendorSettlementMapper vendorSettlementMapper;
	private final NoticeMapper noticeMapper;
	private final VendorProductService vendorProductService;
	// private final MemberShipService memberShipService;

	// 차트 데이터를 JS에 넘길 JSON 변환용 (기존 서블릿의 Gson 대신 pom.xml에 이미 있는 Jackson 사용)
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final long MAX_DOCUMENT_FILE_SIZE = 5L * 1024 * 1024; // 판매자 서류 첨부 1개 최대 5MB (기존 @MultipartConfig와 동일)
	private static final long MAX_IMAGE_FILE_SIZE = 10L * 1024 * 1024; // 상품 등록 이미지 1장 최대 10MB (기존 @MultipartConfig와 동일)
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

	/*
	 * 상품 등록 화면. 입점 승인('승인')이 끝난 판매자만 쓸 수 있고, 그 외에는 안내 화면(product_write_locked)을 보여준다.
	 */
	@GetMapping(value = "/product_write.htm")
	public ModelAndView productWrite(HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		if (!"승인".equals(loginSeller.getApprovalStatus())) {
			ModelAndView locked = new ModelAndView("vendor.product_write_locked");
			locked.addObject("menu", "productWrite"); // 사이드바 활성 메뉴
			locked.addObject("sellerApprovalStatus", loginSeller.getApprovalStatus());
			return locked;
		}

		ModelAndView mav = new ModelAndView("vendor.product_write");
		mav.addObject("menu", "productWrite"); // 사이드바 활성 메뉴
		return mav;
	}

	/*
	 * 상품 등록 처리 (기존 VendorProductWriteServlet.doPost). product_write.jsp가 fetch + FormData(multipart)로 보내고
	 * JSON {success, message, productNo}를 받는다. 이미지는 먼저 디스크에 저장한 뒤,
	 * 저장된 URL과 함께 PRODUCT/PRODUCT_OPTION/PRODUCT_IMAGE를 한 트랜잭션으로 INSERT한다(VendorProductService).
	 */
	@PostMapping(value = "/product_write.htm", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> productWritePost(MultipartHttpServletRequest request, HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return writeResult(401, false, "로그인이 필요합니다.", 0);
		}

		if (!"승인".equals(loginSeller.getApprovalStatus())) {
			return writeResult(403, false, "입점 승인이 완료된 판매자만 상품을 등록할 수 있습니다.", 0);
		}

		if (isBlank(loginSeller.getZipcode()) || isBlank(loginSeller.getBusinessAddress())) {
			return writeResult(400, false, "사업자 정보에 주소가 등록되어 있지 않습니다. 판매자센터에서 사업자 정보를 먼저 등록해주세요.", 0);
		}

		try {
			ProductWriteDTO dto = buildProductWriteDTO(request, loginSeller);

			if (isBlank(dto.getProductName())) {
				return writeResult(400, false, "노출상품명을 입력해주세요.", 0);
			}
			if (isBlank(dto.getInternalName())) {
				return writeResult(400, false, "등록상품명(판매자관리용)을 입력해주세요.", 0);
			}
			if (dto.getProductPrice() <= 0) {
				return writeResult(400, false, "기본 상품가격을 입력해주세요.", 0);
			}
			if (isBlank(dto.getShippingZipcode()) || isBlank(dto.getShippingAddress())) {
				return writeResult(400, false, "출고지 우편번호와 주소를 입력해주세요.", 0);
			}
			if (dto.getOptions().isEmpty()) {
				return writeResult(400, false, "옵션을 최소 1개 이상 추가해주세요.", 0);
			}

			int productNo = vendorProductService.registerProduct(dto);

			try {
				vendorActionLogMapper.insertLog(loginSeller.getSellerNo(), "상품 등록", "PRODUCT", productNo, dto.getProductName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return writeResult(200, true, null, productNo);

		} catch (ImageTooLargeException e) {
			return writeResult(400, false, "첨부 이미지의 개수 또는 용량이 너무 큽니다. 이미지 수를 줄이거나 용량을 낮춰 다시 시도해주세요.", 0);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return writeResult(400, false, "입력값을 다시 확인해주세요.", 0);
		} catch (Exception e) {
			e.printStackTrace();
			return writeResult(500, false, "상품 등록 중 오류가 발생했습니다.", 0);
		}
	}

	// 폼 파라미터 + 업로드 이미지로 등록 DTO를 만든다 (이미지는 이 시점에 디스크에 저장되고 URL만 DTO에 담김)
	private ProductWriteDTO buildProductWriteDTO(MultipartHttpServletRequest request, SellerDTO loginSeller) throws IOException {

		ProductWriteDTO dto = new ProductWriteDTO();

		dto.setSellerNo(loginSeller.getSellerNo());
		dto.setSubCategoryNo(Integer.parseInt(request.getParameter("categoryNo")));
		dto.setProductPrice(parseIntOrZero(request.getParameter("productPrice")));

		dto.setSaleMethod("판매자배송");

		dto.setBrandName(blankToNull(request.getParameter("brandName")));
		dto.setNoBrandYn(request.getParameter("noBrandYn"));
		dto.setProductName(request.getParameter("displayName"));
		dto.setInternalName(request.getParameter("internalName"));

		String optionYn = request.getParameter("optionYn");
		dto.setOptionYn("N".equals(optionYn) ? "N" : "Y");

		dto.setDetailType(request.getParameter("detailType"));

		// 출고지 주소록 기능이 아직 없어서, 등록 폼에서 입력받되 비어 있으면 사업장 주소로 대체
		String shippingZipcode = blankToNull(request.getParameter("shippingZipcode"));
		String shippingAddress = blankToNull(request.getParameter("shippingAddress"));
		String shippingDetailAddress = blankToNull(request.getParameter("shippingDetailAddress"));

		dto.setShippingZipcode(shippingZipcode != null ? shippingZipcode : loginSeller.getZipcode());
		dto.setShippingAddress(shippingAddress != null ? shippingAddress : loginSeller.getBusinessAddress());
		dto.setShippingDetailAddress(shippingDetailAddress != null ? shippingDetailAddress : loginSeller.getBusinessDetailAddress());

		dto.setJejuShippingYn(request.getParameter("jejuShippingYn"));
		dto.setDeliveryServiceCode(request.getParameter("courier"));
		dto.setDeliveryMethod(request.getParameter("deliveryMethod"));
		dto.setBundleShippingYn(request.getParameter("bundleShippingYn"));
		dto.setShippingFeeType(request.getParameter("shippingFeeType"));

		dto.setLeadTimeInputType(request.getParameter("leadTimeInputType"));
		dto.setLeadTimeDays(parseIntOrNull(request.getParameter("leadTimeDays")));
		dto.setSameDayShipYn(request.getParameter("sameDayShipYn"));
		dto.setSameDayCutoffTime(blankToNull(request.getParameter("cutoffTime")));

		dto.setSaleStatus("승인 대기");

		int optionCount = parseIntOrZero(request.getParameter("optionCount"));
		for (int i = 0; i < optionCount; i++) {
			dto.getOptions().add(buildOptionWriteDTO(request, i, loginSeller.getSellerNo()));
		}

		int descImageCount = parseIntOrZero(request.getParameter("descImageCount"));
		for (int i = 0; i < descImageCount; i++) {
			String url = saveUploadedImage(request, "descImage_" + i, loginSeller.getSellerNo());
			if (url != null) {
				dto.getDetailImageUrls().add(url);
			}
		}

		return dto;
	}

	// 옵션 i번째 행 - 파라미터 이름은 "option_{i}_필드명", 이미지는 "option_{i}_mainImage" / "option_{i}_extraImage_{j}"
	private ProductOptionWriteDTO buildOptionWriteDTO(MultipartHttpServletRequest request, int index, int sellerNo) throws IOException {

		String prefix = "option_" + index + "_";
		ProductOptionWriteDTO option = new ProductOptionWriteDTO();

		option.setOption1Type(blankToNull(request.getParameter(prefix + "option1Type")));
		option.setOption1Value(blankToNull(request.getParameter(prefix + "option1Value")));
		option.setOption2Type(blankToNull(request.getParameter(prefix + "option2Type")));
		option.setOption2Value(blankToNull(request.getParameter(prefix + "option2Value")));
		option.setOption3Type(blankToNull(request.getParameter(prefix + "option3Type")));
		option.setOption3Value(blankToNull(request.getParameter(prefix + "option3Value")));
		option.setNormalPrice(parseIntOrNull(request.getParameter(prefix + "normalPrice")));
		option.setSalePrice(parseIntOrZero(request.getParameter(prefix + "salePrice")));
		option.setAutoPriceAdjustYn(request.getParameter(prefix + "autoPriceAdjustYn"));
		option.setQuantity(parseIntOrZero(request.getParameter(prefix + "quantity")));
		option.setSellerProductCode(blankToNull(request.getParameter(prefix + "sellerProductCode")));
		option.setModelNo(blankToNull(request.getParameter(prefix + "modelNo")));
		option.setBarcode(blankToNull(request.getParameter(prefix + "barcode")));

		option.setMainImageUrl(saveUploadedImage(request, prefix + "mainImage", sellerNo));

		int extraCount = parseIntOrZero(request.getParameter(prefix + "extraImageCount"));
		for (int j = 0; j < extraCount; j++) {
			String extraUrl = saveUploadedImage(request, prefix + "extraImage_" + j, sellerNo);
			if (extraUrl != null) {
				option.getExtraImageUrls().add(extraUrl);
			}
		}

		return option;
	}

	/*
	 * 업로드 이미지 1장을 {업로드 기본경로}/{sellerNo}/{UUID}.{확장자} 로 저장하고 DB에 넣을 상대경로("upload/..")를 돌려준다.
	 * 파일이 없으면 null. 10MB를 넘으면 ImageTooLargeException.
	 */
	private String saveUploadedImage(MultipartHttpServletRequest request, String partName, int sellerNo) throws IOException {

		MultipartFile file = request.getFile(partName);

		if (file == null || file.isEmpty()) {
			return null;
		}

		if (file.getSize() > MAX_IMAGE_FILE_SIZE) {
			throw new ImageTooLargeException();
		}

		String submittedFileName = file.getOriginalFilename();
		String originalName = (submittedFileName != null) ? Paths.get(submittedFileName).getFileName().toString() : "";

		String ext = "";
		int dotIndex = originalName.lastIndexOf('.');
		if (dotIndex >= 0) {
			ext = originalName.substring(dotIndex);
		}

		String savedName = UUID.randomUUID() + ext;

		File uploadDir = new File(UploadPaths.resolveBaseDir(request.getServletContext()), String.valueOf(sellerNo));

		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		file.transferTo(new File(uploadDir, savedName));

		return "upload/" + sellerNo + "/" + savedName;
	}

	// 상품 등록 JSON 응답 {success, message, productNo} (기존 서블릿의 Result 클래스와 같은 모양)
	private ResponseEntity<Map<String, Object>> writeResult(int status, boolean success, String message, int productNo) {

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("success", success);
		result.put("message", message);
		result.put("productNo", productNo);

		return ResponseEntity.status(status).body(result);
	}

	// 이미지 1장이 MAX_IMAGE_FILE_SIZE를 넘을 때 - 400 응답으로 바꾸기 위한 표시용 예외
	private static class ImageTooLargeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private String blankToNull(String value) {
		return isBlank(value) ? null : value;
	}

	private int parseIntOrZero(String value) {
		return isBlank(value) ? 0 : Integer.parseInt(value.trim());
	}

	private Integer parseIntOrNull(String value) {
		return isBlank(value) ? null : Integer.valueOf(value.trim());
	}
	

	@GetMapping(value = "/product_options.htm")
	public ModelAndView productOptions(
			@RequestParam(value = "productNo", required = false) String productNoParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		Integer productNo = parseProductNo(productNoParam);
		int sellerNo = loginSeller.getSellerNo();

		List<VendorProductOptionDTO> optionList = vendorProductMapper.findOptionsBySellerNo(sellerNo, productNo);
		List<VendorProductOptionDTO> productFilterOptions = vendorProductMapper
				.findDistinctOptionProductsBySellerNo(sellerNo);

		ModelAndView mav = new ModelAndView("vendor.product_options");
		mav.addObject("menu", "productOptions"); // 사이드바 활성 메뉴

		mav.addObject("optionList", optionList);
		mav.addObject("groupedOptions", groupByProduct(optionList));
		mav.addObject("productFilterOptions", productFilterOptions);
		mav.addObject("selectedProductNo", productNo);

		return mav;
	}

	// ?productNo= 파라미터 파싱. 없거나 숫자가 아니면 null(전체 상품)
	private Integer parseProductNo(String productNoParam) {
		if (productNoParam == null || productNoParam.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(productNoParam.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// 상품번호, 옵션번호 순으로 이미 정렬된 optionList를 상품 단위로 묶는다 (테이블 rowspan 렌더링용)
	private List<VendorProductOptionGroupDTO> groupByProduct(List<VendorProductOptionDTO> optionList) {

		Map<Integer, VendorProductOptionGroupDTO> groupsByProductNo = new LinkedHashMap<>();

		for (VendorProductOptionDTO option : optionList) {
			VendorProductOptionGroupDTO group = groupsByProductNo.computeIfAbsent(option.getProductNo(), no -> {
				VendorProductOptionGroupDTO g = new VendorProductOptionGroupDTO();
				g.setProductNo(option.getProductNo());
				g.setProductName(option.getProductName());
				g.setThumbnailUrl(option.getThumbnailUrl());
				g.setOptions(new ArrayList<>());
				return g;
			});
			group.getOptions().add(option);
		}

		return new ArrayList<>(groupsByProductNo.values());
	}

	@GetMapping(value = "/order.htm")
	public ModelAndView order(
			@RequestParam(value = "startDate", required = false) String startDateParam,
			@RequestParam(value = "endDate", required = false) String endDateParam,
			@RequestParam(value = "orderStatus", required = false) String orderStatus,
			@RequestParam(value = "deliveryStatus", required = false) String deliveryStatus,
			@RequestParam(value = "paymentStatus", required = false) String paymentStatus,
			@RequestParam(value = "page", required = false) String pageParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		int sellerNo = loginSeller.getSellerNo();
		java.sql.Date startDate = parseSqlDate(startDateParam);
		java.sql.Date endDate = parseSqlDate(endDateParam);
		int page = parsePage(pageParam);

		List<VendorOrderListDTO> orderList = vendorOrderMapper.findBySellerNo(sellerNo, startDate, endDate,
				orderStatus, deliveryStatus, paymentStatus, (page - 1) * PAGE_SIZE, PAGE_SIZE);
		int totalCount = vendorOrderMapper.countBySellerNo(sellerNo, startDate, endDate,
				orderStatus, deliveryStatus, paymentStatus);
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		VendorOrderStatSummaryDTO orderStat = vendorOrderMapper.countStats(sellerNo);

		ModelAndView mav = new ModelAndView("vendor.order");
		mav.addObject("menu", "orders"); // 사이드바 활성 메뉴

		mav.addObject("orderList", orderList);
		mav.addObject("orderStat", orderStat);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);

		// 검색폼에 입력값을 그대로 남겨두기 위해 원본 파라미터를 그대로 되돌려준다
		mav.addObject("searchStartDate", startDateParam);
		mav.addObject("searchEndDate", endDateParam);
		mav.addObject("searchOrderStatus", orderStatus);
		mav.addObject("searchDeliveryStatus", deliveryStatus);
		mav.addObject("searchPaymentStatus", paymentStatus);

		return mav;
	}

	// ?startDate=/?endDate= (yyyy-MM-dd) 파싱. 없거나 형식이 잘못됐으면 null(조건 안 붙임)
	private java.sql.Date parseSqlDate(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return java.sql.Date.valueOf(LocalDate.parse(value.trim()));
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/*
	 * 배송 관리 - 배송중인 주문을 모니터링하고 지연 건을 잡아내는 용도. 배송완료 처리는 여기서 하지 않음
	 * (실제 배송완료는 배송기사가 처리해야 할 일이라, 지금은 관리자가 배송 관리 화면에서 대행 중).
	 */
	@GetMapping(value = "/delivery.htm")
	public ModelAndView delivery(HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		List<VendorDeliveryDTO> deliveryList = vendorOrderMapper.findShippingBySellerNo(loginSeller.getSellerNo());

		// 배송 시작 후 3일 이상 지난 건 (VendorDeliveryDTO.isDelayed 기준)
		long delayedCount = deliveryList.stream().filter(VendorDeliveryDTO::isDelayed).count();

		ModelAndView mav = new ModelAndView("vendor.delivery");
		mav.addObject("menu", "delivery"); // 사이드바 활성 메뉴

		mav.addObject("deliveryList", deliveryList);
		mav.addObject("delayedCount", delayedCount);

		return mav;
	}

	// return은 예약어라서 함수이름으로 사용안됨
	@GetMapping(value = "/return.htm")
	public ModelAndView vendorReturn(HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		List<VendorReturnDTO> returnList = vendorOrderMapper.findReturnsBySellerNo(loginSeller.getSellerNo());

		// 상단 통계 카드 - 유형(RETURN_TYPE)별 건수
		long cancelCount = returnList.stream().filter(r -> "취소".equals(r.getReturnType())).count();
		long returnCount = returnList.stream().filter(r -> "반품".equals(r.getReturnType())).count();
		long exchangeCount = returnList.stream().filter(r -> "교환".equals(r.getReturnType())).count();

		ModelAndView mav = new ModelAndView("vendor.return");
		mav.addObject("menu", "return"); // 사이드바 활성 메뉴

		mav.addObject("returnList", returnList);
		mav.addObject("cancelCount", cancelCount);
		mav.addObject("returnCount", returnCount);
		mav.addObject("exchangeCount", exchangeCount);

		return mav;
	}

	/*
	 * 출고/운송장 관리 - '결제완료' 상태인 출고 대기 주문만 모아서 보여주고, 출고 지연 임박 건을 잡아낸다.
	 * 실제 송장 등록/출고 처리는 주문 쪽 출고 처리(기존 /vendor/order/ship)를 그대로 쓴다.
	 */
	@GetMapping(value = "/shipping.htm")
	public ModelAndView shipping(HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		List<VendorShippingDTO> shippingList = vendorOrderMapper.findWaitingBySellerNo(loginSeller.getSellerNo());

		// 결제완료 후 2일 이상 지나도 출고 안 된 건 (VendorShippingDTO.isDelayed 기준)
		long delayedCount = shippingList.stream().filter(VendorShippingDTO::isDelayed).count();

		ModelAndView mav = new ModelAndView("vendor.shipping");
		mav.addObject("menu", "shipping"); // 사이드바 활성 메뉴

		mav.addObject("shippingList", shippingList);
		mav.addObject("delayedCount", delayedCount);

		return mav;
	}

	/*
	 * 출고 처리 - '결제완료' 주문을 송장번호와 함께 '배송중'으로 전환 (기존 VendorOrderShipServlet, POST
	 * /vendor/order/ship).
	 * "주문 목록"(order.jsp)과 "출고/운송장 관리"(shipping.jsp) 두 화면의 폼이 같이 쓴다.
	 * 처리 후엔 폼의 redirectTo 화면으로 돌아가고, 송장번호 중복이면 ?shipError=duplicateInvoice 를 붙인다.
	 */
	@PostMapping(value = "/order/ship.htm")
	public ModelAndView shipOrder(
			@RequestParam(value = "orderNo", required = false) String orderNoParam,
			@RequestParam(value = "invoiceNo", required = false) String invoiceNo,
			@RequestParam(value = "redirectTo", required = false) String redirectTo,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		String redirectPath = resolveShipRedirectTo(redirectTo);

		if (invoiceNo == null || invoiceNo.isBlank()) {
			return new ModelAndView("redirect:" + redirectPath);
		}

		int orderNo;
		try {
			orderNo = Integer.parseInt(orderNoParam);
		} catch (NumberFormatException e) {
			// orderNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
			return new ModelAndView("redirect:" + redirectPath);
		}

		int sellerNo = loginSeller.getSellerNo();
		String trimmedInvoiceNo = invoiceNo.trim();

		ShipResult result = vendorOrderService.shipOrder(orderNo, sellerNo, trimmedInvoiceNo);

		if (result == ShipResult.SUCCESS) {
			// 로그 기록 실패가 출고 처리 자체를 되돌리면 안 되므로 트랜잭션 밖에서 따로 남긴다 (기존과 동일)
			try {
				vendorActionLogMapper.insertLog(sellerNo, "배송 처리", "ORDERS", orderNo, "송장번호 " + trimmedInvoiceNo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String shipErrorParam = (result == ShipResult.INVOICE_DUPLICATE) ? "?shipError=duplicateInvoice" : "";

		return new ModelAndView("redirect:" + redirectPath + shipErrorParam);
	}

	// 처리 후 돌아갈 화면. 허용된 경로 외에는 무시하고 주문 목록으로 보낸다 - 오픈 리다이렉트 방지.
	private String resolveShipRedirectTo(String redirectTo) {

		if ("/vendor/shipping.htm".equals(redirectTo)) {
			return redirectTo;
		}

		return "/vendor/order.htm";
	}

	/*
	 * 정산관리 - 정산내역 리스트. 실제 수수료율/정산주기 데이터가 없어 근사치로 계산한다
	 * (VendorSettlementDTO 주석 참고).
	 */
	@GetMapping(value = "/settlement.htm")
	public ModelAndView settlement(HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		List<VendorSettlementDTO> settlementList = vendorSettlementMapper.findBySellerNo(loginSeller.getSellerNo());

		// 상단 카드 - 전체 회차의 정산금액(매출 - 수수료) 합계
		long totalSettlementAmount = settlementList.stream().mapToLong(VendorSettlementDTO::getSettlementAmount).sum();

		ModelAndView mav = new ModelAndView("vendor.settlement");
		mav.addObject("menu", "settlement"); // 사이드바 활성 메뉴

		mav.addObject("settlementList", settlementList);
		mav.addObject("totalSettlementAmount", totalSettlementAmount);

		return mav;
	}

	/*
	 * 정산관리 - 정산상세. "정산내역 리스트"의 한 정산기간(주 단위) 행을 클릭했을 때,
	 * 그 안에 포함된 주문라인을 하나씩 펼쳐서 보여준다.
	 */
	@GetMapping(value = "/settlement_detail.htm")
	public ModelAndView settlementDetail(
			@RequestParam(value = "periodStart", required = false) String periodStartParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		LocalDate periodStart = parseLocalDate(periodStartParam);

		if (periodStart == null) {
			return new ModelAndView("redirect:/vendor/settlement.htm");
		}

		List<VendorSettlementDetailDTO> detailList = vendorSettlementMapper
				.findDetailBySellerNo(loginSeller.getSellerNo(), periodStart);

		VendorSettlementDTO summary = buildSettlementSummary(periodStart, detailList);

		ModelAndView mav = new ModelAndView("vendor.settlement_detail");
		mav.addObject("menu", "settlement"); // 사이드바 활성 메뉴

		mav.addObject("summary", summary);
		mav.addObject("detailList", detailList);

		return mav;
	}

	// 정산내역 리스트(findBySellerNo)가 GROUP BY로 만드는 요약을, 이미 조회한 detailList로부터 그대로 다시 만든다
	// (같은 정산기간을 다시 쿼리하지 않기 위함)
	private VendorSettlementDTO buildSettlementSummary(LocalDate periodStart,
			List<VendorSettlementDetailDTO> detailList) {

		VendorSettlementDTO summary = new VendorSettlementDTO();
		summary.setPeriodStart(periodStart);

		Set<Integer> orderNos = new HashSet<>();
		long salesAmount = 0;

		for (VendorSettlementDetailDTO detail : detailList) {
			orderNos.add(detail.getOrderNo());
			salesAmount += detail.getLineAmount();
		}

		summary.setOrderCount(orderNos.size());
		summary.setSalesAmount(salesAmount);

		return summary;
	}

	// yyyy-MM-dd 파싱. 없거나 형식이 잘못됐으면 null
	private LocalDate parseLocalDate(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/*
	 * 공지사항 목록 - 조회 전용, 등록/수정/삭제는 관리자만 가능.
	 * (기존에는 VendorAuthFilter가 로그인을 확인했는데, 아직 Interceptor가 없어서 여기서 직접 확인)
	 */
	@GetMapping(value = "/notice.htm")
	public ModelAndView notice(
			@RequestParam(value = "page", required = false) String pageParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		int page = parsePage(pageParam);

		List<NoticeDTO> noticeList = noticeMapper.findAll((page - 1) * PAGE_SIZE, PAGE_SIZE);
		int totalCount = noticeMapper.countAll();
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		ModelAndView mav = new ModelAndView("vendor.notice");
		mav.addObject("menu", "notice"); // 사이드바 활성 메뉴

		mav.addObject("noticeList", noticeList);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);

		return mav;
	}

	// 공지사항 상세 - 조회 전용. 번호가 잘못됐거나 없는 공지면 목록으로 돌려보낸다.
	@GetMapping(value = "/notice_detail.htm")
	public ModelAndView notice_detail(
			@RequestParam(value = "noticeNo", required = false) String noticeNoParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		int noticeNo;
		try {
			noticeNo = Integer.parseInt(noticeNoParam);
		} catch (NumberFormatException e) {
			return new ModelAndView("redirect:/vendor/notice.htm");
		}

		NoticeDTO notice = noticeMapper.findByNoticeNo(noticeNo);

		if (notice == null) {
			return new ModelAndView("redirect:/vendor/notice.htm");
		}

		ModelAndView mav = new ModelAndView("vendor.notice_detail");
		mav.addObject("menu", "notice"); // 사이드바 활성 메뉴
		mav.addObject("notice", notice);

		return mav;
	}

	// 상품 상세정보 (상품 목록에서 상품을 클릭했을 때). 번호가 잘못됐거나 이 판매자 상품이 아니면 목록으로.
	@GetMapping(value = "/product_detail.htm")
	public ModelAndView productDetail(
			@RequestParam(value = "productNo", required = false) String productNoParam,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		int productNo;
		try {
			productNo = Integer.parseInt(productNoParam);
		} catch (NumberFormatException e) {
			return new ModelAndView("redirect:/vendor/product.htm");
		}

		VendorProductDetailDTO product = vendorProductService.getProductDetail(productNo, loginSeller.getSellerNo());

		if (product == null) {
			return new ModelAndView("redirect:/vendor/product.htm");
		}

		ModelAndView mav = new ModelAndView("vendor.product_detail");
		mav.addObject("menu", "products"); // 사이드바 활성 메뉴 (상품 목록)
		mav.addObject("product", product);

		return mav;
	}

	/*
	 * 상품 노출여부 변경 (소프트 삭제/복원). 실제 행을 지우지 않고 PRODUCT.DISPLAY_YN만 바꿔서
	 * 판매자 상품 목록에서만 안 보이게 한다. 기존 구매자의 주문내역(ORDER_DETAIL)은 PRODUCT를 그대로 참조하므로 영향이 없다.
	 * 상품 목록(product.jsp)과 상품 상세(product_detail.jsp)의 숨김/숨김 해제 버튼이 같이 쓰고, 처리 후엔 상품
	 * 목록으로 간다.
	 */
	@PostMapping(value = "/product_visibility.htm")
	public ModelAndView productVisibility(
			@RequestParam(value = "productNo", required = false) String productNoParam,
			@RequestParam(value = "displayYn", required = false) String displayYn,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		if (!"Y".equals(displayYn) && !"N".equals(displayYn)) {
			return new ModelAndView("redirect:/vendor/product.htm");
		}

		int productNo;
		try {
			productNo = Integer.parseInt(productNoParam);
		} catch (NumberFormatException e) {
			// productNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
			return new ModelAndView("redirect:/vendor/product.htm");
		}

		int sellerNo = loginSeller.getSellerNo();

		if (vendorProductMapper.updateDisplayYn(productNo, sellerNo, displayYn) == 1) {
			String actionType = "Y".equals(displayYn) ? "상품 노출" : "상품 숨김";
			try {
				vendorActionLogMapper.insertLog(sellerNo, actionType, "PRODUCT", productNo, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new ModelAndView("redirect:/vendor/product.htm");
	}

	/*
	 * 상품 판매중지/판매재개 토글. '승인 대기'/'품절' 상태는 건드리지 않고, '판매 중' <-> '판매 중지'만 전환한다.
	 * 상품 목록(product.jsp)의 판매중지/판매재개 버튼이 쓰고, 처리 후엔 상품 목록으로 간다.
	 */
	@PostMapping(value = "/product_status.htm")
	public ModelAndView product_status(
			@RequestParam(value = "productNo", required = false) String productNoParam,
			@RequestParam(value = "saleStatus", required = false) String saleStatus,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		if (!"판매 중".equals(saleStatus) && !"판매 중지".equals(saleStatus)) {
			return new ModelAndView("redirect:/vendor/product.htm");
		}

		int productNo;
		try {
			productNo = Integer.parseInt(productNoParam);
		} catch (NumberFormatException e) {
			// productNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
			return new ModelAndView("redirect:/vendor/product.htm");
		}

		int sellerNo = loginSeller.getSellerNo();

		if (vendorProductMapper.updateSaleStatus(productNo, sellerNo, saleStatus) == 1) {
			String actionType = "판매 중".equals(saleStatus) ? "판매 재개" : "판매 중지";
			try {
				vendorActionLogMapper.insertLog(sellerNo, actionType, "PRODUCT", productNo, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new ModelAndView("redirect:/vendor/product.htm");
	}
	
	// 상품 옵션 관리 - 옵션 하나의 판매가/정상가/재고수량/상태 수정. 처리 후엔 옵션 목록으로 간다.
	@PostMapping(value = "/product_option_update.htm")
	public ModelAndView productOptionUpdate(
			@RequestParam(value = "optionId", required = false) String optionIdParam,
			@RequestParam(value = "price", required = false) String priceParam,
			@RequestParam(value = "normalPrice", required = false) String normalPriceParam,
			@RequestParam(value = "quantity", required = false) String quantityParam,
			@RequestParam(value = "status", required = false) String status,
			HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		try {
			int optionId = Integer.parseInt(optionIdParam);
			int price = Integer.parseInt(priceParam);
			int quantity = Integer.parseInt(quantityParam);

			// 정상가는 선택 입력 - 비어 있으면 null
			Integer normalPrice = (normalPriceParam != null && !normalPriceParam.isBlank())
					? Integer.valueOf(normalPriceParam.trim())
					: null;

			if (!"Y".equals(status) && !"N".equals(status)) {
				status = "Y";
			}

			int sellerNo = loginSeller.getSellerNo();

			if (vendorProductMapper.updateOption(optionId, sellerNo, price, normalPrice, quantity, status) == 1) {
				String detail = "판매가 " + price + "원, 재고 " + quantity + "개, 상태 " + ("Y".equals(status) ? "정상" : "품절");
				try {
					vendorActionLogMapper.insertLog(sellerNo, "옵션 수정", "PRODUCT_OPTION", optionId, detail);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (NumberFormatException e) {
			// 값이 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		return new ModelAndView("redirect:/vendor/product_options.htm");
	}
	
	/*
	 * 판매자 정보관리. 회원가입 직후 '입점 대기' 판매자가 사업장주소·통신판매업신고번호·대표카테고리·정산계좌·
	 * 서류(사업자등록증/통신판매신고증)를 추가로 입력하는 페이지이자, 승인된 판매자가 같은 정보를 수정하는 페이지.
	 * 입력값은 JSP가 세션의 loginSeller에서 채운다.
	 */
	@GetMapping(value = "/business_info.htm")
	public ModelAndView businessInfo(HttpSession session) {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		return businessInfoView(null);
	}

	/*
	 * 판매자 정보관리 제출 (multipart - 서류 이미지 첨부). '입점 대기'/'반려' 상태에서 제출하면 '심사 중'으로 바뀌고,
	 * '승인' 상태에서 수정하는 경우는 재심사로 되돌리지 않는다(VendorMapper.updateBusinessInfo).
	 * 성공하면 세션의 loginSeller를 DB 최신값으로 갱신하고 대시보드로, 실패하면 같은 화면에 error를 보여준다.
	 */
	@PostMapping(value = "/business_info.htm")
	public ModelAndView businessInfoPost(MultipartHttpServletRequest request, HttpSession session) throws Exception {

		SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");

		if (loginSeller == null) {
			return new ModelAndView("redirect:/vendor/login.htm");
		}

		String zipcode = request.getParameter("zipcode");
		String businessAddress = request.getParameter("businessAddress");
		String businessDetailAddress = request.getParameter("businessDetailAddress");
		String mailOrderNo = request.getParameter("mailOrderNo");
		String categoryNo = request.getParameter("categoryNo");
		String bankName = request.getParameter("bankName");
		String accountNo = request.getParameter("accountNo");
		String accountHolder = request.getParameter("accountHolder");

		if (isBlank(zipcode) || isBlank(businessAddress) || isBlank(mailOrderNo) || isBlank(categoryNo)
				|| isBlank(bankName) || isBlank(accountNo) || isBlank(accountHolder)) {
			return businessInfoView("필수 입력값을 모두 입력해주세요.");
		}

		String businessCertUrl;
		String mailOrderCertUrl;
		try {
			businessCertUrl = saveBusinessDocument(request, "businessCert", loginSeller.getBusinessCertUrl(), loginSeller.getSellerNo());
			mailOrderCertUrl = saveBusinessDocument(request, "mailOrderCert", loginSeller.getMailOrderCertUrl(), loginSeller.getSellerNo());
		} catch (ImageTooLargeException e) {
			return businessInfoView("첨부파일은 파일당 최대 5MB까지 가능합니다.");
		}

		// 최초 제출 시에는 서류 첨부 둘 다 필수 (재제출이면 기존 파일 경로가 유지됨)
		if (businessCertUrl == null || mailOrderCertUrl == null) {
			return businessInfoView("사업자등록증과 통신판매신고증을 모두 첨부해주세요.");
		}

		SellerDTO dto = new SellerDTO();

		dto.setSellerNo(loginSeller.getSellerNo());
		dto.setZipcode(zipcode);
		dto.setBusinessAddress(businessAddress);
		dto.setBusinessDetailAddress(businessDetailAddress);
		dto.setMailOrderNo(mailOrderNo);
		dto.setBankName(bankName);
		dto.setAccountNo(accountNo);
		dto.setAccountHolder(accountHolder);
		dto.setBusinessCertUrl(businessCertUrl);
		dto.setMailOrderCertUrl(mailOrderCertUrl);

		try {
			dto.setCategoryNo(Integer.valueOf(categoryNo.trim()));
		} catch (NumberFormatException e) {
			return businessInfoView("필수 입력값을 모두 입력해주세요.");
		}

		if (vendorMapper.updateBusinessInfo(dto) != 1) {
			return businessInfoView("저장에 실패했습니다.");
		}

		// 세션의 로그인 정보를 최신 상태(approval_status='심사 중' 등)로 갱신
		SellerDTO refreshed = vendorMapper.findByEmail(loginSeller.getEmail());
		session.setAttribute("loginSeller", refreshed);

		return new ModelAndView("redirect:/vendor/dashboard.htm");
	}

	// 판매자 정보관리 화면 (error가 있으면 상단에 표시)
	private ModelAndView businessInfoView(String error) {

		ModelAndView mav = new ModelAndView("vendor.business_info");
		mav.addObject("menu", "businessInfo"); // 사이드바 활성 메뉴

		if (error != null) {
			mav.addObject("error", error);
		}

		return mav;
	}

	/*
	 * 서류 첨부가 새로 들어왔으면 {업로드 기본경로}/{sellerNo}/{UUID}.{확장자} 로 저장 후 상대경로("upload/..")를 반환하고,
	 * 첨부가 없으면(재제출 등) 기존 경로(existingUrl)를 그대로 반환한다. 5MB를 넘으면 ImageTooLargeException.
	 */
	private String saveBusinessDocument(MultipartHttpServletRequest request, String partName, String existingUrl, int sellerNo) throws IOException {

		MultipartFile file = request.getFile(partName);

		if (file == null || file.isEmpty() || isBlank(file.getOriginalFilename())) {
			return existingUrl;
		}

		if (file.getSize() > MAX_DOCUMENT_FILE_SIZE) {
			throw new ImageTooLargeException();
		}

		String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();

		String ext = "";
		int dotIndex = originalName.lastIndexOf('.');
		if (dotIndex >= 0) {
			ext = originalName.substring(dotIndex);
		}

		String savedName = UUID.randomUUID() + ext;

		File uploadDir = new File(UploadPaths.resolveBaseDir(request.getServletContext()), String.valueOf(sellerNo));

		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		file.transferTo(new File(uploadDir, savedName));

		return "upload/" + sellerNo + "/" + savedName;
	}
	
}
