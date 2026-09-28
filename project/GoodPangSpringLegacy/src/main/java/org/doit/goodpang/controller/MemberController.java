package org.doit.goodpang.controller;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /*
     * 회원정보 수정 페이지
     */
    @GetMapping("/modify")
    public String modify(
            Authentication authentication,
            Model model) {

        // 1. 로그인 사용자
        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        // CustomUser 구조에 따라 getter 이름은 맞춰주세요.
        int memberNo =
                customUser.getMember().getMemberNo();

        log.info("회원정보 수정 memberNo : " + memberNo);

        // 2. DB에서 최신 회원정보 조회
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
        return "userModify";
    }
}