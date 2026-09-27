package org.doit.goodpang.service;

import java.util.List;

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
     * 주문 취소 비즈니스 로직 수행
     */
    @Transactional
    public Integer orderCancelAction(int orderNo, long memberNo, String cancelReason) {
        log.info("> OrderCancelService.orderCancelAction() call... orderNo: " + orderNo 
                + ", memberNo: " + memberNo + ", reason: " + cancelReason);
        
        // 주입받은 orderCancelMapper 인스턴스로 매퍼 메서드 호출
        return orderCancelMapper.orderCancelAction(orderNo, memberNo, cancelReason);
    }

	public  List<OrderDetailDTO> getCancelInfo(int orderNo) {
		
		return orderCancelMapper.selectOrderDetailList(orderNo);
	}
}