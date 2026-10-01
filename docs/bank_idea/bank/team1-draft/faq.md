# 팀원1 설계 FAQ

설계하면서 나온 질문과 답을 기록합니다.

---

## Q. MEMBER 테이블의 VERIFICATION_STATUS 값은 무엇인가요?

`VERIFICATION_STATUS`는 **고객의 본인확인(비대면 실명확인) 진행 상태**를 저장하는 컬럼입니다. 회원가입 때가 아니라 **첫 입출금 계좌를 개설할 때** 본인확인을 하기 때문에 이 컬럼이 필요합니다.

### 가능한 값 4가지

| 값 | 의미 | 언제 이 값이 되나 |
|---|---|---|
| `NONE` | 미확인 | 회원가입 직후 (기본값) |
| `PENDING` | 심사중 | 첫 계좌 개설 때 신분증 정보와 1원 인증을 제출했을 때 |
| `VERIFIED` | 확인완료 | 직원(STAFF 이상)이 심사를 승인했을 때 |
| `REJECTED` | 반려 | 직원이 사유를 적고 반려했을 때 |

### 상태가 바뀌는 흐름

```
회원가입 ──▶ NONE ──(본인확인 신청)──▶ PENDING ──(직원 승인)──▶ VERIFIED
                                          │
                                          └──(직원 반려)──▶ REJECTED ──(재신청)──▶ PENDING
```

- 신청은 `NONE`이나 `REJECTED`일 때만 할 수 있습니다. 이미 심사 중이거나 확인이 끝난 회원은 다시 신청할 수 없습니다.
- `VERIFIED`가 되면 다시 바뀌지 않습니다.

### 무엇에 쓰이나

**1. 기능 사용 가능 여부를 판단합니다.**

| 기능 | NONE / PENDING / REJECTED | VERIFIED |
|---|:---:|:---:|
| 로그인, 상품 조회, 공지/FAQ, 1:1 문의 | O | O |
| 첫 입출금 계좌 개설 신청 | O (`PENDING` 제외) | - |
| 계좌 추가 개설, 입출금, 이체 | X | O |
| 예적금 가입, 카드 신청, 대출 신청, 이체한도 변경 신청 | X | O |

**2. 화면에 안내를 표시합니다.** 대시보드의 본인확인 안내 배너와 마이페이지의 상태 표시(미확인/심사중/완료/반려)에 씁니다.

### IDENTITY_VERIFICATION 테이블과의 관계

본인확인 신청 이력은 `IDENTITY_VERIFICATION` 테이블에 따로 쌓입니다. 반려되고 다시 신청하면 행이 하나 더 생깁니다. `MEMBER.VERIFICATION_STATUS`는 그 **최신 결과를 요약해 둔 값**입니다. 매번 이력 테이블을 조회하지 않고 회원 정보만으로 바로 판단하려고 둔 컬럼입니다.

두 값이 어긋나지 않도록, 상태는 `IdentityVerification`의 `request()`, `approve()`, `reject()` 메서드 안에서만 바꾸게 만들어 두었습니다. 그래서 이력 테이블과 회원 테이블이 항상 같은 트랜잭션에서 함께 바뀝니다.

### 관련 코드/제약

| 위치 | 내용 |
|---|---|
| DB | CHECK 제약 `CK_MEMBER_VERIFICATION`으로 4개 값만 허용 (`schema-team1.sql`) |
| Java | `member.domain.VerificationStatus` enum |
| 상태 변경 | `IdentityVerification.request()` / `approve()` / `reject()` → `Member`의 상태 변경 메서드 호출 |
| 기능 제한 검사 | `Member.isVerified()`, Service 공통 메서드 `memberService.requireVerified()` |

---

## Q. MEMBER 테이블의 STATUS 값은 무엇인가요?

`STATUS`는 **고객 계정 자체의 상태**, 즉 이 고객이 우리 은행 서비스를 이용할 수 있는지를 나타냅니다. 본인확인 진행 상태(`VERIFICATION_STATUS`)와는 별개입니다.

### 가능한 값 3가지

| 값 | 의미 | 누가 바꾸나 | 로그인 |
|---|---|---|:---:|
| `ACTIVE` | 정상 | 회원가입 시 기본값, MANAGER가 정지 해제 | O |
| `SUSPENDED` | 정지 | MANAGER가 관리자 회원 관리 화면(14.2)에서 정지 | X |
| `WITHDRAWN` | 탈퇴 | 고객 본인이 마이페이지에서 탈퇴 | X |

### 상태가 바뀌는 흐름

```
회원가입 ──▶ ACTIVE ◀──(MANAGER 해제)── SUSPENDED
               │  └──(MANAGER 정지)──▶ SUSPENDED
               │
               └──(본인 탈퇴)──▶ WITHDRAWN   (되돌릴 수 없음)
```

