package org.doit.ik.controller;

import java.util.List;
import org.doit.ik.domain.OrderItemVO;
import org.doit.ik.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order/*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/order_list")
    public String getOrderList(
            @RequestParam(value = "year", defaultValue = "recent") String yearFilter,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        // 세션 또는 시큐리티 연동 전 테스트용 임시 회원번호 (로그인 회원 PK)
        int memberNo = 1; 

        int pageSize = 5;
        int totalCount = orderService.getOrderCount(memberNo, yearFilter);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<OrderItemVO> orderList = orderService.getOrderListPaged(memberNo, yearFilter, page, pageSize);

        model.addAttribute("orderList", orderList);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("curPage", page);
        model.addAttribute("totalPages", totalPages);

        return "order/order_list"; // WEB-INF/views/order/order_list.jsp 호출
    }
}