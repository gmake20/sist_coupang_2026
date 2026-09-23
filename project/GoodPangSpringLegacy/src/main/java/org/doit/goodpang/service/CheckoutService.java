package org.doit.goodpang.service;

import org.doit.goodpang.mapper.CheckoutMapper;
import org.doit.goodpang.mapper.WowMembershipMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
@RequiredArgsConstructor
public class CheckoutService {

    private final CheckoutMapper checkoutMapper;
    private final WowMembershipMapper wowMembershipMapper;


    @Transactional
    public int createBuyCheckout(
            Long memberNo,
            int productNo,
            int quantity,
            Integer optionId) {
    	
        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "수량은 1개 이상이어야 합니다."
            );
        }

        Integer productPrice =
                checkoutMapper.getProductPrice(
                        productNo
                );

        if (productPrice == null) {

            throw new IllegalArgumentException(
                    "상품을 찾을 수 없습니다."
            );
        }

        int optionPrice = 0;

        if (optionId != null) {

            Integer price =
                    checkoutMapper.getOptionPrice(
                            productNo,
                            optionId
                    );

            if (price == null) {

                throw new IllegalArgumentException(
                        "잘못된 상품 옵션입니다."
                );
            }

            optionPrice = price;
        }
        
        int unitPrice =
                productPrice
                + optionPrice;


        int productAmount =
                unitPrice
                * quantity;


        int instantDiscount = 0;
        int couponDiscount = 0;
        int cashUsed = 0;

        boolean isWowMember =
                wowMembershipMapper.isWowMember(
                        memberNo
                ) > 0;

        int deliveryFee;

        if (isWowMember) {

            deliveryFee = 0;

        } else {

            deliveryFee =
                    productAmount >= 19800
                    ? 0
                    : 3000;
        }

        int totalPrice =
                productAmount
                - instantDiscount
                - couponDiscount
                - cashUsed
                + deliveryFee;

        int checkoutNo =
                checkoutMapper.getNextCheckoutNo();


        int checkoutResult =
                checkoutMapper.insertCheckout(
                        checkoutNo,
                        memberNo,
                        productAmount,
                        instantDiscount,
                        couponDiscount,
                        cashUsed,
                        deliveryFee,
                        totalPrice
                );

        if (checkoutResult != 1) {

            throw new IllegalStateException(
                    "CHECKOUT 생성 실패"
            );
        }
        int itemResult =
                checkoutMapper.insertCheckoutItem(
                        checkoutNo,
                        productNo,
                        optionId,
                        quantity,
                        unitPrice
                );

        if (itemResult != 1) {

            throw new IllegalStateException(
                    "CHECKOUT_ITEM 생성 실패"
            );
        }


        return checkoutNo;
    }
}