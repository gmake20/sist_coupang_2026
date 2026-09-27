package org.doit.goodpang.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.doit.goodpang.domain.CartItemDTO;
import org.doit.goodpang.mapper.CartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;


    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(
            Long memberNo) {

        return cartMapper
                .getCartItems(
                        memberNo
                );
    }


    @Transactional
    public int addCart(
            Long memberNo,
            Integer optionId,
            Integer quantity) {

        if (quantity == null
                || quantity <= 0) {

            throw new IllegalArgumentException(
                    "수량은 1개 이상이어야 합니다."
            );
        }

        return cartMapper.addCart(
                memberNo,
                optionId,
                quantity
        );
    }


    @Transactional
    public int updateQuantity(
            Long memberNo,
            Integer optionId,
            Integer quantity) {

        if (quantity == null
                || quantity <= 0) {

            throw new IllegalArgumentException(
                    "수량은 1개 이상이어야 합니다."
            );
        }

        return cartMapper.updateQuantity(
                memberNo,
                optionId,
                quantity
        );
    }


    @Transactional
    public int deleteCart(
            Long memberNo,
            Integer optionId) {

        return cartMapper.deleteCart(
                memberNo,
                optionId
        );
    }


    @Transactional(readOnly = true)
    public CartItemDTO getCartItemByOptionId(
            Integer optionId) {

        return cartMapper
                .getCartItemByOptionId(
                        optionId
                );
    }


    @Transactional(readOnly = true)
    public int getCartCount(
            Long memberNo) {

        return cartMapper
                .getCartCount(
                        memberNo
                );
    }


    @Transactional
    public int deleteSelected(
            Long memberNo,
            int[] optionIds) {

        if (optionIds == null
                || optionIds.length == 0) {

            return 0;
        }

        List<Integer> ids =
                new ArrayList<>();

        for (int optionId : optionIds) {
            ids.add(optionId);
        }

        return cartMapper.deleteSelected(
                memberNo,
                ids
        );
    }


    @Transactional(readOnly = true)
    public List<CartItemDTO> getGuestCartItems(
            Map<Integer, Integer> guestCart) {

        if (guestCart == null
                || guestCart.isEmpty()) {

            return new ArrayList<>();
        }

        List<Integer> optionIds =
                new ArrayList<>(
                        guestCart.keySet()
                );

        List<CartItemDTO> cartItems =
                cartMapper
                        .getGuestCartItems(
                                optionIds
                        );

        for (CartItemDTO item
                : cartItems) {

            Integer quantity =
                    guestCart.get(
                            item.getOptionId()
                    );

            if (quantity != null) {
                item.setQuantity(
                        quantity
                );
            }
        }

        return cartItems;
    }


    @Transactional
    public int clearCart(
            Long memberNo) {

        return cartMapper
                .clearCart(
                        memberNo
                );
    }
    
    
}