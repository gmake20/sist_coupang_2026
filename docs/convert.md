# GoodPang → GoodPangSpringLegacy 전환
## 판매자센터 · 관리자 파트

JSP/Servlet 프로젝트를 Spring Legacy(Spring MVC 5.0 + MyBatis + Tiles + Spring Security)로 옮긴 작업 발표

- 담당: 판매자센터(`/vendor/**`), 관리자(`/admin/**`)
- 브랜치: `feature/scm`

> 💬 발표 포인트: "무엇을 옮겼는지" → "어떻게 옮겼는지(패턴)" → "옮기면서 부딪힌 문제" → "Spring이라서 더 좋아진 점" 순서로 설명

---

## 1. 전환 범위

| 구분 | 기존 GoodPang | GoodPangSpringLegacy |
|---|---|---|
| 판매자 기능 | 서블릿 **23개** | `VendorController` 1개 (URL 매핑 27개) |
| 관리자 기능 | 서블릿 **16개** | `AdminController` 1개 (URL 매핑 17개) |
| DB 접근 | DAO 클래스 (JDBC, 약 3,600줄) | MyBatis Mapper 인터페이스 + XML (SQL 65개) |
| 화면 | JSP 29개 (각자 헤더/사이드바 include) | Tiles 레이아웃 + JSP (판매자 18개, 관리자 11개) |
| 트랜잭션 | `setAutoCommit(false)` / `commit` / `rollback` 직접 | Service + `@Transactional` |
| 로그인/권한 | Filter(`VendorAuthFilter`, `AdminAuthFilter`) | 판매자: 세션 확인 / 관리자: **Spring Security** |
| 액션 로그 | 서블릿마다 직접 `log(...)` 호출 | **AOP** `ActionLogAspect` 한 곳 |

**39개 서블릿 전부 이전 완료**

---

## 2. 전체 구조 비교

### Before (GoodPang)
```
브라우저 → @WebServlet("/vendor/order") → VendorOrderListDAO (JDBC) → Oracle
                     │
                     └→ request.getRequestDispatcher("vendor-order.jsp").forward()
                              └ <%@ include file="topbar.jspf" %> (페이지마다)
```

### After (GoodPangSpringLegacy)
```
브라우저 → DispatcherServlet → Spring Security 필터
              → VendorController (@GetMapping("/order.htm"))
                    → Service (@Transactional) → Mapper (MyBatis XML) → Oracle
              → ModelAndView("vendor.order")
                    → Tiles: layout.jsp = topbar + sidebar + content(order.jsp)
```

| 계층 | 패키지 | 만든 것 |
|---|---|---|
| Controller | `controller` | `VendorController`, `AdminController`, `CategoryController` |
| Service | `service` | `VendorOrderService`, `VendorProductService`, `VendorAccountService`, `AdminService`, `AdminDeliveryService` |
| Mapper | `mapper` | `Vendor*Mapper` 6개, `Admin*Mapper` 4개, `NoticeMapper`, `CategoryMapper` |
| AOP | `aop` | `ActionLogAspect` |
| Security | `security` | `AdminLoginSuccessHandler` |

---

## 3. 전환 패턴 ① Servlet → Controller

```java
// Before - 서블릿 하나 = URL 하나
@WebServlet("/vendor/delivery")
public class VendorDeliveryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) ... {
        HttpSession session = request.getSession(false);
        SellerDTO loginSeller = (SellerDTO) session.getAttribute("loginSeller");
        if (loginSeller == null) { response.sendRedirect(request.getContextPath() + "/vendor/login"); return; }

        List<VendorDeliveryDTO> list = new VendorDeliveryDAO().findShippingBySellerNo(loginSeller.getSellerNo());
        request.setAttribute("deliveryList", list);
        request.getRequestDispatcher("/WEB-INF/views/vendor-delivery.jsp").forward(request, response);
    }
}
```

