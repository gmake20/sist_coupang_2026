# GoodPangSpringLegacy - 액션 로그 기록을 AOP로 통합

판매자센터(`/vendor/**`)와 관리자(`/admin/**`)에서 주요 작업을 할 때 남기는 **액션 로그**(`VENDOR_ACTION_LOG`, `ADMIN_ACTION_LOG`)를,
Controller 곳곳에서 직접 기록하던 방식에서 **Spring AOP(`@Aspect` + `@AfterReturning`) 한 곳에서 기록**하는 방식으로 바꾼 작업을 정리한 문서입니다.

- 대상 프로젝트: `project/GoodPangSpringLegacy`
- 참고한 수업 예제: `project/SL06_AOP`의 `org.doit.ik.aop4` (`@Aspect` 어노테이션 방식)
- 작성일: 2026-09-29

---

## 1. 왜 바꿨나

### 1.1 바꾸기 전

로그를 남기는 코드가 **Controller 13곳**에 흩어져 있었고, 모두 같은 모양이었습니다.

```java
// VendorController.productVisibility() - 변경 전
if (vendorProductMapper.updateDisplayYn(productNo, sellerNo, displayYn) == 1) {
    String actionType = "Y".equals(displayYn) ? "상품 노출" : "상품 숨김";
    try {
        vendorActionLogMapper.insertLog(sellerNo, actionType, "PRODUCT", productNo, null);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

문제점:

- **핵심 기능(처리)과 부가 기능(로그 기록)이 섞여 있음** - `SL06_AOP`의 `aop` 패키지(`CalculatorImpl`에 시간 측정 코드가 섞인 예제)와 같은 상황
- 같은 `try { insertLog } catch { }` 코드가 13번 반복
- 로그 규칙(실패하면 안 남긴다, 로그 실패가 처리를 되돌리면 안 된다)을 13곳에서 각자 지켜야 함
- 관리자 쪽은 `writeAdminLog()` 헬퍼, 판매자 쪽은 직접 호출 등 방식도 제각각

### 1.2 바꾼 후

```java
// VendorController.productVisibility() - 변경 후
// 바뀌었으면 판매자 액션 로그("상품 노출"/"상품 숨김")는 ActionLogAspect가 남긴다
vendorProductService.changeDisplayYn(productNo, sellerNo, displayYn);
```

- Controller는 **처리만** 요청한다 (로그 코드 없음)
- Service는 처리하고 **결과를 반환**한다 (`true/false`, 새 번호, `SUCCESS` 등)
- `ActionLogAspect`가 Service 메서드의 **반환값과 파라미터를 보고** 성공했을 때만 로그를 남긴다

---

## 2. 전체 구조

```
[브라우저] → Controller ──호출──▶ Service 메서드 (처리 + 결과 반환)
                                      │
                                      │ 메서드가 예외 없이 끝나면
                                      ▼
                         ActionLogAspect.@AfterReturning
                           - returning = 반환값 (성공 여부)
                           - args(...) = 파라미터 (누가/무엇을)
                           - 성공일 때만 insertLog(...)
                                      │
                         ┌────────────┴────────────┐
                         ▼                         ▼
               VendorActionLogMapper      AdminActionLogMapper
               (VENDOR_ACTION_LOG)        (ADMIN_ACTION_LOG)
