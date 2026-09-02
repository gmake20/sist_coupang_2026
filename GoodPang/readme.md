기본 URL : http://scym3.cafe24.com:8080/

## 배송 JSON API 


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
    "deliveryNo": 32,
    "orderNo": 93,
    "deliveryServiceCode": "경동택배",
    "invoiceNo": "111223352142314",
    "deliveryStatus": "배송중",
    "deliveryStartDate": "Sep 1, 2026, 8:11:39 AM",
    "buyerName": "관*자",
    "buyerPhone": "010-1234-****",
    "productName": "레몬소울 띠비 프린팅 반팔 티셔츠",
    "productImageUrl": "upload/7/24aefce9-6f67-49df-b0a7-9dd6f8c210cf.png",
    "storeName": "모던웨어",
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