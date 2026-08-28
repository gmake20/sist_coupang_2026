package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.goodpang.dao.ProductDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.ProductDTO;
import com.goodpang.dto.ReviewDTO;

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
                
                // 배송예정일 — "내일(요일) M/d" 형태로 매번 계산
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                String deliveryDate = "내일(" + dayOfWeek + ") " + tomorrow.getMonthValue() + "/" + tomorrow.getDayOfMonth();
                request.setAttribute("deliveryDate", deliveryDate);

                // 적립 혜택 — 가격의 5%
                int rewardCash = product.getProductPrice() * 5 / 100;
                request.setAttribute("rewardCash", rewardCash);

                request.setAttribute("p", product);
                
                request.setAttribute("p", product);
                
                // 리뷰
                ReviewDAO reviewDAO = new ReviewDAO();
                List<ReviewDTO> reviews = reviewDAO.selectReviewsByProductNo(productNo);
                request.setAttribute("reviews", reviews);
                request.setAttribute("reviewCount", reviews.size());
                // 평균 평점 — 2026-08-27: 별점 UI를 두 자리 다 스프라이트 이미지로 바꾸면서 필요해짐
                // (review-atf/review-score 는 "리뷰 개별 별점"이 아니라 "상품 전체 평균 별점"을 보여주는 자리라
                //  이미 읽어온 reviews 리스트에서 바로 평균을 냄 — DB 를 한 번 더 조회할 필요 없음)
                double avgRating = 0;
                if (!reviews.isEmpty()) {
                    int sum = 0;
                    for (ReviewDTO r : reviews) sum += r.getRating();
                    avgRating = Math.round((double) sum / reviews.size() * 10) / 10.0;   // 소수 첫째자리까지
                }
                request.setAttribute("avgRating", avgRating);
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
