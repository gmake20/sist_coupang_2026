package org.doit.goodpang.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.VendorDeliveryDTO;
import org.doit.goodpang.domain.VendorOrderDetailDTO;
import org.doit.goodpang.domain.VendorOrderItemDTO;
import org.doit.goodpang.domain.VendorOrderListDTO;
import org.doit.goodpang.domain.VendorOrderStatSummaryDTO;
import org.doit.goodpang.domain.VendorReturnDTO;
import org.doit.goodpang.domain.VendorShippingDTO;
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

	// 배송 관리 - 이 판매자 상품이 포함된 배송중 주문 (배송 시작일 최신순)
	public List<VendorDeliveryDTO> findShippingBySellerNo(@Param("sellerNo") int sellerNo);

	// 취소/반품/교환 관리 - 이 판매자 상품이 포함된 신청 목록 (최근 신청순)
	public List<VendorReturnDTO> findReturnsBySellerNo(@Param("sellerNo") int sellerNo);

	// 출고/운송장 관리 - 이 판매자 상품이 포함된 결제완료(출고 대기) 주문 (오래된 주문순)
	public List<VendorShippingDTO> findWaitingBySellerNo(@Param("sellerNo") int sellerNo);

	// ===== 출고 처리 - VendorOrderService.shipOrder에서 한 트랜잭션으로 호출 =====

	// 이 판매자가 이 주문에 등록한 상품의 택배사 코드. 이 판매자 상품이 없는 주문이면 null
	public String findDeliveryServiceCode(@Param("orderNo") int orderNo, @Param("sellerNo") int sellerNo);

	// 같은 택배사에 같은 송장번호가 이미 있으면 1 이상
	public int countDeliveryByInvoice(@Param("deliveryServiceCode") String deliveryServiceCode,
			@Param("invoiceNo") String invoiceNo);

	// 결제완료 → 배송중. 조건(결제완료 + 이 판매자 상품 포함)에 안 맞으면 0
	public int updateOrderStatusToShipping(@Param("orderNo") int orderNo, @Param("sellerNo") int sellerNo);

	// DELIVERY 행 생성 (배송중, 배송 시작일 = 지금)
	public int insertDelivery(@Param("orderNo") int orderNo, @Param("deliveryServiceCode") String deliveryServiceCode,
			@Param("invoiceNo") String invoiceNo);

	// ===== 주문 상세 - VendorOrderService.getOrderDetail에서 조립 =====

	// 주문 + 주문자 + 배송지 + 최근 배송 이력. 이 판매자 상품이 없는 주문이면 null
	public VendorOrderDetailDTO selectOrderDetail(@Param("orderNo") int orderNo, @Param("sellerNo") int sellerNo);

	// 이 주문 중 이 판매자 상품 라인만
	public List<VendorOrderItemDTO> selectOrderItems(@Param("orderNo") int orderNo, @Param("sellerNo") int sellerNo);

}
