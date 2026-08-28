package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.goodpang.dao.OrderCancelDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.OrderDetailDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/cancel_history")
public class CancelHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 로그인 인증 확인
        MemberDTO loginMember = LoginUtil.requireLogin(request, response);
        if (loginMember == null) {
            return;
        }
        
        int memberNo =
                loginMember.getMemberNo();
        
        System.out.println("[DEBUG CancelHistoryServlet] === 취소/반품 내역 조회 시작 ===");
        System.out.println("[DEBUG CancelHistoryServlet] 로그인 memberNo: " + memberNo);

        // 2. 파라미터 및 데이터 초기화 (NullPointerException 및 미초기화 에러 방지)
        List<OrderDetailDTO> cancelList = new ArrayList<>();

        try {
            OrderCancelDAO dao = new OrderCancelDAO();
            cancelList = dao.getCancelHistoryList(memberNo);

            int resultCount = (cancelList != null) ? cancelList.size() : 0;
            System.out.println("[DEBUG CancelHistoryServlet] DB 취소 내역 조회 완료건: " + resultCount);

        } catch (Exception e) {
            System.err.println("[ERROR CancelHistoryServlet] DB 취소 내역 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. JSP 바인딩
        request.setAttribute("cancelList", cancelList);

        // 4. cancel_history.jsp 포워딩
        request.getRequestDispatcher("/cancel_history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}