package com.mockbank.member.domain;

import com.mockbank.common.domain.LoginLock;
import com.mockbank.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.YesNoConverter;

/**
 * 고객 회원. 은행 직원은 Employee 로 분리되어 있다.
 * Spring Security 권한은 ROLE_USER 하나이며, 다른 팀원 엔티티는 @ManyToOne(fetch = FetchType.LAZY) 로 참조한다.
 */
@Getter
@Entity
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GEN")
    @SequenceGenerator(name = "SEQ_MEMBER_GEN", sequenceName = "SEQ_MEMBER", allocationSize = 1)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "LOGIN_ID", nullable = false, unique = true, length = 30, updatable = false)
    private String loginId;

    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @Column(name = "NAME", nullable = false, length = 30)
    private String name;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PHONE", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "VERIFICATION_STATUS", nullable = false, length = 20)
    private VerificationStatus verificationStatus;

    @Embedded
    private LoginLock loginLock;

    @Column(name = "TERMS_AGREED_AT", nullable = false)
    private LocalDateTime termsAgreedAt;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "MARKETING_AGREE_YN", nullable = false, length = 1)
    private Boolean marketingAgreed;

    @Column(name = "WITHDRAWN_AT")
    private LocalDateTime withdrawnAt;

    /** 일반 회원 가입. 가입 즉시 정상 상태이며 본인확인은 첫 계좌 개설 때 진행한다. */
    public static Member createUser(String loginId, String encodedPassword, String name, String email,
                                    String phone, LocalDate birthDate, boolean marketingAgreed) {
        Member member = new Member();
        member.loginId = loginId;
        member.password = encodedPassword;
        member.name = name;
        member.email = email;
        member.phone = normalizePhone(phone);
        member.birthDate = birthDate;
        member.status = MemberStatus.ACTIVE;
        member.verificationStatus = VerificationStatus.NONE;
        member.loginLock = LoginLock.unlocked();
        member.termsAgreedAt = LocalDateTime.now();
        member.marketingAgreed = marketingAgreed;
        return member;
    }

    // ===== 상태 확인 =====

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public boolean isVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }

    public boolean isLoginLocked() {
        return loginLock.isLocked();
    }

    // ===== 로그인 실패 / 잠금 =====

    /** 로그인 실패 1회 기록. 5회째에 잠근다. */
    public void recordLoginFailure() {
        loginLock.recordFailure();
    }

    public void recordLoginSuccess() {
        loginLock.recordSuccess();
    }

    /** 비밀번호 재설정 성공 시 로그인 잠금 해제 */
    public void unlockLogin() {
        loginLock.unlock();
    }

    // ===== 회원정보 =====

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfile(String email, String phone, boolean marketingAgreed) {
        this.email = email;
        this.phone = normalizePhone(phone);
        this.marketingAgreed = marketingAgreed;
    }

    /**
     * 회원 탈퇴. 계좌/거래내역이 참조하므로 행은 지우지 않고,
     * 이메일·휴대폰을 마스킹해 개인정보를 파기하고 같은 번호로 재가입할 수 있게 한다.
     * 계좌/상품 보유 여부 검사는 Service에서 먼저 처리한다.
     */
    public void withdraw() {
        if (status == MemberStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }
        status = MemberStatus.WITHDRAWN;
        withdrawnAt = LocalDateTime.now();
        email = "withdrawn_" + id + "@deleted.local";
        phone = "W" + id;
        marketingAgreed = false;
    }

    // ===== 직원 조치 (MANAGER) =====

    public void suspend() {
        if (status != MemberStatus.ACTIVE) {
            throw new IllegalStateException("정상 상태 회원만 정지할 수 있습니다.");
        }
        status = MemberStatus.SUSPENDED;
    }

    public void reactivate() {
        if (status != MemberStatus.SUSPENDED) {
            throw new IllegalStateException("정지 상태 회원만 해제할 수 있습니다.");
        }
        status = MemberStatus.ACTIVE;
    }

    // ===== 본인확인 상태 전이 (IdentityVerification 에서만 호출) =====

    void markVerificationPending() {
        if (verificationStatus != VerificationStatus.NONE && verificationStatus != VerificationStatus.REJECTED) {
            throw new IllegalStateException("본인확인을 신청할 수 없는 상태입니다: " + verificationStatus);
        }
        verificationStatus = VerificationStatus.PENDING;
    }

    void markVerified() {
        verificationStatus = VerificationStatus.VERIFIED;
    }

    void markVerificationRejected() {
        verificationStatus = VerificationStatus.REJECTED;
    }

    private static String normalizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("[^0-9]", "");
    }
}