```java
// After - 메서드 하나 = URL 하나, 의존성은 주입받음
@GetMapping("/delivery.htm")
public ModelAndView delivery(HttpSession session) {
    ...
    List<VendorDeliveryDTO> deliveryList = vendorOrderMapper.findShippingBySellerNo(loginSeller.getSellerNo());

    ModelAndView mav = new ModelAndView("vendor.delivery");   // Tiles 정의 이름
    mav.addObject("deliveryList", deliveryList);
    return mav;
}
```

| Servlet | Spring |
|---|---|
| `request.getParameter("page")` | `@RequestParam(value = "page", required = false) String page` |
| `request.setAttribute(...)` | `mav.addObject(...)` |
| `response.sendRedirect(...)` + `return;` | `return new ModelAndView("redirect:/vendor/login.htm");` |
| `new XxxDAO()` | `@RequiredArgsConstructor` + `private final XxxMapper` (생성자 주입) |
| JSON 응답: `PrintWriter` + Gson | `@ResponseBody` + `ResponseEntity` (Jackson 자동 변환) |

---

## 4. 전환 패턴 ② DAO(JDBC) → MyBatis Mapper

```java
// Before - SQL + 연결 + 파라미터 + 결과 매핑을 전부 직접
String sql = "SELECT ... WHERE P.SELLER_NO = ? AND P.DISPLAY_YN = ?";
try (Connection conn = ConnectionProvider.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setInt(1, sellerNo);
    pstmt.setString(2, displayYn);
    try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) list.add(mapRow(rs));   // rs.getInt("PRODUCT_NO") ... 20줄
    }
}
```

```xml
<!-- After - SQL만 XML에, 매핑은 자동 (mapUnderscoreToCamelCase: PRODUCT_NO → productNo) -->
<select id="findBySellerNo" resultType="org.doit.goodpang.domain.VendorProductListDTO">
    SELECT ... WHERE P.SELLER_NO = #{sellerNo} AND P.DISPLAY_YN = #{displayYn}
</select>
```

**DAO에서 Java로 하던 일을 SQL로 옮긴 예**

| 기존 Java 코드 | MyBatis 전환 |
|---|---|
| `mapRow()`로 날짜 라벨 `"5/13"` 만들기 | `TO_CHAR(D.STAT_DATE, 'FMMM/DD') AS LABEL` |
| `buildOptionLabel()` - 옵션값을 " / "로 잇기 | `SUBSTR(CASE WHEN ... THEN ' / ' \|\| PO.OPTION1_VALUE END \|\| ..., 4)` |
| `StringBuilder`로 검색 조건 이어붙이기 | `<where>` + `<if test="...">` 동적 SQL |
| `nextVal()`로 시퀀스 먼저 조회 | `<selectKey order="BEFORE">` |
| 조회 3번(주문/상품/배송이력) 후 Java에서 합치기 | `LEFT JOIN` + `ROW_NUMBER()`로 2번 |
| `rs.getInt()` + `rs.wasNull()` | DTO 필드를 `Integer`로 두면 자동으로 null |
| 컬럼명 ≠ 필드명 (`MEMBER_NAME` → `buyerName`) | `M.MEMBER_NAME AS BUYER_NAME` 별칭 |

---

## 5. 전환 패턴 ③ JSP include → Tiles, 스크립틀릿 → EL

```xml
<!-- tiles.xml - 판매자 화면은 전부 같은 레이아웃 -->
<definition name="vendor.*" template="/WEB-INF/views/vendor/inc/layout.jsp">
    <put-attribute name="css"     value="/vendor/inc/css/{1}.css" type="string"/>
    <put-attribute name="topbar"  value="/WEB-INF/views/vendor/inc/topbar.jsp"/>
    <put-attribute name="sidebar" value="/WEB-INF/views/vendor/inc/sidebar.jsp"/>
    <put-attribute name="content" value="/WEB-INF/views/vendor/{1}.jsp"/>
</definition>
<!-- 관리자 화면은 원래 각자 단독 화면이라 JSP를 그대로 template으로 -->
<definition name="admin.*" template="/WEB-INF/views/admin/{1}.jsp"/>
```

