package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/modify")
public class ModifyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /*
     * 회원정보 수정 페이지
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // 1. 로그인 여부 확인
        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        MemberDTO loginMember =
                (MemberDTO) session.getAttribute("loginMember");

        if (loginMember == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            // 2. 로그인 회원 번호
            int memberNo = loginMember.getMemberNo();

            // 3. DB에서 최신 회원정보 조회
            MemberDAO memberDAO = new MemberDAO();

            MemberDTO member =
                    memberDAO.getMember(memberNo);

            if (member == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "회원 정보를 찾을 수 없습니다."
                );
                return;
            }

            // 4. JSP에 전달
            request.setAttribute("member", member);

            // 5. JSP 이동
            request.getRequestDispatcher(
                    "/userModify.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                    "회원정보 조회 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}