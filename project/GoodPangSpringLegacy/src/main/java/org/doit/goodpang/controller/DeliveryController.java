package org.doit.goodpang.controller;

import java.util.List;

import org.doit.goodpang.domain.AdminDeliveryDTO;
import org.doit.goodpang.mapper.AdminDeliveryMapper;
import org.doit.goodpang.service.AdminDeliveryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

/*
 * 배송 앱용 JSON API - 구버전 DeliveryJsonServlet(/deliveries/json), DeliveryCompleteJsonServlet(/delivery-complete/json).
 *   GET  /deliveries/json         배송중인 상품 목록 (관리자 배송 관리 화면 admin/deliveries.jsp와 같은 데이터)
 *   POST /delivery-complete/json  배송완료 처리
 *
 * 로그인/CSRF 없이 누구나 호출 가능 - security-context.xml에서 두 URL을 security="none"으로 뺐다.
 * (원래는 배송 앱 쪽 인증이 필요하지만 앱 인증이 아직 없어서 이 프로젝트 범위에서는 보안 없이 연다)
 *
 * Jackson이 아니라 Gson으로 직렬화하는 이유:
 *   배송 앱(GoodPang/app DeliveryDateFormatter.kt)이 deliveryStartDate를 Gson 기본 날짜 형식
 *   ("Sep 2, 2026, 7:28:44 AM")으로 파싱한다. Jackson은 Date를 숫자(epoch millis)로 내보내서 앱에서 깨지므로
 *   기존 서블릿과 같은 Gson 결과 문자열을 그대로 내려보낸다 (ProductController /option과 같은 방식).
 */
@Controller
@RequiredArgsConstructor
public class DeliveryController {

	private static final Gson gson = new Gson();

	private final AdminDeliveryMapper adminDeliveryMapper;
	private final AdminDeliveryService adminDeliveryService;

	@GetMapping(value = "/deliveries/json", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String deliveries() {

		List<AdminDeliveryDTO> deliveryList = adminDeliveryMapper.findShipping();

		for (AdminDeliveryDTO delivery : deliveryList) {
			delivery.setBuyerName(maskName(delivery.getBuyerName()));
			delivery.setBuyerPhone(maskPhone(delivery.getBuyerPhone()));
		}

		return gson.toJson(deliveryList);
	}

	/*
	 * 배송완료 처리 - 구버전 DeliveryCompleteJsonServlet(/delivery-complete/json). 배송 앱이 deliveryNo를 form으로 POST.
	 * 관리자 화면의 배송완료와 같은 AdminDeliveryService.completeDelivery를 호출한다 (DELIVERY + ORDERS 한 트랜잭션).
	 * 판매자 액션 로그("배송 완료")는 ActionLogAspect가 남긴다. 관리자 로그는 세션에 adminNo가 없어서 남지 않음 (기존 서블릿도 판매자 로그만 남김).
	 * deliveryNo를 String으로 받는 이유: 누락/숫자 아님도 400이 아니라 기존과 같은 JSON 메시지로 응답하기 위해.
	 */
	@PostMapping(value = "/delivery-complete/json", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String deliveryComplete(@RequestParam(value = "deliveryNo", required = false) String deliveryNoParam) {

		JsonObject result = new JsonObject();

		int deliveryNo;
		try {
			deliveryNo = Integer.parseInt(deliveryNoParam);
		} catch (NumberFormatException e) {
			result.addProperty("success", false);
			result.addProperty("message", "deliveryNo가 올바르지 않습니다.");
			return gson.toJson(result);
		}

		boolean success = adminDeliveryService.completeDelivery(deliveryNo) != null;

		result.addProperty("success", success);
		if (!success) {
			result.addProperty("message", "이미 처리되었거나 존재하지 않는 배송번호입니다.");
		}

		return gson.toJson(result);
	}

	// "홍길동" -> "홍*동", "이연" -> "이*" (ReviewService.maskName()과 동일한 규칙)
	private String maskName(String name) {
		if (name == null || name.length() <= 1) {
			return name;
		}
		if (name.length() == 2) {
			return name.charAt(0) + "*";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(name.charAt(0));
		for (int i = 1; i < name.length() - 1; i++) {
			sb.append("*");
		}
		sb.append(name.charAt(name.length() - 1));
		return sb.toString();
	}

	// 로그인 없이 열리는 API라 뒷자리만 마스킹해서 노출 ("010-1234-5678" -> "010-1234-****")
	private String maskPhone(String phone) {
		if (phone == null || phone.isBlank()) {
			return phone;
		}
		int maskLength = Math.min(4, phone.length());
		String visible = phone.substring(0, phone.length() - maskLength);
		return visible + "*".repeat(maskLength);
	}

}
