package org.doit.goodpang.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.AdminActionLogDTO;
import org.doit.goodpang.domain.AdminDeliveryDTO;
import org.doit.goodpang.domain.AdminProductApprovalDTO;
import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.domain.SellerDTO;
import org.doit.goodpang.domain.VendorActionLogDTO;
import org.doit.goodpang.domain.VendorActionLogSearchDTO;
import org.doit.goodpang.mapper.AdminActionLogMapper;
import org.doit.goodpang.mapper.AdminDeliveryMapper;
import org.doit.goodpang.mapper.AdminProductMapper;
import org.doit.goodpang.mapper.NoticeMapper;
import org.doit.goodpang.mapper.VendorActionLogMapper;
import org.doit.goodpang.mapper.VendorMapper;
import org.doit.goodpang.service.AdminDeliveryService;
import org.doit.goodpang.service.AdminDeliveryService.CompleteResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	private final NoticeMapper noticeMapper;
	private final AdminActionLogMapper adminActionLogMapper;
	private final AdminProductMapper adminProductMapper;
	private final AdminDeliveryMapper adminDeliveryMapper;
	private final AdminDeliveryService adminDeliveryService;
	private final VendorActionLogMapper vendorActionLogMapper;
	private final VendorMapper vendorMapper;

	private static final int PAGE_SIZE = 20; // 목록 한 페이지 행 수

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

	// ===================================================================
	// 상품 승인 관리 (기존 AdminProductListServlet / AdminProductApprovalServlet)
	// 판매자가 등록한 상품은 '승인 대기'로 들어오고, 여기서 승인('판매 중') 또는 반려('판매 중지')한다.
	// ===================================================================

	// 상품 목록 - 승인 대기 상품이 맨 위
	@GetMapping(value = "/products.htm")
	public ModelAndView products() {

		List<AdminProductApprovalDTO> productList = adminProductMapper.findAll();

		ModelAndView mav = new ModelAndView("admin.products");
		mav.addObject("productList", productList);
		return mav;
	}

	/*
	 * 상품 승인/반려 (목록의 승인·반려 버튼 - POST + CSRF 토큰).
	 * action=approve → '판매 중', action=reject → '판매 중지'. '승인 대기' 상품만 바뀐다.
	 * 기존 서블릿은 실제로 바뀌지 않았어도(이미 처리된 상품 등) 로그를 남겼는데, 여기서는 바뀐 경우에만 남긴다.
	 */
	@PostMapping(value = "/product_approve.htm")
	public ModelAndView productApprove(
			@RequestParam(value = "productNo", required = false) String productNoParam,
			@RequestParam(value = "action", required = false) String action,
			HttpSession session) {

		Integer productNo = parseNo(productNoParam);

		if (productNo == null) {
			return new ModelAndView("redirect:/admin/products.htm");
		}

		String saleStatus;
		String actionType;

		if ("approve".equals(action)) {
			saleStatus = "판매 중";
			actionType = "상품 승인";
		} else if ("reject".equals(action)) {
			saleStatus = "판매 중지";
			actionType = "상품 반려";
		} else {
			return new ModelAndView("redirect:/admin/products.htm");
		}

		if (adminProductMapper.updateApprovalStatus(productNo, saleStatus) == 1) {
			writeAdminLog((Integer) session.getAttribute("adminNo"), actionType, "PRODUCT", productNo);
		}

		return new ModelAndView("redirect:/admin/products.htm");
	}

	// ===================================================================
	// 배송 관리 (기존 AdminDeliveryListServlet / AdminDeliveryCompleteServlet)
	// 실제 배송완료는 배송기사가 처리해야 할 일이지만, 지금은 관리자가 여기서 대행한다.
	// ===================================================================

	// 배송중인 주문 목록 - 배송 시작일 최신순
	@GetMapping(value = "/deliveries.htm")
	public ModelAndView deliveries() {

		List<AdminDeliveryDTO> deliveryList = adminDeliveryMapper.findShipping();

		ModelAndView mav = new ModelAndView("admin.deliveries");
		mav.addObject("deliveryList", deliveryList);
		return mav;
	}

	/*
	 * 배송완료 처리 (목록의 버튼 - POST + CSRF 토큰).
	 * DELIVERY/ORDERS를 한 트랜잭션으로 '배송완료'로 바꾼 뒤(AdminDeliveryService), 기존과 같이
	 * 관리자 로그 1건 + 이 주문에 상품이 있는 판매자마다 판매자 로그 1건씩 남긴다.
	 * 로그는 트랜잭션 밖이라, 로그 기록이 실패해도 배송완료는 유지된다.
	 */
	@PostMapping(value = "/delivery_complete.htm")
	public ModelAndView deliveryComplete(
			@RequestParam(value = "deliveryNo", required = false) String deliveryNoParam,
			HttpSession session) {

		Integer deliveryNo = parseNo(deliveryNoParam);

		if (deliveryNo == null) {
			// deliveryNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
			return new ModelAndView("redirect:/admin/deliveries.htm");
		}

		CompleteResult result = adminDeliveryService.completeDelivery(deliveryNo);

		if (result != null) {
			writeAdminLog((Integer) session.getAttribute("adminNo"), "배송완료 처리", "DELIVERY", deliveryNo);

			for (int sellerNo : result.getSellerNos()) {
				try {
					vendorActionLogMapper.insertLog(sellerNo, "배송 완료", "ORDERS", result.getOrderNo(), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return new ModelAndView("redirect:/admin/deliveries.htm");
	}

	// ===================================================================
	// 판매자 입점 심사 (기존 SellerListServlet / SellerDetailServlet / SellerApprovalServlet)
	// ===================================================================

	// 전체 판매자 목록 - 최근 가입순. 행을 누르면 상세로
	@GetMapping(value = "/sellers.htm")
	public ModelAndView sellers() {

		List<SellerDTO> sellerList = vendorMapper.findAllSellers();

		ModelAndView mav = new ModelAndView("admin.sellers");
		mav.addObject("sellerList", sellerList);
		return mav;
	}

	// 판매자 상세 (사업자/정산계좌/첨부서류 + 승인·반려·정지 버튼). 번호가 잘못됐거나 없는 판매자면 목록으로
	@GetMapping(value = "/seller_detail.htm")
	public ModelAndView sellerDetail(@RequestParam(value = "sellerNo", required = false) String sellerNoParam) {

		Integer sellerNo = parseNo(sellerNoParam);
		SellerDTO seller = (sellerNo == null) ? null : vendorMapper.findBySellerNo(sellerNo);

		if (seller == null) {
			return new ModelAndView("redirect:/admin/sellers.htm");
		}

		ModelAndView mav = new ModelAndView("admin.seller_detail");
		mav.addObject("seller", seller);
		return mav;
	}

	/*
	 * 판매자 승인/반려/정지/정지해제 (상세 화면의 버튼들 - POST + CSRF 토큰). 처리 후 같은 판매자 상세로 돌아간다.
	 *   approve    → '승인'
	 *   reject     → '반려' + 반려 사유(rejectReason, 비었으면 기본 문구)
	 *   suspend    → '정지' + 정지 사유(suspendReason) - REJECT_REASON 컬럼을 정지 사유에도 재사용 (기존과 동일)
	 *   reactivate → '승인' (정지 해제)
	 * 판매자 탈퇴와 같은 VendorMapper.updateApprovalStatus를 쓴다. 로그는 실제로 바뀐 경우에만 남긴다.
	 */
	@PostMapping(value = "/seller_approve.htm")
	public ModelAndView sellerApprove(
			@RequestParam(value = "sellerNo", required = false) String sellerNoParam,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "rejectReason", required = false) String rejectReason,
			@RequestParam(value = "suspendReason", required = false) String suspendReason,
			HttpSession session) {

		Integer sellerNo = parseNo(sellerNoParam);

		if (sellerNo == null) {
			return new ModelAndView("redirect:/admin/sellers.htm");
		}

		String approvalStatus;
		String reason = null;
		String actionType;

		if ("approve".equals(action)) {
			approvalStatus = "승인";
			actionType = "판매자 승인";
		} else if ("reject".equals(action)) {
			approvalStatus = "반려";
			reason = (rejectReason == null || rejectReason.isBlank()) ? "사유가 입력되지 않았습니다." : rejectReason;
			actionType = "판매자 반려";
		} else if ("suspend".equals(action)) {
			approvalStatus = "정지";
			reason = (suspendReason == null || suspendReason.isBlank()) ? "사유가 입력되지 않았습니다." : suspendReason;
			actionType = "판매자 정지";
		} else if ("reactivate".equals(action)) {
			approvalStatus = "승인";
			actionType = "판매자 정지해제";
		} else {
			return new ModelAndView("redirect:/admin/seller_detail.htm?sellerNo=" + sellerNo);
		}

		if (vendorMapper.updateApprovalStatus(sellerNo, approvalStatus, reason) == 1) {
			Integer adminNo = (Integer) session.getAttribute("adminNo");
			if (adminNo != null) {
				try {
					adminActionLogMapper.insertLog(adminNo, actionType, "SELLER", sellerNo, reason);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return new ModelAndView("redirect:/admin/seller_detail.htm?sellerNo=" + sellerNo);
	}

	// ===================================================================
	// 로그 조회 (기존 AdminActionLogListServlet / AdminVendorActionLogListServlet)
	// ===================================================================

	// 관리자 액션 로그 - 최신순, 20건씩
	@GetMapping(value = "/action_logs.htm")
	public ModelAndView actionLogs(@RequestParam(value = "page", required = false) String pageParam) {

		int page = parsePage(pageParam);

		List<AdminActionLogDTO> logList = adminActionLogMapper.findAll((page - 1) * PAGE_SIZE, PAGE_SIZE);
		int totalCount = adminActionLogMapper.countAll();
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		ModelAndView mav = new ModelAndView("admin.action_logs");
		mav.addObject("logList", logList);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);
		return mav;
	}

	/*
	 * 판매자 액션 로그 - 스토어명(부분일치)/작업종류/대상종류/기간으로 검색, 최신순 20건씩.
	 * 검색값은 그대로 되돌려줘서 검색폼에 남기고, 페이지 링크에도 이어붙인다.
	 */
	@GetMapping(value = "/vendor_action_logs.htm")
	public ModelAndView vendorActionLogs(
			@RequestParam(value = "page", required = false) String pageParam,
			@RequestParam(value = "storeName", required = false) String storeName,
			@RequestParam(value = "actionType", required = false) String actionType,
			@RequestParam(value = "targetType", required = false) String targetType,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) {

		int page = parsePage(pageParam);

		VendorActionLogSearchDTO search = new VendorActionLogSearchDTO();
		search.setStoreName(storeName == null ? null : storeName.trim());
		search.setActionType(actionType);
		search.setTargetType(targetType);
		search.setStartDate(parseDate(startDate));
		search.setEndDate(parseDate(endDate));

		List<VendorActionLogDTO> logList = vendorActionLogMapper.findAll(search, (page - 1) * PAGE_SIZE, PAGE_SIZE);
		int totalCount = vendorActionLogMapper.countAll(search);
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		ModelAndView mav = new ModelAndView("admin.vendor_action_logs");
		mav.addObject("logList", logList);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);

		// 검색폼에 입력값을 그대로 남겨두고, 페이지네이션 링크에도 검색조건을 이어붙이기 위함
		mav.addObject("searchStoreName", search.getStoreName());
		mav.addObject("searchActionType", actionType);
		mav.addObject("searchTargetType", targetType);
		mav.addObject("searchStartDate", startDate);
		mav.addObject("searchEndDate", endDate);
		return mav;
	}

	// yyyy-MM-dd 파싱. 없거나 형식이 잘못됐으면 null(조건 안 붙임)
	private LocalDate parseDate(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(value.trim());
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	// ===================================================================
	// 공지사항 관리 (기존 AdminNoticeList/Write/Edit/DeleteServlet)
	// 판매자센터 공지 화면(VendorController.notice)과 같은 NOTICE 테이블/NoticeMapper를 쓴다.
	// ===================================================================

	// 공지 목록 - '공지' 타입 먼저, 그 안에서 최신순
	@GetMapping(value = "/notices.htm")
	public ModelAndView notices(@RequestParam(value = "page", required = false) String pageParam) {

		int page = parsePage(pageParam);

		List<NoticeDTO> noticeList = noticeMapper.findAll((page - 1) * PAGE_SIZE, PAGE_SIZE);
		int totalCount = noticeMapper.countAll();
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		ModelAndView mav = new ModelAndView("admin.notices");
		mav.addObject("noticeList", noticeList);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);

		return mav;
	}

	// 공지 등록 화면
	@GetMapping(value = "/notice_write.htm")
	public ModelAndView noticeWrite() {
		return new ModelAndView("admin.notice_write");
	}

	/*
	 * 공지 등록 처리. 제목/내용/구분('공지' 또는 '안내')이 하나라도 없으면 같은 화면에 error
	 * (입력했던 값은 JSP가 param으로 다시 채움). 성공하면 관리자 액션 로그를 남기고 목록으로.
	 * 작성자(adminNo)는 로그인 시 AdminLoginSuccessHandler가 세션에 넣어둔 값.
	 */
	@PostMapping(value = "/notice_write.htm")
	public ModelAndView noticeWritePost(
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "noticeType", required = false) String noticeType,
			HttpSession session) {

		Integer adminNo = (Integer) session.getAttribute("adminNo");

		if (!isValidNotice(title, content, noticeType) || adminNo == null) {
			ModelAndView mav = new ModelAndView("admin.notice_write");
			mav.addObject("error", "제목, 내용, 구분을 모두 입력해주세요.");
			return mav;
		}

		NoticeDTO notice = new NoticeDTO();
		notice.setTitle(title.trim());
		notice.setContent(content);
		notice.setNoticeType(noticeType);
		notice.setAdminNo(adminNo);

		if (noticeMapper.insertNotice(notice) == 1) {
			writeAdminLog(adminNo, "공지 등록", "NOTICE", notice.getNoticeNo());
		}

		return new ModelAndView("redirect:/admin/notices.htm");
	}

	// 공지 수정 화면 - 번호가 잘못됐거나 없는 공지면 목록으로
	@GetMapping(value = "/notice_edit.htm")
	public ModelAndView noticeEdit(@RequestParam(value = "noticeNo", required = false) String noticeNoParam) {

		Integer noticeNo = parseNo(noticeNoParam);
		NoticeDTO notice = (noticeNo == null) ? null : noticeMapper.findByNoticeNo(noticeNo);

		if (notice == null) {
			return new ModelAndView("redirect:/admin/notices.htm");
		}

		ModelAndView mav = new ModelAndView("admin.notice_edit");
		mav.addObject("notice", notice);
		return mav;
	}

	/*
	 * 공지 수정 처리. 값이 빠졌으면 같은 화면에 error - 기존 서블릿은 DB의 원래 내용으로 되돌려 보여줬는데,
	 * 여기서는 방금 입력했던 값을 그대로 다시 보여준다(고치던 내용이 사라지지 않게).
	 */
	@PostMapping(value = "/notice_edit.htm")
	public ModelAndView noticeEditPost(
			@RequestParam(value = "noticeNo", required = false) String noticeNoParam,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "noticeType", required = false) String noticeType,
			HttpSession session) {

		Integer noticeNo = parseNo(noticeNoParam);

		if (noticeNo == null) {
			return new ModelAndView("redirect:/admin/notices.htm");
		}

		NoticeDTO notice = new NoticeDTO();
		notice.setNoticeNo(noticeNo);
		notice.setTitle(title);
		notice.setContent(content);
		notice.setNoticeType(noticeType);

		if (!isValidNotice(title, content, noticeType)) {
			ModelAndView mav = new ModelAndView("admin.notice_edit");
			mav.addObject("error", "제목, 내용, 구분을 모두 입력해주세요.");
			mav.addObject("notice", notice);
			return mav;
		}

		notice.setTitle(title.trim());

		if (noticeMapper.updateNotice(notice) == 1) {
			writeAdminLog((Integer) session.getAttribute("adminNo"), "공지 수정", "NOTICE", noticeNo);
		}

		return new ModelAndView("redirect:/admin/notices.htm");
	}

	// 공지 삭제 (목록의 삭제 버튼 - POST + CSRF 토큰). 번호가 잘못됐으면 아무것도 지우지 않고 목록으로
	@PostMapping(value = "/notice_delete.htm")
	public ModelAndView noticeDelete(
			@RequestParam(value = "noticeNo", required = false) String noticeNoParam,
			HttpSession session) {

		Integer noticeNo = parseNo(noticeNoParam);

		if (noticeNo != null && noticeMapper.deleteNotice(noticeNo) == 1) {
			writeAdminLog((Integer) session.getAttribute("adminNo"), "공지 삭제", "NOTICE", noticeNo);
		}

		return new ModelAndView("redirect:/admin/notices.htm");
	}

	// 제목/내용이 비어 있지 않고, 구분이 '공지' 또는 '안내'인지
	private boolean isValidNotice(String title, String content, String noticeType) {
		boolean validType = "공지".equals(noticeType) || "안내".equals(noticeType);
		return title != null && !title.isBlank() && content != null && !content.isBlank() && validType;
	}

	/*
	 * 관리자 액션 로그 (ADMIN_ACTION_LOG). targetType 예: "NOTICE", "PRODUCT".
	 * 로그 기록 실패가 실제 처리(공지 등록, 상품 승인 등)를 되돌리면 안 되므로 예외는 삼킨다 (기존과 동일).
	 */
	private void writeAdminLog(Integer adminNo, String actionType, String targetType, int targetNo) {

		if (adminNo == null) {
			return;
		}

		try {
			adminActionLogMapper.insertLog(adminNo, actionType, targetType, targetNo, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 번호 파라미터(noticeNo, productNo 등) 파싱. 없거나 숫자가 아니면 null
	private Integer parseNo(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// ?page= 파싱. 없거나 숫자가 아니면 1페이지
	private int parsePage(String pageParam) {
		try {
			return Math.max(1, Integer.parseInt(pageParam));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
