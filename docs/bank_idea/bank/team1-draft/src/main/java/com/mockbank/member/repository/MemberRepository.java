package com.mockbank.member.repository;

import com.mockbank.member.domain.Member;
import com.mockbank.member.domain.VerificationStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /** 로그인 (UserDetailsService) */
    Optional<Member> findByLoginId(String loginId);

    /** 회원가입 중복 확인 */
    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    /** 아이디 찾기 (이름 + 휴대폰 + 생년월일) */
    Optional<Member> findByNameAndPhoneAndBirthDate(String name, String phone, LocalDate birthDate);

    /** 관리자 회원 관리 - 본인확인 상태별 목록 */
    Page<Member> findByVerificationStatus(VerificationStatus verificationStatus, Pageable pageable);
}
