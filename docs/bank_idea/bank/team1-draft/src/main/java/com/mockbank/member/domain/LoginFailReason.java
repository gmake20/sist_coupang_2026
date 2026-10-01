package com.mockbank.member.domain;

public enum LoginFailReason {
    BAD_CREDENTIALS,  // 비밀번호 불일치
    LOGIN_LOCKED,     // 5회 실패로 잠김
    SUSPENDED,        // 관리자 정지 회원
    WITHDRAWN,        // 탈퇴 회원
    UNKNOWN_ID        // 존재하지 않는 아이디
}
