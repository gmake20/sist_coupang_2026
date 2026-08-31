/*package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/wow/join")
public class WowJoinServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final MemberDAO memberDAO = new MemberDAO();
    private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        MemberDTO loginMember = null;

        if (session != null) {
            loginMember =
                    (MemberDTO) session.getAttribute("loginMember");
        }

        if (loginMember == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if ("WOW".equals(loginMember.getRank())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        int memberNo = loginMember.getMemberNo();

        List<PaymentMethodDTO> paymentMethods =
        		paymentMethodDAO.getPaymentMethods(
        		        loginMember.getMemberNo()
        		);

        request.setAttribute(
                "paymentMethods",
                paymentMethods
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/wow_payment_methods.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        MemberDTO loginMember = LoginUtil.requireLogin(request, response);

        if (loginMember == null) {
            return;
        }

        if ("WOW".equals(loginMember.getRank())) {
            response.sendRedirect(request.getContextPath());
            return;
        }

        int memberNo = loginMember.getMemberNo();
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
                paymentMethodDAO.existsPaymentMethod(
                        memberNo,
                        paymentMethodNo
                );

        if (!valid) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "사용할 수 없는 결제수단입니다."
            );
            return;
        }

        boolean paymentSuccess = true;

        if (!paymentSuccess) {
            throw new ServletException("결제에 실패했습니다.");
        }


 * 결제 성공 후 WOW 전환

        int result = memberDAO.updateToWow(memberNo);

        if (result != 1) {
            throw new ServletException(
                    "와우 멤버십 가입 처리에 실패했습니다."
            );
        }

        loginMember.setRank("WOW");

        request.getSession()
               .setAttribute("loginMember", loginMember);

        response.sendRedirect(
                request.getContextPath()
                + "/?wowJoined=Y"
        );
    }
}*/

package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.MemberDAO;
import com.goodpang.dao.PaymentMethodDAO;
import com.goodpang.dto.MemberDTO;
import com.goodpang.dto.PaymentMethodDTO;
import com.goodpang.util.LoginUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/wow/join")
public class WowJoinServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final MemberDAO memberDAO = new MemberDAO();
	private final PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		MemberDTO loginMember = LoginUtil.requireLogin(request, response);

		if (loginMember == null) {
			return;
		}

		if ("WOW".equalsIgnoreCase(loginMember.getRank())) {
			response.sendRedirect(request.getContextPath() + "/wow/membership");
			return;
		}

		List<PaymentMethodDTO> paymentMethods =
				paymentMethodDAO.getPaymentMethods(loginMember.getMemberNo());

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

		if ("WOW".equalsIgnoreCase(loginMember.getRank())) {
			response.sendRedirect(request.getContextPath() + "/wow/membership");
			return;
		}

		int memberNo = loginMember.getMemberNo();
		String paymentMethodNoParam = request.getParameter("paymentMethodNo");

		if (paymentMethodNoParam == null || paymentMethodNoParam.isBlank()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "결제수단을 선택해주세요.");
			return;
		}

		int paymentMethodNo;

		try {
			paymentMethodNo = Integer.parseInt(paymentMethodNoParam);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 결제수단입니다.");
			return;
		}

		boolean valid = paymentMethodDAO.existsPaymentMethod(memberNo, paymentMethodNo);

		if (!valid) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "사용할 수 없는 결제수단입니다.");
			return;
		}

		boolean paymentSuccess = true;

		if (!paymentSuccess) {
			throw new ServletException("결제에 실패했습니다.");
		}

		int result = memberDAO.updateToWow(memberNo);

		if (result != 1) {
			throw new ServletException("와우 멤버십 가입 처리에 실패했습니다.");
		}

		loginMember.setRank("WOW");
		request.getSession().setAttribute("loginMember", loginMember);

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