- 정지는 `ACTIVE`일 때만, 해제는 `SUSPENDED`일 때만 할 수 있습니다 (`Member.suspend()` / `reactivate()`).
- 탈퇴는 되돌릴 수 없습니다. 이미 탈퇴한 회원은 다시 탈퇴할 수 없습니다 (`Member.withdraw()`).

### 탈퇴하면 어떻게 되나

계좌, 거래내역, 대출이 회원을 참조하고 있어서 **행을 지우지 않고 상태만 `WITHDRAWN`으로 바꿉니다.** 대신 개인정보를 정리합니다.

| 항목 | 처리 | 이유 |
|---|---|---|
| `EMAIL` | `withdrawn_{회원ID}@deleted.local`로 마스킹 | 개인정보 파기, 같은 이메일로 재가입 가능 |
| `PHONE` | `W{회원ID}`로 마스킹 | 개인정보 파기, 같은 번호로 재가입 가능 (UNIQUE 제약 회피) |
| `MARKETING_AGREE_YN` | `N` | 탈퇴 후 마케팅 수신 중단 |
| `WITHDRAWN_AT` | 탈퇴 시각 기록 | |
| `LOGIN_ID` | **그대로 유지** | 같은 아이디를 다른 사람이 쓰면 과거 기록과 혼동되므로 재사용 금지 |

> 탈퇴 조건(잔액이 남은 계좌, 상환 중인 대출, 가입 중인 예적금이 있으면 탈퇴 불가 등)은 아직 팀에서 정하지 않았습니다. 팀원2·팀원3과 정한 뒤 Service에서 `withdraw()` 호출 전에 검사합니다.

### 로그인 실패 기록

정지·탈퇴 회원이 로그인하면 실패로 처리하고 `LOGIN_LOG.FAIL_REASON`에 사유를 남깁니다.

| STATUS | 로그인 실패 사유 |
|---|---|
| `SUSPENDED` | `SUSPENDED` |
| `WITHDRAWN` | `WITHDRAWN` |

### 헷갈리기 쉬운 다른 상태들과의 차이

| 구분 | 컬럼 | 대상 | 의미 | 해제 방법 |
|---|---|---|---|---|
| 회원 상태 | `MEMBER.STATUS` | 고객 계정 전체 | 서비스 이용 가능 여부 (정상/정지/탈퇴) | MANAGER가 정지 해제 |
| 본인확인 상태 | `MEMBER.VERIFICATION_STATUS` | 고객 계정 | 금융 기능 사용 자격 (미확인/심사중/완료/반려) | 직원 심사 |
| 로그인 잠금 | `MEMBER.LOGIN_LOCKED_AT` | 고객 로그인 | 비밀번호 5회 오류로 생긴 **임시** 잠금. `STATUS`는 `ACTIVE` 그대로 | 비밀번호 재설정 |
| 계좌 상태 | `ACCOUNT.STATUS` (팀원2) | 계좌 하나 | 계좌 단위 동결·잠금 (계좌 비밀번호 5회 오류 등) | MANAGER가 계좌 관리 화면에서 해제 |

- 로그인 잠금은 본인이 비밀번호를 재설정하면 풀리는 일시적인 보안 조치라서 `STATUS`를 바꾸지 않습니다. `SUSPENDED`는 은행이 의도적으로 내린 조치라서 직원만 풀 수 있습니다.
- 회원이 정지되어도 계좌 상태는 그대로입니다. 로그인을 할 수 없어 본인 거래는 막히지만, 다른 사람이 그 계좌로 입금하는 것은 가능합니다.

### 관련 코드/제약

| 위치 | 내용 |
|---|---|
| DB | CHECK 제약 `CK_MEMBER_STATUS`로 3개 값만 허용, 기본값 `'ACTIVE'` (`schema-team1.sql`) |
| Java | `member.domain.MemberStatus` enum |
| 상태 변경 | `Member.suspend()` / `reactivate()` (MANAGER), `Member.withdraw()` (본인) |
| 상태 확인 | `Member.isActive()` — 로그인 시 `UserDetailsService`에서 검사 |

---

## Q. BRANCH 테이블은 무엇인가요?

`BRANCH`는 **은행의 지점과 ATM 정보**를 저장하는 테이블입니다. 두 가지 용도로 쓰입니다.

| 용도 | 사용하는 곳 | 설명 |
|---|---|---|
| 지점/ATM 찾기 | 고객 화면 41번 (`/branches`) | 비회원도 지도(카카오맵)나 목록에서 가까운 지점·ATM을 검색 |
| 직원 소속 지점 | `EMPLOYEE.BRANCH_ID` | 모든 직원은 하나의 지점(본점 포함)에 소속 |

### 지점과 ATM을 한 테이블에 둔 이유

이름, 주소, 좌표, 영업시간처럼 저장할 정보가 거의 같아서 `BRANCH_TYPE` 컬럼 하나로 구분합니다.

| BRANCH_TYPE | 의미 | 특징 |
|---|---|---|
| `BRANCH` | 지점 (본점 포함) | 전화번호 있음, 평일 영업시간, 직원 소속 가능 |
| `ATM` | ATM | 전화번호 없음(NULL), 24시간 운영 가능, **직원 소속 불가** |

