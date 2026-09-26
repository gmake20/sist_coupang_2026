package org.doit.goodpang.controller;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.PaymentMethodDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.PaymentMethodService;
import org.doit.goodpang.util.PaymentMethodValidator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/payment-method")
@RequiredArgsConstructor
@Log4j
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping("/add")
    public String addForm(
            Authentication authentication,

            @RequestParam(
                    value = "redirect",
                    required = false
            )
            String redirect,

            HttpSession session) {

        getCustomUser(authentication);

        if (redirect != null
                && !redirect.isBlank()
                && redirect.startsWith("/")) {

            session.setAttribute(
                    "paymentMethodRedirect",
                    redirect
            );
        }

        return "payment_method_add";
    }

    @PostMapping("/add")
    public String add(
            Authentication authentication,

            @RequestParam(
                    value = "checkoutNo",
                    required = false
            )
            Integer checkoutNo,

            @RequestParam(
                    value = "redirect",
                    required = false
            )
            String redirect,

            @RequestParam("paymentType")
            String paymentType,

            @RequestParam(
                    value = "bankCode",
                    required = false
            )
            String bankCode,

            @RequestParam(
                    value = "accountNumber",
                    required = false
            )
            String accountNumber,

            @RequestParam(
                    value = "accountHolder",
                    required = false
            )
            String accountHolder,

            @RequestParam(
                    value = "cardCompany",
                    required = false
            )
            String cardCompany,

            @RequestParam(
                    value = "cardNumber1",
                    required = false
            )
            String cardNumber1,

            @RequestParam(
                    value = "cardNumber2",
                    required = false
            )
            String cardNumber2,

            @RequestParam(
                    value = "cardNumber3",
                    required = false
            )
            String cardNumber3,

            @RequestParam(
                    value = "cardNumber4",
                    required = false
            )
            String cardNumber4,

            @RequestParam(
                    value = "paymentDefault",
                    required = false
            )
            String paymentDefaultParam,

            HttpSession session) {

        CustomUser customUser =
                getCustomUser(authentication);

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        String error =
                PaymentMethodValidator
                        .validatePaymentType(
                                paymentType
                        );

        if (error != null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    error
            );
        }


        boolean paymentDefault =
                "Y".equals(paymentDefaultParam);


        PaymentMethodDTO dto =
                new PaymentMethodDTO();

        dto.setMemberNo(memberNo);
        dto.setPaymentType(paymentType);
        dto.setPaymentDefault(paymentDefault);

        if ("BANK".equals(paymentType)) {

            error =
                    PaymentMethodValidator
                            .validateBank(
                                    bankCode,
                                    accountNumber,
                                    accountHolder
                            );

            if (error != null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        error
                );
            }


            accountNumber =
                    accountNumber
                            .replace("-", "")
                            .trim();

            String accountLast4 =
                    accountNumber.substring(
                            accountNumber.length() - 4
                    );


            dto.setBankCode(bankCode);
            dto.setAccountLast4(accountLast4);
            dto.setAccountHolder(
                    accountHolder.trim()
            );
        }

        else if ("CARD".equals(paymentType)) {

            String cardNumber =
                    value(cardNumber1)
                    + value(cardNumber2)
                    + value(cardNumber3)
                    + value(cardNumber4);


            error =
                    PaymentMethodValidator
                            .validateCard(
                                    cardCompany,
                                    cardNumber
                            );

            if (error != null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        error
                );
            }


            String cardLast4 =
                    cardNumber.substring(
                            cardNumber.length() - 4
                    );

            dto.setCardCompany(cardCompany);
            dto.setCardLast4(cardLast4);
        }

        paymentMethodService
                .addPaymentMethod(dto);

        if (redirect != null
                && !redirect.isBlank()
                && redirect.startsWith("/")) {

            return "redirect:" + redirect;
        }

        String sessionRedirect =
                (String) session.getAttribute(
                        "paymentMethodRedirect"
                );

        if (sessionRedirect != null
                && !sessionRedirect.isBlank()
                && sessionRedirect.startsWith("/")) {

            session.removeAttribute(
                    "paymentMethodRedirect"
            );

            return "redirect:" + sessionRedirect;
        }

        if (checkoutNo != null) {

            return "redirect:/order/payment"
                    + "?checkoutNo="
                    + checkoutNo;
        }


        return "redirect:/payment-method/list";
    }

    private CustomUser getCustomUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                        instanceof CustomUser)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        return (CustomUser)
                authentication.getPrincipal();
    }


    private String value(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}