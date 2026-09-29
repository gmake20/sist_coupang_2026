package org.doit.goodpang.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.domain.ProductWriteDTO;
import org.doit.goodpang.mapper.AdminActionLogMapper;
import org.doit.goodpang.mapper.VendorActionLogMapper;
import org.doit.goodpang.service.AdminDeliveryService.CompleteResult;
import org.doit.goodpang.service.AdminService.SellerStatusAction;
import org.doit.goodpang.service.VendorOrderService.ShipResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/*
 * 판매자/관리자 액션 로그 Aspect (SL06_AOP aop4 방식 - @Aspect + @AfterReturning).
 *
 * 전에는 Controller 13곳에서 처리 결과를 확인한 뒤 try { insertLog(...) } catch { } 를 직접 했는데,
 * 이제 Controller/Service는 처리만 하고 로그는 여기서 한 번에 남긴다.
 *   - 대상(Target): org.doit.goodpang.service 의 처리 메서드들 (반환값 = 처리 결과)
 *   - Advice: @AfterReturning - 메서드가 예외 없이 끝났을 때만 실행. 반환값(returning)과 파라미터(args)를 받아서
 *             "실제로 처리됐을 때만"(true / SUCCESS / 새 번호 > 0 등) 로그를 남긴다.
 *
 * @Order(1): @Transactional(순서 기본값 = 가장 낮은 우선순위)보다 바깥에서 감싸도록 한다.
 *   → 이 Advice는 트랜잭션이 commit된 뒤에 실행된다. commit이 실패하면 로그도 안 남고,
 *     로그 기록이 실패해도 이미 commit된 처리는 되돌려지지 않는다 (기존 "트랜잭션 밖에서 로그" 동작과 같음).
 *
 * 로그 기록 실패는 사용자 처리에 영향을 주면 안 되므로 예외를 삼키고 에러 로그만 남긴다 (기존과 동일).
 * argNames: @Pointcut/@AfterReturning 모두 파라미터 이름을 직접 적는다. 적지 않으면 AspectJ가 class 파일의
 *   디버그 정보(지역변수 이름)에서 이름을 찾는데, 빌드 설정에 따라 그 정보가 없으면
 *   "Required parameter names not available" 오류로 서버가 뜨지 않는다.
 */
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Log4j
public class ActionLogAspect {

	private final VendorActionLogMapper vendorActionLogMapper;
	private final AdminActionLogMapper adminActionLogMapper;

	// ===================================================================
	// 판매자 액션 로그 (VENDOR_ACTION_LOG)
	// ===================================================================

	@Pointcut(value = "execution(int org.doit.goodpang.service.VendorProductService.registerProduct(..)) && args(dto)", argNames = "dto")
	private void registerProduct(ProductWriteDTO dto) {}

	// 상품 등록 - 반환값이 새 상품번호
	@AfterReturning(pointcut = "registerProduct(dto)", returning = "productNo", argNames = "dto,productNo")
	public void afterRegisterProduct(ProductWriteDTO dto, int productNo) {
		vendorLog(dto.getSellerNo(), "상품 등록", "PRODUCT", productNo, dto.getProductName());
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.VendorProductService.changeDisplayYn(..)) && args(productNo, sellerNo, displayYn)", argNames = "productNo,sellerNo,displayYn")
	private void changeDisplayYn(int productNo, int sellerNo, String displayYn) {}

	// 상품 노출/숨김
	@AfterReturning(pointcut = "changeDisplayYn(productNo, sellerNo, displayYn)", returning = "changed",
			argNames = "productNo,sellerNo,displayYn,changed")
	public void afterChangeDisplayYn(int productNo, int sellerNo, String displayYn, boolean changed) {
		if (changed) {
			vendorLog(sellerNo, "Y".equals(displayYn) ? "상품 노출" : "상품 숨김", "PRODUCT", productNo, null);
		}
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.VendorProductService.changeSaleStatus(..)) && args(productNo, sellerNo, saleStatus)", argNames = "productNo,sellerNo,saleStatus")
	private void changeSaleStatus(int productNo, int sellerNo, String saleStatus) {}