본점은 별도 구분 없이 `BRANCH_TYPE = 'BRANCH'`인 행 하나("모의은행 본점")로 저장합니다.

### 주요 컬럼

| 컬럼 | 설명 |
|---|---|
| `NAME`, `ADDRESS` | 이름, 주소 — 검색 화면에서 이름·주소로 `LIKE` 검색 |
| `LATITUDE`, `LONGITUDE` | 위도·경도 — 카카오맵에 마커를 찍는 좌표. `NUMBER(10,7)`로 소수점 7자리(약 1cm 정밀도) |
| `PHONE` | 전화번호 (ATM은 NULL) |
| `BUSINESS_HOURS` | 영업시간 문구 (예: "평일 09:00~16:00") |
| `OPEN_24H_YN` | 24시간 운영 여부 — ATM 찾기에서 필터로 사용 |
| `USE_YN` | 사용 여부 — 삭제 대신 `N`으로 처리 |

이용 가능 업무(입금, 출금 등)는 `BRANCH_SERVICE` 테이블에 따로 저장합니다. 아래 "BRANCH의 이용 가능 업무" 질문을 참고하세요.

### 주요 설계 포인트

- **삭제하지 않고 `USE_YN = 'N'`으로 숨깁니다.** 직원이 지점을 참조(FK)하고 있어서 실제로 지우면 오류가 나거나 직원의 소속 기록이 사라지기 때문입니다. 조회 화면은 `USE_YN = 'Y'`인 행만 보여줍니다.
- **직원 소속은 지점만 가능합니다.** "ATM 행이 아닌지"는 DB 제약으로 검사할 수 없어서(다른 테이블의 값을 봐야 함) `Employee` Entity에서 검사합니다.
- **이용 가능 업무는 별도 테이블(`BRANCH_SERVICE`)로 분리했습니다.** 처음에는 쉼표 문자열 컬럼(`SERVICES`)이었지만, 값이 통일되지 않고 업무별 필터가 어려워서 바꿨습니다.
- **사용 중지할 때 주의할 점**: 재직 중인 직원이 소속된 지점을 `USE_YN = 'N'`으로 바꾸면 안 됩니다. 이 검사는 아직 코드에 없어서, 관리 화면을 만들 때 Service에서 "소속 직원이 있으면 사용 중지 불가"를 확인해야 합니다.

### 시드 데이터 (`data-team1.sql`)

| 구분 | 데이터 |
|---|---|
| 지점 6개 | 본점, 시청, 강남, 여의도, 판교, 서면 |
| ATM 4개 | 강남역, 홍대입구역, 잠실, 종로 |

모두 모의 데이터이고, 좌표는 지역의 대표 위치입니다. `EMPLOYEE`가 참조하므로 **시드 파일에서 가장 먼저 입력**하고, DDL도 `EMPLOYEE`보다 먼저 만듭니다.

### 권한

| 기능 | 누가 |
|---|---|
| 지점/ATM 찾기 (조회) | 누구나 (비회원 포함) |
| 지점/ATM 등록·수정·사용 중지 (화면 42번) | `SYSTEM_ADMIN` |

### 관련 코드/제약

| 위치 | 내용 |
|---|---|
| DB | `CK_BRANCH_TYPE`(BRANCH/ATM), `CK_BRANCH_LAT`·`CK_BRANCH_LNG`(좌표 범위), `CK_BRANCH_OPEN_24H`·`CK_BRANCH_USE`(Y/N), 인덱스 `IX_BRANCH_TYPE` |
| Java | `branch.domain.Branch`, `BranchType` enum |
| 상태 변경 | `Branch.create()` / `update()` / `disable()` |
| 검색 | `BranchRepository.search(type, keyword, service, open24h)` — 사용 중인 행만, 유형·이름·주소·업무·24시간 조건 |
| 직원 소속 검사 | `Employee.assignBranch()` — ATM이면 예외 |

---

## Q. BRANCH의 이용 가능 업무(SERVICES)는 어떤 값이 있고, 프로젝트에서 어떻게 쓰이나요?

### 처음 설계의 문제

처음에는 `BRANCH.SERVICES` 컬럼에 `"입금,출금,이체"`처럼 쉼표로 이어 붙인 자유 문자열을 저장했습니다. 이 방식에는 문제가 있었습니다.

1. **값이 통일되지 않습니다.** `입금`, `입 금`, `현금입금`처럼 같은 뜻이 다르게 들어갈 수 있습니다.
2. **업무별 필터를 만들기 어렵습니다.** "입금 가능한 ATM만 보기"를 하려면 `LIKE '%입금%'`로 찾아야 해서 부정확하고 인덱스도 못 씁니다.
3. **범위와 어긋나는 값이 섞였습니다.** 제외된 외환 업무(`외환상담`)가 시드에 들어가 있었습니다.