```jsp
<%-- Before --%>  <%= loginSeller.getZipcode() != null ? loginSeller.getZipcode() : "" %>
<%-- After  --%>  <c:out value="${sessionScope.loginSeller.zipcode}" />
```

- Controller는 `"vendor.order"`만 반환 → 레이아웃·상단바·사이드바는 Tiles가 조립
- 페이지별 CSS는 `/vendor/inc/css/{뷰이름}.css`가 **있을 때만** `<link>` 출력

---

## 6. 트러블슈팅 ① Tiles에서 변수가 안 넘어감

**증상**: `sellerGrade cannot be resolved to a variable` 컴파일 오류

```jsp
<%-- layout.jsp --%>
<% String sellerGrade = null; %>
<tiles:insertAttribute name="topbar"/>   <%-- topbar.jsp에서 sellerGrade 사용 → 오류 --%>
```

**원인**
- 기존 `<%@ include %>`는 **정적 include** → 두 파일이 서블릿 하나로 합쳐져서 지역변수 공유 가능
- `<tiles:insertAttribute>`는 **동적 include** → topbar.jsp가 별도 서블릿으로 컴파일됨 → 지역변수가 안 보임

**해결**: 스크립틀릿 변수 → **request 속성 + EL**
```java
mav.addObject("menu", "orders");          // Controller
```
```jsp
<a class="${menu eq 'orders' ? 'active' : ''}" ...>   <%-- sidebar.jsp --%>
```

---

## 7. 트러블슈팅 ② 403 / 406 - 응답을 거부당함

### 403 Forbidden (로그인·등록 버튼을 누르면)
- **원인**: Spring Security의 **CSRF 보호** - POST에 `_csrf` 토큰이 없으면 필터가 차단 (`permitAll`이어도 차단)
- **해결**: 모든 POST 폼에 토큰 추가

| 폼 종류 | 토큰 넣는 방법 |
|---|---|
| 일반 form | `<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">` |
| 파일첨부 form (multipart) | CsrfFilter가 본문을 못 읽음 → `action="...?${_csrf.parameterName}=${_csrf.token}"` |
| `fetch()` + FormData | 헤더로: `headers: { "${_csrf.headerName}": "${_csrf.token}" }` |

### 406 Not Acceptable (상품 등록 시)
- **원인**: Spring 5.0은 **URL 확장자로 응답 형식을 결정** → `.htm` = "HTML 요청"인데 메서드는 JSON만 응답 (`produces = "application/json"`)
- **해결**: JSON 응답 URL만 `/vendor/product_write.htm` → **`/vendor/product_write.json`**

---

## 8. 트러블슈팅 ③ Oracle + MyBatis 데이터 타입

| 증상 | 원인 | 해결 |
|---|---|---|
| 옵션의 정상가를 비우면 `부적합한 열 유형: 1111` | MyBatis는 null을 타입 없이(`OTHER`) 넘기는데 Oracle JDBC가 거부 | `#{normalPrice, jdbcType=NUMERIC}` |
| (옮기면서 미리 막은 문제) 대시보드 날짜 집계가 0으로 나올 수 있음 | JDBC의 `java.sql.Date`(자정)와 달리 `java.util.Date`는 **시간까지** TIMESTAMP로 넘어가서 `TRUNC(날짜) = 파라미터` 비교가 어긋남 | `TRUNC(#{targetDate})` |
| 정산 기간 `LocalDate` 매핑 | - | MyBatis 3.4.6 기본 `LocalDateTypeHandler`가 DATE ↔ `LocalDate` 자동 변환 |
| 로그인 실패 메시지가 안 보임 | Controller는 `redirect:...?error=`(URL 파라미터)로 보내는데 JSP는 `request.getAttribute("error")`를 읽음 | JSP에서 `${param.error}` |

---

## 9. 트랜잭션: 직접 제어 → `@Transactional`

