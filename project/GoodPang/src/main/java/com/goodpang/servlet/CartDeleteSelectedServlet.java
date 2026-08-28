package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.CartItemDTO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart/delete-selected")
public class CartDeleteSelectedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String[] optionIdParams =
                request.getParameterValues("optionId");

        if (optionIdParams == null
                || optionIdParams.length == 0) {

            response.sendRedirect(
                    request.getContextPath() + "/cart"
            );
            return;
        }

        try {

            HttpSession session =
                    request.getSession();

            MemberDTO loginMember =
                    (MemberDTO) session.getAttribute(
                            "loginMember"
                    );

            /*
             * 로그인 사용자
             */
            if (loginMember != null) {

                int[] optionIds =
                        new int[optionIdParams.length];

                for (int i = 0;
                     i < optionIdParams.length;
                     i++) {

                    optionIds[i] =
                            Integer.parseInt(
                                    optionIdParams[i]
                            );
                }

                CartDAO dao = new CartDAO();

                dao.deleteSelected(
                        loginMember.getMemberNo(),
                        optionIds
                );

            /*
             * 비로그인 사용자
             */
            } else {

                List<CartItemDTO> cartItems =
                        (List<CartItemDTO>)
                        session.getAttribute(
                                "cartItems"
                        );

                if (cartItems != null) {

                    for (String optionIdParam
                            : optionIdParams) {

                        int optionId =
                                Integer.parseInt(
                                        optionIdParam
                                );

                        cartItems.removeIf(
                                item ->
                                    item.getOptionId()
                                    == optionId
                        );
                    }

                    session.setAttribute(
                            "cartItems",
                            cartItems
                    );
                }
            }

            response.sendRedirect(
                    request.getContextPath() + "/cart"
            );

        } catch (Exception e) {

            throw new ServletException(
                    "선택 상품 삭제 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}