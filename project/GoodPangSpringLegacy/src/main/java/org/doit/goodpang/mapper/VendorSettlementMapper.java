package org.doit.goodpang.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.VendorSettlementDTO;
import org.doit.goodpang.domain.VendorSettlementDetailDTO;
import org.springframework.stereotype.Repository;

/*
 * 판매자센터 정산관리(vendor/settlement.jsp) - 기존 GoodPang VendorSettlementDAO의 조회 쿼리.
 * 수수료율/정산주기는 실제 값이 아니라 가정치다 (VendorSettlementDTO 상단 주석 참고).
 */
@Repository
public interface VendorSettlementMapper {

	// 배송완료 주문을 배송완료일 기준 1주(월요일 시작) 단위로 묶은 정산 회차 목록 - 최근 회차순
	public List<VendorSettlementDTO> findBySellerNo(@Param("sellerNo") int sellerNo);

	// 정산상세 - periodStart(월요일)로 시작하는 정산기간에 포함된 주문라인 (배송완료일순)
	public List<VendorSettlementDetailDTO> findDetailBySellerNo(@Param("sellerNo") int sellerNo,
			@Param("periodStart") LocalDate periodStart);

}
