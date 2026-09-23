package org.doit.goodpang.controller;

import java.sql.SQLException;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.mapper.MemberMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@AllArgsConstructor
public class SignupController {

    private MemberMapper memberMapper;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(MemberVO memberVO, Model model) {
    	
        MemberVO existingMember = null;
		try {
			existingMember = memberMapper.read(memberVO.getMemberId());
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (existingMember != null) {

            model.addAttribute(
                "error",
                "이미 사용 중인 아이디입니다."
            );

            return "signup";
        }

        String encodedPassword =
                passwordEncoder.encode(
                    memberVO.getMemberPw()
                );

        memberVO.setMemberPw(encodedPassword);

        // 기본 권한
        memberVO.setRank("ROLE_USER");

        // 활성 회원
        memberVO.setStatus(1);

        int result =
                memberMapper.insert(memberVO);

        if (result == 0) {

            model.addAttribute(
                "error",
                "회원가입 처리 중 오류가 발생했습니다."
            );

            return "signup";
        }

        return "redirect:/login?signup=true";
    }
}