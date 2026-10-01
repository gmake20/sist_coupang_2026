# 팀원1 테이블 설계 초안

팀원1 담당 테이블 8개의 DDL, 시드 데이터, JPA Entity, Repository 초안입니다.
1주차에 Spring Boot 프로젝트를 만든 뒤 같은 경로로 복사해서 사용합니다.
컬럼 상세는 [table.md](table.md)를 참고하세요.

**고객과 은행 직원은 테이블과 로그인을 분리합니다.** 고객은 `MEMBER`(아이디로 `/login`), 직원은 `EMPLOYEE`(사번으로 `/admin/login`)입니다.

| 구분 | 테이블 | 용도 | Entity |
|---|---|---|---|
| 고객 | `MEMBER` | 고객 회원 | `member.domain.Member` |
| 고객 | `IDENTITY_VERIFICATION` | 본인확인 심사 이력 | `member.domain.IdentityVerification` |
| 고객 | `LOGIN_LOG` | 고객 로그인 이력 | `member.domain.LoginLog` |
| 고객 | `TRANSFER_LIMIT_REQUEST` | 이체한도 변경 신청 | `member.domain.TransferLimitRequest` |
| 직원 | `EMPLOYEE` | 은행 직원 | `employee.domain.Employee` |
| 직원 | `EMPLOYEE_LOGIN_LOG` | 직원 로그인 이력 | `employee.domain.EmployeeLoginLog` |
| 공통 | `BRANCH` | 지점/ATM, 직원 소속 지점 | `branch.domain.Branch` |
| 공통 | `BRANCH_SERVICE` | 지점/ATM 이용 가능 업무 | `Branch.services` (`@ElementCollection`), `BranchServiceType` enum |

## 직원 권한 3단계

| 권한 | 할 수 있는 일 |
|---|---|
| `STAFF` 일반 직원 | 회원·계좌·거래 조회, 본인확인 심사, 1:1 문의 답변, 공지/FAQ 관리 |
| `MANAGER` 책임자 | STAFF 업무 + 회원 정지/해제, 계좌 동결/해제·잠금 해제, 이체한도 승인, 대출 심사, 카드 발급 승인, 상품 관리 |
| `SYSTEM_ADMIN` 시스템 관리자 | 직원 계정 관리, 지점/ATM 관리, 활동 로그·직원 로그인 이력 조회 (고객 업무는 하지 않음) |

## 파일 구성

```
src/main/resources/db/
  schema-team1.sql   테이블·시퀀스·인덱스 DDL (MEMBER → BRANCH → EMPLOYEE → 나머지 순서)
  data-team1.sql     지점/ATM과 이용 가능 업무, 직원 3명, 고객 3명, 본인확인 이력 시드
src/main/java/com/mockbank/
  common/entity/BaseTimeEntity.java   CREATED_AT/UPDATED_AT (팀원4 공통 모듈로 이관 예정)
  common/domain/ReviewStatus.java     심사 상태 (PENDING/APPROVED/REJECTED)
  common/domain/LoginLock.java        로그인 5회 실패 잠금 (MEMBER, EMPLOYEE 공용 @Embeddable)
  member/domain/, member/repository/      고객 관련 Entity, Repository
  employee/domain/, employee/repository/  직원 관련 Entity, Repository
  branch/domain/, branch/repository/      지점/ATM
```

패키지 이름 `com.mockbank`는 임시입니다. 팀에서 정한 이름으로 바꿔서 사용하세요.

## 테스트 계정 (data-team1.sql)

**고객** (`/login`)

| 아이디 | 비밀번호 | 상태 | 용도 |
|---|---|---|---|
| `user1` | `User1234!` | 본인확인 완료 | 계좌·이체 등 2주차부터 개발에 사용 |
| `user2` | `User1234!` | 본인확인 전 | 첫 계좌 개설(본인확인 신청) 흐름 |
| `user3` | `User1234!` | 본인확인 심사중 | 직원 본인확인 심사 화면 |

**직원** (`/admin/login`, 개발 편의상 비밀번호 변경 강제는 꺼 둠)

| 사번 | 비밀번호 | 권한 | 소속 |
|---|---|---|---|
| `E0001` | `Staff1234!` | SYSTEM_ADMIN | 본점 |
| `E0002` | `Staff1234!` | MANAGER | 강남지점 |
| `E0003` | `Staff1234!` | STAFF | 강남지점 |

