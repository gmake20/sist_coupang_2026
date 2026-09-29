package org.doit.goodpang.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.doit.goodpang.domain.OrderDetailDTO;
import org.doit.goodpang.mapper.OrderCancelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
@RequiredArgsConstructor
public class OrderCancelService {
    
    private final OrderCancelMapper orderCancelMapper;
    
    
    /**
     * 1. 주문 취소 비즈니스 로직 수행 (트랜잭션 보장)
     */
    @Transactional // 하나라도 에러 나면 전체 롤백[cite: 3]
    public Integer orderCancelAction(int orderNo, long memberNo, String cancelReason) {
        log.info("> OrderCancelService.orderCancelAction() call... orderNo: " + orderNo 
                + ", memberNo: " + memberNo + ", reason: " + cancelReason);


      // [Step 1] ORDERS 테이블 상태 변경 ('주문취소')[cite: 1]
        int updatedRows = orderCancelMapper.orderCancelAction(orderNo, memberNo, cancelReason);
        log.info("> [Step 1 결과] updatedRows: " + updatedRows);
        if (updatedRows > 0) {
            // [Step 2] 취소할 상품 상세 목록 조회[cite: 1]
            List<OrderDetailDTO> details = orderCancelMapper.selectOrderDetailList(orderNo);

            // [Step 3] PRODUCT_RETURN 테이블에 상품별 반품 내역 INSERT[cite: 1]
            for (OrderDetailDTO detail : details) {
                orderCancelMapper.insertProductReturn(
                    detail.getQuantity(),       // returnQty
                    cancelReason,               // returnReason
                    detail.getItemPrice(),      // refundAmount
                    detail.getOrderDetailNo()   // orderDetailNo
                );
            }

            // [Step 4] 오라클 재고 복원 프로시저(PRC_ORDER_STOCK_IN) 호출[cite: 1]
            Map<String, Object> params = new HashMap<>();
            params.put("orderNo", orderNo);
            orderCancelMapper.restoreStock(params);

            return updatedRows; // 성공 시 1 반환 (컨트롤러로 전달)[cite: 1]
        }

        return 0; // 조건 실패 시 0 반환
    }
    

    /**
     * 2. 취소 신청 Form 화면용 단건/상품 정보 조회[cite: 8]
     */
    public List<OrderDetailDTO> getCancelInfo(int orderNo) {
        return orderCancelMapper.selectOrderDetailList(orderNo);
    }

	// 취소내역 조회
    public int getCancelHistoryCount(long memberNo) {
        return orderCancelMapper.getCancelHistoryCount(memberNo);
    }
    
    /**
     * 회원의 취소/반품 내역 페이징 목록 조회[cite: 4, 5]
     */
    public List<OrderDetailDTO> getCancelHistoryPaged(long memberNo, int curPage, int pageSize) {
        int startRow = (curPage - 1) * pageSize + 1;
        int endRow = curPage * pageSize;
        return orderCancelMapper.getCancelHistoryPaged(memberNo, startRow, endRow);
    }
    // 주문 상세 내역 조회

    public List<OrderDetailDTO> getCancelDetailList(int orderNo) {
        return orderCancelMapper.getCancelDetailList(orderNo);
    }
}