또 실제로 이 값을 쓰는 곳은 화면 표시뿐이었고, 검색 조건에도 업무 로직에도 쓰이지 않았습니다.

### 바꾼 설계: 별도 테이블 + 고정 코드 (B안)

`SERVICES` 컬럼을 없애고 **`BRANCH_SERVICE(BRANCH_ID, SERVICE_CODE)`** 테이블로 분리했습니다. 지점 1곳에 업무가 여러 개 있으므로 1:N 관계입니다.

| 코드 | 표시명 | 지점 | ATM |
|---|---|:---:|:---:|
| `DEPOSIT` | 입금 | O | O |
| `WITHDRAW` | 출금 | O | O |
| `TRANSFER` | 이체 | O | O |
| `BALANCE_INQUIRY` | 잔액조회 | O | O |
| `ACCOUNT_OPENING` | 계좌개설 | O | X (창구 업무) |
| `LOAN_CONSULTING` | 대출상담 | O | X (창구 업무) |
| `CARD_ISSUANCE` | 카드발급 | O | X (창구 업무) |

- 허용값은 DB CHECK 제약(`CK_BRANCH_SERVICE_CODE`)과 Java enum(`BranchServiceType`)으로 고정했습니다.
- 직원이 필요한 창구 업무는 ATM에 등록할 수 없습니다. `Branch` Entity에서 검사합니다.
- 지점/ATM마다 업무가 1개 이상 있어야 합니다.
- 같은 업무를 두 번 등록할 수 없습니다 (PK가 `BRANCH_ID + SERVICE_CODE`).

### 프로젝트에서 쓰이는 곳

| 화면 | 사용 방법 |
|---|---|
| 41번 지점/ATM 찾기 | 결과 목록에 업무를 한글로 표시 (`label`), **업무 필터**("입금 가능한 곳만")와 24시간 필터 제공 |
| 42번 지점/ATM 관리 (SYSTEM_ADMIN) | 업무를 체크박스로 선택. ATM이면 창구 업무 체크박스를 비활성화 |

거래(입출금·이체) 로직과는 관계가 없습니다. 이 프로젝트의 거래는 모두 인터넷뱅킹에서 일어나고, 어느 지점/ATM에서 거래했는지는 기록하지 않기 때문입니다.

### JPA 구현 방법

별도 Entity를 만들지 않고 `@ElementCollection`으로 `Branch` 안에 `Set<BranchServiceType>`으로 둡니다. 업무는 혼자서는 의미가 없고 항상 지점에 딸려 있는 값이기 때문입니다.

```java
@ElementCollection(fetch = FetchType.LAZY)
@CollectionTable(name = "BRANCH_SERVICE", joinColumns = @JoinColumn(name = "BRANCH_ID"))
@Enumerated(EnumType.STRING)
@Column(name = "SERVICE_CODE")
private Set<BranchServiceType> services = new HashSet<>();
```

- 수정할 때는 컬렉션 객체를 새로 만들지 않고 `clear()` 후 `addAll()`로 내용만 바꿉니다. 그래야 Hibernate가 바뀐 부분만 반영합니다.
- 밖에서 목록을 직접 바꾸지 못하도록 `getServices()`는 읽기 전용이고, 화면 표시 순서(enum 선언 순서)로 정렬해서 돌려줍니다.
- 검색 쿼리는 `left join fetch b.services`로 업무 목록을 함께 가져와서, 목록 화면에서 지점마다 추가 쿼리가 나가는 문제(N+1)를 막습니다. 업무 필터는 JPQL의 `:service member of b.services`로 처리합니다.

### 시드 데이터

| 대상 | 업무 |
|---|---|
| 모든 지점 (6곳) | 입금, 출금, 이체, 계좌개설, 카드발급 |
| 판교지점 제외 지점 (5곳) | + 대출상담 |
| ATM 3곳 (강남역, 홍대입구역, 종로) | 입금, 출금, 이체, 잔액조회 |
| 잠실 ATM | 출금, 잔액조회 (출금 전용 기기) |

### 관련 코드/제약

| 위치 | 내용 |
|---|---|
| DB | `BRANCH_SERVICE` 테이블, `PK_BRANCH_SERVICE`, `FK_BRANCH_SERVICE_BRANCH`, `CK_BRANCH_SERVICE_CODE`, 인덱스 `IX_BRANCH_SERVICE_CODE` |
| Java | `branch.domain.BranchServiceType` enum (`label`, `counterOnly`, `isAvailableAt()`) |
| Entity | `Branch.services`, `Branch.provides()`, `Branch.update()`에서 업무 검사 |
| 검색 | `BranchRepository.search(type, keyword, service, open24h)` |

---

## Q. IDENTITY_VERIFICATION 테이블은 무엇인가요?

`IDENTITY_VERIFICATION`은 **고객의 본인확인(비대면 실명확인) 신청과 직원 심사 이력**을 저장하는 테이블입니다.

