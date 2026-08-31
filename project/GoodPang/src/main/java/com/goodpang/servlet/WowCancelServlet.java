package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.WowMembershipDAO;
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

    private final WowMembershipDAO wowMembershipDAO =
            new WowMembershipDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * 로그인 확인
         */
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
         * 실제 WOW_MEMBERSHIP 테이블을 기준으로
         * 와우 회원 여부 확인
         */
        boolean wowMember =
                wowMembershipDAO.isWowMember(
                        memberNo
                );

        if (!wowMember) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/wow/membership"
            );

            return;
        }

        /*
         * 와우 멤버십 해지 신청
         *
         * STATUS = CANCEL_PENDING
         * AUTO_PAYMENT_YN = N
         * CANCEL_DATE = SYSDATE
         */
        int result =
                wowMembershipDAO.cancelMembership(
                        memberNo
                );

        if (result != 1) {

            throw new ServletException(
                    "와우 멤버십 해지 처리에 실패했습니다."
            );
        }

        /*
         * CANCEL_PENDING 상태에서는
         * 남은 이용기간 동안 와우 혜택 유지
         *
         * 따라서 wowMember = true 유지
         */
        request.getSession()
               .setAttribute(
                       "wowMember",
                       true
               );

        response.sendRedirect(
                request.getContextPath()
                + "/wow/membership?canceled=Y"
        );
    }
}
