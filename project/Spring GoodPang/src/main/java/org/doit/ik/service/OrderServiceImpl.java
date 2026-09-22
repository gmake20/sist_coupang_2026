package org.doit.ik.service;

import java.util.List;
import org.doit.ik.domain.OrderItemVO;
import org.doit.ik.mapper.OrderMapper;
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
    public List<OrderItemVO> getOrderListPaged(int memberNo, String yearFilter, int page, int pageSize) {
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;
        return orderMapper.getOrderListPaged(memberNo, yearFilter, startRow, endRow);
    }
}