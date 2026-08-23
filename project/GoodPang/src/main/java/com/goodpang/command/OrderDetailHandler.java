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

        // 4. 이동할 View(JSP) 경로 반환
        return "/WEB-INF/views/order/orderDetail.jsp";
    }
}