실제 은행은 인터넷으로 첫 계좌를 만들 때 신분증 확인, 다른 은행 계좌로 1원 보내기 같은 방법으로 본인인지 확인합니다. 이 프로젝트는 그 과정을 흉내 내서, 고객이 첫 입출금 계좌를 개설할 때 정보를 제출하면 직원이 확인하고 승인하거나 반려합니다.

### 언제 만들어지나

```
고객: 첫 입출금 계좌 개설 신청
  ├─ 신분증 정보 + 1원 인증 정보 입력
  ├─ IDENTITY_VERIFICATION 1건 생성 (STATUS = PENDING)
  ├─ MEMBER.VERIFICATION_STATUS = PENDING
  └─ ACCOUNT 1건 생성 (상태 = 개설대기, 팀원2)
        ↓
직원(STAFF 이상): 관리자 회원 관리 화면에서 심사
  ├─ 승인 → STATUS = APPROVED, 회원 VERIFIED, 개설대기 계좌 → 정상
  └─ 반려 → STATUS = REJECTED(사유 필수), 회원 REJECTED, 개설대기 계좌 → 개설거절
```

### 컬럼

| 컬럼 | 설명 |
|---|---|
| `VERIFICATION_ID` | 신청 ID (PK) |
| `MEMBER_ID` | 신청한 고객 (→ `MEMBER`) |
| `ID_CARD_TYPE` | 신분증 종류: `RESIDENT_CARD` 주민등록증 / `DRIVER_LICENSE` 운전면허증 |
| `ID_ISSUE_DATE` | 신분증 발급일자 (mock). 실제 은행의 신분증 진위확인에서 발급일자를 대조하는 것을 흉내 냄 |
| `AUTH_BANK_NAME` | 1원 인증에 쓴 다른 은행 이름 (mock) |
| `AUTH_ACCOUNT_MASKED` | 1원 인증 계좌번호. **가린 값만 저장** (예: `110-***-**1234`) |
| `STATUS` | `PENDING` 대기 / `APPROVED` 승인 / `REJECTED` 반려 |
| `REJECT_REASON` | 반려 사유 |
| `REQUESTED_AT` | 신청일시 |
| `REVIEWED_EMPLOYEE_ID` | 심사한 직원 (→ `EMPLOYEE`) |
| `REVIEWED_AT` | 심사일시 |

주민등록번호, 신분증 사진 같은 **실제 개인정보는 저장하지 않습니다.** 학원 프로젝트에서 실제 개인정보를 다룰 이유가 없고, 저장하면 보안 책임만 커지기 때문입니다.

### 이력을 쌓는 방식

반려된 뒤 다시 신청하면 기존 행을 고치지 않고 **새 행을 추가**합니다. 그래야 "언제, 누가, 왜 반려했는지"가 남습니다.

| VERIFICATION_ID | MEMBER_ID | STATUS | REJECT_REASON | 심사 직원 |
|---|---|---|---|---|
| 1 | 10 | `REJECTED` | 신분증 발급일자 불일치 | E0003 |
| 2 | 10 | `APPROVED` | | E0002 |

고객 10번은 한 번 반려된 뒤 다시 신청해서 승인받았습니다. 최신 결과(`VERIFIED`)는 `MEMBER.VERIFICATION_STATUS`에도 반영되어 있어, 평소에는 회원 정보만 보고 판단합니다.

### DB가 강제하는 규칙

| 규칙 | 제약 |
|---|---|
| 회원 1명당 심사 대기(`PENDING`)는 1건만 | `UX_IV_ONE_PENDING` — `CASE WHEN STATUS='PENDING' THEN MEMBER_ID END`에 유니크 인덱스. Oracle은 NULL을 유니크 검사에서 빼므로 PENDING 행끼리만 중복 검사됨 |
| 반려하면 사유 필수 | `CK_IV_REJECT_REASON` |
| 심사가 끝난 건은 심사 직원·심사일시 필수 | `CK_IV_REVIEWED` |
| 신분증 종류·상태는 정해진 값만 | `CK_IV_ID_CARD_TYPE`, `CK_IV_STATUS` |

"대기 1건" 규칙을 DB에서도 막는 이유는, 고객이 신청 버튼을 빠르게 두 번 누르면 Java 검사를 동시에 통과해 중복 신청이 생길 수 있기 때문입니다.

### Java(Entity)가 강제하는 규칙

| 규칙 | 위치 |
|---|---|
| 신청은 회원이 `NONE` 또는 `REJECTED`일 때만 | `IdentityVerification.request()` → `Member.markVerificationPending()` |
| 신분증 발급일자는 미래일 수 없음 | `request()` |
| 계좌번호는 받아서 바로 가림 처리 | `maskAccountNumber()` |
| 심사는 **STAFF 이상 재직 직원만** (MANAGER 가능, SYSTEM_ADMIN·휴직·퇴사 직원 불가) | `approve()` / `reject()` → `Employee.hasAuthority(STAFF)` |
| 이미 처리된 건은 다시 처리 불가 | `startReview()` |
| 승인·반려 시 회원 상태도 함께 변경 | `approve()` → `Member.markVerified()`, `reject()` → `Member.markVerificationRejected()` |

