package com.mockbank.member.repository;

import com.mockbank.member.domain.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    /** 마이페이지 보안설정 - 내 로그인 이력 (IX_LOGIN_LOG_MEMBER 인덱스 사용) */
    Page<LoginLog> findByMember_IdOrderByLoginAtDesc(Long memberId, Pageable pageable);
}
