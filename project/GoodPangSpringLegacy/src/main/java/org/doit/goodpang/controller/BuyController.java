package org.doit.goodpang.controller;

import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.CheckoutService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
public class BuyController {

    private final CheckoutService checkoutService;


    @PostMapping("/order/buy")
    public String buy(
    		Authentication authentication,
            @RequestParam("productNo") int productNo,
            @RequestParam("quantity") int quantity,
            @RequestParam(
                    value = "optionId",
                    required = false
            ) Integer optionId) {

    	
        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "수량은 1개 이상이어야 합니다."
            );
        }


        int checkoutNo =
                checkoutService.createBuyCheckout(
                        memberNo,
                        productNo,
                        quantity,
                        optionId
                );


        return "redirect:/order/payment?checkoutNo="
                + checkoutNo;
    }
}