- `user3`의 개설대기(PENDING) 계좌는 팀원2의 ACCOUNT 시드에서 함께 만들어야 합니다.
- 시연용 계정이므로 배포 환경에서는 비밀번호를 바꾸세요.

## 프로젝트에 적용하는 방법

1. **의존성**: `spring-boot-starter-data-jpa`, `spring-boot-starter-security`, Oracle JDBC 드라이버(`ojdbc11`), `lombok`
2. **JPA Auditing 활성화**: 설정 클래스 하나에 `@EnableJpaAuditing` 추가 (`BaseTimeEntity`가 동작하려면 필요)
3. **DDL 실행 순서**: `schema-team1.sql` → 다른 팀원 DDL(`ACCOUNT` 등) → `schema-team1.sql` 맨 아래 주석 처리된 `FK_TLR_ACCOUNT` 추가 → `data-team1.sql`
4. **application.yml**
   ```yaml
   spring:
     jpa:
       hibernate:
         ddl-auto: validate   # 테이블은 DDL 파일로 만들고, JPA는 구조가 맞는지만 확인
       open-in-view: false
   ```
5. **비밀번호 인코더**: `new BCryptPasswordEncoder()`를 쓰세요. 시드 해시에 `{bcrypt}` 접두사가 없어서 `DelegatingPasswordEncoder`로는 로그인되지 않습니다.
6. **Spring Security: 로그인 2개**
   - `SecurityFilterChain`을 2개 만듭니다.
     - 직원용: `@Order(1)`, `securityMatcher("/admin/**")`, 로그인 페이지 `/admin/login`, `EmployeeRepository`로 사용자 조회, 권한 `ROLE_` + `Employee.role`
     - 고객용: 나머지 경로, 로그인 페이지 `/login`, `MemberRepository`로 사용자 조회, 권한 `ROLE_USER`
   - `RoleHierarchy`에 `ROLE_MANAGER > ROLE_STAFF`를 등록하면 MANAGER가 STAFF 화면에도 들어갈 수 있습니다.
   - 직원의 `passwordChangeRequired`가 `true`이면 로그인 성공 핸들러에서 비밀번호 변경 화면으로 보냅니다.
   - 버튼 단위 권한(예: 계좌 조회는 STAFF, 동결은 MANAGER)은 Service 메서드에 `@PreAuthorize("hasRole('MANAGER')")`로 겁니다. Entity의 `approve()`에서도 한 번 더 검사합니다.

## 다른 팀원과 맞춰야 할 것

| 상대 | 내용 |
|---|---|
| 전원 | README 10-1장 공통 규칙. 고객은 `@ManyToOne(fetch = LAZY) Member`, 처리한 직원은 `@ManyToOne(fetch = LAZY) Employee`로 참조 |
| 팀원2 | 본인확인 승인 시 호출할 `AccountService.activatePending(memberId)`, 한도 승인 시 호출할 한도 변경 메서드, `TransferLimitRequest.accountId`를 `Account` 연관관계로 바꾸는 시점 |
| 팀원3 | `LOAN.REVIEWED_EMPLOYEE_ID → EMPLOYEE`, 대출 승인은 MANAGER 권한 검사 |
| 팀원4 | `ADMIN_LOG.EMPLOYEE_ID`, `NOTICE.EMPLOYEE_ID`, `INQUIRY.ANSWERED_EMPLOYEE_ID → EMPLOYEE`. 직원 관리 화면(SYSTEM_ADMIN)에서 팀원1의 직원 Service 호출. `BaseTimeEntity`와 공통 예외 클래스 이관 |

## 검증 상태

- JDK 21 + Hibernate 6.5 / Spring Data JPA 3.3 / Lombok 1.18.36으로 **컴파일 성공**
- 도메인 규칙 39개 **단위 검사 통과**: 권한 포함 관계(MANAGER ⊃ STAFF, SYSTEM_ADMIN은 심사 불가), 직원 소속은 지점만, 휴직·퇴사 직원 권한 없음, 임시 비밀번호, 로그인 5회 잠금, 지점/ATM 업무 규칙(ATM 창구 업무 불가, 최소 1개), 본인확인·한도 신청 심사 권한, 탈퇴 마스킹 등
- **Oracle DB에서는 아직 실행하지 않음** — 1주차에 로컬 Oracle에서 DDL 실행 후 `ddl-auto: validate`로 Entity와 테이블이 일치하는지 확인 필요
