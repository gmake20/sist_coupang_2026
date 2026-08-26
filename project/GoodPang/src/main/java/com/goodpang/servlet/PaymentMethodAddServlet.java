package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;

import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/payment-method/add")
public class PaymentMethodAddServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		MemberDTO member =
				LoginUtil.requireLogin(request, response);

		if (member == null) {
			return;
		}

		int memberNo = member.getMemberNo();

		String checkoutNo =
				request.getParameter("checkoutNo");

		String paymentType =
				request.getParameter("paymentType");

		String bankCode =
				request.getParameter("bankCode");

		String accountNumber =
				request.getParameter("accountNumber");

		String accountHolder =
				request.getParameter("accountHolder");

		String cardCompany =
				request.getParameter("cardCompany");

		String cardNumber1 =
				request.getParameter("cardNumber1");

		String cardNumber2 =
				request.getParameter("cardNumber2");

		String cardNumber3 =
				request.getParameter("cardNumber3");

		String cardNumber4 =
				request.getParameter("cardNumber4");

		String paymentDefaultParam =
				request.getParameter("paymentDefault");

		if (checkoutNo == null
				|| checkoutNo.isBlank()) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"checkout 번호가 없습니다."
			);
			return;
		}

		if (paymentType == null
				|| paymentType.isBlank()) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"결제수단 종류가 없습니다."
			);
			return;
		}

		boolean paymentDefault =
				"Y".equals(paymentDefaultParam);

		PaymentMethodDTO dto =
				new PaymentMethodDTO();

		dto.setMemberNo(memberNo);
		dto.setPaymentType(paymentType);
		dto.setPaymentDefault(paymentDefault);

		if ("BANK_TRANSFER".equals(paymentType)) {

			if (bankCode == null
					|| bankCode.isBlank()
					|| accountNumber == null
					|| accountNumber.isBlank()
					|| accountHolder == null
					|| accountHolder.isBlank()) {

				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"계좌 정보를 입력해주세요."
				);
				return;
			}

			accountNumber =
					accountNumber.replace("-", "");

			if (!accountNumber.matches("[0-9]+")) {
				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"계좌번호는 숫자만 입력해주세요."
				);
				return;
			}

			if (accountNumber.length() < 4) {
				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"계좌번호가 올바르지 않습니다."
				);
				return;
			}

			String accountLast4 =
					accountNumber.substring(
							accountNumber.length() - 4
					);

			dto.setBankCode(bankCode);
			dto.setAccountLast4(accountLast4);
			dto.setAccountHolder(accountHolder);

		} else if ("CARD".equals(paymentType)) {

			if (cardCompany == null
					|| cardCompany.isBlank()) {

				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"카드사를 선택해주세요."
				);
				return;
			}

			String cardNumber =
					value(cardNumber1)
					+ value(cardNumber2)
					+ value(cardNumber3)
					+ value(cardNumber4);

			if (!cardNumber.matches("[0-9]{16}")) {
				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"카드번호 16자리를 정확하게 입력해주세요."
				);
				return;
			}

			String cardLast4 =
					cardNumber.substring(12);

			dto.setCardCompany(cardCompany);
			dto.setCardLast4(cardLast4);

		} else {
			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"지원하지 않는 결제수단입니다."
			);
			return;
		}

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			PaymentMethodDAO dao =
					new PaymentMethodDAO();

			if (paymentDefault) {
				dao.clearDefault(
						conn,
						memberNo,
						paymentType
				);
			}

			int result;

			if ("BANK_TRANSFER".equals(paymentType)) {
				result =
						dao.insertBankAccount(
								conn,
								dto
						);
			} else {
				result =
						dao.insertCard(
								conn,
								dto
						);
			}

			if (result <= 0) {
				throw new Exception(
						"결제수단 등록 실패"
				);
			}

			conn.commit();

			response.sendRedirect(
					request.getContextPath()
					+ "/order/payment?checkoutNo="
					+ checkoutNo
			);

		} catch (Exception e) {

			if (conn != null) {
				try {
					conn.rollback();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			throw new ServletException(
					"결제수단 등록 중 오류가 발생했습니다.",
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

	private String value(String value) {
		return value == null
				? ""
				: value.trim();
	}
}