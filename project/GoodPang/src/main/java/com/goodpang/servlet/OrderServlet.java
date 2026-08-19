package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderDAO;
import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.OrderItemDTO;
import com.goodpang.dto.OrderSummaryDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/payment")
public class OrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int memberNo = 1;
        int orderNo = 100;
		/*
		 * AddressDTO address = orderDAO.getAddress(memberNo);
		 * 
		 * List<OrderItemDTO> orderItems = orderDAO.getOrderItems(orderNo);
		 * 
		 * OrderSummaryDTO summary = orderDAO.getOrderSummary(orderNo);
		 * 
		 * request.setAttribute("address", address); request.setAttribute("orderItems",
		 * orderItems); request.setAttribute("summary", summary);
		 * 
		 * request.getRequestDispatcher( "/coupang_order_payment.jsp" ).forward(request,
		 * response);
		 */
        OrderDAO dao = new OrderDAO();

        AddressDTO address =
                dao.getAddress(memberNo);

        List<OrderItemDTO> orderItems =
                dao.getOrderItems(orderNo);

        OrderSummaryDTO summary =
                dao.getOrderSummary(orderNo);

        request.setAttribute("address", address);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("summary", summary);

        request.getRequestDispatcher(
                "/coupang_order_payment.jsp"
        ).forward(request, response);
    }
}