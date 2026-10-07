# 팀원1 테이블 정의서

> 기준: `src/main/resources/db/schema-team1.sql` (Oracle)
> 표기: PK 기본키, FK 외래키, UK 유니크, CK 체크 제약

고객(`MEMBER`)과 은행 직원(`EMPLOYEE`)은 테이블과 로그인을 분리합니다.

| 구분 | 테이블 | 설명 |
|---|---|---|
| 고객 | [MEMBER](#1-member) | 고객 회원 |
| 고객 | [IDENTITY_VERIFICATION](#4-identity_verification) | 본인확인 심사 이력 |
| 고객 | [LOGIN_LOG](#5-login_log) | 고객 로그인 이력 |
| 고객 | [TRANSFER_LIMIT_REQUEST](#7-transfer_limit_request) | 이체한도 변경 신청 |
| 직원 | [EMPLOYEE](#3-employee) | 은행 직원 |
| 직원 | [EMPLOYEE_LOGIN_LOG](#6-employee_login_log) | 직원 로그인 이력 |
| 공통 | [BRANCH](#2-branch) | 지점/ATM (직원 소속 지점) |
| 공통 | [BRANCH_SERVICE](#2-1-branch_service) | 지점/ATM 이용 가능 업무 |

## 직원 권한 3단계

| 권한 (ROLE) | Spring Security | 할 수 있는 일 |
|---|---|---|
| `STAFF` 일반 직원 | `ROLE_STAFF` | 회원·계좌·거래 조회, 본인확인 심사, 1:1 문의 답변, 공지/FAQ 관리 |
| `MANAGER` 책임자 | `ROLE_MANAGER` | STAFF 업무 전부 + 회원 정지/해제, 계좌 동결/해제·잠금 해제, 이체한도 승인, 대출 심사, 카드 발급 승인, 상품 관리 |
| `SYSTEM_ADMIN` 시스템 관리자 | `ROLE_SYSTEM_ADMIN` | 직원 계정 관리, 지점/ATM 관리, 관리자 활동 로그·직원 로그인 이력 조회 |

- `MANAGER`는 `STAFF` 권한을 포함합니다 (`RoleHierarchy: ROLE_MANAGER > ROLE_STAFF`).
- `SYSTEM_ADMIN`은 **직무 분리** 원칙에 따라 고객 업무(심사·승인)를 하지 않습니다. 시스템을 관리하는 사람이 대출 승인까지 하면 내부 통제가 무너지기 때문입니다.

---

## 1. MEMBER

고객 회원 · 시퀀스 `SEQ_MEMBER`

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| MEMBER_ID | NUMBER(19) | N | | PK | 회원 ID |
| LOGIN_ID | VARCHAR2(30 CHAR) | N | | UK | 로그인 아이디 (탈퇴 후에도 재사용 불가) |
| PASSWORD | VARCHAR2(100) | N | | | BCrypt 해시 |
| NAME | VARCHAR2(30 CHAR) | N | | | 이름 |
| EMAIL | VARCHAR2(100 CHAR) | N | | UK | 이메일 (탈퇴 시 마스킹) |
| PHONE | VARCHAR2(20) | N | | UK | 휴대폰번호, 숫자만 (탈퇴 시 마스킹) |
| BIRTH_DATE | DATE | N | | | 생년월일 |
| STATUS | VARCHAR2(20) | N | 'ACTIVE' | CK | `ACTIVE` 정상 / `SUSPENDED` 정지 / `WITHDRAWN` 탈퇴 |
| VERIFICATION_STATUS | VARCHAR2(20) | N | 'NONE' | CK | `NONE` 미확인 / `PENDING` 심사중 / `VERIFIED` 완료 / `REJECTED` 반려 |
| LOGIN_FAIL_COUNT | NUMBER(2) | N | 0 | | 로그인 연속 실패 횟수 |
| LOGIN_LOCKED_AT | TIMESTAMP | Y | | | 로그인 잠금 시각 (NULL이면 잠기지 않음, 5회 실패 시 기록) |
| TERMS_AGREED_AT | TIMESTAMP | N | | | 필수 약관 동의 시각 |
| MARKETING_AGREE_YN | CHAR(1) | N | 'N' | CK | 마케팅 수신 동의 `Y` / `N` |
| CREATED_AT | TIMESTAMP | N | SYSTIMESTAMP | | 가입일시 |
| UPDATED_AT | TIMESTAMP | Y | | | 수정일시 |
| WITHDRAWN_AT | TIMESTAMP | Y | | | 탈퇴일시 |

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_MEMBER | PK | MEMBER_ID |
| UK_MEMBER_LOGIN_ID | UNIQUE | LOGIN_ID |
| UK_MEMBER_EMAIL | UNIQUE | EMAIL |
| UK_MEMBER_PHONE | UNIQUE | PHONE |
| CK_MEMBER_STATUS | CHECK | STATUS IN ('ACTIVE','SUSPENDED','WITHDRAWN') |
| CK_MEMBER_VERIFICATION | CHECK | VERIFICATION_STATUS IN ('NONE','PENDING','VERIFIED','REJECTED') |
| CK_MEMBER_MARKETING | CHECK | MARKETING_AGREE_YN IN ('Y','N') |

---

## 2. BRANCH

지점/ATM (본점 포함) · 직원의 소속 지점으로도 사용 · `EMPLOYEE`보다 먼저 생성 · 시퀀스 `SEQ_BRANCH`

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| BRANCH_ID | NUMBER(19) | N | | PK | 지점/ATM ID |
| BRANCH_TYPE | VARCHAR2(10) | N | | CK | `BRANCH` 지점 / `ATM` |
| NAME | VARCHAR2(50 CHAR) | N | | | 이름 |
| ADDRESS | VARCHAR2(200 CHAR) | N | | | 주소 |
| LATITUDE | NUMBER(10,7) | N | | CK | 위도 (-90 ~ 90) |
| LONGITUDE | NUMBER(10,7) | N | | CK | 경도 (-180 ~ 180) |
| PHONE | VARCHAR2(20) | Y | | | 전화번호 (ATM은 NULL) |
| BUSINESS_HOURS | VARCHAR2(50 CHAR) | Y | | | 영업시간 (예: 평일 09:00~16:00) |
| OPEN_24H_YN | CHAR(1) | N | 'N' | CK | 24시간 운영 여부 |
| USE_YN | CHAR(1) | N | 'Y' | CK | 사용 여부 (삭제 대신 N) |
| CREATED_AT | TIMESTAMP | N | SYSTIMESTAMP | | 등록일시 |
| UPDATED_AT | TIMESTAMP | Y | | | 수정일시 |

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_BRANCH | PK | BRANCH_ID |
| CK_BRANCH_TYPE | CHECK | BRANCH_TYPE IN ('BRANCH','ATM') |
| CK_BRANCH_OPEN_24H | CHECK | OPEN_24H_YN IN ('Y','N') |
| CK_BRANCH_USE | CHECK | USE_YN IN ('Y','N') |
| CK_BRANCH_LAT | CHECK | LATITUDE BETWEEN -90 AND 90 |
| CK_BRANCH_LNG | CHECK | LONGITUDE BETWEEN -180 AND 180 |
| IX_BRANCH_TYPE | INDEX | (BRANCH_TYPE, USE_YN) |

이용 가능 업무는 아래 `BRANCH_SERVICE` 테이블에 따로 저장합니다.

---

## 2-1. BRANCH_SERVICE

지점/ATM 이용 가능 업무 · 지점 1곳에 업무 여러 개 · JPA는 `Branch.services` (`@ElementCollection` + `BranchServiceType` enum)

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| BRANCH_ID | NUMBER(19) | N | | PK, FK → BRANCH | 지점/ATM |
| SERVICE_CODE | VARCHAR2(30) | N | | PK, CK | 업무 코드 (아래 표) |

**업무 코드**

| 코드 | 표시명 | 지점 | ATM | 비고 |
|---|---|:---:|:---:|---|
| `DEPOSIT` | 입금 | O | O | |
| `WITHDRAW` | 출금 | O | O | |
| `TRANSFER` | 이체 | O | O | |
| `BALANCE_INQUIRY` | 잔액조회 | O | O | |
| `ACCOUNT_OPENING` | 계좌개설 | O | X | 창구 업무 |
| `LOAN_CONSULTING` | 대출상담 | O | X | 창구 업무 |
| `CARD_ISSUANCE` | 카드발급 | O | X | 창구 업무 |

- 창구 업무(직원이 필요한 업무)는 ATM에 등록할 수 없습니다. 다른 테이블(`BRANCH.BRANCH_TYPE`)을 봐야 해서 DB 제약 대신 Entity에서 검사합니다.
- 지점/ATM마다 업무가 1개 이상 있어야 합니다 (Entity에서 검사).
- 화면 표시 순서는 위 표 순서(enum 선언 순서)입니다.

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_BRANCH_SERVICE | PK | (BRANCH_ID, SERVICE_CODE) — 같은 업무 중복 등록 방지 |
| FK_BRANCH_SERVICE_BRANCH | FK | BRANCH_ID → BRANCH |
| CK_BRANCH_SERVICE_CODE | CHECK | SERVICE_CODE IN (위 7개 코드) |
| IX_BRANCH_SERVICE_CODE | INDEX | (SERVICE_CODE, BRANCH_ID) — "입금 가능한 곳" 같은 업무별 검색 |

---

## 3. EMPLOYEE

은행 직원 · 사번으로 `/admin/login` 로그인 · 시퀀스 `SEQ_EMPLOYEE`

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| EMPLOYEE_ID | NUMBER(19) | N | | PK | 직원 ID |
| EMPLOYEE_NO | VARCHAR2(10) | N | | UK | 사번 (직원 로그인 아이디, 예: `E0001`) |
| PASSWORD | VARCHAR2(100) | N | | | BCrypt 해시 |
| NAME | VARCHAR2(30 CHAR) | N | | | 이름 |
| EMAIL | VARCHAR2(100 CHAR) | N | | UK | 업무용 이메일 |
| PHONE | VARCHAR2(20) | Y | | | 내선/업무 연락처 |
| BRANCH_ID | NUMBER(19) | N | | FK → BRANCH | 소속 지점 (`BRANCH_TYPE = BRANCH`만, 본점 포함) |
| POSITION | VARCHAR2(20 CHAR) | N | | | 직급 (사원/대리/과장/차장/부장/지점장 등) — 표시용, 권한과 무관 |
| ROLE | VARCHAR2(20) | N | 'STAFF' | CK | `STAFF` 일반 직원 / `MANAGER` 책임자 / `SYSTEM_ADMIN` 시스템 관리자 |
| STATUS | VARCHAR2(20) | N | 'ACTIVE' | CK | `ACTIVE` 재직 / `ON_LEAVE` 휴직 / `RETIRED` 퇴사 (재직만 로그인 가능) |
| TEMP_PW_YN | CHAR(1) | N | 'Y' | CK | 임시 비밀번호 여부 — `Y`면 첫 로그인 시 비밀번호 변경 강제 |
| LOGIN_FAIL_COUNT | NUMBER(2) | N | 0 | | 로그인 연속 실패 횟수 |
| LOGIN_LOCKED_AT | TIMESTAMP | Y | | | 로그인 잠금 시각 (5회 실패 시 기록, 비밀번호 초기화로 해제) |
| HIRED_AT | DATE | N | | | 입사일 |
| RETIRED_AT | DATE | Y | | CK | 퇴사일 (퇴사 상태일 때만 값 존재) |
| CREATED_AT | TIMESTAMP | N | SYSTIMESTAMP | | 등록일시 |
| UPDATED_AT | TIMESTAMP | Y | | | 수정일시 |

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_EMPLOYEE | PK | EMPLOYEE_ID |
| UK_EMPLOYEE_NO | UNIQUE | EMPLOYEE_NO |
| UK_EMPLOYEE_EMAIL | UNIQUE | EMAIL |
| FK_EMPLOYEE_BRANCH | FK | BRANCH_ID → BRANCH |
| CK_EMPLOYEE_ROLE | CHECK | ROLE IN ('STAFF','MANAGER','SYSTEM_ADMIN') |
| CK_EMPLOYEE_STATUS | CHECK | STATUS IN ('ACTIVE','ON_LEAVE','RETIRED') |
| CK_EMPLOYEE_TEMP_PW | CHECK | TEMP_PW_YN IN ('Y','N') |
| CK_EMPLOYEE_RETIRED | CHECK | 퇴사(RETIRED)면 RETIRED_AT 필수, 아니면 NULL |
| IX_EMPLOYEE_BRANCH | INDEX | BRANCH_ID — 지점별 직원 목록 |

- 퇴사한 직원도 행을 지우지 않습니다. 과거 심사 이력이 이 직원을 참조하기 때문입니다.
- 소속이 ATM이 아닌 지점인지는 DB 제약으로 검사할 수 없어서 Entity(`Employee.assignBranch`)에서 검사합니다.

---

## 4. IDENTITY_VERIFICATION

본인확인(비대면 실명확인 mock) 심사 이력 · 반려 후 재신청 시 새 행 추가 · **심사 권한: STAFF 이상** · 시퀀스 `SEQ_IDENTITY_VERIFICATION`

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| VERIFICATION_ID | NUMBER(19) | N | | PK | 본인확인 신청 ID |
| MEMBER_ID | NUMBER(19) | N | | FK → MEMBER | 신청 회원 |
| ID_CARD_TYPE | VARCHAR2(20) | N | | CK | `RESIDENT_CARD` 주민등록증 / `DRIVER_LICENSE` 운전면허증 |
| ID_ISSUE_DATE | DATE | N | | | 신분증 발급일자 (mock) |
| AUTH_BANK_NAME | VARCHAR2(30 CHAR) | N | | | 1원 인증 타행 은행명 (mock) |
| AUTH_ACCOUNT_MASKED | VARCHAR2(30) | N | | | 1원 인증 계좌번호 (마스킹 값만, 예: `110-***-**1234`) |
| STATUS | VARCHAR2(20) | N | 'PENDING' | CK | `PENDING` 대기 / `APPROVED` 승인 / `REJECTED` 반려 |
| REJECT_REASON | VARCHAR2(500 CHAR) | Y | | CK | 반려 사유 (반려 시 필수) |
| REQUESTED_AT | TIMESTAMP | N | SYSTIMESTAMP | | 신청일시 |
| REVIEWED_EMPLOYEE_ID | NUMBER(19) | Y | | FK → EMPLOYEE | 심사 직원 (심사 완료 시 필수) |
| REVIEWED_AT | TIMESTAMP | Y | | | 심사일시 (심사 완료 시 필수) |

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_IDENTITY_VERIFICATION | PK | VERIFICATION_ID |
| FK_IV_MEMBER | FK | MEMBER_ID → MEMBER |
| FK_IV_EMPLOYEE | FK | REVIEWED_EMPLOYEE_ID → EMPLOYEE |
| CK_IV_ID_CARD_TYPE | CHECK | ID_CARD_TYPE IN ('RESIDENT_CARD','DRIVER_LICENSE') |
| CK_IV_STATUS | CHECK | STATUS IN ('PENDING','APPROVED','REJECTED') |
| CK_IV_REJECT_REASON | CHECK | 반려(REJECTED)면 REJECT_REASON 필수 |
| CK_IV_REVIEWED | CHECK | PENDING이 아니면 REVIEWED_EMPLOYEE_ID, REVIEWED_AT 필수 |
| UX_IV_ONE_PENDING | UNIQUE INDEX | 회원당 PENDING 1건만 (`CASE WHEN STATUS='PENDING' THEN MEMBER_ID END`) |
| IX_IV_MEMBER | INDEX | (MEMBER_ID, REQUESTED_AT) — 회원별 이력 조회 |
| IX_IV_STATUS | INDEX | (STATUS, REQUESTED_AT) — 직원 심사 대기 목록 |

---

## 5. LOGIN_LOG

고객 로그인 이력 · 추가만 하고 수정/삭제하지 않음 · 시퀀스 `SEQ_LOGIN_LOG`

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| LOGIN_LOG_ID | NUMBER(19) | N | | PK | 로그 ID |
| MEMBER_ID | NUMBER(19) | Y | | FK → MEMBER | 회원 (존재하지 않는 아이디로 시도하면 NULL) |
| LOGIN_ID_INPUT | VARCHAR2(30 CHAR) | N | | | 사용자가 입력한 아이디 |
| SUCCESS_YN | CHAR(1) | N | | CK | 성공 여부 `Y` / `N` |
| FAIL_REASON | VARCHAR2(30) | Y | | CK | `BAD_CREDENTIALS` / `LOGIN_LOCKED` / `SUSPENDED` / `WITHDRAWN` / `UNKNOWN_ID` |
| IP_ADDRESS | VARCHAR2(45) | N | | | 접속 IP (IPv6 고려) |
| USER_AGENT | VARCHAR2(500) | Y | | | 접속 기기/브라우저 정보 |
| LOGIN_AT | TIMESTAMP | N | SYSTIMESTAMP | | 로그인 시도 일시 |

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_LOGIN_LOG | PK | LOGIN_LOG_ID |
| FK_LOGIN_LOG_MEMBER | FK | MEMBER_ID → MEMBER |
| CK_LOGIN_LOG_SUCCESS | CHECK | SUCCESS_YN IN ('Y','N') |
| CK_LOGIN_LOG_FAIL_REASON | CHECK | FAIL_REASON 허용값 5개 또는 NULL |
| CK_LOGIN_LOG_REASON_MATCH | CHECK | 성공이면 FAIL_REASON NULL, 실패면 필수 |
| IX_LOGIN_LOG_MEMBER | INDEX | (MEMBER_ID, LOGIN_AT DESC) — 내 최근 로그인 이력 |

---

## 6. EMPLOYEE_LOGIN_LOG

직원 로그인 이력 · 추가만 하고 수정/삭제하지 않음 · SYSTEM_ADMIN이 조회 · 시퀀스 `SEQ_EMPLOYEE_LOGIN_LOG`

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| LOGIN_LOG_ID | NUMBER(19) | N | | PK | 로그 ID |
| EMPLOYEE_ID | NUMBER(19) | Y | | FK → EMPLOYEE | 직원 (존재하지 않는 사번으로 시도하면 NULL) |
| EMPLOYEE_NO_INPUT | VARCHAR2(30) | N | | | 입력한 사번 |
| SUCCESS_YN | CHAR(1) | N | | CK | 성공 여부 `Y` / `N` |
| FAIL_REASON | VARCHAR2(30) | Y | | CK | `BAD_CREDENTIALS` / `LOGIN_LOCKED` / `ON_LEAVE` / `RETIRED` / `UNKNOWN_ID` |
| IP_ADDRESS | VARCHAR2(45) | N | | | 접속 IP |
| USER_AGENT | VARCHAR2(500) | Y | | | 접속 기기/브라우저 정보 |
| LOGIN_AT | TIMESTAMP | N | SYSTIMESTAMP | | 로그인 시도 일시 |

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_EMPLOYEE_LOGIN_LOG | PK | LOGIN_LOG_ID |
| FK_ELL_EMPLOYEE | FK | EMPLOYEE_ID → EMPLOYEE |
| CK_ELL_SUCCESS | CHECK | SUCCESS_YN IN ('Y','N') |
| CK_ELL_FAIL_REASON | CHECK | FAIL_REASON 허용값 5개 또는 NULL |
| CK_ELL_REASON_MATCH | CHECK | 성공이면 FAIL_REASON NULL, 실패면 필수 |
| IX_ELL_EMPLOYEE | INDEX | (EMPLOYEE_ID, LOGIN_AT DESC) — 직원별 이력 |
| IX_ELL_LOGIN_AT | INDEX | (LOGIN_AT DESC) — 전체 직원 최근 이력 |

---

## 7. TRANSFER_LIMIT_REQUEST

이체한도 변경 신청 (마이페이지 보안설정 13.5) · 신청 팀원1 / 승인 화면 팀원4 / 한도 반영 팀원2 · 시퀀스 `SEQ_TRANSFER_LIMIT_REQUEST`

- **감액**(희망 < 현재): 신청 즉시 `APPROVED`, 심사 직원 없음 — 한도를 내리면 더 안전해지므로 심사하지 않음
- **증액**(희망 > 현재): `PENDING` → **MANAGER**가 승인/반려

| 컬럼 | 타입 | NULL | 기본값 | 키/제약 | 설명 |
|---|---|:---:|---|---|---|
| REQUEST_ID | NUMBER(19) | N | | PK | 신청 ID |
| MEMBER_ID | NUMBER(19) | N | | FK → MEMBER | 신청 회원 |
| ACCOUNT_ID | NUMBER(19) | N | | FK → ACCOUNT ※ | 대상 계좌 |
| CURRENT_LIMIT | NUMBER(15) | N | | CK | 신청 시점 1일 이체한도 (원) |
| REQUESTED_LIMIT | NUMBER(15) | N | | CK | 희망 1일 이체한도 (원) |
| REASON | VARCHAR2(300 CHAR) | N | | | 신청 사유 |
| STATUS | VARCHAR2(20) | N | 'PENDING' | CK | `PENDING` 대기(증액만) / `APPROVED` 승인 / `REJECTED` 반려 |
| REJECT_REASON | VARCHAR2(500 CHAR) | Y | | CK | 반려 사유 (반려 시 필수) |
| REVIEWED_EMPLOYEE_ID | NUMBER(19) | Y | | FK → EMPLOYEE | 심사 직원 (증액 심사 완료 시 필수, 감액은 NULL) |
| REVIEWED_AT | TIMESTAMP | Y | | | 심사일시 (증액 심사 완료 시 필수, 감액은 NULL) |
| CREATED_AT | TIMESTAMP | N | SYSTIMESTAMP | | 신청일시 (감액은 이 시각에 즉시 반영) |
| UPDATED_AT | TIMESTAMP | Y | | | 수정일시 |

※ `FK_TLR_ACCOUNT`는 팀원2의 `ACCOUNT` 테이블 생성 후 추가 (`schema-team1.sql` 맨 아래 주석)

**제약조건 / 인덱스**

| 이름 | 종류 | 대상 |
|---|---|---|
| PK_TRANSFER_LIMIT_REQUEST | PK | REQUEST_ID |
| FK_TLR_MEMBER | FK | MEMBER_ID → MEMBER |
| FK_TLR_EMPLOYEE | FK | REVIEWED_EMPLOYEE_ID → EMPLOYEE |
| FK_TLR_ACCOUNT | FK | ACCOUNT_ID → ACCOUNT (ACCOUNT 생성 후 추가) |
| CK_TLR_STATUS | CHECK | STATUS IN ('PENDING','APPROVED','REJECTED') |
| CK_TLR_LIMIT | CHECK | CURRENT_LIMIT ≥ 0, REQUESTED_LIMIT > 0, 두 값이 달라야 함 |
| CK_TLR_REJECT_REASON | CHECK | 반려(REJECTED)면 REJECT_REASON 필수 |
| CK_TLR_DECREASE_AUTO | CHECK | 감액이면 STATUS = APPROVED, 심사 직원·심사일시 NULL |
| CK_TLR_REVIEWED | CHECK | 처리된 증액 건은 REVIEWED_EMPLOYEE_ID, REVIEWED_AT 필수 |
| UX_TLR_ONE_PENDING | UNIQUE INDEX | 계좌당 PENDING 1건만 (`CASE WHEN STATUS='PENDING' THEN ACCOUNT_ID END`) |
| IX_TLR_STATUS | INDEX | (STATUS, CREATED_AT) — 심사 대기 목록 |
| IX_TLR_MEMBER | INDEX | (MEMBER_ID, CREATED_AT) — 내 신청 내역 |

---

## 테이블 관계

| 부모 | 자식 | 관계 | FK 컬럼 |
|---|---|---|---|
| BRANCH | EMPLOYEE | 1:N | BRANCH_ID (소속 지점) |
| BRANCH | BRANCH_SERVICE | 1:N | BRANCH_ID (이용 가능 업무) |
| MEMBER | IDENTITY_VERIFICATION | 1:N | MEMBER_ID (신청 회원) |
| EMPLOYEE | IDENTITY_VERIFICATION | 1:N | REVIEWED_EMPLOYEE_ID (심사 직원) |
| MEMBER | LOGIN_LOG | 1:N | MEMBER_ID (NULL 허용) |
| EMPLOYEE | EMPLOYEE_LOGIN_LOG | 1:N | EMPLOYEE_ID (NULL 허용) |
| MEMBER | TRANSFER_LIMIT_REQUEST | 1:N | MEMBER_ID (신청 회원) |
| EMPLOYEE | TRANSFER_LIMIT_REQUEST | 1:N | REVIEWED_EMPLOYEE_ID (심사 직원) |
| ACCOUNT (팀원2) | TRANSFER_LIMIT_REQUEST | 1:N | ACCOUNT_ID |
| MEMBER | 다른 팀원 테이블 <br>(ACCOUNT, LOAN, CARD, INQUIRY 등) | 1:N | MEMBER_ID |
| EMPLOYEE | 다른 팀원 테이블 <br>(ADMIN_LOG, NOTICE, INQUIRY 답변, LOAN 심사 등) | 1:N | EMPLOYEE_ID / REVIEWED_EMPLOYEE_ID / ANSWERED_EMPLOYEE_ID |
