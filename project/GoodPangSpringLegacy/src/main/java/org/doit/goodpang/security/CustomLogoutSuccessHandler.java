package org.doit.goodpang.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String referer = request.getHeader("Referer");

        if (referer != null && !referer.trim().isEmpty()) {
            response.sendRedirect(referer);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/");
    }
}