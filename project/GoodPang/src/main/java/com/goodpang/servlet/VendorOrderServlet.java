package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorOrderListDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorOrderListDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 주문/배송 관리(vendor_orders.jsp) 화면 진입.
 */
@WebServlet("/vendor/order")
public class VendorOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final VendorOrderListDAO orderListDAO = new VendorOrderListDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		List<VendorOrderListDTO> orderList = orderListDAO.findBySellerNo(loginSeller.getSellerNo());

		request.setAttribute("orderList", orderList);

		request.getRequestDispatcher("/WEB-INF/views/vendor-order.jsp").forward(request, response);
	}

}
