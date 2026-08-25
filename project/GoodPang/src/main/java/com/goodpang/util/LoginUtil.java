package com.goodpang.util;

import java.io.IOException;

import com.goodpang.dto.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginUtil {

    public static MemberDTO requireLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession();

        MemberDTO loginMember =
                (MemberDTO) session.getAttribute(
                    "loginMember"
                );

        if (loginMember == null) {

            String requestUri =
                    request.getRequestURI();

            String queryString =
                    request.getQueryString();

            if (queryString != null
                    && !queryString.isBlank()) {

                requestUri += "?" + queryString;
            }

            // 로그인 성공 후 돌아올 페이지 저장
            session.setAttribute(
                "redirectAfterLogin",
                requestUri
            );

            response.sendRedirect(
                request.getContextPath()
                + "/login"
            );

            return null;
        }

        return loginMember;
    }
}