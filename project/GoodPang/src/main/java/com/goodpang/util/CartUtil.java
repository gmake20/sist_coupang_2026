package com.goodpang.util;

import java.util.List;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.CartItemDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CartUtil {

	public static void refreshCartSession(
			HttpServletRequest request,
			int memberNo) {

		CartDAO cartDAO = new CartDAO();

		List<CartItemDTO> cartItems =
				cartDAO.getCartItems(memberNo);

		List<CartItemDTO> cartPreviewItems =
				cartDAO.getCartItems(memberNo);
		
		System.out.println(
				"memberNo = " + memberNo
		);

		System.out.println(
				"cartItems size = " + cartItems.size()
		);

		request.getSession().setAttribute(
				"cartPreviewItems",
				cartPreviewItems
		);

		request.getSession().setAttribute(
				"cartCount",
				cartPreviewItems.size()
		);
	}
	
	public static void refreshGuestCartSession(
			HttpServletRequest request,
			Map<Integer, Integer> guestCart) {

		HttpSession session = request.getSession();

		if (guestCart == null || guestCart.isEmpty()) {
			session.setAttribute("cartCount", 0);
			session.removeAttribute("cartPreviewItems");
			return;
		}

		session.setAttribute("cartCount", guestCart.size());
	}
}