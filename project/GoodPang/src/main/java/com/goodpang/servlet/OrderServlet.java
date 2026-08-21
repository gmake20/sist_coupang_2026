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
// http://localhost:8080/GoodPang/order/payment
public class OrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int memberNo = 1;
        int orderNo = 100;
		
        OrderDAO dao = new OrderDAO();

        AddressDTO address =
                dao.getAddress(memberNo);
        
        List<AddressDTO> addressList =
        	    orderDAO.getAddressList(memberNo);
        
        OrderSummaryDTO summary =
                orderDAO.getOrderSummary(orderNo);
        
//        List<OrderItemDTO> orderItems =
//                dao.getOrderItems(orderNo);

        request.setAttribute("address", address);
        
        request.setAttribute(
        	    "addressList",
        	    addressList
        	);
		/*
		 * request.setAttribute("orderItems", orderItems);
		 */
        request.setAttribute("summary", summary);
        
        request.setAttribute("orderNo", orderNo);

        request.getRequestDispatcher(
                "/coupang_order_payment.jsp"
        ).forward(request, response);
    }
}