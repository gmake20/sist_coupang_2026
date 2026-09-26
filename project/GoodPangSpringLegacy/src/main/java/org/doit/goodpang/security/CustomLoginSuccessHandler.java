package org.doit.goodpang.security;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.CartService;
import org.doit.goodpang.service.WowMembershipService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Component
@RequiredArgsConstructor
@Log4j
public class CustomLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final CartService cartService;

    private final WowMembershipService
            wowMembershipService;


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException,
                   ServletException {

        CustomUser customUser =
                (CustomUser)
                authentication.getPrincipal();

        MemberVO member =
                customUser.getMember();

        Long memberNo =
                member.getMemberNo();

        HttpSession session =
                request.getSession();

        // 사용자 기본 정보
        session.setAttribute(
                "memberNo",
                memberNo
        );

        session.setAttribute(
                "memberName",
                member.getMemberName()
        );


        // 와우 회원 여부
        boolean wowMember =
                wowMembershipService
                    .isWowMember(memberNo);

        session.setAttribute(
                "wowMember",
                wowMember
        );


        // 비회원 장바구니 병합
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> guestCart =
                (Map<Integer, Integer>)
                session.getAttribute(
                        "guestCart"
                );

        if (guestCart != null
                && !guestCart.isEmpty()) {

            for (
                Map.Entry<Integer, Integer>
                    entry
                    : guestCart.entrySet()
            ) {

                Integer optionId =
                        entry.getKey();

                Integer quantity =
                        entry.getValue();

                cartService.addCart(
                        memberNo,
                        optionId,
                        quantity
                );
            }

            session.removeAttribute(
                    "guestCart"
            );
        }


        // 장바구니 개수
        int cartCount =
                cartService.getCartCount(
                        memberNo
                );

        session.setAttribute(
                "cartCount",
                cartCount
        );


        // 세션 30분
        session.setMaxInactiveInterval(
                30 * 60
        );


        // 로그인 이전 페이지
        String redirectUrl =
                (String)
                session.getAttribute(
                    "redirectAfterLogin"
                );

        session.removeAttribute(
                "redirectAfterLogin"
        );

        if (redirectUrl != null
                && !redirectUrl.isBlank()
                && !redirectUrl.contains(
                        "/login"
                )) {

            response.sendRedirect(
                    redirectUrl
            );

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/"
        );
    }
}