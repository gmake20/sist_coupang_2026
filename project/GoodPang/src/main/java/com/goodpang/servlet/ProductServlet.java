package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.goodpang.dao.ProductDAO;
import com.goodpang.dao.ProductImageDAO;
import com.goodpang.dao.ProductOptionDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.ProductDTO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.dto.ReviewDTO;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
<<<<<<< HEAD
import jakarta.servlet.http.HttpSession;
=======

import com.google.gson.Gson;

import com.goodpang.dao.ProductDAO;
import com.goodpang.dao.ProductImageDAO;
import com.goodpang.dao.ProductOptionDAO;
import com.goodpang.dao.ProductViewLogDAO;
import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.ProductDTO;
import com.goodpang.dto.ProductImageDTO;
import com.goodpang.dto.ProductOptionDTO;
import com.goodpang.dto.ReviewDTO;
>>>>>>> 5ba6b04e9a3a1bea7460c30b322ae4e0b2707a3c


@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private static final Gson gson = new Gson();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String productNoParam = request.getParameter("productNo");
        
        WowMembershipDAO wowMembershipDAO =
                new WowMembershipDAO();

        boolean isWowMember = false;

        HttpSession session =
                request.getSession(false);

        if (session != null) {

            MemberDTO loginMember =
                    (MemberDTO) session.getAttribute(
                            "loginMember"
                    );

            if (loginMember != null) {

                isWowMember =
                        wowMembershipDAO.isWowMember(
                                loginMember.getMemberNo()
                        );
            }
        }

        request.setAttribute(
                "isWowMember",
                isWowMember
        );
        

        try {
            if (productNoParam != null && !productNoParam.isEmpty()) {

                int productNo = Integer.parseInt(productNoParam);

                ProductDAO dao = new ProductDAO();
                ProductDTO product = dao.selectProduct(productNo);

                if (product == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "상품을 찾을 수 없습니다.");
                    return;
                }
                
                // 배송예정일 — "내일(요일) M/d" 형태로 매번 계산
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                String deliveryDate = "내일(" + dayOfWeek + ") " + tomorrow.getMonthValue() + "/" + tomorrow.getDayOfMonth();
                request.setAttribute("deliveryDate", deliveryDate);

                request.setAttribute("p", product);

                // 판매자센터 대시보드의 "오늘 방문자수" / "오늘 상품 노출수" 집계용 조회 로그
                Integer memberNo = (Integer) request.getSession().getAttribute("memberNo");
                new ProductViewLogDAO().logView(productNo, memberNo, request.getSession().getId());

                // 옵션 + 옵션별/상세설명 사진
                //  옵션 하나(OPTION_ID)마다 대표/추가 사진이 딸려있고, OPTION_ID 가 null 인 사진은
                //  상세설명용이라 따로 뺌)
                ProductOptionDAO optionDAO = new ProductOptionDAO();
                List<ProductOptionDTO> options = optionDAO.selectOptionsByProductNo(productNo);

                /* 2026-08-30 확정 — PRODUCT_OPTION.PRICE/NORMAL_PRICE 는 PRODUCT.PRODUCT_PRICE(기본가)에
                   더해지는 "추가금"(CartDAO.getCartItems() 와 같은 전제). 그래서:
                     판매가 총액 = PRODUCT_PRICE + 옵션의 PRICE       (할인된 추가금)
                     정상가 총액 = PRODUCT_PRICE + 옵션의 NORMAL_PRICE (할인 전 추가금, 선택 입력이라 없을 수 있음)
                   정상가 총액이 판매가 총액보다 클 때만 "할인 중"으로 보고 취소선/할인율을 보여줌.
                   지금은(2026-08-30) NORMAL_PRICE 를 채운 판매자가 없어서 할인 표시가 실제로는 안 뜨는데,
                   판매자가 정상가를 입력하기 시작하면 코드 수정 없이 자동으로 뜨게 됨. */
                int displayPrice = product.getProductPrice();
                Integer displayNormalPrice = null;

                if (!options.isEmpty()) {
                    ProductOptionDTO firstOption = options.get(0);
                    displayPrice = product.getProductPrice() + firstOption.getPrice();

                    if (firstOption.getNormalPrice() != null) {
                        int normalTotal = product.getProductPrice() + firstOption.getNormalPrice();
                        if (normalTotal > displayPrice) {
                            displayNormalPrice = normalTotal;
                        }
                    }
                }

                int discountRate = 0;
                if (displayNormalPrice != null && displayNormalPrice > 0) {
                    discountRate = (int) Math.round((1 - (double) displayPrice / displayNormalPrice) * 100);
                }

                request.setAttribute("displayPrice", displayPrice);
                request.setAttribute("displayNormalPrice", displayNormalPrice);
                request.setAttribute("discountRate", discountRate);

                // 적립 혜택 — 가격의 5% (옵션 가격 기준으로 계산해야 해서 옵션 조회 뒤로 옮김)
                int rewardCash = displayPrice * 5 / 100;
                request.setAttribute("rewardCash", rewardCash);

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
