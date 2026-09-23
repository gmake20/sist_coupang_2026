package org.doit.goodpang.controller;

import java.util.List;

import org.doit.goodpang.domain.AddressDTO;
import org.doit.goodpang.domain.CheckoutDTO;
import org.doit.goodpang.domain.OrderCompleteDTO;
import org.doit.goodpang.domain.PaymentMethodDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.AddressService;
import org.doit.goodpang.service.CheckoutService;
import org.doit.goodpang.service.OrderService;
import org.doit.goodpang.service.OrderService.OrderResult;
import org.doit.goodpang.service.OrderService.StockOutException;
import org.doit.goodpang.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AddressService addressService;
    private final PaymentMethodService paymentMethodService;
    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    public String checkout(
    		Authentication authentication,

            @RequestParam("checkoutNo")
            int checkoutNo,

            @RequestParam("addressNo")
            int addressNo,

            @RequestParam("paymentMethod")
            String paymentMethod,

            @RequestParam(
                value = "paymentMethodNo",
                required = false
            )
            Integer bankPaymentMethodNo,

            @RequestParam(
                value = "cardPaymentMethodNo",
                required = false
            )
            Integer cardPaymentMethodNo,

            RedirectAttributes rttr) {

    	CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();
        
        Integer paymentMethodNo;

        switch (paymentMethod) {

        case "BANK_TRANSFER":

            if (bankPaymentMethodNo == null) {
                throw new IllegalArgumentException(
                        "계좌를 선택해주세요."
                );
            }

            paymentMethodNo =
                    bankPaymentMethodNo;

            break;

        case "CARD":

            if (cardPaymentMethodNo == null) {
                throw new IllegalArgumentException(
                        "카드를 선택해주세요."
                );
            }

            paymentMethodNo =
                    cardPaymentMethodNo;

            break;

        case "COUPAY_MONEY":

            paymentMethodNo = null;
            break;

        default:

            throw new IllegalArgumentException(
                    "지원하지 않는 결제수단입니다."
            );
        }

        try {

            OrderResult result =
                    orderService.createOrder(
                            checkoutNo,
                            memberNo,
                            addressNo,
                            paymentMethod,
                            paymentMethodNo
                    );

            if (result.isAlreadyCompleted()) {

                return "redirect:/order/already-completed";
            }

            rttr.addAttribute(
                    "orderNo",
                    result.getOrderNo()
            );

            return "redirect:/order/complete";

        } catch (StockOutException e) {

            rttr.addAttribute(
                    "checkoutNo",
                    checkoutNo
            );

            rttr.addAttribute(
                    "stockFail",
                    e.getStockFail()
                     .getProductName()
            );

            rttr.addAttribute(
                    "stockLeft",
                    e.getStockFail()
                     .getLeft()
            );

            return "redirect:/order/payment";
        }
    }	
    

    @GetMapping("/complete")
    public String complete(
            Authentication authentication,
            @RequestParam("orderNo") int orderNo,
            Model model) {


    	CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();
        OrderCompleteDTO orderComplete =
                orderService.getOrderComplete(
                        orderNo,
                        memberNo
                );

        if (orderComplete == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "주문 정보를 찾을 수 없습니다."
            );
        }

        model.addAttribute(
                "orderComplete",
                orderComplete
        );

        model.addAttribute(
                "orderNo",
                orderNo
        );

        return "order/complete";
    }
    
    
    @GetMapping("/payment")
    public String payment(
            Authentication authentication,
            @RequestParam("checkoutNo") int checkoutNo,
            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        AddressDTO address =
                addressService.getAddress(memberNo);

        List<AddressDTO> addressList =
                addressService.getAddressList(memberNo);

		
		List<PaymentMethodDTO> paymentMethods =
		paymentMethodService.getBankMethods(memberNo);
		
		List<PaymentMethodDTO> cardMethods =
		paymentMethodService.getCardMethods(memberNo);
		
		CheckoutDTO checkout =
	            checkoutService.getCheckout(
	                    checkoutNo,
	                    memberNo
	            );
		 
        model.addAttribute(
                "address",
                address
        );

        model.addAttribute(
                "addressList",
                addressList
        );

		
		model.addAttribute( "paymentMethods", paymentMethods );
		  
		model.addAttribute( "cardMethods", cardMethods );
		
	    model.addAttribute(
	            "checkout",
	            checkout
	    );
		

        model.addAttribute(
                "checkoutNo",
                checkoutNo
        );

        return "order/payment";
    }
}