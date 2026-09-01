package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.VendorProductOptionDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorProductOptionDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 옵션 관리(vendor-product-options.jsp) 화면 진입.
 */
@WebServlet("/vendor/product/options")
public class VendorProductOptionListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final VendorProductOptionDAO optionDAO = new VendorProductOptionDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		List<VendorProductOptionDTO> optionList = optionDAO.findBySellerNo(loginSeller.getSellerNo());
		request.setAttribute("optionList", optionList);

		request.getRequestDispatcher("/WEB-INF/views/vendor-product-options.jsp").forward(request, response);
	}

}
