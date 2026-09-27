package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.CartItemDTO;

public interface CartMapper {

    List<CartItemDTO> getCartItems(
            @Param("memberNo") Long memberNo
    );

    int addCart(
            @Param("memberNo") Long memberNo,
            @Param("optionId") Integer optionId,
            @Param("quantity") Integer quantity
    );

    int updateQuantity(
            @Param("memberNo") Long memberNo,
            @Param("optionId") Integer optionId,
            @Param("quantity") Integer quantity
    );

    int deleteCart(
            @Param("memberNo") Long memberNo,
            @Param("optionId") Integer optionId
    );

    CartItemDTO getCartItemByOptionId(
            @Param("optionId") Integer optionId
    );

    int getCartCount(
            @Param("memberNo") Long memberNo
    );

    int deleteSelected(
            @Param("memberNo") Long memberNo,
            @Param("optionIds") List<Integer> optionIds
    );

    List<CartItemDTO> getGuestCartItems(
            @Param("optionIds") List<Integer> optionIds
    );

    int clearCart(
            @Param("memberNo") Long memberNo
    );
}