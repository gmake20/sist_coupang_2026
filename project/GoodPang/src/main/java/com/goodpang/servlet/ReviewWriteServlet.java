package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.ReviewDTO;
import com.goodpang.dto.ReviewItemDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/review/write")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024,
		maxFileSize = 10 * 1024 * 1024,
		maxRequestSize = 100 * 1024 * 1024
		)
public class ReviewWriteServlet
extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final ReviewDAO reviewDAO =
			new ReviewDAO();

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {

		String orderDetailNoParam =
				request.getParameter(
						"orderDetailNo"
						);

		String productNoParam =
				request.getParameter(
						"productNo"
						);


		if (orderDetailNoParam == null
				|| productNoParam == null) {

			response.sendError(
					HttpServletResponse
					.SC_BAD_REQUEST,
					"잘못된 리뷰 요청입니다."
					);

			return;
		}
		
	

		int orderDetailNo =
				Integer.parseInt(
						orderDetailNoParam
						);

		int productNo =
				Integer.parseInt(
						productNoParam
						);
		
		MemberDTO loginMember =
			    LoginUtil.requireLogin(request, response);

			if (loginMember == null) {
			    return;
			}

			ReviewItemDTO reviewItem =
			    reviewDAO.getReviewItem(
			        orderDetailNo,
			        productNo,
			        loginMember.getMemberNo()
			    );
			
			


		if (reviewItem == null) {

			response.sendError(
					HttpServletResponse
					.SC_NOT_FOUND,
					"리뷰 작성 대상 상품을 찾을 수 없습니다."
					);

			return;
		}
		
		/* 작성한 리뷰 개수 */
		int reviewCount =
		    reviewDAO.countReviewsByMemberNo(
		        loginMember.getMemberNo()
		    );

		request.setAttribute(
			    "reviewCount",
			    reviewCount
			);

		
		request.setAttribute(
				"reviewItem",
				reviewItem
				);


		request.getRequestDispatcher(
				"/review_write.jsp"
				).forward(
						request,
						response
						);
	}
	@Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember =
            LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        try {

            String orderDetailNoParam =
                request.getParameter("orderDetailNo");

            String productRatingParam =
                request.getParameter("productRating");

            String serviceRatingParam =
                request.getParameter("serviceRating");

            String reviewContent =
                request.getParameter("reviewContent");

            String reviewSummary =
                request.getParameter("reviewSummary");


            // 필수값 검증
            if (orderDetailNoParam == null
                    || orderDetailNoParam.isBlank()
                    || productRatingParam == null
                    || productRatingParam.isBlank()
                    || reviewContent == null
                    || reviewContent.isBlank()) {

                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "필수 리뷰 정보가 없습니다."
                );

                return;
            }


            int orderDetailNo =
                Integer.parseInt(orderDetailNoParam);

            int productRating =
                Integer.parseInt(productRatingParam);


            // 별점 검증
            if (productRating < 1
                    || productRating > 5) {

                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "상품 별점이 올바르지 않습니다."
                );

                return;
            }


            // 중복 리뷰 방지
            if (reviewDAO.existsByOrderDetailNo(orderDetailNo)) {

                response.sendError(
                    HttpServletResponse.SC_CONFLICT,
                    "이미 작성한 리뷰입니다."
                );

                return;
            }


            ReviewDTO dto = new ReviewDTO();

            dto.setOrderDetailNo(orderDetailNo);
            dto.setMemberNo(loginMember.getMemberNo());
            dto.setProductRating(productRating);
            dto.setReviewContent(reviewContent.trim());
            dto.setReviewSummary(reviewSummary);


            // 서비스 만족도는 선택값
            if (serviceRatingParam != null
                    && !serviceRatingParam.isBlank()) {

                dto.setServiceRating(
                    Integer.parseInt(
                        serviceRatingParam
                    )
                );
            }


            // DB INSERT
            int reviewNo =
                reviewDAO.insertReview(dto);


            // 등록 완료 후 리뷰관리 페이지 이동
            response.sendRedirect(
                request.getContextPath()
                + "/review/list"
            );

        } catch (NumberFormatException e) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "잘못된 리뷰 데이터입니다."
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                "리뷰 등록 중 오류가 발생했습니다.",
                e
            );
        }
    }
}