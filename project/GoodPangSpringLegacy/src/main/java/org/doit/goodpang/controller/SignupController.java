package org.doit.goodpang.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.mapper.MemberMapper;
import org.doit.goodpang.security.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
public class SignupController {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;


    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }


    @PostMapping("/signup")
    public String signup(
            MemberVO memberVO,
            Model model,
            HttpServletRequest request) {

        MemberVO existingMember = null;

        try {

            existingMember =
                    memberMapper.read(
                            memberVO.getMemberId()
                    );

        } catch (ClassNotFoundException | SQLException e) {

            log.error(
                    "회원 중복 조회 중 오류",
                    e
            );

            model.addAttribute(
                    "error",
                    "회원가입 처리 중 오류가 발생했습니다."
            );

            return "signup";
        }


        if (existingMember != null) {

            model.addAttribute(
                    "error",
                    "이미 사용 중인 아이디입니다."
            );

            return "signup";
        }


        String rawPassword =
                memberVO.getMemberPw();


        String encodedPassword =
                passwordEncoder.encode(
                        rawPassword
                );

        memberVO.setMemberPw(
                encodedPassword
        );

        memberVO.setRank(
                "ROLE_USER"
        );

        memberVO.setStatus(1);


        int result =
                memberMapper.insert(
                        memberVO
                );


        if (result == 0) {

            model.addAttribute(
                    "error",
                    "회원가입 처리 중 오류가 발생했습니다."
            );

            return "signup";
        }


        // ============================
        // 회원가입 성공 → 자동 로그인
        // ============================
        try {

            UserDetails userDetails =
                    customUserDetailsService
                            .loadUserByUsername(
                                    memberVO.getMemberId()
                            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContext securityContext =
                    SecurityContextHolder
                            .createEmptyContext();

            securityContext.setAuthentication(
                    authentication
            );

            SecurityContextHolder.setContext(
                    securityContext
            );


            HttpSession session =
                    request.getSession(true);

            session.setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    securityContext
            );

            if (userDetails instanceof CustomUser) {

                CustomUser customUser =
                        (CustomUser) userDetails;

                session.setAttribute(
                        "loginMember",
                        customUser.getMember()
                );
            }

        } catch (Exception e) {

            log.error(
                    "회원가입 후 자동 로그인 실패",
                    e
            );

            return "redirect:/login?signup=true";
        }


        return "redirect:/";
    }
}