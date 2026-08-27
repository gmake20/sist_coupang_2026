package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.ReviewDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.ReviewItemDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/review/available")
public class ReviewAvailableServlet
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ReviewDAO reviewDAO =
        new ReviewDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember =
            LoginUtil.requireLogin(
                request,
                response
            );

        if (loginMember == null) {
            return;
        }

        int memberNo =
            loginMember.getMemberNo();

        List<ReviewItemDTO> availableList =
            reviewDAO
                .selectAvailableReviewsByMemberNo(
                    memberNo
                );

        int reviewCount =
            reviewDAO
                .countReviewsByMemberNo(
                    memberNo
                );

        request.setAttribute(
            "availableList",
            availableList
        );

        request.setAttribute(
            "reviewCount",
            reviewCount
        );

        request.getRequestDispatcher(
            "/review_available.jsp"
        ).forward(
            request,
            response
        );
    }
}