package com.goodpang.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.goodpang.dao.CartDAO;
import com.goodpang.dao.WowMembershipDAO;
import com.goodpang.dto.CartItemDTO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CartDAO cartDAO = new CartDAO();
    private final WowMembershipDAO wowDAO = new WowMembershipDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession();

        MemberDTO loginMember =
                (MemberDTO) session.getAttribute("loginMember");

        List<CartItemDTO> cartItems;

        // 로그인 사용자
        if (loginMember != null) {

            cartItems =
                    cartDAO.getCartItems(
                            loginMember.getMemberNo()
                    );

        } else {

            // 비로그인 사용자
            cartItems = new ArrayList<>();

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                            session.getAttribute("guestCart");

            if (guestCart != null) {

                for (Map.Entry<Integer, Integer> entry
                        : guestCart.entrySet()) {

                    int optionId =
                            entry.getKey();

                    int quantity =
                            entry.getValue();

                    CartItemDTO item =
                            cartDAO.getCartItemByOptionId(
                                    optionId
                            );

                    if (item != null) {

                        item.setQuantity(quantity);

                        cartItems.add(item);
                    }
                }
            }
        }
        
        if (loginMember != null) {
	        boolean isWowMember =
	                wowDAO.isWowMember(
	                        loginMember.getMemberNo()
	                );
	
	        request.setAttribute(
	                "isWowMember",
	                isWowMember
	        );
        
        }
        

        int totalPrice = 0;

        for (CartItemDTO item : cartItems) {
            totalPrice += item.getTotalPrice();
        }

        request.setAttribute(
                "cartItems",
                cartItems
        );

        request.setAttribute(
                "cartCount",
                cartItems.size()
        );

        request.setAttribute(
                "totalPrice",
                totalPrice
        );

        request.getRequestDispatcher(
                "/cart.jsp"
        ).forward(request, response);
    }
}