package com.goodpang.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.goodpang.dao.OrderListDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderItemDTO;
import com.goodpang.dto.ReviewAvailableDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//http://localhost:8080/GoodPang/order/order_list
@WebServlet("/order/order_list")
public class OrderListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
        MemberDTO loginMember =
                LoginUtil.requireLogin(
                        request,
                        response
                );

        if (loginMember == null) {
            return;
        }

        int memberNo =
                loginMember.getMemberNo();

        ReviewDAO reviewDAO = new ReviewDAO();

        List<ReviewAvailableDTO> reviewList =
                reviewDAO.getReviewStatus(memberNo);

        request.setAttribute("reviewList", reviewList);
       
        OrderListDAO orderListDAO = new OrderListDAO();
        List<OrderItemDTO> orderList = null;
		try {
			orderList = orderListDAO.selectMyPageOrders(memberNo);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
        request.setAttribute("orderList", orderList);

        request.getRequestDispatcher("/order_list.jsp")
               .forward(request, response);
            
    }
}