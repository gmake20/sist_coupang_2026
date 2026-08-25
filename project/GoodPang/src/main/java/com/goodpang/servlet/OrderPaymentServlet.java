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

        String orderNoParam =
                request.getParameter("orderNo");

        String addressNoParam =
                request.getParameter("addressNo");

        if (orderNoParam == null
                || orderNoParam.isBlank()
                || addressNoParam == null
                || addressNoParam.isBlank()) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/order/payment"
            );

            return;
        }


        Connection conn = null;

        try {

            int orderNo =
                    Integer.parseInt(orderNoParam);

            int addressNo =
                    Integer.parseInt(addressNoParam);


            conn =
                    ConnectionProvider.getConnection();

            conn.setAutoCommit(false);


            OrderDAO dao =
                    new OrderDAO();

            int priceResult =
                    dao.updateTotalPrice(
                            conn,
                            orderNo
                    );

            if (priceResult != 1) {

                throw new Exception(
                        "주문 금액 수정 실패"
                );
            }

            int addressResult =
                    dao.updateOrderAddress(
                            conn,
                            orderNo,
                            addressNo
                    );

            if (addressResult != 1) {

                throw new Exception(
                        "배송지 번호 저장 실패"
                );
            }

            int deliveryResult =
                    dao.insertOrderDelivery(
                            conn,
                            orderNo,
                            addressNo
                    );

            if (deliveryResult != 1) {

                throw new Exception(
                        "주문 배송지 저장 실패"
                );
            }

            int statusResult =
                    dao.updateOrderStatus(
                            conn,
                            orderNo,
                            "PAID"
                    );

            if (statusResult != 1) {

                throw new Exception(
                        "주문 상태 변경 실패"
                );
            }

            conn.commit();


            response.sendRedirect(
                    request.getContextPath()
                    + "/order/complete?orderNo="
                    + orderNo
            );


        } catch (NumberFormatException e) {

            rollback(conn);

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 주문번호 또는 배송지번호입니다."
            );


        } catch (SQLIntegrityConstraintViolationException e) {

            rollback(conn);

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath()
                    + "/order/already-completed"
            );


        } catch (Exception e) {

            rollback(conn);

            e.printStackTrace();

            throw new ServletException(
                    "결제 처리 중 오류가 발생했습니다.",
                    e
            );


        } finally {

            if (conn != null) {

                try {

                    conn.setAutoCommit(true);

                    conn.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void rollback(Connection conn) {

        if (conn != null) {

            try {
                conn.rollback();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}