package org.doit.goodpang.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String login(
            @RequestParam(
                value = "redirect",
                required = false
            ) String redirect,
            Authentication authentication,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response) {

        setNoCache(response);

        // 이미 로그인한 사용자면 메인으로 이동
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            return "redirect:/";
        }

        if (redirect != null
                && !redirect.isBlank()
                && redirect.startsWith("/")
                && !redirect.contains("/login")) {

            session.setAttribute(
                    "redirectAfterLogin",
                    redirect
            );

            log.info(
                    "redirectAfterLogin : " + redirect
            );

        } else {

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

                    String contextPath =
                            request.getContextPath();

                    int index =
                            referer.indexOf(contextPath);

                    if (index >= 0) {

                        String redirectUrl =
                                referer.substring(index);

                        session.setAttribute(
                                "redirectAfterLogin",
                                redirectUrl
                        );

                        log.info(
                                "referer redirectAfterLogin : "
                                        + redirectUrl
                        );
                    }
                }
            }
        }

        return "login";
    }


    private void setNoCache(
            HttpServletResponse response) {

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );
    }
}