# sist_coupang_2026

쿠팡과 유사한 웹 쇼핑몰 프로젝트(GoodPang). 이 문서는 관리자(admin) 관련 URL과, 배송완료 처리 URL 정보를 정리한 것입니다.

프로젝트 소스 위치: `project/GoodPang/src/main/java/com/goodpang/servlet`

## 관리자(Admin) 관련 URL

| URL | Method | Servlet | 설명 |
|---|---|---|---|
| `/admin/login` | GET, POST | AdminLoginServlet | 관리자 로그인 폼 조회 / 로그인 처리 |
| `/admin/logout` | GET, POST | AdminLogoutServlet | 관리자 로그아웃 |
| `/admin/dashboard` | GET | AdminDashboardServlet | 관리자 대시보드 |
| `/admin/products` | GET | AdminProductListServlet | 상품 목록 조회 |
| `/admin/product-approve` | POST | AdminProductApprovalServlet | 상품 승인 처리 |
| `/admin/sellers` | GET | SellerListServlet | 판매자(입점) 목록 조회 |
| `/admin/seller-detail` | GET | SellerDetailServlet | 판매자 상세 조회 (`?sellerNo=`) |
| `/admin/seller-approve` | POST | SellerApprovalServlet | 판매자 입점 승인 처리 |
| `/admin/deliveries` | GET | AdminDeliveryListServlet | 배송중인 상품 목록 조회 |
| `/admin/delivery-complete` | POST | AdminDeliveryCompleteServlet | 배송완료 처리 |

## 배송완료 URL

- **URL**: `POST /admin/delivery-complete`
- **Servlet**: `AdminDeliveryCompleteServlet` → `AdminDeliveryDAO.completeDelivery(deliveryNo)`
- **파라미터**: `deliveryNo` (int)
- **동작**: `DELIVERY.DELIVERY_STATUS`를 `배송중` → `배송완료`로 변경하고, 연결된 `ORDERS.ORDER_STATUS`도 `배송완료`로 함께 갱신. 처리 후 `/admin/deliveries` 목록으로 리다이렉트.
- **비고**: 현재는 배송기사 전용 계정이 없어 관리자가 `/admin/deliveries` 목록 화면에서 대신 완료 처리를 하고 있음 (`AdminDeliveryCompleteServlet.java` 주석 참고). 추후 배송기사 로그인/역할이 추가되면 이 URL의 권한 체크 로직을 관리자 전용에서 "본인 담당 배송건"으로 변경할 예정.

## 배송 JSON API (로그인 불필요)

`/admin` 밖에 있어서 `AdminAuthFilter`를 타지 않고, 관리자 로그인 없이 누구나 호출할 수 있는 JSON 버전 API.

| URL | Method | Servlet | 설명 |
|---|---|---|---|
| `/deliveries/json` | GET | DeliveryJsonServlet | 배송중인 상품 목록을 JSON 배열로 조회 |
| `/delivery-complete/json` | POST | DeliveryCompleteJsonServlet | 배송완료 처리 (JSON 응답) |

### GET /deliveries/json

- **동작**: `AdminDeliveryDAO.findShipping()` 결과를 그대로 JSON 배열로 반환 (`/admin/deliveries` 화면과 같은 데이터).
- **응답 예시**:
  ```json
  [
    {
      "deliveryNo": 12,
      "orderNo": 52,
      "deliveryServiceCode": "CJ",
      "invoiceNo": "123456789",
      "deliveryStatus": "배송중",
      "deliveryStartDate": "...",
      "buyerName": "홍*동",
      "buyerPhone": "010-0000-****",
      "zipcode": "12345",
      "address": "서울특별시 강남구 테헤란로 1",
      "detailAddress": "101동 101호",
      "productName": "반팔 티셔츠",
      "productImageUrl": "./pds/short_1.jpg",
      "storeName": "베이직루트",
      "itemCount": 2
    }
  ]
  ```
- **주의**: `buyerName`(가운데 글자 마스킹, 예: `홍*동`)과 `buyerPhone`(뒷 4자리 마스킹, 예: `010-0000-****`)은 마스킹해서 반환하지만, 그 외 항목은 그대로 노출되므로 로컬/테스트 용도로만 사용할 것.

### POST /delivery-complete/json

- **파라미터**: `deliveryNo` (int, `application/x-www-form-urlencoded`)
- **동작**: 내부적으로 `AdminDeliveryDAO.completeDelivery(deliveryNo)`를 그대로 재사용 — `/admin/delivery-complete`와 동일하게 배송 상태 + 주문 상태를 한 트랜잭션으로 갱신.
- **응답 예시**:
  - 성공: `{"success": true}`
  - 실패(이미 처리됐거나 존재하지 않는 배송번호): `{"success": false, "message": "이미 처리되었거나 존재하지 않는 배송번호입니다."}`
  - 실패(`deliveryNo` 누락/숫자 아님): `{"success": false, "message": "deliveryNo가 올바르지 않습니다."}`
