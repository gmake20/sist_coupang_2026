package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;

import com.goodpang.dao.OrderDAO;
import com.goodpang.util.ConnectionProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/order/checkout")
public class OrderPaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String orderNoParam = request.getParameter("orderNo");
        String addressNoParam = request.getParameter("addressNo");

        if (orderNoParam == null
                || orderNoParam.isBlank()
                || addressNoParam == null
                || addressNoParam.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "주문번호 또는 배송지번호가 없습니다."
            );

            return;
        }

        Connection conn = null;

        try {

            int orderNo = Integer.parseInt(orderNoParam);
            int addressNo = Integer.parseInt(addressNoParam);

            conn = ConnectionProvider.getConnection();

            // 트랜잭션 시작
            conn.setAutoCommit(false);

            OrderDAO dao = new OrderDAO();

            int result = dao.insertOrderDelivery(
                    conn,
                    orderNo,
                    addressNo
            );
            if (result != 1) {
                throw new RuntimeException("배송지 저장 실패");
            }

            conn.commit();

            response.sendRedirect(
                    request.getContextPath()
                    + "/order/complete?orderNo="
                    + orderNo
            );

        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }
            response.sendRedirect(
                    request.getContextPath()
                    + "/order/already-completed"
            );
            return;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 주문번호 또는 배송지번호입니다."
            );
            
            return;
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }
            throw new ServletException(
                    "결제 처리 중 오류가 발생했습니다.",
                    e
            );
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}