package org.doit.goodpang.service;

import org.doit.goodpang.mapper.VendorMapper;
import org.doit.goodpang.mapper.VendorProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/*
 * 판매자 계정 처리 - 여러 테이블을 한 트랜잭션으로 바꿔야 하는 작업만 여기 둔다.
 */
@Service
@RequiredArgsConstructor
public class VendorAccountService {

	private final VendorMapper vendorMapper;
	private final VendorProductMapper vendorProductMapper;

	/*
	 * 판매자 자진 탈퇴 - SELLER 상태를 '탈퇴'로 바꾸고, 이 판매자의 상품을 전부 숨김 처리해서 고객 화면에서도 즉시 사라지게 한다.
	 * 기존 VendorWithdrawServlet은 두 UPDATE를 따로 실행해서, 상품 숨김이 실패해도 탈퇴만 된 채로 남을 수 있었다.
	 * 여기서는 한 트랜잭션으로 묶어서 둘 다 되거나 둘 다 안 되게 한다.
	 * 승인 대기 중인 주문/정산 미지급 여부는 확인하지 않음 (2026-09-05 확정, 지금 단계에서는 막지 않기로 함).
	 */
	@Transactional
	public void withdraw(int sellerNo) {
		vendorMapper.updateApprovalStatus(sellerNo, "탈퇴", null);
		vendorProductMapper.hideAllBySeller(sellerNo);
	}
}