```java
// Before - VendorOrderListDAO.shipOrder()
conn.setAutoCommit(false);
try {
    insertDelivery(conn, ...);
    if (!updateStatusToShipping(conn, ...)) { conn.rollback(); return FAILED; }
    conn.commit();
} catch (Exception e) { conn.rollback(); throw e; }
```

```java
// After - VendorOrderService
@Transactional
public ShipResult shipOrder(int orderNo, int sellerNo, String invoiceNo) {
    ...
    if (vendorOrderMapper.updateOrderStatusToShipping(orderNo, sellerNo) != 1) return ShipResult.FAILED;
    vendorOrderMapper.insertDelivery(orderNo, deliveryServiceCode, invoiceNo);
    return ShipResult.SUCCESS;          // 예외가 나면 Spring이 전부 rollback
}
```

| 트랜잭션으로 묶은 처리 | 함께 바뀌는 테이블 |
|---|---|
| 출고 처리 `shipOrder` | ORDERS + DELIVERY |
| 상품 등록 `registerProduct` | PRODUCT + PRODUCT_OPTION + PRODUCT_IMAGE |
| 배송완료 `completeDelivery` | DELIVERY + ORDERS |
| 판매자 탈퇴 `withdraw` | SELLER + PRODUCT (**기존엔 따로 실행 → 개선**) |

⚠ `<tx:annotation-driven>`은 Service가 만들어지는 **root-context.xml**에 있어야 동작 (servlet-context.xml에 두면 조용히 무시됨)

---

## 10. 관리자 인증: Spring Security (SL14_SECURITY2_JDBC 방식)

```xml
<!-- 관리자 전용 보안 설정 - 회원용 <http>보다 먼저 선언 -->
<http pattern="/admin/**" authentication-manager-ref="adminAuthenticationManager"
      security-context-repository-ref="adminSecurityContextRepository">
    <intercept-url pattern="/admin/login.htm" access="permitAll" />
    <intercept-url pattern="/admin/**" access="hasRole('ADMIN')" />
    <form-login login-page="/admin/login.htm" login-processing-url="/admin/loginProcess"
                username-parameter="adminId" authentication-success-handler-ref="adminLoginSuccessHandler" .../>
    <logout logout-url="/admin/logout.htm" logout-success-url="/admin/login.htm?logout" />
</http>

<authentication-manager id="adminAuthenticationManager">
    <authentication-provider>
        <jdbc-user-service data-source-ref="dataSource"
            users-by-username-query="SELECT ADMIN_ID AS USERNAME, ADMIN_PW AS PASSWORD, 1 AS ENABLED FROM ADMIN WHERE ADMIN_ID = ?"
            authorities-by-username-query="SELECT ADMIN_ID AS USERNAME, 'ROLE_ADMIN' AS AUTHORITY FROM ADMIN WHERE ADMIN_ID = ?" />
        <password-encoder ref="passwordEncoder" />
    </authentication-provider>
</authentication-manager>
```

- **Java 코드 없이 SQL 두 개로 인증** - ADMIN 테이블에 권한 테이블이 없어서 `'ROLE_ADMIN'`을 SQL에서 고정
- `AdminController`에는 로그인 확인 코드가 **한 줄도 없음** (기존 `AdminAuthFilter` 역할을 Security가 대신)

---

## 11. 트러블슈팅 ④ Security를 두 개 쓰면서 생긴 문제

