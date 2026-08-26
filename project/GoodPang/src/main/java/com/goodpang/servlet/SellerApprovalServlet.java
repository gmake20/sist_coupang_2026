package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.SellerDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 판매자 입점심사 승인/반려 처리 (관리자 계정이 아직 없어서, 상세 페이지에서 바로 처리).
 */
@WebServlet("/admin/seller-approve")
public class SellerApprovalServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String sellerNoParam = request.getParameter("sellerNo");
		String action = request.getParameter("action");

		int sellerNo;

		try {
			sellerNo = Integer.parseInt(sellerNoParam);
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/sellers");
			return;
		}

		SellerDAO dao = new SellerDAO();

		if ("approve".equals(action)) {

			dao.updateApprovalStatus(sellerNo, "승인", null);

		} else if ("reject".equals(action)) {

			String rejectReason = request.getParameter("rejectReason");

			if (rejectReason == null || rejectReason.isBlank()) {
				rejectReason = "사유가 입력되지 않았습니다.";
			}

			dao.updateApprovalStatus(sellerNo, "반려", rejectReason);
		}

		response.sendRedirect(request.getContextPath() + "/admin/seller-detail?sellerNo=" + sellerNo);
	}

}