회원의 본인확인 상태를 바꾸는 메서드는 `IdentityVerification`에서만 부를 수 있게(package-private) 만들어서, 이력 테이블과 회원 테이블이 어긋나지 않게 했습니다.

### 다른 팀원과 연결되는 부분

승인·반려는 Service의 한 트랜잭션 안에서 다음을 함께 처리합니다.

| 처리 | 담당 |
|---|---|
| 심사 결과 저장, 회원 상태 변경 | 팀원1 (`IdentityVerification.approve()/reject()`) |
| 개설대기 계좌를 정상 또는 개설거절로 변경 | 팀원2 (`AccountService.activatePending(memberId)` / `rejectPending(memberId)`) |
| 관리자 작업 로그 기록 | 팀원4 (`ADMIN_LOG`) |
| 고객에게 결과 알림 | 팀원4 (알림, 커밋 후 발행) |

반려 시 계좌를 개설거절로 바꾸는 `rejectPending()`은 아직 팀원2와 이름을 맞추지 않았습니다. 1주차에 함께 정해야 합니다.

### 시드 데이터

| 회원 | 상태 | 심사 직원 |
|---|---|---|
| `user1` | `APPROVED` (승인 완료) | E0003 (STAFF) |
| `user3` | `PENDING` (심사 대기) | — |
| `user2` | 신청 이력 없음 (`NONE`) | — |

### 관련 코드/제약

| 위치 | 내용 |
|---|---|
| DB | `schema-team1.sql`의 `IDENTITY_VERIFICATION`, 인덱스 `UX_IV_ONE_PENDING`, `IX_IV_MEMBER`(회원별 이력), `IX_IV_STATUS`(심사 대기 목록) |
| Java | `member.domain.IdentityVerification`, `IdCardType` enum, `common.domain.ReviewStatus` enum |
| Repository | `IdentityVerificationRepository.findByStatus()`(심사 목록, 회원 정보 함께 조회), `findByMember_IdAndStatus()`, `findByMember_IdOrderByRequestedAtDesc()`(신청 이력) |
| 함께 보기 | 이 문서의 "MEMBER 테이블의 VERIFICATION_STATUS 값" 질문 |

---

## Q. 본인확인이 승인되면 MEMBER 테이블에 INSERT 되나요?

**아니요.** `MEMBER` 행은 **회원가입 때 이미 INSERT** 되어 있고, 본인확인 승인 때는 그 행의 `VERIFICATION_STATUS` 값만 **UPDATE** 합니다.

순서가 "회원가입 → 본인확인 신청 → 승인"이기 때문입니다. `IDENTITY_VERIFICATION.MEMBER_ID`가 `MEMBER`를 참조(FK)하므로, 회원 행이 먼저 있어야 본인확인 신청 행을 만들 수 있습니다.

### 단계별로 실행되는 SQL

| 단계 | MEMBER | IDENTITY_VERIFICATION | ACCOUNT (팀원2) |
|---|---|---|---|
| ① 회원가입 | **INSERT** (`VERIFICATION_STATUS = 'NONE'`) | — | — |
| ② 첫 계좌 개설 + 본인확인 신청 | **UPDATE** → `'PENDING'` | **INSERT** (`STATUS = 'PENDING'`) | **INSERT** (개설대기) |
| ③-a 직원 승인 | **UPDATE** → `'VERIFIED'` | **UPDATE** → `'APPROVED'`, 심사 직원·일시 | **UPDATE** → 정상 |
| ③-b 직원 반려 | **UPDATE** → `'REJECTED'` | **UPDATE** → `'REJECTED'`, 사유·심사 직원·일시 | **UPDATE** → 개설거절 |
| ④ 반려 후 재신청 | **UPDATE** → `'PENDING'` | **새 행 INSERT** (`'PENDING'`) | 새 계좌 INSERT (개설대기) |

```sql
-- ① 회원가입
INSERT INTO MEMBER (MEMBER_ID, LOGIN_ID, ..., VERIFICATION_STATUS) VALUES (10, 'hong', ..., 'NONE');

-- ② 본인확인 신청
INSERT INTO IDENTITY_VERIFICATION (VERIFICATION_ID, MEMBER_ID, ..., STATUS) VALUES (1, 10, ..., 'PENDING');
UPDATE MEMBER SET VERIFICATION_STATUS = 'PENDING' WHERE MEMBER_ID = 10;

-- ③ 직원 승인
UPDATE IDENTITY_VERIFICATION SET STATUS = 'APPROVED', REVIEWED_EMPLOYEE_ID = 3, REVIEWED_AT = SYSTIMESTAMP
 WHERE VERIFICATION_ID = 1;
UPDATE MEMBER SET VERIFICATION_STATUS = 'VERIFIED' WHERE MEMBER_ID = 10;
```

