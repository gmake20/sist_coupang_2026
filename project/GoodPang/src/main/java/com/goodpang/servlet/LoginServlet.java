package com.goodpang.servlet;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession();

        String redirectAfterLogin =
                (String) session.getAttribute(
                    "redirectAfterLogin"
                );

        if (redirectAfterLogin == null
                || redirectAfterLogin.isBlank()) {

            String referer =
                    request.getHeader("Referer");

            if (referer != null
                    && !referer.isBlank()
                    && !referer.contains("/login")) {

                session.setAttribute(
                    "redirectAfterLogin",
                    referer
                );
            }
        }

        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

    	
        request.setCharacterEncoding("UTF-8");

        String email =
            request.getParameter("email");

        String password =
            request.getParameter("password");
        
        MemberDAO dao =
            new MemberDAO();

        MemberDTO member =
            dao.findByEmail(email);

        // 이메일 없음
        if (member == null) {

            request.setAttribute(
                "error",
                "아이디 또는 비밀번호가 올바르지 않습니다."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);

            return;
        }

        // 비밀번호 확인
        if (!BCrypt.checkpw(
                password,
                member.getMemberPw())) {

            request.setAttribute(
                "error",
                "아이디 또는 비밀번호가 올바르지 않습니다."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);

            return;
        }


        HttpSession session =
            request.getSession();

        session.setAttribute(
            "loginMember",
            member
        );

        session.setAttribute(
            "memberNo",
            member.getMemberNo()
        );

        session.setAttribute(
            "memberName",
            member.getMemberName()
        );

        // 30분
        session.setMaxInactiveInterval(
            30 * 60
        );

        String redirectUrl =
                (String) session.getAttribute("redirectAfterLogin");

        session.removeAttribute("redirectAfterLogin");

        if (redirectUrl != null && !redirectUrl.isBlank()) {
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}