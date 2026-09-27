package org.doit.goodpang.service;

import java.util.List;

import org.doit.goodpang.domain.CheckoutDTO;
import org.doit.goodpang.domain.CheckoutItemDTO;
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
    
    @Transactional(readOnly = true)
    public CheckoutDTO getCheckout(
            int checkoutNo,
            long memberNo) {

        return checkoutMapper.getCheckout(
                checkoutNo,
                memberNo
        );
    }	
    
    @Transactional(readOnly = true)
    public CheckoutDTO getCheckout(
            int checkoutNo,
            Long memberNo) {

        return checkoutMapper.getCheckout(
                checkoutNo,
                memberNo
        );
    }


    @Transactional(readOnly = true)
    public List<CheckoutItemDTO> getCheckoutItems(
            int checkoutNo) {

        return checkoutMapper
                .getCheckoutItems(
                        checkoutNo
                );
    }


    @Transactional(readOnly = true)
    public List<CheckoutItemDTO> getCheckoutItemsProduct(
            int checkoutNo) {

        return checkoutMapper
                .getCheckoutItemsProduct(
                        checkoutNo
                );
    }

    @Transactional
    public int createCartCheckout(
            Long memberNo,
            int[] optionIds,
            int[] quantities) {

        if (optionIds == null
                || quantities == null
                || optionIds.length == 0
                || optionIds.length
                    != quantities.length) {

            throw new IllegalArgumentException(
                    "구매할 상품이 없습니다."
            );
        }


        /*
         * 1. CHECKOUT 생성
         */
        CheckoutDTO checkout =
                new CheckoutDTO();

        checkout.setMemberNo(
                memberNo
        );


        int result =
                checkoutMapper
                    .createCheckout(
                            checkout
                    );


        if (result != 1) {

            throw new IllegalStateException(
                    "CHECKOUT 생성에 실패했습니다."
            );
        }


        int checkoutNo =
                checkout.getCheckoutNo();


        /*
         * 2. CHECKOUT_ITEM 생성
         */
        int itemCount = 0;


        for (int i = 0;
                i < optionIds.length;
                i++) {

            int optionId =
                    optionIds[i];

            int quantity =
                    quantities[i];


            if (quantity < 1) {
                continue;
            }


            int rowCount =
                    checkoutMapper
                        .addCheckoutItem(
                                checkoutNo,
                                optionId,
                                quantity
                        );


            if (rowCount > 0) {
                itemCount++;
            }
        }


        /*
         * 유효한 상품이 하나도 없으면
         * RuntimeException 발생
         *
         * @Transactional 때문에
         * CHECKOUT까지 rollback
         */
        if (itemCount == 0) {

            throw new IllegalArgumentException(
                    "구매할 수 있는 상품이 없습니다."
            );
        }


        /*
         * 3. 금액 계산
         *
         * WOW 회원이면 무료배송
         */
        int amountResult =
                checkoutMapper
                    .updateCheckoutAmountV2(
                            checkoutNo
                    );


        if (amountResult != 1) {

            throw new IllegalStateException(
                    "CHECKOUT 금액 계산에 실패했습니다."
            );
        }


        return checkoutNo;
    }
}