JPA에서는 이 SQL을 직접 쓰지 않습니다. 회원가입 때만 `memberRepository.save()`로 INSERT 하고, 그다음부터는 `verification.approve(employee)`처럼 Entity 값을 바꾸면 트랜잭션이 끝날 때 Hibernate가 UPDATE 문을 자동으로 실행합니다(변경 감지, dirty checking).

### 정리

- `MEMBER` = **회원가입 때 1번 INSERT**, 이후 상태는 UPDATE로만 바뀜
- `IDENTITY_VERIFICATION` = **신청할 때마다 INSERT**, 심사 결과는 그 행에 UPDATE
- 본인확인은 "회원이 되는 절차"가 아니라, **이미 회원인 사람이 금융 기능(계좌·이체·상품)을 쓸 자격을 얻는 절차**입니다.

---

## Q. TRANSFER_LIMIT_REQUEST 테이블은 무엇인가요?

`TRANSFER_LIMIT_REQUEST`는 **고객의 이체한도 변경 신청과 직원 심사 결과**를 저장하는 테이블입니다.

계좌마다 하루에 이체할 수 있는 금액(1일 이체한도)이 정해져 있습니다. 실제 값은 팀원2의 `ACCOUNT` 테이블에 있습니다. 고객이 이 한도를 바꾸고 싶으면 마이페이지 보안설정(화면 30번)에서 신청하고, **MANAGER 직원**이 관리자 계좌 관리 화면(33번)에서 승인하거나 반려합니다.

실제 은행에서도 한도를 **올리는** 것은 금융사기 피해를 키울 수 있어서 추가 확인 절차를 거치고, **내리는** 것은 오히려 안전해지므로 바로 반영합니다. 이 프로젝트도 같은 방식입니다.

| 구분 | 처리 | 이유 |
|---|---|---|
| **감액** (희망 < 현재) | 신청 즉시 `APPROVED`, ACCOUNT 한도 바로 변경. 심사 직원 없음 | 한도를 내리면 빠져나갈 수 있는 돈이 줄어 더 안전함 |
| **증액** (희망 > 현재) | `PENDING` → **MANAGER**가 승인/반려 | 피해 범위가 넓어지므로 책임자 확인 필요 |

### 흐름

```
고객 (본인확인 완료): 마이페이지 → 이체한도 변경 신청 (계좌, 희망 한도, 사유)
  ※ 같은 계좌에 심사 중인 신청이 있으면 새 신청 불가
  │
  ├─ 감액 → INSERT (STATUS = APPROVED, 심사 직원 NULL)
  │         + ACCOUNT 한도 UPDATE (팀원2, 같은 트랜잭션) → 끝
  │
  └─ 증액 → INSERT (STATUS = PENDING, 신청 시점 한도 기록)
              ↓
     MANAGER: 관리자 계좌 관리 → 한도 변경 신청 목록에서 심사
       ├─ 승인 → 계좌의 현재 한도가 신청 당시와 같은지 확인
       │         → STATUS = APPROVED + ACCOUNT 한도 UPDATE (같은 트랜잭션)
       └─ 반려 → STATUS = REJECTED (사유 필수), ACCOUNT는 그대로
```

### 컬럼

| 컬럼 | 설명 |
|---|---|
| `REQUEST_ID` | 신청 ID (PK) |
| `MEMBER_ID` | 신청한 고객 (→ `MEMBER`) |
| `ACCOUNT_ID` | 한도를 바꿀 계좌 (→ `ACCOUNT`, 팀원2) |
| `CURRENT_LIMIT` | **신청 시점의** 1일 이체한도 (원) |
| `REQUESTED_LIMIT` | 희망하는 1일 이체한도 (원) |
| `REASON` | 신청 사유 |
| `STATUS` | `PENDING` 대기(증액만) / `APPROVED` 승인 / `REJECTED` 반려 |
| `REJECT_REASON` | 반려 사유 |
| `REVIEWED_EMPLOYEE_ID`, `REVIEWED_AT` | 심사한 직원(MANAGER)과 심사일시. **감액은 둘 다 NULL** (심사 없이 반영) |
| `CREATED_AT`, `UPDATED_AT` | 신청일시, 수정일시 |

**`CURRENT_LIMIT`을 따로 저장하는 이유**: `ACCOUNT`의 한도는 나중에 또 바뀔 수 있습니다. 신청 당시 한도를 남겨 두어야 "100만 원 → 500만 원으로 올려 달라는 신청이었다"는 기록이 정확히 남습니다. 직원도 심사 화면에서 얼마나 올리는 신청인지 바로 볼 수 있습니다.

### DB가 강제하는 규칙

