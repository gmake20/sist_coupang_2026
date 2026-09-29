package org.doit.goodpang.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.OrderDetailDTO;

public interface OrderCancelMapper {

    // 1. 주문 상태 변경
    int orderCancelAction(
            @Param("orderNo") int orderNo,
            @Param("memberNo") long memberNo,
            @Param("returnReason") String returnReason
    );

    // 2. 주문 상세 조회
    List<OrderDetailDTO> selectOrderDetailList(@Param("orderNo") int orderNo);

    // 3. 반품/환불 등록
    int insertProductReturn(
            @Param("returnQty") int returnQty,
            @Param("returnReason") String returnReason,
            @Param("refundAmount") long refundAmount,
            @Param("orderDetailNo") long orderDetailNo
    );
 // 4. ★ 재고 복원 프로시저(PRC_ORDER_STOCK_IN) 호출
    void restoreStock(Map<String, Object> params);
    
 // cancel_history

   int getCancelHistoryCount(@Param("memberNo") long memberNo);

    List<OrderDetailDTO> getCancelHistoryPaged(
        @Param("memberNo") long memberNo,
        @Param("startRow") int startRow,
        @Param("endRow") int endRow
    );
    
    //cancel_detail

    List<OrderDetailDTO> getCancelDetailList(@Param("orderNo") int orderNo);




}