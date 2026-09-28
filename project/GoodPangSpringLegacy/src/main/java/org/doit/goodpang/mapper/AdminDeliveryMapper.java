package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.AdminDeliveryDTO;
import org.springframework.stereotype.Repository;

/*
 * 관리자 배송 관리 - 기존 GoodPang AdminDeliveryDAO.
 * (배송완료 처리는 AdminDeliveryService.completeDelivery에서 아래 쿼리들을 한 트랜잭션으로 호출)
 */
@Repository
public interface AdminDeliveryMapper {

	// 배송중인 전체 주문 - 배송 시작일 최신순
	public List<AdminDeliveryDTO> findShipping();

	// DELIVERY 배송중 → 배송완료(배송 완료일 = 지금). 배송중이 아니면(이미 처리됨 등) 0
	public int updateDeliveryDone(@Param("deliveryNo") int deliveryNo);

	// 배송번호로 주문번호
	public Integer findOrderNoByDeliveryNo(@Param("deliveryNo") int deliveryNo);

	// ORDERS 배송중 → 배송완료
	public int updateOrderDone(@Param("orderNo") int orderNo);

	// 이 주문에 상품이 들어 있는 판매자 번호들 (한 주문에 여러 판매자 상품이 섞일 수 있음 - 판매자 로그용)
	public List<Integer> findSellerNosByOrderNo(@Param("orderNo") int orderNo);

}
