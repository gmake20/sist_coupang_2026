package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.ProductListDAO;
import com.goodpang.dto.SellerDTO;
import com.goodpang.dto.VendorProductListDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 판매자센터 상품 목록(vendor_products.jsp) 화면 진입.
 */
@WebServlet("/vendor/product")
public class VendorProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ProductListDAO productListDAO = new ProductListDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		boolean hiddenView = "hidden".equals(request.getParameter("view"));

		List<VendorProductListDTO> productList = hiddenView
				? productListDAO.findHiddenBySellerNo(loginSeller.getSellerNo())
				: productListDAO.findBySellerNo(loginSeller.getSellerNo());

		request.setAttribute("productList", productList);
		request.setAttribute("hiddenView", hiddenView);

		request.getRequestDispatcher("/WEB-INF/views/vendor-product.jsp").forward(request, response);
	}

}
