package org.doit.goodpang.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.PaymentMethodDTO;
import org.doit.goodpang.domain.WowMembershipDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.PaymentMethodService;
import org.doit.goodpang.service.WowMembershipService;
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
@RequestMapping("/wow")
@RequiredArgsConstructor
public class WowMembershipController {

    private final WowMembershipService wowMembershipService;
    private final PaymentMethodService paymentMethodService;

    @GetMapping("/membership")
    public String membership(
            Authentication authentication,
            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        WowMembershipDTO membership =
                wowMembershipService
                        .getMembershipDetail(memberNo);

        model.addAttribute(
                "membership",
                membership
        );

        model.addAttribute("savingAmount", 17000);
        model.addAttribute("deliverySaving", 0);
        model.addAttribute("discountSaving", 14000);
        model.addAttribute("eatsSaving", 3000);
        model.addAttribute("cashSaving", 0);
        model.addAttribute("returnSaving", 0);
        model.addAttribute("globalSaving", 0);

        return "wow/wowmembership";
    }
    
    @GetMapping("/join")
    public String joinForm(
            Authentication authentication,
            @RequestParam(
                    value = "mode",
                    required = false
            ) String mode,
            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        log.info(
                "와우 멤버십 가입 화면 memberNo : "
                + memberNo
        );


        if (wowMembershipService.isWowMember(memberNo)) {

            return "redirect:/wow/membership";
        }

        List<PaymentMethodDTO> paymentMethods =
                paymentMethodService
                        .getPaymentMethods(memberNo);

        model.addAttribute(
                "paymentMethods",
                paymentMethods
        );

        if ("modal".equals(mode)) {

            return "wow/wow_join_modal";
        }

        return "wow/wow_join";
    }

    @PostMapping("/join")
    public String join(
            Authentication authentication,

            @RequestParam(
                    value = "paymentMethodNo",
                    required = false
            )
            Integer paymentMethodNo,

            @RequestParam(
                    value = "productNo",
                    required = false
            )
            String productNo,

            @RequestParam(
                    value = "afterWowJoin",
                    required = false
            )
            String afterWowJoin,

            HttpSession session,
            RedirectAttributes rttr) {


        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();


        if (wowMembershipService.isWowMember(memberNo)) {

            // 상품 구매 과정에서 와우 가입 페이지로 온 경우
            if ("buy".equals(afterWowJoin)) {

                return "forward:/order/buy";
            }

            return "redirect:/wow/membership";
        }

        if (paymentMethodNo == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "결제수단을 선택해주세요."
            );
        }


        // 해당 회원의 결제수단인지 확인
        boolean valid =
                paymentMethodService
                        .existsPaymentMethod(
                                memberNo,
                                paymentMethodNo
                        );


        if (!valid) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "사용할 수 없는 결제수단입니다."
            );
        }

        wowMembershipService.joinMembership(
                memberNo,
                paymentMethodNo
        );

        session.setAttribute(
                "wowMember",
                true
        );

        if (productNo != null
                && !productNo.isBlank()) {

            rttr.addAttribute(
                    "productNo",
                    productNo
            );

            return "redirect:/wow/welcome";
        }


        return "redirect:/wow/welcome";
    }
    
    @GetMapping("/welcome")
    public String welcome(
            Authentication authentication,

            @RequestParam(
                    value = "productNo",
                    required = false
            )
            String productNo,

            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        // 실제 DB에서 와우 회원 여부 확인
        boolean wowMember =
                wowMembershipService
                        .isWowMember(memberNo);

        if (!wowMember) {

            return "redirect:/wow/join";
        }

        model.addAttribute(
                "returnProductNo",
                productNo
        );


        return "wow/wow_welcome";
    }
    
    @PostMapping("/cancel")
    public String cancel(
            Authentication authentication,
            HttpSession session,
            RedirectAttributes rttr) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();


        // 현재 멤버십 정보 조회
        WowMembershipDTO membership =
                wowMembershipService
                        .findByMemberNo(memberNo);

        if (membership == null) {

            session.setAttribute(
                    "wowMember",
                    false
            );

            rttr.addAttribute(
                    "notMember",
                    "Y"
            );

            return "redirect:/wow/membership";
        }


        String status =
                membership.getStatus();

        if ("CANCEL_PENDING".equals(status)) {

            session.setAttribute(
                    "wowMember",
                    true
            );

            rttr.addAttribute(
                    "alreadyCanceled",
                    "Y"
            );

            return "redirect:/wow/membership";
        }

        if ("EXPIRED".equals(status)
                || "SUSPENDED".equals(status)) {

            session.setAttribute(
                    "wowMember",
                    false
            );

            rttr.addAttribute(
                    "alreadyExpired",
                    "Y"
            );

            return "redirect:/wow/membership";
        }

        if ("ACTIVE".equals(status)) {

            int result =
                    wowMembershipService
                            .cancelMembership(memberNo);

            if (result > 0) {

                session.setAttribute(
                        "wowMember",
                        true
                );

                rttr.addAttribute(
                        "canceled",
                        "Y"
                );

                return "redirect:/wow/membership";
            }
        }

        boolean wowMember =
                wowMembershipService
                        .isWowMember(memberNo);

        session.setAttribute(
                "wowMember",
                wowMember
        );


        return "redirect:/wow/membership";
    }
}