```

### 2.1 AOP 용어 대응

| AOP 용어 | 이 작업에서 | SL06_AOP aop4 에서 |
|---|---|---|
| Target (대상 객체) | `org.doit.goodpang.service`의 처리 메서드 12개 | `CalculatorImpl4` |
| JoinPoint (적용 가능 지점) | 그 Service 메서드들의 실행 | `add()` 등 계산 메서드 실행 |
| Pointcut (적용할 지점을 고르는 규칙) | `execution(boolean ...AdminService.deleteNotice(..)) && args(noticeNo)` 등 | `execution(* org.doit.ik.aop..*.*(*,*))` |
| Advice (부가 기능) | `@AfterReturning` - 로그 기록 | `@Before`, `@AfterReturning`, `@Around` |
| Aspect (Pointcut + Advice) | `ActionLogAspect` | `LogPrintProfiler4` |

### 2.2 `@AfterReturning`을 고른 이유

| Advice | 실행 시점 | 이 작업에 맞는가 |
|---|---|---|
| `@Before` | 메서드 실행 전 | ✗ 아직 처리 전이라 성공 여부를 모름 |
| `@After` | 끝나면 항상 (예외가 나도) | ✗ 실패해도 실행됨 |
| **`@AfterReturning`** | **예외 없이 정상 반환된 뒤** | **○ 반환값(`returning`)으로 성공 여부 판단 가능** |
| `@AfterThrowing` | 예외가 났을 때 | ✗ |
| `@Around` | 전후 모두 | △ 가능하지만 `proceed()` 호출 등 불필요하게 복잡 |

---

## 3. 변경된 파일

### 3.1 새로 만든 파일

| 파일 | 역할 |
|---|---|
| [`aop/ActionLogAspect.java`](../project/GoodPangSpringLegacy/src/main/java/org/doit/goodpang/aop/ActionLogAspect.java) | 판매자/관리자 액션 로그를 남기는 Aspect. `@Pointcut` + `@AfterReturning` 12쌍 |
| [`service/AdminService.java`](../project/GoodPangSpringLegacy/src/main/java/org/doit/goodpang/service/AdminService.java) | 기존에 AdminController가 Mapper를 직접 호출하던 관리자 처리(공지 등록/수정/삭제, 상품 승인/반려, 판매자 상태 변경)를 옮김. 판매자 상태는 enum `SellerStatusAction`으로 받음 |

### 3.2 수정한 파일

| 파일 | 변경 내용 |
|---|---|
| [`service/VendorProductService.java`](../project/GoodPangSpringLegacy/src/main/java/org/doit/goodpang/service/VendorProductService.java) | `changeDisplayYn`, `changeSaleStatus`, `updateOption` 추가 (성공 여부 `boolean` 반환). 기존에는 Controller가 Mapper를 직접 호출 |
| [`controller/VendorController.java`](../project/GoodPangSpringLegacy/src/main/java/org/doit/goodpang/controller/VendorController.java) | 로그 코드 6곳 삭제, Mapper 직접 호출을 Service 호출로 교체, `VendorActionLogMapper` 주입 제거 |
| [`controller/AdminController.java`](../project/GoodPangSpringLegacy/src/main/java/org/doit/goodpang/controller/AdminController.java) | 로그 코드 7곳과 `writeAdminLog()` 헬퍼 삭제, `AdminService` 사용, 로그 때문에만 받던 `HttpSession` 파라미터 정리 |
| [`webapp/WEB-INF/spring/root-context.xml`](../project/GoodPangSpringLegacy/src/main/webapp/WEB-INF/spring/root-context.xml) | `xmlns:aop` + `<aop:aspectj-autoproxy />` 추가 |

- `pom.xml`은 수정하지 않았습니다. `aspectjrt`, `aspectjweaver`(1.9.7)가 이미 들어 있습니다.
- DB 테이블, JSP는 수정하지 않았습니다. 로그 내용(작업 종류 문구, 대상 종류 등)은 기존과 같습니다.

---

## 4. 로그 대응표

### 4.1 판매자 액션 로그 (`VENDOR_ACTION_LOG`)

| 화면/기능 | Service 메서드 (Pointcut 대상) | 로그를 남기는 조건 | ACTION_TYPE | TARGET_TYPE / TARGET_NO | DETAIL |
|---|---|---|---|---|---|
| 상품 등록 | `VendorProductService.registerProduct(dto)` → `int` | 항상 (예외 없이 반환되면 등록 완료) | 상품 등록 | PRODUCT / 반환된 새 상품번호 | 상품명 |
| 상품 숨김/노출 | `VendorProductService.changeDisplayYn(productNo, sellerNo, displayYn)` → `boolean` | `true` | 상품 노출 (Y) / 상품 숨김 (N) | PRODUCT / productNo | - |
| 판매중지/재개 | `VendorProductService.changeSaleStatus(productNo, sellerNo, saleStatus)` → `boolean` | `true` | 판매 재개 / 판매 중지 | PRODUCT / productNo | - |
| 옵션 수정 | `VendorProductService.updateOption(optionId, sellerNo, price, normalPrice, quantity, status)` → `boolean` | `true` | 옵션 수정 | PRODUCT_OPTION / optionId | 판매가·재고·상태 요약 |
| 출고 처리 | `VendorOrderService.shipOrder(orderNo, sellerNo, invoiceNo)` → `ShipResult` | `SUCCESS` (송장 중복/실패는 안 남김) | 배송 처리 | ORDERS / orderNo | 송장번호 |
| 판매자 탈퇴 | `VendorAccountService.withdraw(sellerNo)` → `void` | 항상 (예외 없이 끝나면 처리 완료) | 판매자 탈퇴 | SELLER / sellerNo | - |
| 배송완료 (관리자가 처리) | `AdminDeliveryService.completeDelivery(deliveryNo)` → `CompleteResult` | `null`이 아니면, **주문에 상품이 있는 판매자마다 1건씩** | 배송 완료 | ORDERS / 주문번호 | - |

### 4.2 관리자 액션 로그 (`ADMIN_ACTION_LOG`)

관리자 번호(`ADMIN_NO`)는 파라미터가 아니라 **세션의 `adminNo`** 에서 가져옵니다 (`AdminLoginSuccessHandler`가 로그인 시 넣어둔 값).

| 화면/기능 | Service 메서드 (Pointcut 대상) | 로그를 남기는 조건 | ACTION_TYPE | TARGET_TYPE / TARGET_NO | REASON |
|---|---|---|---|---|---|
| 공지 등록 | `AdminService.registerNotice(notice)` → `int` | 반환값 > 0 (새 공지번호) | 공지 등록 | NOTICE / 새 공지번호 | - |
| 공지 수정 | `AdminService.updateNotice(notice)` → `boolean` | `true` | 공지 수정 | NOTICE / notice.noticeNo | - |
| 공지 삭제 | `AdminService.deleteNotice(noticeNo)` → `boolean` | `true` | 공지 삭제 | NOTICE / noticeNo | - |
| 상품 승인/반려 | `AdminService.decideProductApproval(productNo, approve)` → `boolean` | `true` ('승인 대기' 상품이 실제로 바뀐 경우) | 상품 승인 / 상품 반려 | PRODUCT / productNo | - |
| 판매자 승인/반려/정지/정지해제 | `AdminService.changeSellerStatus(sellerNo, action, reason)` → `boolean` | `true` | `action`의 `logActionType` (판매자 승인/반려/정지/정지해제) | SELLER / sellerNo | 반려·정지일 때만 사유 |
| 배송완료 처리 | `AdminDeliveryService.completeDelivery(deliveryNo)` → `CompleteResult` | `null`이 아니면 | 배송완료 처리 | DELIVERY / deliveryNo | - |

### 4.3 `SellerStatusAction` (AdminService 안의 enum)

같은 '승인'이라도 입점 승인과 정지 해제를 로그에서 구분하기 위해 enum으로 받습니다.

| 값 | 저장되는 APPROVAL_STATUS | 로그 ACTION_TYPE | 사유 저장 |
|---|---|---|---|
| `APPROVE` | 승인 | 판매자 승인 | ✗ (REJECT_REASON을 null로 지움) |
| `REJECT` | 반려 | 판매자 반려 | ○ |
| `SUSPEND` | 정지 | 판매자 정지 | ○ (REJECT_REASON 컬럼을 정지 사유에도 재사용 - 기존과 동일) |
| `REACTIVATE` | 승인 | 판매자 정지해제 | ✗ |

---

## 5. 코드 설명

### 5.1 Pointcut + Advice 한 쌍 (예: 공지 삭제)

```java
@Pointcut(value = "execution(boolean org.doit.goodpang.service.AdminService.deleteNotice(..)) && args(noticeNo)",
          argNames = "noticeNo")
