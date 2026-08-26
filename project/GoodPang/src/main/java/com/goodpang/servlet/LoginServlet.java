package com.goodpang.servlet;

import java.io.IOException;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.goodpang.dao.CartDAO;
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

				String contextPath =
						request.getContextPath();

				int index =
						referer.indexOf(
								contextPath
								);

				if (index >= 0) {

					String redirectUrl =
							referer.substring(index);

					session.setAttribute(
							"redirectAfterLogin",
							redirectUrl
							);
				}
			}
		}

		request
		.getRequestDispatcher("/login.jsp")
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

		if (email == null
				|| email.isBlank()
				|| password == null
				|| password.isBlank()) {

			request.setAttribute(
					"error",
					"아이디 또는 비밀번호를 입력해주세요."
					);

			request
			.getRequestDispatcher("/login.jsp")
			.forward(request, response);

			return;
		}

		MemberDAO dao =
				new MemberDAO();

		MemberDTO member =
				dao.findByEmail(email);

		if (member == null) {

			request.setAttribute(
					"error",
					"아이디 또는 비밀번호가 올바르지 않습니다."
					);

			request
			.getRequestDispatcher("/login.jsp")
			.forward(request, response);

			return;
		}

		if (!BCrypt.checkpw(
				password,
				member.getMemberPw())) {

			request.setAttribute(
					"error",
					"아이디 또는 비밀번호가 올바르지 않습니다."
					);

			request
			.getRequestDispatcher("/login.jsp")
			.forward(request, response);

			return;
		}

		HttpSession session =
				request.getSession();

		session.setAttribute(
				"loginMember",
				member
				);
		
		CartDAO cartDAO =
				new CartDAO();

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> guestCart =
			(Map<Integer, Integer>)
			
		session.getAttribute("guestCart");

		if (guestCart != null
				&& !guestCart.isEmpty()) {

			for (Map.Entry<Integer, Integer> entry
					: guestCart.entrySet()) {

				int optionId =
						entry.getKey();

				int quantity =
						entry.getValue();

				cartDAO.addCart(
						member.getMemberNo(),
						optionId,
						quantity
						);

			}

			// DB CART로 옮긴 다음 삭제
			session.removeAttribute("guestCart");
		}
		
		int cartCount =
				cartDAO.getCartCount(
				member.getMemberNo()
				);

				session.setAttribute(
				"cartCount",
				cartCount
				);
		

		session.setAttribute(
				"memberNo",
				member.getMemberNo()
				);

		session.setAttribute(
				"memberName",
				member.getMemberName()
				);

		session.setMaxInactiveInterval(
				30 * 60
				);

		String redirectUrl =
				(String) session.getAttribute(
						"redirectAfterLogin"
						);

		session.removeAttribute(
				"redirectAfterLogin"
				);

		if (redirectUrl != null
				&& !redirectUrl.isBlank()
				&& !redirectUrl.contains("/login")) {

			response.sendRedirect(
					redirectUrl
					);

			return;
		}

		response.sendRedirect(
				request.getContextPath() + "/"
				);
	}
}