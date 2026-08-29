package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import com.goodpang.dao.ProductDAO;
import com.goodpang.dao.ProductImageDAO;
import com.goodpang.dao.ProductOptionDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.ProductDTO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.dto.ReviewDTO;


@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private static final Gson gson = new Gson();

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

                // 옵션 + 옵션별/상세설명 사진
                //  옵션 하나(OPTION_ID)마다 대표/추가 사진이 딸려있고, OPTION_ID 가 null 인 사진은
                //  상세설명용이라 따로 뺌)
                ProductOptionDAO optionDAO = new ProductOptionDAO();
                List<ProductOptionDTO> options = optionDAO.selectOptionsByProductNo(productNo);

                ProductImageDAO imageDAO = new ProductImageDAO();
                List<ProductImageDTO> images = imageDAO.selectImagesByProductNo(productNo);

                List<ProductImageDTO> detailImages = new ArrayList<>();

                for (ProductImageDTO image : images) {
                    if (image.getOptionId() == null) {
                        // OPTION_ID 없는 사진 = 상세설명 사진
                        detailImages.add(image);
                        continue;
                    }
                    // OPTION_ID 있는 사진 = 그 옵션의 대표/추가 사진 → 해당 옵션에 붙여줌
                    for (ProductOptionDTO option : options) {
                        if (option.getOptionId() == image.getOptionId()) {
                            option.getImages().add(image);
                            break;
                        }
                    }
                }

                request.setAttribute("options", options);
                request.setAttribute("detailImages", detailImages);

                // 옵션 드롭박스/색상칩은 js 가 그림(js/product.js 의 setupOptionSelect).
                // JSP 에서 JSON 을 손으로 조립하면 값에 따옴표 같은 게 들어갈 때 깨져서,
                // Gson 으로 안전하게 만들어 넘김 (Gson 이 <, > 도 이스케이프해줘서 </script> 사고도 없음)
                request.setAttribute("optionsJson", gson.toJson(options));

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