private void deleteNotice(int noticeNo) {}

@AfterReturning(pointcut = "deleteNotice(noticeNo)", returning = "changed", argNames = "noticeNo,changed")
public void afterDeleteNotice(int noticeNo, boolean changed) {
    if (changed) {
        adminLog("공지 삭제", "NOTICE", noticeNo, null);
    }
}
```

| 부분 | 의미 |
|---|---|
| `execution(boolean ...AdminService.deleteNotice(..))` | `boolean`을 반환하는 `AdminService.deleteNotice` 메서드 실행에 적용 |
| `&& args(noticeNo)` | 그 메서드의 파라미터를 Advice의 `noticeNo`로 받음 (aop4 예제는 파라미터를 안 받았지만, 여기서는 "무엇을" 기록해야 해서 받음) |
| `returning = "changed"` | 메서드의 반환값을 Advice의 `changed`로 받음 |
| `argNames = "..."` | 파라미터 이름을 직접 지정 (5.4 참고) |

### 5.2 트랜잭션과의 순서 - `@Order(1)`

`shipOrder`, `completeDelivery`, `registerProduct`, `withdraw`는 `@Transactional` 메서드입니다.
`@Transactional`도 AOP(프록시)로 동작하므로, **두 Aspect 중 어느 쪽이 바깥에서 감싸는지**가 중요합니다.

```
@Order(1) ActionLogAspect   ← 바깥 (숫자가 작을수록 바깥)
  └ @Transactional (기본 순서 = 가장 안쪽)
       └ 실제 Service 메서드
