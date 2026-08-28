package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AdminDeliveryDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 배송완료 처리 (관리자 계정이 아직 없어서, 목록 화면에서 바로 처리).
 */
@WebServlet("/admin/delivery-complete")
public class AdminDeliveryCompleteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		AdminDeliveryDAO dao = new AdminDeliveryDAO();

		try {
			int deliveryNo = Integer.parseInt(request.getParameter("deliveryNo"));
			dao.completeDelivery(deliveryNo);
		} catch (NumberFormatException e) {
			// deliveryNo가 없거나 숫자가 아니면 아무 것도 바꾸지 않고 목록으로 돌려보낸다.
		}

		response.sendRedirect(request.getContextPath() + "/admin/deliveries");
	}

}