| 증상 | 원인 | 해결 |
|---|---|---|
| 관리자로 로그인 후 쇼핑몰 메인이 깨짐 (`Invalid property 'principal.member'`) | 회원·관리자가 세션의 로그인 정보 **한 칸**을 같이 씀 → 관리자 로그인이 회원 로그인을 덮어씀 → 쇼핑몰 헤더가 관리자 principal에서 `member`를 찾다 실패 | 관리자 로그인 정보를 **별도 키**(`ADMIN_SPRING_SECURITY_CONTEXT`)에 저장 |
| 회원 로그인이 관리자 인증으로 처리될 위험 | `<authentication-manager>`가 2개면 기본 인증관리자 별칭을 한쪽이 가져감 | 둘 다 `id`를 주고 `<http>`마다 `authentication-manager-ref`로 명시 |
| 관리자 첫 로그인만 404 (`/admin/login`) | Security가 "로그인 전에 가려던 주소"를 기억 → 옛 주소(`.htm` 없음)로 돌려보냄 | 성공 핸들러에서 `/admin/**.htm` 화면일 때만 복귀, 아니면 대시보드 |

---

## 12. AOP: 흩어진 액션 로그를 한 곳으로 (SL06_AOP aop4 방식)

**Before** - Controller 13곳에 같은 코드
```java
if (vendorProductMapper.updateDisplayYn(productNo, sellerNo, displayYn) == 1) {
    try { vendorActionLogMapper.insertLog(sellerNo, "상품 숨김", "PRODUCT", productNo, null); }
    catch (Exception e) { e.printStackTrace(); }
}
```

**After** - Controller는 처리만, 로그는 Aspect가
```java
vendorProductService.changeDisplayYn(productNo, sellerNo, displayYn);   // Controller
```
```java
@Aspect @Component @Order(1)
public class ActionLogAspect {
    @Pointcut(value = "execution(boolean ...VendorProductService.changeDisplayYn(..)) && args(productNo, sellerNo, displayYn)",
              argNames = "productNo,sellerNo,displayYn")
    private void changeDisplayYn(int productNo, int sellerNo, String displayYn) {}

    @AfterReturning(pointcut = "changeDisplayYn(productNo, sellerNo, displayYn)", returning = "changed",
                    argNames = "productNo,sellerNo,displayYn,changed")
    public void afterChangeDisplayYn(int productNo, int sellerNo, String displayYn, boolean changed) {
        if (changed) vendorLog(sellerNo, "Y".equals(displayYn) ? "상품 노출" : "상품 숨김", "PRODUCT", productNo, null);
    }
}
```

- `@AfterReturning`: 예외 없이 끝났을 때만 + **반환값으로 성공 여부 판단** → 실패하면 로그 없음
- `@Order(1)`: `@Transactional`보다 바깥 → **commit 뒤에** 로그 (로그 실패가 처리를 되돌리지 않음)
- 판매자 로그 7종 + 관리자 로그 6종, 가짜 Mapper로 실행 검증 (성공 15건 / 실패 0건)
- 삽질: `argNames` 없이 만들었더니 "Required parameter names not available"로 **서버가 안 뜸** → 전부 명시

---

## 13. 배포(Ubuntu + Tomcat 9)에서 만난 문제

| 증상 | 원인 | 해결 |
|---|---|---|
| 서버 시작 시 `ClassNotFoundException: ContextLoaderListener` | 이클립스 배포 설정에서 Maven 라이브러리가 `WEB-INF/lib`에 안 들어감 | Deployment Assembly에 Maven Dependencies 추가 |
| 업로드 성공 메시지는 나오는데 썸네일이 깨짐 | 저장 폴더(`UPLOAD_BASE_DIR`)와 `/upload` URL 매핑 폴더(`ROOT.xml`)가 **서로 다름** | 둘 다 `/root/goodpang-uploads`로 통일 |
| 업로드해도 오류 없이 "아무 일 없음" | 성공해도 안내 없이 대시보드로 이동 + "기존 첨부파일" 문구가 CSS(`show` 누락)로 숨겨져 있었음 | 같은 화면으로 돌아와 **flash 메시지 + 서류 썸네일** 표시 |

```
업로드 파일 흐름 (서버)
  저장: UploadPaths → 환경변수 UPLOAD_BASE_DIR (/opt/tomcat9/bin/setenv.sh)
  조회: /upload/** → ROOT.xml <PostResources base="..." webAppMount="/upload"/>
  → 두 경로가 반드시 같아야 함
```

---

