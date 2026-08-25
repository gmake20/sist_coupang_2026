package com.goodpang.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.goodpang.dao.ProductDAO;
import com.goodpang.dto.ProductDTO;

/*
 * /product?productNo=1 로 들어오면 DB 에서 상품 하나를 읽어서 product.jsp 로 넘겨줌.
 * (OrderDetailServlet.java 와 같은 패턴 — @WebServlet + doGet + forward)
 *
 * ★ 실제 데이터는 PRODUCT_NO 1~21번만 있음 (SEQ_PRODUCT 가 22부터 시작하는 걸로 확인).
 *   product.jsp 에 지금 하드코딩된 productId=25 는 없는 번호라 테스트할 땐
 *   1~21 사이 값으로 접속해볼 것.
 */
@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String productNoParam = request.getParameter("productNo");

        System.out.println("[DEBUG ProductServlet] === 상품 상세 조회 시작 ===");
        System.out.println("[DEBUG ProductServlet] 전달받은 productNoParam: " + productNoParam);

        try {
            if (productNoParam != null && !productNoParam.isEmpty()) {

                int productNo = Integer.parseInt(productNoParam);

                ProductDAO dao = new ProductDAO();
                ProductDTO product = dao.selectProduct(productNo);

                if (product == null) {
                    System.out.println("[DEBUG ProductServlet] 상품을 찾을 수 없음: productNo=" + productNo);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "상품을 찾을 수 없습니다.");
                    return;
                }

                request.setAttribute("p", product);
            }

        } catch (NumberFormatException e) {
            System.err.println("[ERROR] productNo 숫자 변환 실패 (잘못된 파라미터): " + productNoParam);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] DB 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        request.getRequestDispatcher(
                "/product.jsp"
        ).forward(request, response);
    }
}