```

- `ActionLogAspect`가 바깥이므로 `@AfterReturning`은 **트랜잭션 commit이 끝난 뒤** 실행됩니다.
- commit이 실패하면(예외) `@AfterReturning`이 실행되지 않으므로 **로그도 남지 않습니다.**
- 로그 기록이 실패해도 이미 commit된 처리는 **되돌려지지 않습니다.**
  → 기존 Controller의 "트랜잭션 밖에서 따로 로그를 남긴다"는 동작과 같습니다.

### 5.3 로그 기록 실패 처리

```java
private void vendorLog(int sellerNo, String actionType, String targetType, int targetNo, String detail) {
    try {
        vendorActionLogMapper.insertLog(sellerNo, actionType, targetType, targetNo, detail);
    } catch (Exception e) {
        log.error("판매자 액션 로그 기록 실패: ...", e);
    }
}
```

- 로그 기록이 실패해도 사용자의 처리(상품 등록, 출고 등) 결과 화면에는 영향을 주지 않습니다 (예외를 삼킴 - 기존과 동일).
- 기존에는 `e.printStackTrace()`였고, 지금은 `log.error(...)`로 어떤 로그가 실패했는지 남깁니다.
- 관리자 로그는 세션에 `adminNo`가 없으면 기록을 건너뛰고 `log.warn`을 남깁니다.

### 5.4 `argNames`를 모두 적은 이유 (중요)

처음에는 `argNames` 없이 만들었는데, 실행 테스트에서 **Spring 시작 자체가 실패**했습니다.

```
IllegalStateException: Required parameter names not available when parsing pointcut decideProductApproval
in type org.doit.goodpang.aop.ActionLogAspect
```

- 파라미터를 받는 포인트컷(`args(noticeNo)` 등)은 AspectJ가 **Advice/Pointcut 메서드의 파라미터 이름**을 알아야 연결할 수 있습니다.
- 이름을 따로 적지 않으면 **class 파일의 디버그 정보(지역변수 이름)** 에서 읽는데, 빌드 설정(`javac -g` 옵션 등)에 따라 이 정보가 없을 수 있습니다.
- 그래서 모든 `@Pointcut`, `@AfterReturning`에 `argNames`를 직접 적어 **빌드 설정과 관계없이 동작**하게 했습니다.
- 새 Advice를 추가할 때도 **반드시 `argNames`를 적어야 합니다.** (`returning` 이름도 `argNames`에 포함)

### 5.5 `root-context.xml` 설정

```xml
<tx:annotation-driven transaction-manager="transactionManager" />

