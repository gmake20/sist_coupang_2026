package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.OrderDetailDAO;
import com.goodpang.dto.OrderDetailDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/order_detail")
// http://localhost:8080/GoodPang/order/order_detail
public class OrderDetailServlet extends HttpServlet {
   
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

      // String orderNoParam = request.getParameter("orderNo");
    	String orderNoParam ="1001";
        
        System.out.println("[DEBUG OrderDetailHandler] === 주문 상세 정보 조회 시작 ===");
        System.out.println("[DEBUG OrderDetailHandler] 전달받은 orderNoParam: " + orderNoParam);
        try {
	        if (orderNoParam != null && !orderNoParam.isEmpty()) {
	            int orderNo = Integer.parseInt(orderNoParam);
	
	            // 2. DAO 호출하여 DB 데이터 가져오기
	            OrderDetailDAO dao = new OrderDetailDAO();
	            List<OrderDetailDTO> detailList = dao.getOrderDetailList(orderNo);
	            
	            int resultCount = (detailList != null) ? detailList.size() : 0;
	            System.out.println("[DEBUG OrderDetailHandler] DB 조회 완료건:"+ resultCount);
	
	            // 3. JSP에 전달할 데이터 세팅
	            request.setAttribute("detailList", detailList);
	            request.setAttribute("orderNo", orderNo);
	            
	        }
        
	    } catch (NumberFormatException e) {
	        System.err.println("[ERROR] 주문번호 숫자 변환 실패 (잘못된 파라미터): " + orderNoParam);
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.err.println("[ERROR] DB 조회 중 예외 발생: " + e.getMessage());
	        e.printStackTrace();
	    }
    
        request.getRequestDispatcher(
                "/order_detail.jsp"
        ).forward(request, response);
    }
}