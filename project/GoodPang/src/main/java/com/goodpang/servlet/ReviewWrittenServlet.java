package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.ReviewAvailableDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/review/written")
public class ReviewWrittenServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		MemberDTO loginMember = LoginUtil.requireLogin(request, response);

		if (loginMember == null) {
			return;
		}

		int memberNo = loginMember.getMemberNo();

		ReviewDAO reviewDAO = new ReviewDAO();
		List<ReviewAvailableDTO> reviewList = reviewDAO.getReviewStatus(memberNo);

		int writtenCount = 0;

		for (ReviewAvailableDTO review : reviewList) {
			if (review.isReviewWritten()) {
				writtenCount++;
			}
		}

		request.setAttribute("reviewList", reviewList);
		request.setAttribute("writtenCount", writtenCount);

		request.getRequestDispatcher("/review_written.jsp")
		.forward(request, response);
	}
}