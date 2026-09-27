package org.doit.goodpang.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.VendorOrderListDTO;
import org.doit.goodpang.domain.VendorOrderStatSummaryDTO;
import org.springframework.stereotype.Repository;

/*
 * 판매자센터 주문/배송 관리(vendor/order.jsp) - 기존 GoodPang VendorOrderListDAO의 조회 쿼리.
 * 검색 필터(startDate/endDate/orderStatus/deliveryStatus/paymentStatus)는 전부 선택사항(null이면 조건 안 붙임).
 */
@Repository
public interface VendorOrderMapper {

	// 판매자의 상품이 포함된 주문 라인(ORDER_DETAIL) 목록 - 최근 주문순. offset = (page - 1) * pageSize
	public List<VendorOrderListDTO> findBySellerNo(@Param("sellerNo") int sellerNo,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("orderStatus") String orderStatus, @Param("deliveryStatus") String deliveryStatus,
			@Param("paymentStatus") String paymentStatus,
			@Param("offset") int offset, @Param("pageSize") int pageSize);

	// 페이지네이션용 총 개수 - 필터 조건은 findBySellerNo와 같음(XML의 orderFilters 공유)
	public int countBySellerNo(@Param("sellerNo") int sellerNo,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("orderStatus") String orderStatus, @Param("deliveryStatus") String deliveryStatus,
			@Param("paymentStatus") String paymentStatus);

	// 상단 통계 카드(출고대기/배송중/배송완료/오늘 배송완료)
	public VendorOrderStatSummaryDTO countStats(@Param("sellerNo") int sellerNo);

}
