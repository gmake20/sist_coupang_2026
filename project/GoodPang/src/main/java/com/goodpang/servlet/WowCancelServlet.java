package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/wow/cancel")
public class WowCancelServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        if (!"WOW".equals(loginMember.getRank())) {
            response.sendRedirect(request.getContextPath() + "/wow/membership");
            return;
        }

        int result = memberDAO.cancelWow(loginMember.getMemberNo());

        if (result != 1) {
            throw new ServletException("와우 멤버십 해지 처리에 실패했습니다.");
        }

        loginMember.setRank("USER");
        request.getSession().setAttribute("loginMember", loginMember);

        response.sendRedirect(request.getContextPath() + "/wow/membership?canceled=Y");
    }
}