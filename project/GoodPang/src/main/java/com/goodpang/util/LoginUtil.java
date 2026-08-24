package com.goodpang.util;

import java.io.IOException;

import com.goodpang.dto.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginUtil {

    private LoginUtil() {
        // 객체 생성 방지
    }

    /**
     * 로그인 여부 확인
     *
     * 로그인 O → 로그인 회원 MemberDTO 반환
     * 로그인 X → 현재 주소 저장 후 /login 이동, null 반환
     */
    public static MemberDTO requireLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);

        // 로그인되어 있는 경우
        if (session != null) {

            MemberDTO loginMember =
                    (MemberDTO) session.getAttribute(
                            "loginMember"
                    );

            if (loginMember != null) {
                return loginMember;
            }
        }

        // =========================
        // 로그인하지 않은 경우
        // =========================

        // 세션이 없다면 생성
        session = request.getSession();

        // 현재 요청 URL
        String redirectUrl =
                request.getRequestURI();

        // GET 파라미터까지 저장
        String queryString =
                request.getQueryString();

        if (queryString != null &&
            !queryString.isBlank()) {

            redirectUrl += "?" + queryString;
        }

        // 로그인 후 이동할 주소 저장
        session.setAttribute(
                "redirectAfterLogin",
                redirectUrl
        );

        // 로그인 페이지로 이동
        response.sendRedirect(
                request.getContextPath() + "/login"
        );

        return null;
    }
}