package com.mockbank.member.domain;

import com.mockbank.common.domain.ReviewStatus;
import com.mockbank.employee.domain.Employee;
import com.mockbank.employee.domain.EmployeeRole;
import com.mockbank.common.entity.BaseTimeEntity;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이체한도 변경 신청 (마이페이지 보안설정 13.5).
 * 신청: 팀원1 / 승인 화면: 팀원4 / 실제 한도 반영: 팀원2(ACCOUNT)
 *
 * - 감액(희망 한도 < 현재 한도): 신청 즉시 APPROVED. 심사 직원 없음. 한도를 내리면 더 안전해지므로 심사하지 않는다.
 * - 증액(희망 한도 > 현재 한도): PENDING → MANAGER 가 승인/반려.
 *
 * Service 에서 함께 처리할 것
 * - 신청 전: 같은 계좌에 PENDING 신청이 있으면 새 신청(감액 포함)을 받지 않는다 (existsByAccountIdAndStatus).
 * - 감액 신청 직후, 증액 승인 직후: 팀원2의 AccountService 로 ACCOUNT 한도를 같은 트랜잭션에서 변경한다.
 */
@Getter
@Entity
@Table(name = "TRANSFER_LIMIT_REQUEST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferLimitRequest extends BaseTimeEntity {

    public static final EmployeeRole REVIEW_ROLE = EmployeeRole.MANAGER;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TRANSFER_LIMIT_REQUEST_GEN")
    @SequenceGenerator(name = "SEQ_TRANSFER_LIMIT_REQUEST_GEN", sequenceName = "SEQ_TRANSFER_LIMIT_REQUEST", allocationSize = 1)
    @Column(name = "REQUEST_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    // TODO: 팀원2의 Account 엔티티가 생기면 @ManyToOne(fetch = LAZY) Account 로 변경
    @Column(name = "ACCOUNT_ID", nullable = false, updatable = false)
    private Long accountId;

    @Column(name = "CURRENT_LIMIT", nullable = false, updatable = false)
    private Long currentLimit;

    @Column(name = "REQUESTED_LIMIT", nullable = false, updatable = false)
    private Long requestedLimit;

    @Column(name = "REASON", nullable = false, length = 300)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private ReviewStatus status;

    @Column(name = "REJECT_REASON", length = 500)
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWED_EMPLOYEE_ID")
    private Employee reviewedEmployee;

    @Column(name = "REVIEWED_AT")
    private LocalDateTime reviewedAt;

    public static TransferLimitRequest request(Member member, Long accountId, long currentLimit,
                                               long requestedLimit, String reason) {
        if (!member.isVerified()) {
            throw new IllegalStateException("본인확인을 완료한 회원만 신청할 수 있습니다.");
        }
        if (requestedLimit <= 0 || requestedLimit == currentLimit) {
            throw new IllegalArgumentException("변경할 한도를 올바르게 입력해야 합니다.");
        }
        TransferLimitRequest request = new TransferLimitRequest();
        request.member = member;
        request.accountId = accountId;
        request.currentLimit = currentLimit;
        request.requestedLimit = requestedLimit;
        request.reason = reason;
        // 감액은 즉시 반영, 증액만 심사
        request.status = request.isIncrease() ? ReviewStatus.PENDING : ReviewStatus.APPROVED;
        return request;
    }

    public boolean isIncrease() {
        return requestedLimit > currentLimit;
    }

    /** 심사 없이 즉시 반영된 감액 신청 */
    public boolean isAutoApproved() {
        return status == ReviewStatus.APPROVED && reviewedEmployee == null;
    }

    /**
     * 증액 승인.
     * @param accountLimitNow 승인 시점의 실제 계좌 한도 (ACCOUNT 에서 조회). 신청 이후 한도가 바뀌었으면 승인하지 않는다.
     */
    public void approve(Employee reviewer, long accountLimitNow) {
        if (status == ReviewStatus.PENDING && accountLimitNow != currentLimit) {
            throw new IllegalStateException("신청 후 계좌 한도가 변경되었습니다. 반려 후 다시 신청해야 합니다.");
        }
        startReview(reviewer);
        status = ReviewStatus.APPROVED;
    }

    public void reject(Employee reviewer, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("반려 사유를 입력해야 합니다.");
        }
        startReview(reviewer);
        status = ReviewStatus.REJECTED;
        rejectReason = reason;
    }

    private void startReview(Employee reviewer) {
        if (status != ReviewStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }
        if (reviewer == null || !reviewer.hasAuthority(REVIEW_ROLE)) {
            throw new IllegalArgumentException("이체한도 심사 권한이 없습니다. (MANAGER 재직 직원)");
        }
        reviewedEmployee = reviewer;
        reviewedAt = LocalDateTime.now();
    }
}
