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
import jakarta.servlet.http.HttpSession;

@WebServlet("/wow/join")
public class WowJoinServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
   
    private final MemberDAO memberDAO =
            new MemberDAO();

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

        request.getRequestDispatcher(
                "/wow_join.jsp"
        ).forward(request, response);
    }	


    @Override
    protected void doPost(
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
        
        /*
         * 이미 WOW 회원인지 확인
         */
        if ("WOW".equals(
                loginMember.getRank())) {

            response.sendRedirect(
                    request.getContextPath()
            );

            return;
        }

        int rowCount =
                memberDAO.updateToWow(
                        memberNo
                );


        if (rowCount == 1) {

            /*
             * 세션에 저장되어 있는
             * MemberDTO도 WOW로 변경
             */
            loginMember.setRank("WOW");

            HttpSession session =
                    request.getSession();

            session.setAttribute(
                    "loginMember",
                    loginMember
            );

            response.sendRedirect(
                    request.getContextPath()
            );

        } else {
            throw new ServletException(
                    "와우 멤버십 가입에 실패했습니다."
            );
        }
    }
}