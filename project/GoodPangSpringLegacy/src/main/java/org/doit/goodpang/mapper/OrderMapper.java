package org.doit.goodpang.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.OrderItemDTO;

@Mapper
public interface OrderMapper {

    // 1. 연도/기간 필터에 따른 회원 총 주문 건수 조회
    int getOrderCount(
        @Param("memberNo") int memberNo, 
        @Param("yearFilter") String yearFilter
    );

    // 2. 연도 필터링 및 페이징 목록 조회
    List<OrderItemDTO> getOrderListPaged(
        @Param("memberNo") int memberNo, 
        @Param("yearFilter") String yearFilter, 
        @Param("startRow") int startRow, 
        @Param("endRow") int endRow
    );
    
    // 3. 특정 주문 상세 목록 조회 (order_detail 기능 확장용)
    List<OrderItemDTO> getOrderDetailList(
        @Param("orderNo") int orderNo, 
        @Param("memberNo") int memberNo
    );
}