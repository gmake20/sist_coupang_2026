package org.doit.goodpang.service;

import java.util.List;
import org.doit.goodpang.domain.OrderItemDTO;

public interface OrderService {
    int getOrderCount(int memberNo, String yearFilter);
    List<OrderItemDTO> getOrderListPaged(int memberNo, String yearFilter, int page, int pageSize);
    List<OrderItemDTO> getOrderDetailList(int orderNo, int memberNo);
}