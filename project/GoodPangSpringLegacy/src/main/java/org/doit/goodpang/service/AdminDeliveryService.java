package org.doit.goodpang.service;

import java.util.List;

import org.doit.goodpang.mapper.AdminDeliveryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/*
 * 관리자 배송 관리 - 여러 테이블을 한 트랜잭션으로 바꿔야 하는 작업만 여기 둔다.
 */
@Service
@RequiredArgsConstructor
public class AdminDeliveryService {

	private final AdminDeliveryMapper adminDeliveryMapper;

	/*
	 * 배송완료 처리 (기존 AdminDeliveryDAO.completeDelivery).
	 * DELIVERY와 ORDERS를 함께 '배송완료'로 바꾸고, 이 주문에 상품이 들어 있는 판매자 번호 목록을 돌려준다
	 * (판매자 로그 기록용 - 한 주문에 여러 판매자 상품이 섞여 있을 수 있어 판매자별로 로그를 남겨야 함).
	 * 배송중이 아닌 배송(이미 처리됨 등)이면 아무것도 바꾸지 않고 null.
	 * 기존 DAO의 setAutoCommit(false)/commit/rollback은 @Transactional이 대신한다 - 도중에 예외가 나면 전부 rollback.
	 */
	@Transactional
	public CompleteResult completeDelivery(int deliveryNo) {

		if (adminDeliveryMapper.updateDeliveryDone(deliveryNo) != 1) {
			return null;
		}

		Integer orderNo = adminDeliveryMapper.findOrderNoByDeliveryNo(deliveryNo);

		adminDeliveryMapper.updateOrderDone(orderNo);
		List<Integer> sellerNos = adminDeliveryMapper.findSellerNosByOrderNo(orderNo);

		return new CompleteResult(orderNo, sellerNos);
	}

	// completeDelivery()의 결과 - 배송완료된 주문번호와, 그 주문에 상품이 있는 판매자 번호들
	public static class CompleteResult {

		private final int orderNo;
		private final List<Integer> sellerNos;

		public CompleteResult(int orderNo, List<Integer> sellerNos) {
			this.orderNo = orderNo;
			this.sellerNos = sellerNos;
		}

		public int getOrderNo() {
			return orderNo;
		}

		public List<Integer> getSellerNos() {
			return sellerNos;
		}
	}
}
