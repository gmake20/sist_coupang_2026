package org.doit.goodpang.controller;

import java.util.List;

import org.doit.goodpang.domain.OrderItemDTO;
import org.doit.goodpang.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. 주문 목록 페이지 (/order/order_list)
    @GetMapping("/order_list")
    public String getOrderList(
    		Authentication authentication,
            @RequestParam(value = "year", defaultValue = "recent") String yearFilter,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        // 세션 또는 스프링 시큐리티 처리 전 임시 테스트용 회원번호
        int memberNo = 1; 

        int pageSize = 5;
        int totalCount = orderService.getOrderCount(memberNo, yearFilter);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<OrderItemDTO> orderList = orderService.getOrderListPaged(memberNo, yearFilter, page, pageSize);

        model.addAttribute("orderList", orderList);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("curPage", page);
        model.addAttribute("totalPages", totalPages);

        return "order.order_list"; // /WEB-INF/views/order/order_list.jsp
    }

    // 2. 주문 상세 페이지 (/order/order_detail)
    @GetMapping("/order_detail")
    public String getOrderDetail(
            @RequestParam("orderNo") int orderNo,
            Model model) {

        int memberNo = 1; // 임시 회원번호

        List<OrderItemDTO> detailList = orderService.getOrderDetailList(orderNo, memberNo);
        OrderItemDTO orderInfo = null;

        if (detailList != null && !detailList.isEmpty()) {
            orderInfo = detailList.get(0); // 공통 주문정보용 대표 객체
        }

        model.addAttribute("detailList", detailList);
        model.addAttribute("orderInfo", orderInfo);

        return "order.order_detail"; // /WEB-INF/views/order/order_detail.jsp
    }
}