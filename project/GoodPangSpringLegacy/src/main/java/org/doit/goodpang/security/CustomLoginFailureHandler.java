package org.doit.goodpang.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class CustomLoginFailureHandler
        implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        // 탈퇴 회원
        if ("탈퇴한 회원입니다.".equals(
                exception.getMessage()
        )) {

            request.setAttribute(
                    "withdrawnMember",
                    true
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/member/member_withdrawn.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/login?error=true"
        );
    }
}