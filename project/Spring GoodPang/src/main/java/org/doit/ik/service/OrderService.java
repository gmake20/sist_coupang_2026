package org.doit.ik.service;

import java.util.List;
import org.doit.ik.domain.OrderItemVO;

public interface OrderService {
    int getOrderCount(int memberNo, String yearFilter);
    List<OrderItemVO> getOrderListPaged(int memberNo, String yearFilter, int page, int pageSize);
}