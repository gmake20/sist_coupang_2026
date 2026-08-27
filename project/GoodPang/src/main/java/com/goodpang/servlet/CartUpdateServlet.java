package com.goodpang.servlet;

import java.io.IOException;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart/update")
public class CartUpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CartDAO cartDAO = new CartDAO();

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();

		MemberDTO loginMember =
				(MemberDTO) session.getAttribute("loginMember");

		String optionIdParam =
				request.getParameter("optionId");

		String quantityParam =
				request.getParameter("quantity");

		if (optionIdParam == null
				|| optionIdParam.isBlank()
				|| quantityParam == null
				|| quantityParam.isBlank()) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 장바구니 정보입니다."
					);

			return;
		}

		int optionId;
		int quantity;

		try {

			optionId =
					Integer.parseInt(optionIdParam);

			quantity =
					Integer.parseInt(quantityParam);

		} catch (NumberFormatException e) {

			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"잘못된 장바구니 정보입니다."
					);

			return;
		}

		if (quantity < 1) {
			quantity = 1;
		}

		// 로그인 회원
		if (loginMember != null) {

			cartDAO.updateQuantity(
					loginMember.getMemberNo(),
					optionId,
					quantity
					);

		}

		// 비로그인 회원
		else {

			@SuppressWarnings("unchecked")
			Map<Integer, Integer> guestCart =
			(Map<Integer, Integer>)
			session.getAttribute("guestCart");

			if (guestCart != null
					&& guestCart.containsKey(optionId)) {

				guestCart.put(
						optionId,
						quantity
						);

			}

		}

		response.sendRedirect(
				request.getContextPath() + "/cart"
				);

	}
}