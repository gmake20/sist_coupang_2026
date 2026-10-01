package com.mockbank.member.repository;

import com.mockbank.common.domain.ReviewStatus;
import com.mockbank.member.domain.IdentityVerification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityVerificationRepository extends JpaRepository<IdentityVerification, Long> {

    /** 관리자 본인확인 심사 목록 (회원 정보를 함께 조회해 N+1 방지) */
    @EntityGraph(attributePaths = "member")
    Page<IdentityVerification> findByStatus(ReviewStatus status, Pageable pageable);

    /** 회원의 현재 심사 대기 건 */
    Optional<IdentityVerification> findByMember_IdAndStatus(Long memberId, ReviewStatus status);

    /** 회원의 신청 이력 (최신순) - 반려 사유 표시용 */
    List<IdentityVerification> findByMember_IdOrderByRequestedAtDesc(Long memberId);
}
