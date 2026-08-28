# 관리자(/admin) URL 목록

프로젝트 전체에서 `/admin`으로 시작하는 URL을 정리한 문서입니다. (2026-08-28 기준)

⚠ **아래 URL들은 전부 관리자 로그인/인증 체크가 없습니다.** 세션이나 URL만 알면 누구나 접근 가능한 상태입니다. 별도의 관리자 계정 시스템이 아직 프로젝트에 없어서, 임시로 화면에서 바로 승인/반려 처리를 하도록 만들어져 있습니다.

## 판매자 관련

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/sellers` | GET | `SellerListServlet` | `admin-seller-list.jsp` | 판매자 목록 조회 (입점심사 상태 확인용) |
| `/admin/seller-detail` | GET | `SellerDetailServlet` | `admin-seller-detail.jsp` | 판매자 상세정보 (목록에서 클릭 시) |
| `/admin/seller-approve` | POST | `SellerApprovalServlet` | - | 판매자 입점심사 승인/반려 처리 (`action=approve`/`reject`, 반려 시 `rejectReason` 저장) |

## 상품 관련

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/products` | GET | `AdminProductListServlet` | `admin-product-list.jsp` | 상품 목록 조회 ('승인 대기' 상품이 위로 정렬됨) |
| `/admin/product-approve` | POST | `AdminProductApprovalServlet` | - | 상품 승인/반려 처리 (`action=approve`→`판매 중`, `action=reject`→`판매 중지`) |

## 배송 관련

| URL | Method | 서블릿 | View | 설명 |
|---|---|---|---|---|
| `/admin/deliveries` | GET | `AdminDeliveryListServlet` | `admin-delivery-list.jsp` | 배송중(`DELIVERY_STATUS='배송중'`)인 상품 목록 조회 |
| `/admin/delivery-complete` | POST | `AdminDeliveryCompleteServlet` | - | 배송완료 처리 — `DELIVERY.DELIVERY_STATUS`를 `배송완료`로, `DELIVERY_END_DATE`를 `SYSDATE`로, `ORDERS.ORDER_STATUS`도 `배송완료`로 함께 갱신 |

### 상품 승인/반려 관련 스키마 제약

`PRODUCT.SALE_STATUS`는 CHECK 제약상 `'판매 중'/'품절'/'판매 중지'/'승인 대기'`만 허용되고, 판매자(`SELLER`)와 달리 별도의 `'반려'` 상태값이나 반려사유 컬럼(`REJECT_REASON`)이 없습니다. 그래서 상품 "반려"는 기존에 허용된 `'판매 중지'`로 전환하는 것으로 대신하며, 반려 사유 입력란은 없습니다.

## 관련 소스 파일

- `project/GoodPang/src/main/java/com/goodpang/servlet/SellerListServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/SellerDetailServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/SellerApprovalServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminProductListServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminProductApprovalServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/dao/AdminProductDAO.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminDeliveryListServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/servlet/AdminDeliveryCompleteServlet.java`
- `project/GoodPang/src/main/java/com/goodpang/dao/AdminDeliveryDAO.java`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-seller-list.jsp`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-seller-detail.jsp`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-product-list.jsp`
- `project/GoodPang/src/main/webapp/WEB-INF/views/admin-delivery-list.jsp`
