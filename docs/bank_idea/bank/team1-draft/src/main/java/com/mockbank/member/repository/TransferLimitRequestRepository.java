package com.mockbank.member.repository;

import com.mockbank.common.domain.ReviewStatus;
import com.mockbank.member.domain.TransferLimitRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferLimitRequestRepository extends JpaRepository<TransferLimitRequest, Long> {

    /** 관리자 계좌 관리 - 한도 변경 심사 목록 */
    @EntityGraph(attributePaths = "member")
    Page<TransferLimitRequest> findByStatus(ReviewStatus status, Pageable pageable);

    /** 계좌당 심사 대기 1건 제한 (DB 유니크 인덱스 UX_TLR_ONE_PENDING 와 같은 규칙을 미리 검사) */
    boolean existsByAccountIdAndStatus(Long accountId, ReviewStatus status);

    /** 마이페이지 - 내 신청 내역 */
    Page<TransferLimitRequest> findByMember_IdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
