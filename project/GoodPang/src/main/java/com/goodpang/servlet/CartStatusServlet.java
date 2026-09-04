package com.goodpang.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.goodpang.dao.CartDAO;
import com.goodpang.dto.CartItemDTO;
import com.goodpang.dto.MemberDTO;

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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType(
                "application/json; charset=UTF-8"
        );

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            response.getWriter().write(
                    "{\"count\":0,\"items\":[]}"
            );
            return;
        }

        MemberDTO member =
                (MemberDTO) session.getAttribute(
                        "loginMember"
                );

        if (member == null) {
            response.getWriter().write(
                    "{\"count\":0,\"items\":[]}"
            );
            return;
        }

        int memberNo =
                member.getMemberNo();

        List<CartItemDTO> cartItems =
                cartDAO.getCartItems(
                        memberNo
                );

        int count =
                cartItems.size();

        session.setAttribute(
                "cartCount",
                count
        );

        session.setAttribute(
                "cartPreviewItems",
                cartItems
        );

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "count",
                count
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