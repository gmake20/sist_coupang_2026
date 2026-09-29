package org.doit.goodpang.controller;

import java.util.List;

import org.doit.goodpang.domain.AddressDTO;
import org.doit.goodpang.domain.CheckoutDTO;
import org.doit.goodpang.domain.CheckoutItemDTO;
import org.doit.goodpang.domain.OrderCompleteDTO;
import org.doit.goodpang.domain.OrderDetailDTO;
import org.doit.goodpang.domain.OrderItemDTO;
import org.doit.goodpang.domain.PaymentMethodDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.AddressService;
import org.doit.goodpang.service.CartService;
import org.doit.goodpang.service.CheckoutService;
import org.doit.goodpang.service.OrderCancelService;
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
    private final CartService cartService;
    
    
    // 1. 주문 목록 페이지 (/order/order_list)
    @GetMapping("/order_list")
    public String getOrderList(
    		Authentication authentication,
            @RequestParam(value = "year", defaultValue = "recent") String yearFilter,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        // 세션 또는 스프링 시큐리티 처리 전 임시 테스트용 회원번호
        //  int memberNo = 1; 
    	
		
		 CustomUser customUser = (CustomUser) authentication.getPrincipal();
		 
		 Long memberNo = customUser.getMember() .getMemberNo();
		 

        int pageSize = 5;
       // System.out.println("😍😍😍yearFilter" +yearFilter);
        log.info("OrderController 주문리스트 😍😍😍yearFilter" +yearFilter);
        int totalCount = orderService.getOrderCount(memberNo, yearFilter);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<OrderItemDTO> orderList = orderService.getOrderListPaged(memberNo, yearFilter, page, pageSize);
        
        System.out.println("orderList size = " +
                (orderList == null ? "null" : orderList.size()));
        
        model.addAttribute("orderList", orderList);
        model.addAttribute("yearFilter", yearFilter);
        model.addAttribute("curPage", page);
        model.addAttribute("totalPages", totalPages);

        return "order.order_list"; // /WEB-INF/views/order/order_list.jsp
    }

    // 2. 주문 상세 페이지 (/order/order_detail)
    @GetMapping("/order_detail")
    public String getOrderDetail(
    		Authentication authentication,
            @RequestParam("orderNo") int orderNo,
            Model model) {    	
    	
    	log.info("========== OrderController 주문상세  진입 ==========");

       // int memberNo = 1; // 임시 회원번호
    	
         CustomUser customUser = (CustomUser) authentication.getPrincipal();
		 
		 Long memberNo = customUser.getMember() .getMemberNo();

        List<OrderDetailDTO> detailList = orderService.getOrderDetailList(memberNo, orderNo);
        OrderDetailDTO orderInfo = null;
        

        if (detailList != null && !detailList.isEmpty()) {
            orderInfo = detailList.get(0); // 공통 주문정보용 대표 객체
        }

        model.addAttribute("detailList", detailList);
        model.addAttribute("orderInfo", orderInfo);
        
        System.out.println("========== 주문상세 ==========");
        System.out.println("orderNo = " + orderNo);
        System.out.println("memberNo = " + memberNo);
        System.out.println("detailList size = " +
                (detailList == null ? "null" : detailList.size()));

        if (detailList != null && !detailList.isEmpty()) {
            System.out.println("orderDetailNo = "
                    + detailList.get(0).getOrderDetailNo());

            System.out.println("productNo = "
                    + detailList.get(0).getProductNo());

            System.out.println("productName = "
                    + detailList.get(0).getProductName());
        }

        System.out.println("============================");

        return "order.order_detail"; // /WEB-INF/views/order/order_detail.jsp
    }

    private final OrderCancelService orderCancelService; // 서비스 주입[cite: 1, 8]

    // 주문 취소 처리
    
    @GetMapping("/order_cancel")
    public String orderCancelForm(@RequestParam("orderNo") int orderNo, Model model) {
        log.info("> 주문 취소 페이지(Form) 진입 - orderNo: " + orderNo);

     // 주문번호에 해당하는 모든 상품 목록 조회
        List<OrderDetailDTO> cancelList = orderCancelService.getCancelInfo(orderNo);

        if (cancelList != null && !cancelList.isEmpty()) {
            model.addAttribute("cancelList", cancelList); // ⭕ 리스트 전체 전달
			/*
			 * model.addAttribute("cancelList", cancelList.get(0)); // 공통 대표 정보 (주문일, 배송비 등)
			 */        }

        return "order/order_cancel";
    }
         

      
    
    @PostMapping("/order_cancel")
    public String orderCancelAction(
            @RequestParam("orderNo") int orderNo,
          
            @RequestParam("cancelReason") String cancelReason,
            Authentication authentication,
            RedirectAttributes rttr // 리다이렉트 시 1회성 메시지 전달용[cite: 7]
    ) {
        log.info("> 주문 취소 컨트롤러 진입 - orderNo: " + orderNo + ", reason: " + cancelReason);
        
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        long memberNo = customUser.getMember().getMemberNo();
        
        // 1. 주문 취소 비즈니스 로직 수행
        Integer result = orderCancelService.orderCancelAction(orderNo, memberNo, cancelReason);

        // 2. 결과 처리 메시지 담기
        if (result != null && result > 0) {
            rttr.addFlashAttribute("msg", "주문 취소가 성공적으로 완료되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "주문 취소 처리에 실패했습니다.");
        }

        // 3. ⭕ 올바른 리다이렉트: 주문 취소 후 이동할 '목록 페이지' 경로로 리다이렉트!
        // (예: /mypage/order_list 또는 프로젝트의 실제 주문 목록 URL)
        return "redirect:/order/cancel_confirm"; 
    }
    

    // 3. 취소 완료 화면 (GET 요청: /order/cancel_confirm)
   
    @GetMapping("/cancel_confirm")
    public String orderCancelConfirm() {
        return "order/cancel_confirm"; // cancel_confirm.jsp 전달
    }
   // 취소내역조회
    
 // OrderController.java 내 추가
   
    @GetMapping("/cancel_history")
    public String getCancelHistory(
            Authentication authentication,
            @RequestParam(value = "page", defaultValue = "1") int curPage,
            Model model) {

        log.info("========== OrderController 취소 내역 진입 ==========");

        // 1. 로그인 인증 및 회원번호 추출
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        long memberNo = customUser.getMember().getMemberNo();

        // 2. 페이지당 5개 설정 (기존 서블릿 로직 동일)
        int pageSize = 5;

        int totalCount = orderCancelService.getCancelHistoryCount(memberNo);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;

        // 3. 목록 조회
        List<OrderDetailDTO> cancelList = orderCancelService.getCancelHistoryPaged(memberNo, curPage, pageSize);

        // 4. Model 바인딩
        model.addAttribute("cancelList", cancelList);
        model.addAttribute("curPage", curPage);
        model.addAttribute("totalPages", totalPages);
   
        // 5. Tiles Definition 반환
        return "order.cancel_history";
    }
    //취소상세
 

    @GetMapping("/cancel_detail")
    public String getCancelDetail(
            Authentication authentication,
            @RequestParam("orderNo") int orderNo,
            Model model) {

        log.info("========== OrderController 취소상세 진입 - orderNo: " + orderNo + " ==========");

        // 1. 취소 상세 상품 목록 및 대표 정보 조회[cite: 3, 7]
        List<OrderDetailDTO> cancelDetailList = orderCancelService.getCancelDetailList(orderNo);
        OrderDetailDTO cancelInfo2 = null;

        if (cancelDetailList != null && !cancelDetailList.isEmpty()) {
            cancelInfo2 = cancelDetailList.get(0); // 공통 대표 정보 객체[cite: 3]
        }

        // 2. Model 데이터 바인딩[cite: 3]
        model.addAttribute("cancelDetailList", cancelDetailList);
        model.addAttribute("cancelInfo2", cancelInfo2);

        // 3. Tiles Definition 이름 반환
        return "order.cancel_detail";
    }
    
    //order_tracking
    
    @GetMapping("/order_tracking")
    public String getOrderTracking(
            Authentication authentication,
            @RequestParam("orderNo") int orderNo,
            Model model) {

        log.info("========== OrderController 배송 조회 진입 - orderNo: " + orderNo + " ==========");

        // 1. 로그인 회원 정보 검증 (Spring Security)
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long memberNo = customUser.getMember().getMemberNo();

        // 2. 배송 추적 데이터 서비스 호출
        OrderDetailDTO trackingInfo = orderService.getTrackingInfo(orderNo);

        // 3. Model 바인딩
        model.addAttribute("trackingInfo", trackingInfo);

        // 4. Tiles Definition 이름 리턴
        return "order.order_tracking";
    }
    
    // 끝이요

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
        
        cartService.clearCart(memberNo);

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
      
      List<CheckoutItemDTO> checkoutItems =
	        checkoutService.getCheckoutItemsPRODUCT(
	                checkoutNo
	        );

      
      CheckoutDTO checkout =
               checkoutService.getCheckout(
                       checkoutNo,
                       memberNo
               );
       
        model.addAttribute(
                "address",
                address
        );
   
        
        model.addAttribute("checkoutItems", checkoutItems);
        model.addAttribute("addressList", addressList);
        model.addAttribute("paymentMethods", paymentMethods );
        model.addAttribute("cardMethods", cardMethods );
      
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