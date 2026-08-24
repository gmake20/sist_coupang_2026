package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderItemDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//http://localhost:8080/GoodPang/order/order_list
@WebServlet("/order/order_list")
public class OrderListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
		/* int memberNo = 1; */
         
        HttpSession session = request.getSession();
        
        // 세션에서 로그인한 회원번호 추출
       // Integer memberNo = (Integer) session.getAttribute("memberNo");

        // 미로그인 시 처리
//        if (memberNo == null) {
//          response.sendRedirect(request.getContextPath() + "/member/login.do");
//            return;
//        }
        
        MemberDTO loginMember =
                LoginUtil.requireLogin(
                        request,
                        response
                );
        
        if (loginMember == null) {
            return;
        }
    	
        int memberNo = loginMember.getMemberNo();

        
        // DAO 호출 시 세션에서 꺼낸 memberNo 전달
        OrderDAO dao = new OrderDAO();
        List<OrderItemDTO> orderList = dao.getOrderListByMemberNo(memberNo);

        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("/order_list.jsp")
               .forward(request, response);
        
    }
}