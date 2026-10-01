package com.mockbank.common.domain;

/** 관리자 심사가 필요한 신청 건의 상태 (본인확인, 이체한도 변경 등에서 공통 사용) */
public enum ReviewStatus {
    PENDING,
    APPROVED,
    REJECTED
}
