package org.doit.goodpang.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.CartItemDTO;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartSessionService {

    private final CartService cartService;

    public void refreshCartSession(
            HttpSession session,
            Long memberNo) {

        List<CartItemDTO> cartItems =
                cartService.getCartItems(
                        memberNo
                );

        int cartCount =
                cartItems.size();


        session.setAttribute(
                "cartCount",
                cartCount
        );


        if (cartItems.isEmpty()) {

            session.removeAttribute(
                    "cartPreviewItems"
            );

        } else {

            session.setAttribute(
                    "cartPreviewItems",
                    cartItems
            );
        }
    }

    public void refreshGuestCartSession(
            HttpSession session,
            Map<Integer, Integer> guestCart) {
    	
        if (guestCart == null
                || guestCart.isEmpty()) {

            session.removeAttribute(
                    "guestCart"
            );

            session.removeAttribute(
                    "cartItems"
            );

            session.removeAttribute(
                    "cartPreviewItems"
            );

            session.setAttribute(
                    "cartCount",
                    0
            );

            return;
        }


        List<CartItemDTO> cartItems =
                cartService.getGuestCartItems(
                        guestCart
                );


        session.setAttribute(
                "guestCart",
                guestCart
        );

        session.setAttribute(
                "cartCount",
                guestCart.size()
        );

        session.setAttribute(
                "cartItems",
                cartItems
        );

        session.setAttribute(
                "cartPreviewItems",
                cartItems
        );
    }
}