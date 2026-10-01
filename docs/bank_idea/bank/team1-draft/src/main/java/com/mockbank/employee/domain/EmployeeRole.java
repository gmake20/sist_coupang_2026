package com.mockbank.employee.domain;

/**
 * 직원 권한 3단계. Spring Security 에서는 ROLE_STAFF / ROLE_MANAGER / ROLE_SYSTEM_ADMIN.
 *
 * MANAGER 는 STAFF 업무를 모두 할 수 있다 (RoleHierarchy: ROLE_MANAGER > ROLE_STAFF).
 * SYSTEM_ADMIN 은 직무 분리 원칙에 따라 고객 업무(심사·승인)를 하지 않고 시스템 관리만 한다.
 */
public enum EmployeeRole {
    STAFF,        // 일반 직원: 조회, 본인확인 심사, 고객센터
    MANAGER,      // 책임자: STAFF 업무 + 승인 업무(동결/해제, 한도, 대출, 상품, 카드)
    SYSTEM_ADMIN; // 시스템 관리자: 직원 계정, 지점/ATM, 활동 로그

    /** 이 권한으로 required 권한이 필요한 업무를 할 수 있는지 */
    public boolean includes(EmployeeRole required) {
        return this == required || (this == MANAGER && required == STAFF);
    }
}
