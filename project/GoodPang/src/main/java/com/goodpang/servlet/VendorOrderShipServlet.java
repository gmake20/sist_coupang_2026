package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.VendorOrderListDAO;
import com.goodpang.dto.SellerDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 주문/배송 관리 - '결제완료' 주문을 '배송중'으로 전환.
 */
@WebServlet("/vendor/order/ship")
public class VendorOrderShipServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final VendorOrderListDAO orderListDAO = new VendorOrderListDAO();

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		String invoiceNo = request.getParameter("invoiceNo");

		if (invoiceNo == null || invoiceNo.isBlank()) {
			response.sendRedirect(request.getContextPath() + "/vendor/order");
			return;
		}

		try {
			int orderNo = Integer.parseInt(request.getParameter("orderNo"));
			orderListDAO.shipOrder(orderNo, loginSeller.getSellerNo(), invoiceNo.trim());
		} catch (NumberFormatException e) {
			// orderNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		response.sendRedirect(request.getContextPath() + "/vendor/order");
	}

}
