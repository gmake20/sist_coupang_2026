package com.mockbank.member.domain;

/** 회원의 본인확인(비대면 실명확인 mock) 상태 */
public enum VerificationStatus {
    NONE,      // 미확인 - 가입 직후
    PENDING,   // 심사중 - 첫 계좌 개설 시 신청
    VERIFIED,  // 확인완료 - 계좌/상품/카드/대출 이용 가능
    REJECTED   // 반려 - 재신청 가능
}
