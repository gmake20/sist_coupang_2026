package org.doit.goodpang.service;

import org.doit.goodpang.domain.VendorOrderDetailDTO;
import org.doit.goodpang.mapper.VendorOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/*
 * 판매자센터 주문/배송 처리 - 여러 쿼리를 한 트랜잭션으로 묶어야 하는 작업만 여기 둔다.
 * (단순 조회는 Controller에서 VendorOrderMapper를 바로 호출)
 */
@Service
@RequiredArgsConstructor
public class VendorOrderService {

	private final VendorOrderMapper vendorOrderMapper;

	// shipOrder()의 처리 결과. 화면에 실패 사유를 구분해서 보여주기 위해 boolean 대신 사용.
	public enum ShipResult {
		SUCCESS,
		INVOICE_DUPLICATE, // 같은 택배사에 이미 등록된 송장번호
		FAILED             // 그 외(주문을 찾을 수 없음, 이미 출고된 주문 등)
	}

	/*
	 * 결제완료 → 배송중 전환 + DELIVERY 행 생성(송장번호 저장)을 하나의 트랜잭션으로 처리.
	 * (기존 VendorOrderListDAO.shipOrder의 setAutoCommit(false)/commit/rollback을 @Transactional이 대신함)
	 *
	 * 기존 DAO는 DELIVERY INSERT → ORDERS UPDATE 순서였고 UPDATE가 실패하면 rollback했는데,
	 * 여기서는 UPDATE를 먼저 해서 조건이 안 맞으면(0건) 아무것도 쓰지 않은 채 FAILED로 끝낸다.
	 * 쿼리 도중 예외(RuntimeException)가 나면 @Transactional이 전체를 rollback 한다.
	 */
	@Transactional
	public ShipResult shipOrder(int orderNo, int sellerNo, String invoiceNo) {

		// 이 판매자 상품이 없는 주문이면 null
		String deliveryServiceCode = vendorOrderMapper.findDeliveryServiceCode(orderNo, sellerNo);

		if (deliveryServiceCode == null) {
			return ShipResult.FAILED;
		}

		if (vendorOrderMapper.countDeliveryByInvoice(deliveryServiceCode, invoiceNo) > 0) {
			return ShipResult.INVOICE_DUPLICATE;
		}

		// 결제완료 상태가 아니면(이미 출고됨 등) 0건
		if (vendorOrderMapper.updateOrderStatusToShipping(orderNo, sellerNo) != 1) {
			return ShipResult.FAILED;
		}

		vendorOrderMapper.insertDelivery(orderNo, deliveryServiceCode, invoiceNo);

		return ShipResult.SUCCESS;
	}

	/*
	 * 주문 상세 - 주문/배송지/배송이력 한 건 + 이 판매자 상품 라인 목록을 조립한다 (기존 VendorOrderDetailDAO.findByOrderNo).
	 * 이 판매자 상품이 들어 있지 않은 주문이면 null.
	 */
	public VendorOrderDetailDTO getOrderDetail(int orderNo, int sellerNo) {

		VendorOrderDetailDTO order = vendorOrderMapper.selectOrderDetail(orderNo, sellerNo);

		if (order == null) {
			return null;
		}

		order.getItems().addAll(vendorOrderMapper.selectOrderItems(orderNo, sellerNo));

		return order;
	}
}
