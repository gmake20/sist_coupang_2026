package com.goodpang.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dao.WowPaymentDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.dto.WowMembershipDTO;
import com.goodpang.util.ConnectionProvider;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/wow/join")
public class WowJoinServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int WOW_PRICE = 7890;

    private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();
    private final WowMembershipDAO wowMembershipDAO = new WowMembershipDAO();
    private final WowPaymentDAO wowPaymentDAO = new WowPaymentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        int memberNo = loginMember.getMemberNo();

        // MEMBER.RANK가 아니라 WOW_MEMBERSHIP 기준으로 확인
        boolean wowMember = wowMembershipDAO.isWowMember(memberNo);

        if (wowMember) {
            response.sendRedirect(request.getContextPath() + "/wow/membership");
            return;
        }

        List<PaymentMethodDTO> paymentMethods =
                paymentMethodDAO.getPaymentMethods(memberNo);

        request.setAttribute("paymentMethods", paymentMethods);

        String mode = request.getParameter("mode");

        if ("modal".equals(mode)) {
            request.getRequestDispatcher("/WEB-INF/views/wow_join_modal.jsp")
                   .forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/wow_join.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        int memberNo = loginMember.getMemberNo();

        // POST에서도 와우 회원 여부 다시 확인
        if (wowMembershipDAO.isWowMember(memberNo)) {
            response.sendRedirect(request.getContextPath() + "/wow/membership");
            return;
        }

        String paymentMethodNoParam = request.getParameter("paymentMethodNo");

        if (paymentMethodNoParam == null || paymentMethodNoParam.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "결제수단을 선택해주세요."
            );
            return;
        }

        int paymentMethodNo;

        try {
            paymentMethodNo = Integer.parseInt(paymentMethodNoParam);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 결제수단입니다."
            );
            return;
        }

        boolean valid =
                paymentMethodDAO.existsPaymentMethod(memberNo, paymentMethodNo);

        if (!valid) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "사용할 수 없는 결제수단입니다."
            );
            return;
        }

        boolean paymentSuccess = true;

        if (!paymentSuccess) {
            throw new ServletException("와우 멤버십 결제에 실패했습니다.");
        }

        Connection conn = null;

        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            WowMembershipDTO membership =
                    wowMembershipDAO.findByMemberNo(memberNo);

            int wowMembershipNo;

            if (membership == null) {
                // 최초 가입
                wowMembershipNo =
                        wowMembershipDAO.getNextMembershipNo(conn);

                wowMembershipDAO.insertMembership(
                        conn,
                        wowMembershipNo,
                        memberNo,
                        paymentMethodNo
                );
            } else {
                // 기존 멤버십 재가입
                wowMembershipNo =
                        membership.getWowMembershipNo();

                int updateResult =
                        wowMembershipDAO.reactivateMembership(
                                conn,
                                memberNo,
                                paymentMethodNo
                        );

                if (updateResult != 1) {
                    throw new Exception("와우 멤버십 재가입 처리 실패");
                }
            }

            // 결제 내역 저장
            wowPaymentDAO.insertPayment(
                    conn,
                    wowMembershipNo,
                    memberNo,
                    paymentMethodNo,
                    WOW_PRICE,
                    "JOIN",
                    "SUCCESS"
            );

            conn.commit();

            // MEMBER.RANK는 변경하지 않고 세션 상태만 변경
            request.getSession().setAttribute("wowMember", true);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ignored) {
                }
            }

            throw new ServletException(
                    "와우 멤버십 가입 처리 중 오류가 발생했습니다.",
                    e
            );

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ignored) {
                }
            }
        }

        String joinMode = request.getParameter("joinMode");

        if ("modal".equals(joinMode)) {
            response.sendRedirect(
                    request.getContextPath() + "/?wowJoined=Y"
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/wow/welcome"
        );
    }
}