| 규칙 | 제약 |
|---|---|
| 계좌 1개당 심사 대기(`PENDING`) 신청은 1건만 | `UX_TLR_ONE_PENDING` (함수 기반 유니크 인덱스, 본인확인과 같은 방식) |
| 희망 한도는 0보다 크고, 현재 한도와 달라야 함 | `CK_TLR_LIMIT` |
| 반려하면 사유 필수 | `CK_TLR_REJECT_REASON` |
| 감액은 항상 즉시 승인, 심사 직원·심사일시 없음 | `CK_TLR_DECREASE_AUTO` |
| 처리된 증액 건은 심사 직원·심사일시 필수 | `CK_TLR_REVIEWED` |

### Java(Entity)가 강제하는 규칙

| 규칙 | 위치 |
|---|---|
| **본인확인 완료(`VERIFIED`) 회원만** 신청 가능 | `TransferLimitRequest.request()` |
| 희망 한도는 0보다 크고 현재 한도와 달라야 함 | `request()` |
| 감액이면 바로 `APPROVED`, 증액이면 `PENDING` | `request()`, `isIncrease()`, `isAutoApproved()` |
| 증액 심사는 **재직 중인 MANAGER만** (STAFF·SYSTEM_ADMIN 불가) | `approve()` / `reject()` → `Employee.hasAuthority(MANAGER)` |
| 승인 시 계좌의 현재 한도가 신청 당시와 다르면 승인 불가 | `approve(reviewer, accountLimitNow)` |
| 이미 처리된 건(즉시 반영된 감액 포함)은 다시 처리 불가 | `startReview()` |
| 같은 계좌에 심사 중인 신청이 있으면 새 신청 불가 (감액 포함) | Service에서 `existsByAccountIdAndStatus(accountId, PENDING)` |

본인확인 심사는 STAFF도 할 수 있지만, 한도 증액은 돈이 빠져나갈 수 있는 범위를 넓히는 일이라 **책임자(MANAGER)만** 승인하도록 했습니다.

**심사 중에는 감액도 막는 이유**: 증액 신청(100만 → 500만)이 심사 중일 때 감액(→ 50만)이 바로 반영되면, 증액 신청의 "현재 한도 100만"이 실제와 달라집니다. 그 상태로 승인하면 50만에서 500만으로 한 번에 오르게 됩니다. 이를 막으려고 ① 심사 중에는 새 신청을 받지 않고, ② 승인할 때도 실제 한도가 신청 당시와 같은지 한 번 더 확인합니다.

### 테이블이 하는 일과 하지 않는 일

| 이 테이블이 하는 일 | 하지 않는 일 |
|---|---|
| 신청 내용과 심사 결과 기록 | 실제 한도 값 보관 (그건 `ACCOUNT`) |
| 대기 중복 신청 방지 | 이체할 때 한도 검사 (그건 팀원2의 이체 로직이 `ACCOUNT` 값으로 검사) |

### 담당 나누기

| 처리 | 담당 |
|---|---|
| 테이블, 신청 화면(30번), 신청·심사 로직 | 팀원1 |
| 승인 시 `ACCOUNT`의 한도 변경 메서드, 이체 시 한도 검사 | 팀원2 |
| 관리자 심사 화면(33번), 관리자 작업 로그 | 팀원4 |

### 결정된 것 / 아직 정하지 않은 것

| 항목 | 상태 |
|---|---|
| 감액도 심사가 필요한가? | **결정**: 감액은 즉시 반영, 증액만 MANAGER 심사 |
| 심사 중 한도가 바뀐 경우 | **결정**: 심사 중에는 새 신청 불가 + 승인 시 현재 한도 재확인 |
| 최대 한도 | 미정 — 예: "1일 최대 5천만 원" 같은 상한을 정해 Service에서 검사 |
| 신청 취소 | 미정 — 필요하면 상태에 `CANCELED` 추가 (대출 신청과 같은 방식) |

### 아직 남은 작업

- `ACCOUNT_ID`의 FK(`FK_TLR_ACCOUNT`)는 팀원2의 `ACCOUNT` 테이블이 생긴 뒤에 추가합니다 (`schema-team1.sql` 맨 아래 주석).
- Java에서도 지금은 `Long accountId`로 두었고, 팀원2의 `Account` Entity가 생기면 `@ManyToOne Account`로 바꿉니다.
- 시드 데이터는 없습니다. 계좌가 있어야 신청할 수 있는데 계좌 시드는 팀원2가 만들기 때문입니다.

### 관련 코드/제약

| 위치 | 내용 |
|---|---|
| DB | `schema-team1.sql`의 `TRANSFER_LIMIT_REQUEST`, 인덱스 `UX_TLR_ONE_PENDING`, `IX_TLR_STATUS`(심사 대기 목록), `IX_TLR_MEMBER`(내 신청 내역) |
| Java | `member.domain.TransferLimitRequest` (`REVIEW_ROLE = MANAGER`, `isIncrease()`, `isAutoApproved()`, `approve(reviewer, accountLimitNow)`), `common.domain.ReviewStatus` |
| Repository | `TransferLimitRequestRepository.findByStatus()`, `existsByAccountIdAndStatus()`, `findByMember_IdOrderByCreatedAtDesc()` |
