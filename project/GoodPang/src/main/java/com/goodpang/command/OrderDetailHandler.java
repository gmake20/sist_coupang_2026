package com.goodpang.command;


import java.util.List;

import com.goodpang.dao.OrderDetailDAO;
import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.servlet.CommandHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class OrderDetailHandler implements CommandHandler {

    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 1. 파라미터 수신 (주문번호)
        String orderNoParam = request.getParameter("orderNo");

        if (orderNoParam != null && !orderNoParam.isEmpty()) {
            int orderNo = Integer.parseInt(orderNoParam);

            // 2. DAO 호출하여 DB 데이터 가져오기
            OrderDetailDAO dao = new OrderDetailDAO();
            List<OrderDetailDTO> detailList = dao.getOrderDetailList(orderNo);

            // 3. JSP에 전달할 데이터 세팅
            request.setAttribute("detailList", detailList);
            request.setAttribute("orderNo", orderNo);
        }

        // 4. 이동할 View(JSP) 경로 반환
        return "/WEB-INF/views/order/orderDetail.jsp";
    }
}