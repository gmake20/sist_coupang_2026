package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;

import com.goodpang.dao.OrderDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.LoginUtil;

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

		MemberDTO member =
				LoginUtil.requireLogin(
						request,
						response
						);

		if (member == null) {
			return;
		}

		int memberNo =
				member.getMemberNo();

		String checkoutNoParam =
				request.getParameter("checkoutNo");

		String addressNoParam =
				request.getParameter("addressNo");

		String paymentMethod =
				request.getParameter("paymentMethod");

		String bankCode =
				request.getParameter("bankCode");

		String cardCompany =
				request.getParameter("cardCompany");
		
		
		Integer paymentMethodNo = null;


		if ("BANK".equals(paymentMethod)) {

		    String paymentMethodNoParam =
		            request.getParameter(
		                    "paymentMethodNo"
		            );

		    if (paymentMethodNoParam == null
		            || paymentMethodNoParam.isBlank()) {

		        response.sendError(
		                HttpServletResponse.SC_BAD_REQUEST,
		                "계좌를 선택해주세요."
		        );

		        return;
		    }
		
		    paymentMethodNo =
		            Integer.valueOf(
		                    paymentMethodNoParam
		            );

		} else if ("CARD".equals(paymentMethod)) {

		    String paymentMethodNoParam =
		            request.getParameter(
		                    "cardPaymentMethodNo"
		            );

		    if (paymentMethodNoParam == null
		            || paymentMethodNoParam.isBlank()) {

		        response.sendError(
		                HttpServletResponse.SC_BAD_REQUEST,
		                "카드를 선택해주세요."
		        );

		        return;
		    }

		    paymentMethodNo =
		            Integer.valueOf(
		                    paymentMethodNoParam
		            );

		} else if ("COUPAY_MONEY".equals(
		        paymentMethod)) {

		    // 쿠페이머니는 PAYMENT_METHOD 테이블의
		    // 등록 카드/계좌가 아니므로 null
		    paymentMethodNo = null;

		} else {

		    response.sendError(
		            HttpServletResponse.SC_BAD_REQUEST,
		            "지원하지 않는 결제수단입니다."
		    );

		    return;
		}


		if (checkoutNoParam == null
				|| checkoutNoParam.isBlank()
				|| addressNoParam == null
				|| addressNoParam.isBlank()
				|| paymentMethod == null || paymentMethod.isBlank()
				) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"결제 정보가 올바르지 않습니다."
					);

			return;
		}

		Connection conn = null;
		try {
			int checkoutNo =
					Integer.parseInt(
							checkoutNoParam
							);
			int addressNo =
					Integer.parseInt(
							addressNoParam
							);
			conn =
					ConnectionProvider
					.getConnection();
			conn.setAutoCommit(false);

			OrderDAO dao =
					new OrderDAO();

			boolean checkoutExists =
					dao.existsCheckout(
							conn,
							checkoutNo,
							memberNo
							);
			if (!checkoutExists) {

				throw new Exception(
						"존재하지 않거나 접근할 수 없는 checkout입니다."
						);
			}
			boolean addressExists =
					dao.existsAddress(
							conn,
							addressNo,
							memberNo
							);
			if (!addressExists) {

				throw new Exception(
						"잘못된 배송지입니다."
						);
			}
			int orderAddressNo =
					dao.insertOrderAddress(
							conn,
							addressNo,
							memberNo
							);
			int orderNo =
			        dao.insertOrderFromCheckout(
			                conn,
			                checkoutNo,
			                memberNo,
			                orderAddressNo,
			                paymentMethod,
			                paymentMethodNo
			        );


			if (orderNo <= 0) {

				throw new Exception(
						"주문 생성 실패"
						);
			}

			int detailCount =
					dao.insertOrderDetailsFromCheckout(
							conn,
							checkoutNo,
							orderNo
							);
			if (detailCount <= 0) {

				throw new Exception(
						"주문 상품 저장 실패"
						);
			}
			dao.deleteCheckoutItems(
					conn,
					checkoutNo
					);
			dao.deleteCheckout(
					conn,
					checkoutNo,
					memberNo
					);
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
					"잘못된 checkout 번호 또는 배송지 번호입니다."
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

	private void rollback(
			Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
