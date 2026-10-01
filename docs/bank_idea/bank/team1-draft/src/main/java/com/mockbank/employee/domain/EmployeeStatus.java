package com.mockbank.employee.domain;

public enum EmployeeStatus {
    ACTIVE,   // 재직
    ON_LEAVE, // 휴직 - 로그인 불가
    RETIRED   // 퇴사 - 로그인 불가, 행은 심사 이력 보존을 위해 유지
}
