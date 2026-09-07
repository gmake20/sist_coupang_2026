package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.CartItemDTO;
import com.goodpang.dto.MemberDTO;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart/status")
public class CartStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CartDAO cartDAO = new CartDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
<<<<<<< HEAD
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");
=======
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
>>>>>>> 87c98c6efadb49f0d585b9d2bccf8154f20d36c3

        HttpSession session = request.getSession();

        MemberDTO loginMember =
                (MemberDTO) session.getAttribute("loginMember");

        List<CartItemDTO> cartItems;
<<<<<<< HEAD

        // 회원
=======
        int cartCount;

>>>>>>> 87c98c6efadb49f0d585b9d2bccf8154f20d36c3
        if (loginMember != null) {

            cartItems =
                    cartDAO.getCartItems(
                            loginMember.getMemberNo()
                    );

<<<<<<< HEAD
        // 비회원
=======
            cartCount = cartItems.size();

>>>>>>> 87c98c6efadb49f0d585b9d2bccf8154f20d36c3
        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
<<<<<<< HEAD
                            session.getAttribute("guestCart");

            cartItems =
                    cartDAO.getGuestCartItems(guestCart);
        }

=======
                    session.getAttribute("guestCart");

            if (guestCart == null || guestCart.isEmpty()) {

                cartItems = new ArrayList<>();
                cartCount = 0;

            } else {

                /*
                 * 중요
                 * 세션 cartPreviewItems를 그대로 가져오는 게 아니라
                 * optionId 기준으로 DB에서 옵션까지 다시 조회
                 */
                cartItems =
                        cartDAO.getGuestCartItems(
                                guestCart
                        );

                cartCount =
                        guestCart.size();
            }
        }

        session.setAttribute(
                "cartCount",
                cartCount
        );

>>>>>>> 87c98c6efadb49f0d585b9d2bccf8154f20d36c3
        session.setAttribute(
                "cartPreviewItems",
                cartItems
        );

<<<<<<< HEAD
        session.setAttribute(
                "cartCount",
                cartItems.size()
        );

        Map<String, Object> result = new HashMap<>();

        result.put(
                "count",
                cartItems.size()
        );

        result.put(
                "items",
                cartItems
        );
=======
        CartStatusResponse result =
                new CartStatusResponse(
                        cartCount,
                        cartItems
                );
>>>>>>> 87c98c6efadb49f0d585b9d2bccf8154f20d36c3

        response.getWriter().write(
                gson.toJson(result)
        );
    }

    private static class CartStatusResponse {

        private final int count;
        private final List<CartItemDTO> items;

        public CartStatusResponse(
                int count,
                List<CartItemDTO> items) {

            this.count = count;
            this.items = items;
        }
    }
}