## 14. 옮기면서 같이 고친 것 (기존 GoodPang에도 있던 문제)

| 구분 | 내용 |
|---|---|
| 보안 - CSRF | 모든 POST 폼에 토큰 (기존엔 CSRF 보호 자체가 없었음) |
| 보안 - XSS | 사용자 입력값 출력을 `<c:out>`으로 (판매자가 스토어명에 스크립트를 넣으면 관리자 화면에서 실행될 수 있었음) |
| 보안 - 오픈 리다이렉트 | 로그인/출고 후 이동 주소를 허용 목록으로 제한 |
| 데이터 정합성 | 판매자 탈퇴(상태 변경 + 상품 숨김)를 한 트랜잭션으로 |
| 로그 정확도 | 상품 승인·판매자 승인에서 **실제로 바뀐 경우에만** 로그 (기존엔 실패해도 기록) |
| UX | 공지 수정 검증 실패 시 입력값 유지, 판매자 입점 신청 서버 오류 메시지 표시 |
| UX | 숫자 입력칸이 마우스 휠로 바뀌는 문제 차단 (가격 50000 → 49997 저장 사례) |
| 정리 | 동작하지 않던 "수정" 버튼 제거, 잘못 복사돼 있던 `order_detail.jsp` 재작성 |

---

## 15. 결과 요약

| 항목 | 결과 |
|---|---|
| 서블릿 이전 | 판매자 23 + 관리자 16 = **39개 전부** |
| 코드 구조 | Controller 2개 / Service 5개 / Mapper 12개 / Aspect 1개로 역할 분리 |
| SQL | Java 문자열 → MyBatis XML **65개** (동적 SQL, 별칭 매핑) |
| 반복 코드 제거 | JDBC 연결/매핑 코드, 트랜잭션 제어 코드, 액션 로그 13곳 |
| 수업 내용 적용 | Spring MVC, MyBatis, Tiles, `@Transactional`, **Spring Security(SL14)**, **AOP(SL06)** |

---

## 16. 남은 과제

| 과제 | 내용 |
|---|---|
| 판매자 로그인 확인 통합 | `VendorController`에 로그인 확인이 22곳 반복 → Interceptor 또는 관리자처럼 Spring Security로 |
| 판매자 정지 즉시 반영 | 지금은 정지돼도 다시 로그인하기 전까지 세션이 유지됨 |
| 업로드 용량 제한 | `multipartResolver` 전체 한도(현재 무제한) + 초과 시 안내 메시지 |
| 없는 기능 | 상품 수정, 판매자 아이디/비밀번호 찾기 |
| 배포 문서 | 실제 서버 경로(`/root/goodpang-uploads`)로 문서 갱신, 배포 스크립트에 `ROOT.xml` 자동 생성 |

---

## 17. 배운 점

1. **"동작하던 코드를 옮기는 것"도 설계다** - Servlet 1개를 Controller 메서드로 옮길 때마다 Controller / Service / Mapper 중 어디에 둘지 결정해야 했음
2. **Spring은 편한 만큼 "보이지 않는 동작"을 알아야 한다** - 확장자로 응답 형식 결정(406), CSRF 필터(403), 프록시 기반 트랜잭션·AOP(설정 위치, 같은 클래스 호출 제약)
3. **include 방식 차이 같은 작은 차이가 컴파일 오류로 이어진다** - 정적 include vs Tiles 동적 include
4. **로그와 증상을 먼저 본다** - 406/403/404, `NotReadablePropertyException`, 서버의 폴더 경로 비교가 원인을 바로 알려줌
5. **수업 예제를 실제 프로젝트에 적용** - SL14(Security JDBC 인증), SL06(AOP)을 그대로 응용

---

## Q & A

- 참고 문서: [`docs/goodpanglegacy_scm.md`](goodpanglegacy_scm.md) (AOP 액션 로그 상세)
- 배포 문서: [`cicd/cicd_springlegacy.md`](../cicd/cicd_springlegacy.md)
