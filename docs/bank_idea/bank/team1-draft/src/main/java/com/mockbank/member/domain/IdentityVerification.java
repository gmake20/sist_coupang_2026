package com.mockbank.member.domain;

import com.mockbank.common.domain.ReviewStatus;
import com.mockbank.employee.domain.Employee;
import com.mockbank.employee.domain.EmployeeRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 본인확인(비대면 실명확인 mock) 심사 이력.
 * 반려 후 재신청하면 새 행을 만든다. 상태가 바뀔 때 Member.verificationStatus 도 함께 바꾼다.
 *
 * 심사 권한: STAFF 이상 (MANAGER 포함, SYSTEM_ADMIN 제외).
 * 승인 시 개설대기 계좌 활성화는 팀원2의 AccountService.activatePending(memberId) 를
 * 같은 트랜잭션 안에서 Service 가 호출한다.
 */
@Getter
@Entity
@Table(name = "IDENTITY_VERIFICATION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdentityVerification {

    public static final EmployeeRole REVIEW_ROLE = EmployeeRole.STAFF;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_IDENTITY_VERIFICATION_GEN")
    @SequenceGenerator(name = "SEQ_IDENTITY_VERIFICATION_GEN", sequenceName = "SEQ_IDENTITY_VERIFICATION", allocationSize = 1)
    @Column(name = "VERIFICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "ID_CARD_TYPE", nullable = false, length = 20)
    private IdCardType idCardType;

    @Column(name = "ID_ISSUE_DATE", nullable = false)
    private LocalDate idIssueDate;

    @Column(name = "AUTH_BANK_NAME", nullable = false, length = 30)
    private String authBankName;

    @Column(name = "AUTH_ACCOUNT_MASKED", nullable = false, length = 30)
    private String authAccountMasked;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private ReviewStatus status;

    @Column(name = "REJECT_REASON", length = 500)
    private String rejectReason;

    @Column(name = "REQUESTED_AT", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWED_EMPLOYEE_ID")
    private Employee reviewedEmployee;

    @Column(name = "REVIEWED_AT")
    private LocalDateTime reviewedAt;

    /**
     * 본인확인 신청. 1원 인증 계좌번호는 원문을 받아 마스킹한 값만 저장한다.
     */
    public static IdentityVerification request(Member member, IdCardType idCardType, LocalDate idIssueDate,
                                               String authBankName, String authAccountNumber) {
        if (idIssueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("신분증 발급일자가 미래일 수 없습니다.");
        }
        member.markVerificationPending();

        IdentityVerification verification = new IdentityVerification();
        verification.member = member;
        verification.idCardType = idCardType;
        verification.idIssueDate = idIssueDate;
        verification.authBankName = authBankName;
        verification.authAccountMasked = maskAccountNumber(authAccountNumber);
        verification.status = ReviewStatus.PENDING;
        verification.requestedAt = LocalDateTime.now();
        return verification;
    }

    public void approve(Employee reviewer) {
        startReview(reviewer);
        status = ReviewStatus.APPROVED;
        member.markVerified();
    }

    public void reject(Employee reviewer, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("반려 사유를 입력해야 합니다.");
        }
        startReview(reviewer);
        status = ReviewStatus.REJECTED;
        rejectReason = reason;
        member.markVerificationRejected();
    }

    private void startReview(Employee reviewer) {
        if (status != ReviewStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }
        if (reviewer == null || !reviewer.hasAuthority(REVIEW_ROLE)) {
            throw new IllegalArgumentException("본인확인 심사 권한이 없습니다. (STAFF 이상 재직 직원)");
        }
        reviewedEmployee = reviewer;
        reviewedAt = LocalDateTime.now();
    }

    /** 예: 11012341234 → 110-***-**1234 */
    static String maskAccountNumber(String accountNumber) {
        String digits = accountNumber == null ? "" : accountNumber.replaceAll("[^0-9]", "");
        if (digits.length() < 8) {
            throw new IllegalArgumentException("계좌번호 형식이 올바르지 않습니다.");
        }
        return digits.substring(0, 3) + "-***-**" + digits.substring(digits.length() - 4);
    }
}