<!-- @Aspect 클래스(ActionLogAspect)를 동작시키는 프록시 생성기 -->
<aop:aspectj-autoproxy />
```

- `@Service` 빈은 루트 컨텍스트(`root-context.xml`)에서 만들어지므로, 프록시 생성 설정도 루트 컨텍스트에 있어야 합니다.
  (`servlet-context.xml`에 두면 Service에 적용되지 않음 - `tx:annotation-driven`을 루트로 옮긴 것과 같은 이유)
- `ActionLogAspect`는 `@Component`라서 루트 컨텍스트의 `component-scan`(`org.doit.goodpang`, `@Controller`만 제외)으로 자동 등록됩니다.

### 5.6 실제 실행 흐름 (예: 상품 등록)

판매자가 상품을 등록할 때, Controller의 Service 호출 한 번이 두 프록시(`ActionLogAspect`, `@Transactional`)를 거쳐 처리되는 순서입니다.

```
VendorController.java:403
  vendorProductService.registerProduct(dto)
        │  (실제로는 스프링이 만든 프록시를 호출)
        ▼
  ┌─ ActionLogAspect 프록시 (@Order(1), 바깥쪽) ────────────────┐
  │  ┌─ @Transactional 프록시 (안쪽) ────────────────────────┐  │
  │  │  트랜잭션 시작                                         │  │
  │  │  → 실제 registerProduct(): 상품/옵션/이미지 INSERT     │  │
  │  │  → return productNo                                    │  │
  │  │  commit                                                │  │
  │  └────────────────────────────────────────────────────────┘  │
  │  정상 return 확인 → afterRegisterProduct(dto, productNo)    │
  │                     → VENDOR_ACTION_LOG INSERT               │
  └──────────────────────────────────────────────────────────────┘
        ▼
  Controller로 productNo 반환
```

- `@Order(1)`로 `ActionLogAspect`가 바깥에 있으므로, 로그는 **상품 등록 commit이 끝난 뒤** 남습니다 (5.2 참고).
- 상품/옵션/이미지 INSERT 중 하나라도 실패하면 예외가 나서 전부 rollback되고, `@AfterReturning`이 실행되지 않아 **로그도 남지 않습니다.**
- 로그 INSERT가 실패해도 `vendorLog`가 예외를 삼키므로(5.3 참고) **이미 등록된 상품은 그대로 유지**되고, Controller는 정상적으로 `productNo`를 받습니다.
- `registerProduct`는 실패하면 0을 반환하지 않고 예외를 던지므로, 다른 Advice와 달리 반환값 검사(`if (changed)` 등) 없이 로그를 남깁니다.

---

## 6. 검증 결과

### 6.1 컴파일

- 프로젝트 전체 `javac` 컴파일 통과

### 6.2 실행 테스트 (가짜 Mapper, DB 없이)

Mapper를 가짜 객체로 바꾸고 Service + `ActionLogAspect`만으로 Spring을 띄워, 12개 처리를 호출해 봤습니다.

| 확인 항목 | 결과 |
|---|---|
| 12개 처리를 모두 성공시킴 | **로그 15건** 기록 - 배송완료는 관리자 1건 + 판매자 2건(판매자 2명) |
| 파라미터/반환값 연결 | 새 상품번호(777), 새 공지번호(55), 송장번호, 정지 사유 등이 정상적으로 로그에 들어감 |
| 정지 해제(`REACTIVATE`)에 사유를 넘김 | REASON이 **null**로 기록됨 (반려·정지만 사유 저장) |
| Mapper가 0건 반환(실패) | **로그 0건** - 실패하면 안 남김 |

### 6.3 아직 확인하지 못한 것 (서버에서 확인 필요)

- **실제 DB에 로그가 쌓이는지** - 서버 배포 후 아래 화면에서 확인
  - 관리자 → 액션 로그 (`/admin/action_logs.htm`)
  - 관리자 → 판매자 액션 로그 (`/admin/vendor_action_logs.htm`)
- **트랜잭션 순서(`@Order(1)`)** - 가짜 Mapper 테스트에는 트랜잭션이 없어서 설정 근거로만 판단함.
  판매자센터에서 출고 처리 후 판매자 액션 로그에 "배송 처리"가 남는지 확인 필요

---

## 7. 앞으로 새 로그를 추가하는 방법

1. **Service 메서드를 만들고 "처리 결과"를 반환한다**
   - 성공/실패: `boolean`
   - 새로 만든 번호: `int` (실패면 0)
   - 여러 결과: 결과 enum 또는 결과 객체 (`ShipResult`, `CompleteResult`처럼)
2. **Controller는 그 Service 메서드만 호출한다** (로그 코드 넣지 않기)
3. **`ActionLogAspect`에 `@Pointcut` + `@AfterReturning` 한 쌍을 추가한다**

```java
@Pointcut(value = "execution(boolean org.doit.goodpang.service.XxxService.doSomething(..)) && args(targetNo, sellerNo)",
          argNames = "targetNo,sellerNo")
