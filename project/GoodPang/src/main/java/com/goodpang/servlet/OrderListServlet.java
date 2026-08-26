package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import com.goodpang.dao.OrderListDAO;
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
    	
		 //int memberNo = 81; 
         
    	 // 로그인 확인
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

        System.out.println("[DEBUG OrderListServlet] === 주문 리스트 정보 조회 시작 ===" + memberNo );
     
        
       
        
     // 3. OrderListDAO를 사용하여 로그인한 회원의 마이페이지 주문 목록 조회[cite: 1]
        OrderListDAO orderListDAO = new OrderListDAO();
        List<OrderItemDTO> orderList = null;
		try {
			orderList = orderListDAO.selectMyPageOrders(memberNo);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // 4. 조회한 결과를 request 영역에 저장
        request.setAttribute("orderList", orderList);

        // 5. 주문 내역 JSP 화면으로 포워딩
        request.getRequestDispatcher("/order_list.jsp")
               .forward(request, response);
            
    }
}