package org.doit.goodpang.service;

import java.util.regex.Pattern;

import org.doit.goodpang.domain.MemberVO;
import org.doit.goodpang.mapper.MemberMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private static final Pattern PHONE_PATTERN =
	            Pattern.compile("^010-\\d{4}-\\d{4}$");


    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    

    public MemberVO getMember(Long memberNo) {

        return memberMapper.getMember(memberNo);
    }
    
    public int updateEmail(
            Long memberNo,
            String newEmail) {

        return memberMapper.updateEmail(
                memberNo,
                newEmail
        );
    }

    public int updatePhone(
            Long memberNo,
            String newPhone) {

        if (newPhone == null
                || newPhone.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "휴대폰 번호를 입력해주세요."
            );
        }

        newPhone = newPhone.trim();

        // 최대 길이: 13자리
        // 010-1234-5678 = 13글자
        if (newPhone.length() > 13) {

            throw new IllegalArgumentException(
                    "휴대폰 번호는 최대 13자리까지 입력할 수 있습니다."
            );
        }

        // 010-1234-5678 형식 검증
        if (!PHONE_PATTERN.matcher(newPhone).matches()) {

            throw new IllegalArgumentException(
                    "휴대폰 번호는 010-1234-5678 형식으로 입력해주세요."
            );
        }

        return memberMapper.updatePhone(
                memberNo,
                newPhone
        );
    }
    
    public void changePassword(
            Long memberNo,
            String currentPassword,
            String newPassword) {

        MemberVO member =
                memberMapper.getMember(memberNo);

        if (member == null) {

            throw new IllegalArgumentException(
                    "회원 정보를 찾을 수 없습니다."
            );
        }

        String encodedPassword =
                member.getMemberPw();

        if (encodedPassword == null
                || encodedPassword.trim().isEmpty()) {

            throw new IllegalStateException(
                    "회원 비밀번호 정보가 존재하지 않습니다."
            );
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(
                currentPassword,
                encodedPassword)) {

            throw new IllegalArgumentException(
                    "현재 비밀번호가 올바르지 않습니다."
            );
        }

        // 기존 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(
                newPassword,
                encodedPassword)) {

            throw new IllegalArgumentException(
                    "새 비밀번호는 현재 비밀번호와 다르게 입력해주세요."
            );
        }

        // 새 비밀번호 검증
        if (newPassword.length() < 8) {

            throw new IllegalArgumentException(
                    "새 비밀번호는 8자리 이상 입력해주세요."
            );
        }

        String encodedNewPassword =
                passwordEncoder.encode(
                        newPassword
                );

        int result =
                memberMapper.updatePassword(
                        memberNo,
                        encodedNewPassword
                );

        if (result <= 0) {

            throw new IllegalStateException(
                    "비밀번호 변경에 실패했습니다."
            );
        }
    }
    
    public void verifyWithdrawPassword(
            Long memberNo,
            String rawPassword) {

        MemberVO member =
                memberMapper.getMember(
                        memberNo
                );


        if (member == null) {

            throw new IllegalArgumentException(
                    "회원 정보를 찾을 수 없습니다."
            );
        }


        String encodedPassword =
                member.getMemberPw();


        if (encodedPassword == null
                || encodedPassword.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "비밀번호 정보를 확인할 수 없습니다."
            );
        }


        boolean passwordMatch =
                passwordEncoder.matches(
                        rawPassword,
                        encodedPassword
                );


        if (!passwordMatch) {

            throw new IllegalArgumentException(
                    "비밀번호가 일치하지 않습니다."
            );
        }
    }
    
    public void withdrawMember(Long memberNo) {

        int result =
                memberMapper.withdrawMember(
                        memberNo
                );

        if (result != 1) {

            throw new IllegalStateException(
                    "회원 탈퇴 처리에 실패했습니다."
            );
        }
    }
}