private void doSomething(int targetNo, int sellerNo) {}

@AfterReturning(pointcut = "doSomething(targetNo, sellerNo)", returning = "changed",
                argNames = "targetNo,sellerNo,changed")
public void afterDoSomething(int targetNo, int sellerNo, boolean changed) {
    if (changed) {
        vendorLog(sellerNo, "작업 이름", "대상 종류", targetNo, null);
    }
}
```

---

## 8. 주의사항

| 주의 | 내용 |
|---|---|
| **Service 메서드 이름/파라미터/반환 타입을 바꾸면 로그가 조용히 빠진다** | 포인트컷은 문자열이라 **컴파일 오류가 나지 않습니다.** Service를 고칠 때는 `ActionLogAspect`의 해당 `@Pointcut`도 같이 확인하세요. (각 Service 클래스 주석에도 적어둠) |
| **같은 클래스 안에서 호출하면 로그가 안 남는다** | AOP는 프록시로 동작하므로, Service 안에서 `this.changeDisplayYn(...)`처럼 자기 메서드를 부르면 프록시를 거치지 않아 Advice가 실행되지 않습니다. (`@Transactional`과 같은 제약 - SL14 예제의 "같은 클래스내의 멤버를 호출하면 트랜잭션이 걸리지 않는다" 주석과 같은 이유) |
| **Controller에서 Mapper를 직접 호출하면 로그가 안 남는다** | Pointcut 대상은 Service 메서드뿐입니다. 로그가 필요한 처리는 반드시 Service를 거치게 하세요. |
| **`argNames`는 필수** | 5.4 참고. 빠뜨리면 빌드 설정에 따라 서버가 뜨지 않습니다. |
| **`<aop:aspectj-autoproxy />`는 `root-context.xml`에** | 5.5 참고. |

---

## 9. 관련 작업 이력 (GoodPang → GoodPangSpringLegacy 이전 중 한 작업)

이 문서의 작업은 기존 JSP/Servlet 프로젝트(`GoodPang`)를 Spring Legacy 프로젝트(`GoodPangSpringLegacy`)로 옮기는 작업 중에 진행했습니다.

- 판매자 서블릿(`Vendor*Servlet`) 23개 → `VendorController`로 이전 완료
- 관리자 서블릿(`Admin*Servlet`, `Seller*Servlet`) 16개 → `AdminController`로 이전 완료
- 관리자 인증 → Spring Security 관리자 전용 `<http pattern="/admin/**">` + `jdbc-user-service` (SL14_SECURITY2_JDBC 방식)
- 트랜잭션이 필요한 처리 → Service + `@Transactional`
  (`VendorOrderService.shipOrder`, `VendorProductService.registerProduct`, `VendorAccountService.withdraw`, `AdminDeliveryService.completeDelivery`)
- **액션 로그 → `ActionLogAspect` (이 문서)**
