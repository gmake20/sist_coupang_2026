package org.doit.goodpang.controller;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/modify")
    public String modify(
            Authentication authentication,
            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember().getMemberNo();

        MemberVO member =
                memberService.getMember(memberNo);

        if (member == null) {
            throw new IllegalArgumentException(
                    "회원 정보를 찾을 수 없습니다."
            );
        }

        // 3. JSP에 전달
        model.addAttribute(
                "member",
                member
        );

        // 4. JSP 이동
        return "member/userModify";
    }
    
    @PostMapping("/update")
    public String update(
            @RequestParam("type") String type,
            @RequestParam(value = "newEmail", required = false) String newEmail,
            @RequestParam(value = "newPhone", required = false) String newPhone,
            Authentication authentication,
            RedirectAttributes rttr) {

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

        Long memberNo =
                loginMember.getMemberNo();

        int result = 0;

        // 이메일 변경
        if ("email".equals(type)) {

            if (newEmail == null
                    || newEmail.trim().isEmpty()) {

                rttr.addFlashAttribute(
                        "error",
                        "변경할 이메일을 입력해주세요."
                );

                return "redirect:/member/modify";
            }

            result =
                    memberService.updateEmail(
                            memberNo,
                            newEmail
                    );

            if (result > 0) {
                loginMember.setEmail(newEmail);
            }

        }

        // 휴대폰 번호 변경
        else if ("phone".equals(type)) {

            if (newPhone == null
                    || newPhone.trim().isEmpty()) {

                rttr.addFlashAttribute(
                        "error",
                        "변경할 휴대폰 번호를 입력해주세요."
                );

                return "redirect:/member/modify";
            }

            result =
                    memberService.updatePhone(
                            memberNo,
                            newPhone
                    );

            if (result > 0) {
                loginMember.setPhone(newPhone);
            }

        } else {

            return "redirect:/member/modify";
        }


        if (result > 0) {

            rttr.addFlashAttribute(
                    "message",
                    "회원정보가 변경되었습니다."
            );

        } else {

            rttr.addFlashAttribute(
                    "error",
                    "회원정보 변경에 실패했습니다."
            );
        }

        return "redirect:/member/modify";
    }
    
    @PostMapping("/password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Authentication authentication,
            RedirectAttributes rttr) {

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

        if (currentPassword == null
                || currentPassword.trim().isEmpty()
                || newPassword == null
                || newPassword.trim().isEmpty()
                || confirmPassword == null
                || confirmPassword.trim().isEmpty()) {

            rttr.addFlashAttribute(
                    "error",
                    "비밀번호를 모두 입력해주세요."
            );

            return "redirect:/member/modify";
        }

        if (newPassword.length() < 8) {

            rttr.addFlashAttribute(
                    "error",
                    "비밀번호는 8자리 이상 입력해주세요."
            );

            return "redirect:/member/modify";
        }

        if (!newPassword.equals(confirmPassword)) {

            rttr.addFlashAttribute(
                    "error",
                    "새 비밀번호가 일치하지 않습니다."
            );

            return "redirect:/member/modify";
        }

        try {

            memberService.changePassword(
                    loginMember.getMemberNo(),
                    currentPassword,
                    newPassword
            );

            rttr.addFlashAttribute(
                    "message",
                    "비밀번호가 변경되었습니다."
            );

        } catch (IllegalArgumentException e) {

            rttr.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/member/modify";
    }
}