package com.goodpang.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dto.CartItemDTO;
import com.goodpang.dto.MemberDTO;
import com.google.gson.Gson;

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
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        HttpSession session = request.getSession();

        MemberDTO loginMember =
                (MemberDTO) session.getAttribute("loginMember");

        List<CartItemDTO> cartItems;

        // 회원
        if (loginMember != null) {

            cartItems =
                    cartDAO.getCartItems(
                            loginMember.getMemberNo()
                    );

        // 비회원
        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                            session.getAttribute("guestCart");

            cartItems =
                    cartDAO.getGuestCartItems(guestCart);
        }

        session.setAttribute(
                "cartPreviewItems",
                cartItems
        );

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

        response.getWriter().write(
                gson.toJson(result)
        );
    }
}