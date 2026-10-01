package com.mockbank.member.domain;

public enum MemberStatus {
    ACTIVE,     // 정상
    SUSPENDED,  // 관리자 정지
    WITHDRAWN   // 탈퇴 (행은 남기고 개인정보 마스킹)
}
