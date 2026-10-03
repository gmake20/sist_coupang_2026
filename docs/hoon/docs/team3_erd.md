# 팀원 3 ERD — 예적금·카드·대출

## 설계 범위

팀원 3은 다음 6개 테이블을 소유한다.

- `PRODUCT`: 예금·적금 상품 마스터
- `PRODUCT_SUBSCRIPTION`: 회원의 예금·적금 가입 내역
- `CARD`: 회원이 신청·보유한 카드
- `CARD_USAGE_HISTORY`: 카드 사용내역(mock)
- `LOAN`: 대출 신청 및 심사 결과
- `LOAN_REPAYMENT_SCHEDULE`: 승인 대출의 회차별 상환 일정

`MEMBER`와 `ACCOUNT`는 다른 팀원이 소유하는 외부 테이블이다. 팀원 3 테이블은 해당 테이블의 PK가 MySQL `BIGINT`인 `MEMBER_ID`, `ACCOUNT_ID`라는 전제로 설계한다.

## 관계도

```mermaid
erDiagram
    MEMBER ||--o{ PRODUCT_SUBSCRIPTION : subscribes
    PRODUCT ||--o{ PRODUCT_SUBSCRIPTION : has
    ACCOUNT ||--o| PRODUCT_SUBSCRIPTION : "product account"

    MEMBER ||--o{ CARD : owns
    ACCOUNT ||--o{ CARD : "payment account"
    CARD ||--o{ CARD_USAGE_HISTORY : records

    MEMBER ||--o{ LOAN : applies
    ACCOUNT ||--o{ LOAN : "repayment account"
    MEMBER o|--o{ LOAN : reviews
    LOAN ||--o{ LOAN_REPAYMENT_SCHEDULE : schedules

    PRODUCT {
        BIGINT product_id PK
        VARCHAR product_name
        VARCHAR product_type "DEPOSIT, SAVINGS"
        DECIMAL base_interest_rate
        VARCHAR preferential_condition
        SMALLINT term_months
        BIGINT minimum_amount
        DECIMAL early_termination_rate
        VARCHAR status "ACTIVE, INACTIVE"
        DATETIME created_at
        DATETIME updated_at
    }

    PRODUCT_SUBSCRIPTION {
        BIGINT subscription_id PK
        BIGINT member_id FK
        BIGINT product_id FK
        BIGINT account_id FK_UK
        BIGINT principal_amount
        BIGINT monthly_payment
        DATE subscribed_at
        DATE maturity_date
        BIGINT expected_interest
        VARCHAR status "ACTIVE, MATURED, CANCELLED"
        DATETIME cancelled_at
        DATETIME created_at
        DATETIME updated_at
    }

    CARD {
        BIGINT card_id PK
        BIGINT member_id FK
        BIGINT account_id FK
        VARCHAR card_number UK
        VARCHAR card_type "CHECK, CREDIT"
        VARCHAR card_name
        BIGINT credit_limit
        VARCHAR status "APPLIED, ACTIVE, SUSPENDED, LOST, CANCELLED"
        DATE issued_at
        DATE expires_at
        DATETIME created_at
        DATETIME updated_at
    }

    CARD_USAGE_HISTORY {
        BIGINT card_usage_id PK
        BIGINT card_id FK
        VARCHAR merchant_name
        VARCHAR merchant_category
        BIGINT amount
        DATETIME used_at
        VARCHAR approval_number UK
        VARCHAR status "APPROVED, CANCELLED"
        DATETIME created_at
    }

    LOAN {
        BIGINT loan_id PK
        BIGINT member_id FK
        BIGINT repayment_account_id FK
        VARCHAR loan_type
        BIGINT requested_amount
        BIGINT approved_amount
        DECIMAL annual_interest_rate
        SMALLINT term_months
        VARCHAR repayment_method
        VARCHAR employment_status
        BIGINT annual_income
        VARCHAR status "APPLIED, APPROVED, REJECTED, REPAYING, REPAID, CANCELLED"
        BIGINT reviewed_by FK
        DATETIME applied_at
        DATETIME reviewed_at
        DATE started_at
        DATE maturity_date
        VARCHAR rejection_reason
        DATETIME created_at
        DATETIME updated_at
    }

    LOAN_REPAYMENT_SCHEDULE {
        BIGINT schedule_id PK
        BIGINT loan_id FK
        SMALLINT installment_no UK
        DATE due_date
        BIGINT principal_amount
        BIGINT interest_amount
        BIGINT total_amount
        VARCHAR status "SCHEDULED, PAID, OVERDUE, WAIVED"
        DATETIME paid_at
        DATETIME created_at
        DATETIME updated_at
    }
```

## 핵심 업무 규칙

1. `PRODUCT_TYPE='DEPOSIT'`이면 `MONTHLY_PAYMENT`은 `NULL`, `SAVINGS`이면 월 납입액을 저장한다. 이 조건은 서비스 계층에서도 검증한다.
2. 예적금 가입 시 전용 계좌가 생성된다는 전제에서 `PRODUCT_SUBSCRIPTION.ACCOUNT_ID`는 유일하다.
3. 카드번호와 승인번호는 프로젝트용 mock 값이지만 중복을 허용하지 않는다. 카드 비밀번호·CVC는 저장하지 않는다.
4. 대출 신청 당시에는 승인금액·금리·심사자·상환일정이 없을 수 있다. 승인 시 해당 값을 채우고 상환 스케줄을 한 트랜잭션에서 생성한다.
5. `(LOAN_ID, INSTALLMENT_NO)`는 유일하며, 회차별 `TOTAL_AMOUNT`는 원금과 이자의 합이어야 한다.
6. 상품이나 금융 계약 데이터는 물리 삭제하지 않고 `STATUS`로 비활성화·해지 상태를 관리한다.

## 팀 간 연동 지점

- 팀원 1: 현재 로그인한 `MEMBER_ID`와 관리자 `MEMBER_ID` 제공
- 팀원 2: 예적금 전용 계좌 생성, 카드 결제계좌 및 대출 상환계좌 검증, 실제 금액 이동과 거래내역 기록
- 팀원 4: `PRODUCT` 관리 화면, 카드 관리, `LOAN` 승인·거절 및 `REVIEWED_BY` 기록, 알림 이벤트 처리
