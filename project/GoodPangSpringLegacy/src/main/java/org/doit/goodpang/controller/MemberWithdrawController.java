package org.doit.goodpang.controller;

import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.MemberService;
import org.doit.goodpang.service.WowMembershipService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
@RequestMapping("/member/withdraw")
public class MemberWithdrawController {

    private final MemberService memberService;
    private final WowMembershipService wowMembershipService;

    @GetMapping
    public String withdrawForm(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {

            return "redirect:/login";
        }

        return "member/member_withdraw";
    }


    @PostMapping
    public String withdraw(
            @RequestParam(value = "password", required = false)
            String password,

            @RequestParam(value = "agree", required = false)
            String agree,

            Authentication authentication,
            HttpSession session,
            Model model) {


        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {

            return "redirect:/login";
        }


        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        MemberVO loginMember =
                customUser.getMember();


        if (loginMember == null) {
            return "redirect:/login";
        }


        // 유의사항 동의 확인
        if (!"Y".equals(agree)) {

            model.addAttribute(
                    "errorMessage",
                    "회원 탈퇴 유의사항에 동의해주세요."
            );

            return "member/member_withdraw";
        }


        // 비밀번호 입력 확인
        if (password == null
                || password.trim().isEmpty()) {

            model.addAttribute(
                    "errorMessage",
                    "비밀번호를 입력해주세요."
            );

            return "member/member_withdraw";
        }


        Long memberNo =
                loginMember.getMemberNo();


        try {

            memberService.verifyWithdrawPassword(
                    memberNo,
                    password
            );

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "member/member_withdraw";
        }


        // 비밀번호 검증 완료 표시
        session.setAttribute(
                "withdrawVerified",
                true
        );


        return "redirect:/member/withdraw/check";
    }

    @GetMapping("/check")
    public String withdrawCheck(
            Authentication authentication,
            HttpSession session,
            Model model) {

        // 로그인 확인
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {

            return "redirect:/login";
        }


        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        MemberVO loginMember =
                customUser.getMember();


        if (loginMember == null) {
            return "redirect:/login";
        }


        // 이전 단계에서 비밀번호 검증했는지 확인
        Boolean withdrawVerified =
                (Boolean) session.getAttribute(
                        "withdrawVerified"
                );


        if (!Boolean.TRUE.equals(withdrawVerified)) {

            return "redirect:/member/withdraw";
        }


        Long memberNo =
                loginMember.getMemberNo();


        // WOW 멤버십 조회
        boolean wowActive =
                wowMembershipService
                        .isWowMember(memberNo);


        Date nextPaymentDate =
                wowMembershipService
                        .nextPaymentDate(memberNo);


        boolean wowCancelPending =
                wowMembershipService
                        .isCancelPending(memberNo);

        model.addAttribute(
                "wowActive",
                wowActive
        );

        model.addAttribute(
                "wowCancelPending",
                wowCancelPending
        );

        model.addAttribute(
                "nextPaymentDate",
                nextPaymentDate
        );


        // 아직 실제 주문/환불 조회 연결 전
        model.addAttribute(
                "activeOrders",
                Collections.emptyList()
        );

        model.addAttribute(
                "refundList",
                Collections.emptyList()
        );


        model.addAttribute(
                "goodPayBalance",
                0
        );

        model.addAttribute(
                "refundAmount",
                0
        );

        model.addAttribute(
                "couponCount",
                0
        );


        // 현재는 임시 false
        boolean withdrawBlocked = false;

        model.addAttribute(
                "withdrawBlocked",
                withdrawBlocked
        );


        return "member/member_withdraw_check";
    }
    
    @PostMapping("/complete")
    public String withdrawComplete(
            Authentication authentication,
            HttpSession session,
            Model model) {

        // 로그인 확인
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUser)) {

            return "redirect:/login";
        }


        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        MemberVO loginMember =
                customUser.getMember();

        if (loginMember == null) {
            return "redirect:/login";
        }


        // 비밀번호 검증 단계 통과 여부
        Boolean withdrawVerified =
                (Boolean) session.getAttribute(
                        "withdrawVerified"
                );

        if (!Boolean.TRUE.equals(withdrawVerified)) {

            return "redirect:/member/withdraw";
        }


        Long memberNo =
                loginMember.getMemberNo();

        boolean withdrawalBlocked =
                wowMembershipService
                        .isWithdrawalBlocked(memberNo);


        if (withdrawalBlocked) {

            model.addAttribute(
                    "withdrawError",
                    "와우 멤버십 해지 신청 후 회원 탈퇴가 가능합니다."
            );

            return "member_withdraw_check";
        }


        try {

            memberService.withdrawMember(
                    memberNo
            );


            log.info(
                    "회원 탈퇴 완료 memberNo="
                    + memberNo
            );


            // Spring Security 인증 제거
            SecurityContextHolder.clearContext();


            // 세션 제거
            session.invalidate();


            return "member/member_withdraw_complete";


        } catch (IllegalStateException e) {
            model.addAttribute(
                    "withdrawError",
                    e.getMessage()
            );

            return "member/member_withdraw_check";


        } catch (Exception e) {

            model.addAttribute(
                    "withdrawError",
                    "회원 탈퇴 처리 중 오류가 발생했습니다."
            );

            return "member/member_withdraw_check";
        }
    }
}