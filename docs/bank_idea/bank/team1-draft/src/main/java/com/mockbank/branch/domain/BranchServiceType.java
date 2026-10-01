package com.mockbank.branch.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 지점/ATM 이용 가능 업무. BRANCH_SERVICE.SERVICE_CODE 에 enum 이름으로 저장한다.
 * 선언 순서가 화면 표시 순서다.
 */
@Getter
@RequiredArgsConstructor
public enum BranchServiceType {

    DEPOSIT("입금", false),
    WITHDRAW("출금", false),
    TRANSFER("이체", false),
    BALANCE_INQUIRY("잔액조회", false),
    ACCOUNT_OPENING("계좌개설", true),
    LOAN_CONSULTING("대출상담", true),
    CARD_ISSUANCE("카드발급", true);

    /** 화면 표시명 */
    private final String label;

    /** 직원이 있어야 하는 창구 업무 - ATM 에는 등록할 수 없다 */
    private final boolean counterOnly;

    public boolean isAvailableAt(BranchType branchType) {
        return !counterOnly || branchType == BranchType.BRANCH;
    }
}
