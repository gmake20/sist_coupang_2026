package org.doit.goodpang.service;

import java.util.List;
import org.doit.goodpang.domain.OrderItemDTO;
import org.doit.goodpang.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public int getOrderCount(int memberNo, String yearFilter) {
        return orderMapper.getOrderCount(memberNo, yearFilter);
    }

    @Override
    public List<OrderItemDTO> getOrderListPaged(int memberNo, String yearFilter, int page, int pageSize) {
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;
        return orderMapper.getOrderListPaged(memberNo, yearFilter, startRow, endRow);
    }

    @Override
    public List<OrderItemDTO> getOrderDetailList(int orderNo, int memberNo) {
        return orderMapper.getOrderDetailList(orderNo, memberNo);
    }
}