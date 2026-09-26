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
import org.doit.goodpang.service.CartSessionService;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
    private final CartSessionService cartSessionService;

    @GetMapping
    public String cart(
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response,
            Model model) {

        log.info(">>>> GET /cart");

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        List<CartItemDTO> cartItems;

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartItems =
                    cartService.getCartItems(memberNo);

            boolean isWowMember =
                    wowMembershipService.isWowMember(memberNo);

            model.addAttribute("isWowMember", isWowMember);

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            if (guestCart == null || guestCart.isEmpty()) {
                cartItems = new ArrayList<>();
            } else {
                cartItems = cartService.getGuestCartItems(guestCart);
            }

            model.addAttribute("isWowMember", false);
        }

        int totalPrice = 0;

        for (CartItemDTO item : cartItems) {
            totalPrice += item.getTotalPrice();
        }

        int cartCount = cartItems.size();

        if (cartItems.isEmpty()) {
            session.removeAttribute("cartPreviewItems");
        } else {
            session.setAttribute("cartPreviewItems", cartItems);
        }

        session.setAttribute("cartCount", cartCount);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("totalPrice", totalPrice);

        return "/cart/cart";
    }

    @PostMapping(
            value = "/add",
            produces = "application/json;charset=UTF-8"
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCart(
            @RequestParam("optionId") Integer optionId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            Authentication authentication,
            HttpSession session) {

        log.info(">>>> POST /cart/add");
        log.info(">>>> optionId = " + optionId);
        log.info(">>>> quantity = " + quantity);

        if (quantity == null || quantity < 1) {
            quantity = 1;
        }

        int cartCount;

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartService.addCart(memberNo, optionId, quantity);
            cartSessionService.refreshCartSession(session, memberNo);

            Integer sessionCartCount =
                    (Integer) session.getAttribute("cartCount");

            cartCount =
                    sessionCartCount != null ? sessionCartCount : 0;

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            if (guestCart == null) {
                guestCart = new HashMap<>();
            }

            guestCart.merge(optionId, quantity, Integer::sum);

            cartSessionService.refreshGuestCartSession(
                    session,
                    guestCart
            );

            cartCount = guestCart.size();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("cartCount", cartCount);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/checkout")
    public String checkout(
            @RequestParam(value = "optionId", required = false) String[] optionIdParams,
            @RequestParam(value = "quantity", required = false) String[] quantityParams,
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response)
            throws IOException {

        log.info(">>>> POST /cart/checkout");

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {

            return "redirect:/login";
        }

        if (optionIdParams == null
                || quantityParams == null
                || optionIdParams.length == 0
                || optionIdParams.length != quantityParams.length) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "구매할 상품이 없습니다."
            );

            return null;
        }

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        try {

            int[] optionIds =
                    new int[optionIdParams.length];

            int[] quantities =
                    new int[quantityParams.length];

            for (int i = 0; i < optionIdParams.length; i++) {
                optionIds[i] =
                        Integer.parseInt(optionIdParams[i]);

                quantities[i] =
                        Integer.parseInt(quantityParams[i]);
            }

            Integer checkoutNo =
                    checkoutService.createCartCheckout(
                            memberNo,
                            optionIds,
                            quantities
                    );

            session.setAttribute(
                    "cartCheckoutNo",
                    checkoutNo
            );

            return "redirect:/order/payment?checkoutNo=" + checkoutNo;

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
            @RequestParam(value = "optionId", required = false) int[] optionIds,
            Authentication authentication,
            HttpSession session) {

        log.info(">>>> POST /cart/delete-selected");

        if (optionIds == null || optionIds.length == 0) {
            return "redirect:/cart";
        }

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartService.deleteSelected(memberNo, optionIds);
            cartSessionService.refreshCartSession(session, memberNo);

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            if (guestCart != null) {
                for (int optionId : optionIds) {
                    guestCart.remove(optionId);
                }

                cartSessionService.refreshGuestCartSession(
                        session,
                        guestCart
                );
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/delete")
    public String deleteCart(
            @RequestParam("optionId") Integer optionId,
            Authentication authentication,
            HttpSession session) {

        log.info(">>>> POST /cart/delete");
        log.info(">>>> optionId = " + optionId);

        if (optionId == null) {
            throw new IllegalArgumentException(
                    "삭제할 상품 정보가 없습니다."
            );
        }

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartService.deleteCart(memberNo, optionId);
            cartSessionService.refreshCartSession(session, memberNo);

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            if (guestCart != null) {
                guestCart.remove(optionId);

                cartSessionService.refreshGuestCartSession(
                        session,
                        guestCart
                );
            }
        }

        return "redirect:/cart";
    }

    @GetMapping("/preview")
    public String preview(
            Authentication authentication,
            HttpSession session) {

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartSessionService.refreshCartSession(
                    session,
                    memberNo
            );

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            cartSessionService.refreshGuestCartSession(
                    session,
                    guestCart
            );
        }

        return "/cart/cart_preview";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam(value = "optionId", required = false) Integer optionId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
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
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartService.updateQuantity(
                    memberNo,
                    optionId,
                    quantity
            );

            cartSessionService.refreshCartSession(
                    session,
                    memberNo
            );

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            if (guestCart == null
                    || !guestCart.containsKey(optionId)) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "장바구니에 존재하지 않는 상품입니다."
                );

                return null;
            }

            guestCart.put(optionId, quantity);

            cartSessionService.refreshGuestCartSession(
                    session,
                    guestCart
            );
        }

        return "redirect:/cart";
    }

    @GetMapping(
            value = "/status",
            produces = "application/json;charset=UTF-8"
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cartStatus(
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response) {

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {

            CustomUser customUser =
                    (CustomUser) authentication.getPrincipal();

            Long memberNo =
                    customUser.getMember().getMemberNo();

            cartSessionService.refreshCartSession(
                    session,
                    memberNo
            );

        } else {

            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart =
                    (Map<Integer, Integer>) session.getAttribute("guestCart");

            cartSessionService.refreshGuestCartSession(
                    session,
                    guestCart
            );
        }

        Integer cartCount =
                (Integer) session.getAttribute("cartCount");

        @SuppressWarnings("unchecked")
        List<CartItemDTO> cartItems =
                (List<CartItemDTO>) session.getAttribute(
                        "cartPreviewItems"
                );

        if (cartCount == null) {
            cartCount = 0;
        }

        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", cartCount);
        result.put("items", cartItems);

        return ResponseEntity.ok(result);
    }
}