	// 판매중지/판매재개
	@AfterReturning(pointcut = "changeSaleStatus(productNo, sellerNo, saleStatus)", returning = "changed",
			argNames = "productNo,sellerNo,saleStatus,changed")
	public void afterChangeSaleStatus(int productNo, int sellerNo, String saleStatus, boolean changed) {
		if (changed) {
			vendorLog(sellerNo, "판매 중".equals(saleStatus) ? "판매 재개" : "판매 중지", "PRODUCT", productNo, null);
		}
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.VendorProductService.updateOption(..)) "
			+ "&& args(optionId, sellerNo, price, normalPrice, quantity, status)", argNames = "optionId,sellerNo,price,normalPrice,quantity,status")
	private void updateOption(int optionId, int sellerNo, int price, Integer normalPrice, int quantity, String status) {}

	// 옵션 수정 - detail에 바뀐 값 요약
	@AfterReturning(pointcut = "updateOption(optionId, sellerNo, price, normalPrice, quantity, status)", returning = "changed",
			argNames = "optionId,sellerNo,price,normalPrice,quantity,status,changed")
	public void afterUpdateOption(int optionId, int sellerNo, int price, Integer normalPrice, int quantity, String status,
			boolean changed) {
		if (changed) {
			String detail = "판매가 " + price + "원, 재고 " + quantity + "개, 상태 " + ("Y".equals(status) ? "정상" : "품절");
			vendorLog(sellerNo, "옵션 수정", "PRODUCT_OPTION", optionId, detail);
		}
	}

	@Pointcut(value = "execution(* org.doit.goodpang.service.VendorOrderService.shipOrder(..)) && args(orderNo, sellerNo, invoiceNo)", argNames = "orderNo,sellerNo,invoiceNo")
	private void shipOrder(int orderNo, int sellerNo, String invoiceNo) {}

	// 출고 처리(송장 등록) - SUCCESS일 때만 (송장 중복/실패는 로그 없음)
	@AfterReturning(pointcut = "shipOrder(orderNo, sellerNo, invoiceNo)", returning = "result",
			argNames = "orderNo,sellerNo,invoiceNo,result")
	public void afterShipOrder(int orderNo, int sellerNo, String invoiceNo, ShipResult result) {
		if (result == ShipResult.SUCCESS) {
			vendorLog(sellerNo, "배송 처리", "ORDERS", orderNo, "송장번호 " + invoiceNo);
		}
	}

	@Pointcut(value = "execution(void org.doit.goodpang.service.VendorAccountService.withdraw(..)) && args(sellerNo)", argNames = "sellerNo")
	private void withdraw(int sellerNo) {}

	// 판매자 탈퇴 - void 메서드라 예외 없이 끝났으면 처리된 것
	@AfterReturning(pointcut = "withdraw(sellerNo)", argNames = "sellerNo")
	public void afterWithdraw(int sellerNo) {
		vendorLog(sellerNo, "판매자 탈퇴", "SELLER", sellerNo, null);
	}

	// ===================================================================
	// 관리자 액션 로그 (ADMIN_ACTION_LOG) - 처리한 관리자 번호는 세션(adminNo)에서 가져온다
	// ===================================================================

	@Pointcut(value = "execution(int org.doit.goodpang.service.AdminService.registerNotice(..)) && args(notice)", argNames = "notice")
	private void registerNotice(NoticeDTO notice) {}

	// 공지 등록 - 반환값이 새 공지 번호(실패면 0)
	@AfterReturning(pointcut = "registerNotice(notice)", returning = "noticeNo", argNames = "notice,noticeNo")
	public void afterRegisterNotice(NoticeDTO notice, int noticeNo) {
		if (noticeNo > 0) {
			adminLog("공지 등록", "NOTICE", noticeNo, null);
		}
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.AdminService.updateNotice(..)) && args(notice)", argNames = "notice")
	private void updateNotice(NoticeDTO notice) {}

	@AfterReturning(pointcut = "updateNotice(notice)", returning = "changed", argNames = "notice,changed")
	public void afterUpdateNotice(NoticeDTO notice, boolean changed) {
		if (changed) {
			adminLog("공지 수정", "NOTICE", notice.getNoticeNo(), null);
		}
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.AdminService.deleteNotice(..)) && args(noticeNo)", argNames = "noticeNo")
	private void deleteNotice(int noticeNo) {}

	@AfterReturning(pointcut = "deleteNotice(noticeNo)", returning = "changed", argNames = "noticeNo,changed")
	public void afterDeleteNotice(int noticeNo, boolean changed) {
		if (changed) {
			adminLog("공지 삭제", "NOTICE", noticeNo, null);
		}
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.AdminService.decideProductApproval(..)) && args(productNo, approve)", argNames = "productNo,approve")
	private void decideProductApproval(int productNo, boolean approve) {}

	// 상품 승인/반려
	@AfterReturning(pointcut = "decideProductApproval(productNo, approve)", returning = "changed",
			argNames = "productNo,approve,changed")
	public void afterDecideProductApproval(int productNo, boolean approve, boolean changed) {
		if (changed) {
			adminLog(approve ? "상품 승인" : "상품 반려", "PRODUCT", productNo, null);
		}
	}

	@Pointcut(value = "execution(boolean org.doit.goodpang.service.AdminService.changeSellerStatus(..)) && args(sellerNo, action, reason)", argNames = "sellerNo,action,reason")
	private void changeSellerStatus(int sellerNo, SellerStatusAction action, String reason) {}

	// 판매자 승인/반려/정지/정지해제 - 반려·정지는 사유도 같이 남긴다
	@AfterReturning(pointcut = "changeSellerStatus(sellerNo, action, reason)", returning = "changed",
			argNames = "sellerNo,action,reason,changed")
	public void afterChangeSellerStatus(int sellerNo, SellerStatusAction action, String reason, boolean changed) {
		if (changed) {
			adminLog(action.getLogActionType(), "SELLER", sellerNo, action.isReasonKept() ? reason : null);
		}
	}

	@Pointcut(value = "execution(* org.doit.goodpang.service.AdminDeliveryService.completeDelivery(..)) && args(deliveryNo)", argNames = "deliveryNo")
	private void completeDelivery(int deliveryNo) {}

	// 배송완료 처리 - 관리자 로그 1건 + 이 주문에 상품이 있는 판매자마다 판매자 로그 1건씩
	@AfterReturning(pointcut = "completeDelivery(deliveryNo)", returning = "result", argNames = "deliveryNo,result")
	public void afterCompleteDelivery(int deliveryNo, CompleteResult result) {
		if (result == null) {
			return;
		}

		adminLog("배송완료 처리", "DELIVERY", deliveryNo, null);

		for (int sellerNo : result.getSellerNos()) {
			vendorLog(sellerNo, "배송 완료", "ORDERS", result.getOrderNo(), null);
		}
	}

	// ===================================================================
	// 공통
	// ===================================================================

	private void vendorLog(int sellerNo, String actionType, String targetType, int targetNo, String detail) {
		try {
			vendorActionLogMapper.insertLog(sellerNo, actionType, targetType, targetNo, detail);
		} catch (Exception e) {
			log.error("판매자 액션 로그 기록 실패: " + actionType + " sellerNo=" + sellerNo + " targetNo=" + targetNo, e);
		}
	}

	private void adminLog(String actionType, String targetType, int targetNo, String reason) {

		Integer adminNo = currentAdminNo();

		if (adminNo == null) {
			log.warn("관리자 액션 로그 생략(세션에 adminNo 없음): " + actionType + " targetNo=" + targetNo);
			return;
		}

		try {
			adminActionLogMapper.insertLog(adminNo, actionType, targetType, targetNo, reason);
		} catch (Exception e) {
			log.error("관리자 액션 로그 기록 실패: " + actionType + " adminNo=" + adminNo + " targetNo=" + targetNo, e);
		}
	}

	// 지금 요청의 세션에 들어 있는 관리자 번호 (AdminLoginSuccessHandler가 로그인 시 넣어둠). 요청 밖이면 null
	private Integer currentAdminNo() {

		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

		if (!(attributes instanceof ServletRequestAttributes)) {
			return null;
		}

		HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
		HttpSession session = request.getSession(false);

		return (session == null) ? null : (Integer) session.getAttribute("adminNo");
	}
}
