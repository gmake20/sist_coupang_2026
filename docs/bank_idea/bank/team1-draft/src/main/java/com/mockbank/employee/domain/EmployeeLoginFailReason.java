package com.mockbank.employee.domain;

public enum EmployeeLoginFailReason {
    BAD_CREDENTIALS,  // 비밀번호 불일치
    LOGIN_LOCKED,     // 5회 실패로 잠김
    ON_LEAVE,         // 휴직
    RETIRED,          // 퇴사
    UNKNOWN_ID        // 존재하지 않는 사번
}
