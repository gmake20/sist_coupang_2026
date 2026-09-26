package org.doit.goodpang.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.CartItemDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.CartService;
import org.doit.goodpang.service.CheckoutService;
import org.doit.goodpang.service.WowMembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
@Log4j
public class CartController {

    private final CartService cartService;
    private final WowMembershipService wowMembershipService;
    private final CheckoutService checkoutService;

    @GetMapping
    public String cart(
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response,
            Model model) {

        log.info(">>>> /cart GET");

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );

        List<CartItemDTO> cartItems;

        /*
         * 로그인 사용자
         */
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember()
                              .getMemberNo();

            cartItems =
                    cartService.getCartItems(
                            memberNo
                    );

            boolean isWowMember =
                    wowMembershipService
                            .isWowMember(
                                    memberNo
                            );

            model.addAttribute(
                    "isWowMember",
                    isWowMember
            );

        /*
         * 비회원
         */
        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );

            if (guestCart == null
                    || guestCart.isEmpty()) {

                cartItems =
                        new ArrayList<>();

            } else {

                cartItems =
                        cartService
                                .getGuestCartItems(
                                        guestCart
                                );
            }

            model.addAttribute(
                    "isWowMember",
                    false
            );
        }


        /*
         * 총 상품 가격
         */
        int totalPrice = 0;

        for (CartItemDTO item : cartItems) {
            totalPrice +=
                    item.getTotalPrice();
        }


        /*
         * 장바구니 개수
         */
        int cartCount =
                cartItems.size();


        /*
         * 상단 장바구니 미리보기
         */
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


        session.setAttribute(
                "cartCount",
                cartCount
        );


        model.addAttribute(
                "cartItems",
                cartItems
        );

        model.addAttribute(
                "cartCount",
                cartCount
        );

        model.addAttribute(
                "totalPrice",
                totalPrice
        );


        return "/cart/cart";
    }
    
    @PostMapping("/add")
    public Object addCart(
            @RequestParam("optionId") Integer optionId,
            @RequestParam(value = "quantity", defaultValue = "1")
            Integer quantity,
            Authentication authentication,
            HttpSession session,
            @RequestParam(value = "ajax", required = false)
            String ajaxParam) {

        log.info(">>>> POST /cart/add");
        log.info(">>>> optionId = " + optionId);
        log.info(">>>> quantity = " + quantity);

        /*
         * 잘못된 값 방어
         */
        if (optionId == null) {
            return ResponseEntity
                    .badRequest()
                    .body(
                        createErrorResponse(
                            "잘못된 상품 정보입니다."
                        )
                    );
        }

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }


        int cartCount;


        /*
         * ====================================
         * 로그인 사용자
         * ====================================
         */
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser)
                    authentication.getPrincipal();

            Long memberNo =
                    customUser
                        .getMember()
                        .getMemberNo();


            /*
             * DB 장바구니 추가
             */
            cartService.addCart(
                    memberNo,
                    optionId,
                    quantity
            );


            /*
             * 장바구니 세션 갱신
             */
            List<CartItemDTO> cartItems =
                    cartService.getCartItems(
                            memberNo
                    );

            cartCount =
                    cartItems.size();


            session.setAttribute(
                    "cartCount",
                    cartCount
            );

            session.setAttribute(
                    "cartPreviewItems",
                    cartItems
            );
        }


        /*
         * ====================================
         * 비로그인 사용자
         * ====================================
         */
        else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );


            if (guestCart == null) {

                guestCart =
                        new HashMap<>();

                session.setAttribute(
                        "guestCart",
                        guestCart
                );
            }


            /*
             * 같은 optionId면 수량 증가
             */
            guestCart.merge(
                    optionId,
                    quantity,
                    Integer::sum
            );


            session.setAttribute(
                    "guestCart",
                    guestCart
            );


            /*
             * 비회원 장바구니 상품 정보 조회
             */
            List<CartItemDTO> cartItems =
                    cartService
                        .getGuestCartItems(
                            guestCart
                        );


            cartCount =
                    guestCart.size();


            session.setAttribute(
                    "cartCount",
                    cartCount
            );

            session.setAttribute(
                    "cartPreviewItems",
                    cartItems
            );
        }


        /*
         * AJAX 요청
         */
        if ("true".equalsIgnoreCase(ajaxParam)) {

            Map<String, Object> result =
                    new HashMap<>();

            result.put(
                    "success",
                    true
            );

            result.put(
                    "cartCount",
                    cartCount
            );

            return ResponseEntity.ok(
                    result
            );
        }


        /*
         * 일반 form 요청
         */
        return "redirect:/cart";
    }


    private Map<String, Object> createErrorResponse(
            String message) {

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "success",
                false
        );

        result.put(
                "message",
                message
        );

        return result;
    }
    
    @PostMapping("/checkout")
    public String checkout(
            @RequestParam(value = "optionId", required = false)
            String[] optionIdParams,

            @RequestParam(value = "quantity", required = false)
            String[] quantityParams,

            Authentication authentication,
            HttpSession session,
            HttpServletResponse response)
            throws IOException {

        log.info(">>>> POST /cart/checkout");

        /*
         * 로그인 확인
         */
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                        instanceof CustomUser)) {

            return "redirect:/login";
        }


        /*
         * 구매 상품 확인
         */
        if (optionIdParams == null
                || quantityParams == null
                || optionIdParams.length == 0
                || optionIdParams.length
                        != quantityParams.length) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "구매할 상품이 없습니다."
            );

            return null;
        }


        CustomUser customUser =
                (CustomUser)
                authentication.getPrincipal();

        Long memberNo =
                customUser
                    .getMember()
                    .getMemberNo();


        try {

            int[] optionIds =
                    new int[optionIdParams.length];

            int[] quantities =
                    new int[quantityParams.length];


            for (int i = 0;
                    i < optionIdParams.length;
                    i++) {

                optionIds[i] =
                        Integer.parseInt(
                                optionIdParams[i]
                        );

                quantities[i] =
                        Integer.parseInt(
                                quantityParams[i]
                        );
            }

            Integer checkoutNo =
                    checkoutService
                        .createCartCheckout(
                                memberNo,
                                optionIds,
                                quantities
                        );

            session.setAttribute(
                    "cartCheckoutNo",
                    checkoutNo
            );


            return "redirect:/order/payment?checkoutNo="
                    + checkoutNo;


        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 상품 정보입니다."
            );

            return null;

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

            return null;
        }
    }
    
    @PostMapping("/delete-selected")
    public String deleteSelected(
            @RequestParam(value = "optionId", required = false)
            int[] optionIds,
            Authentication authentication,
            HttpSession session) {

        if (optionIds == null || optionIds.length == 0) {
            return "redirect:/cart";
        }

        /*
         * 로그인 사용자
         */
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember()
                              .getMemberNo();

            cartService.deleteSelected(
                    memberNo,
                    optionIds
            );

            /*
             * 삭제 후 장바구니 세션 갱신
             */
            List<CartItemDTO> cartItems =
                    cartService.getCartItems(
                            memberNo
                    );

            session.setAttribute(
                    "cartCount",
                    cartItems.size()
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

        /*
         * 비회원
         */
        else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );

            if (guestCart != null) {

                for (int optionId : optionIds) {

                    guestCart.remove(
                            optionId
                    );
                }


                if (guestCart.isEmpty()) {

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

                } else {

                    session.setAttribute(
                            "guestCart",
                            guestCart
                    );


                    List<CartItemDTO> cartItems =
                            cartService
                                .getGuestCartItems(
                                        guestCart
                                );


                    session.setAttribute(
                            "cartCount",
                            guestCart.size()
                    );

                    session.setAttribute(
                            "cartPreviewItems",
                            cartItems
                    );
                }
            }
        }


        return "redirect:/cart";
    }
    
    @PostMapping("/delete")
    public String deleteCart(
            @RequestParam("optionId") Integer optionId,
            Authentication authentication,
            HttpSession session) {

        if (optionId == null) {
            throw new IllegalArgumentException(
                    "삭제할 상품 정보가 없습니다."
            );
        }

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser)
                    authentication.getPrincipal();

            Long memberNo =
                    customUser
                        .getMember()
                        .getMemberNo();

            cartService.deleteCart(
                    memberNo,
                    optionId
            );

            List<CartItemDTO> cartItems =
                    cartService.getCartItems(
                            memberNo
                    );

            session.setAttribute(
                    "cartCount",
                    cartItems.size()
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

        else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );


            if (guestCart != null) {

                guestCart.remove(
                        optionId
                );

                if (guestCart.isEmpty()) {

                    session.removeAttribute(
                            "guestCart"
                    );

                    session.removeAttribute(
                            "cartPreviewItems"
                    );

                    session.setAttribute(
                            "cartCount",
                            0
                    );

                } else {

                    List<CartItemDTO> cartItems =
                            cartService
                                .getGuestCartItems(
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
                            "cartPreviewItems",
                            cartItems
                    );
                }
            }
        }
        return "redirect:/cart";
    }
    
    @GetMapping("/preview")
    public String preview(
            Authentication authentication,
            HttpSession session) {

        log.info(">>>> GET /cart/preview");

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember()
                              .getMemberNo();


            List<CartItemDTO> cartItems =
                    cartService.getCartItems(
                            memberNo
                    );


            session.setAttribute(
                    "cartCount",
                    cartItems.size()
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

        else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );


            if (guestCart == null
                    || guestCart.isEmpty()) {

                session.setAttribute(
                        "cartCount",
                        0
                );

                session.removeAttribute(
                        "cartPreviewItems"
                );

            } else {

                List<CartItemDTO> cartItems =
                        cartService.getGuestCartItems(
                                guestCart
                        );


                session.setAttribute(
                        "cartCount",
                        guestCart.size()
                );

                session.setAttribute(
                        "cartPreviewItems",
                        cartItems
                );
            }
        }


        return "cart_preview";
    }
    
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam(value = "optionId", required = false)
            Integer optionId,

            @RequestParam(value = "quantity", required = false)
            Integer quantity,

            Authentication authentication,
            HttpSession session,
            HttpServletResponse response)
            throws IOException {

        if (optionId == null || quantity == null) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "잘못된 장바구니 정보입니다."
            );

            return null;
        }

        if (quantity < 1) {
            quantity = 1;
        }

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser)
                    authentication.getPrincipal();

            Long memberNo =
                    customUser
                        .getMember()
                        .getMemberNo();

            cartService.updateQuantity(
                    memberNo,
                    optionId,
                    quantity
            );

            List<CartItemDTO> cartItems =
                    cartService.getCartItems(
                            memberNo
                    );

            session.setAttribute(
                    "cartCount",
                    cartItems.size()
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

        else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );

            if (guestCart == null
                    || !guestCart.containsKey(optionId)) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "장바구니에 존재하지 않는 상품입니다."
                );

                return null;
            }

            guestCart.put(
                    optionId,
                    quantity
            );

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
        
        return "redirect:/cart";
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> cartStatus(
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response) {

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        List<CartItemDTO> cartItems;
        int cartCount;

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                        instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser)
                    authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember()
                              .getMemberNo();


            cartItems =
                    cartService.getCartItems(
                            memberNo
                    );

            cartCount =
                    cartItems.size();
        }

        else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>)
                    session.getAttribute(
                            "guestCart"
                    );


            if (guestCart == null
                    || guestCart.isEmpty()) {

                cartItems =
                        new ArrayList<>();

                cartCount = 0;

            } else {

                cartItems =
                        cartService
                            .getGuestCartItems(
                                    guestCart
                            );

                cartCount =
                        guestCart.size();
            }
        }

        session.setAttribute(
                "cartCount",
                cartCount
        );

        session.setAttribute(
                "cartPreviewItems",
                cartItems
        );

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "count",
                cartCount
        );

        result.put(
                "items",
                cartItems
        );


        return ResponseEntity.ok(
                result
        );
    }
}