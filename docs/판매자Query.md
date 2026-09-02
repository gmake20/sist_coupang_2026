# 판매자 대시보드 - 오늘/전날 주문수·매출 Query

`VendorDashboardDAO.fillOrderSalesStat` (project/GoodPang/src/main/java/com/goodpang/dao/VendorDashboardDAO.java:35-52) 에서 사용하는 쿼리.

바인드 변수(`?`) 순서: `targetDate`, `sellerNo`

```sql
SELECT
    COUNT(DISTINCT CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE
                         THEN O.ORDER_NO END) AS TODAY_ORDER_COUNT,
    NVL(SUM(CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE
                  THEN OD.PRICE * OD.ORDER_QTY END), 0) AS TODAY_SALES,
    COUNT(DISTINCT CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE - 1
                         THEN O.ORDER_NO END) AS YESTERDAY_ORDER_COUNT,
    NVL(SUM(CASE WHEN TRUNC(O.ORDER_DATE) = D.TARGET_DATE - 1
                  THEN OD.PRICE * OD.ORDER_QTY END), 0) AS YESTERDAY_SALES
FROM ORDER_DETAIL OD
    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    CROSS JOIN (SELECT DATE '2026-09-01' AS TARGET_DATE FROM DUAL) D  -- 확인할 기준일로 교체
WHERE P.SELLER_NO = 1  -- 확인할 sellerNo로 교체
  AND O.ORDER_STATUS != '주문취소'
  AND TRUNC(O.ORDER_DATE) IN (D.TARGET_DATE, D.TARGET_DATE - 1)
```

## 사용 방법
- `DATE '2026-09-01'` : 확인하고 싶은 기준일로 교체 (미지정 시 화면 기본값은 오늘 날짜)
- `P.SELLER_NO = 1` : 확인하고 싶은 판매자 번호로 교체
- `O.ORDER_STATUS != '주문취소'` 조건으로 주문취소 건은 매출/주문수에서 제외됨

# 판매자 대시보드 - 오늘/전날 방문자수·상품노출수 Query

`VendorDashboardDAO.fillTrafficStat` (project/GoodPang/src/main/java/com/goodpang/dao/VendorDashboardDAO.java:83-98) 에서 사용하는 쿼리.

바인드 변수(`?`) 순서: `targetDate`, `sellerNo`

```sql
SELECT
    COUNT(DISTINCT CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE
                         THEN L.SESSION_ID END) AS TODAY_VISITOR_COUNT,
    COUNT(CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE
               THEN 1 END) AS TODAY_VIEW_COUNT,
    COUNT(DISTINCT CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE - 1
                         THEN L.SESSION_ID END) AS YESTERDAY_VISITOR_COUNT,
    COUNT(CASE WHEN TRUNC(L.VIEW_DATE) = D.TARGET_DATE - 1
               THEN 1 END) AS YESTERDAY_VIEW_COUNT
FROM PRODUCT_VIEW_LOG L
    JOIN PRODUCT P ON L.PRODUCT_NO = P.PRODUCT_NO
    CROSS JOIN (SELECT DATE '2026-09-01' AS TARGET_DATE FROM DUAL) D  -- 확인할 기준일로 교체
WHERE P.SELLER_NO = 1  -- 확인할 sellerNo로 교체
  AND TRUNC(L.VIEW_DATE) IN (D.TARGET_DATE, D.TARGET_DATE - 1)
```

## 사용 방법
- `DATE '2026-09-01'` : 확인하고 싶은 기준일로 교체 (미지정 시 화면 기본값은 오늘 날짜)
- `P.SELLER_NO = 1` : 확인하고 싶은 판매자 번호로 교체
- 방문자수는 `SESSION_ID` 중복제거(DISTINCT COUNT), 상품노출수는 조회 로그 건수 그대로(COUNT)

# 판매자 대시보드 - 매출 현황 차트(일간/주간/월간) Query

`VendorDashboardDAO.getDailySalesStat` / `getWeeklySalesStat` / `getMonthlySalesStat` (project/GoodPang/src/main/java/com/goodpang/dao/VendorDashboardDAO.java:128-351) 에서 사용하는 쿼리.

바인드 변수(`?`) 순서(세 쿼리 공통): `targetDate`, `sellerNo`, `targetDate`

`CONNECT BY LEVEL`로 날짜/주/월 스핀(D)을 만들고, 실적 서브쿼리(S)를 LEFT JOIN 해서 실적 없는 구간도 0으로 채운다.

## 일간 (최근 7일)
```sql
SELECT
    D.STAT_DATE,
    NVL(S.SALES_AMOUNT, 0) AS SALES_AMOUNT,
    NVL(S.ORDER_COUNT, 0) AS ORDER_COUNT
FROM (
    SELECT DATE '2026-09-01' - LEVEL + 1 AS STAT_DATE
    FROM DUAL
    CONNECT BY LEVEL <= 7
) D
LEFT JOIN (
    SELECT
        TRUNC(O.ORDER_DATE) AS ORDER_DAY,
        SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT,
        COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT
    FROM ORDER_DETAIL OD
        JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    WHERE P.SELLER_NO = 1
      AND O.ORDER_STATUS != '주문취소'
      AND TRUNC(O.ORDER_DATE) >= DATE '2026-09-01' - 6
    GROUP BY TRUNC(O.ORDER_DATE)
) S ON S.ORDER_DAY = D.STAT_DATE
ORDER BY D.STAT_DATE
```

## 주간 (최근 5주, ISO 주 기준 월요일 시작)
```sql
SELECT
    D.WEEK_START,
    NVL(S.SALES_AMOUNT, 0) AS SALES_AMOUNT,
    NVL(S.ORDER_COUNT, 0) AS ORDER_COUNT
FROM (
    SELECT TRUNC(DATE '2026-09-01', 'IW') - (LEVEL - 1) * 7 AS WEEK_START
    FROM DUAL
    CONNECT BY LEVEL <= 5
) D
LEFT JOIN (
    SELECT
        TRUNC(O.ORDER_DATE, 'IW') AS WEEK_START,
        SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT,
        COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT
    FROM ORDER_DETAIL OD
        JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    WHERE P.SELLER_NO = 1
      AND O.ORDER_STATUS != '주문취소'
      AND O.ORDER_DATE >= TRUNC(DATE '2026-09-01', 'IW') - 28
    GROUP BY TRUNC(O.ORDER_DATE, 'IW')
) S ON S.WEEK_START = D.WEEK_START
ORDER BY D.WEEK_START
```

## 월간 (최근 5개월)
```sql
SELECT
    D.MONTH_START,
    NVL(S.SALES_AMOUNT, 0) AS SALES_AMOUNT,
    NVL(S.ORDER_COUNT, 0) AS ORDER_COUNT
FROM (
    SELECT ADD_MONTHS(TRUNC(DATE '2026-09-01', 'MM'), -(LEVEL - 1)) AS MONTH_START
    FROM DUAL
    CONNECT BY LEVEL <= 5
) D
LEFT JOIN (
    SELECT
        TRUNC(O.ORDER_DATE, 'MM') AS MONTH_START,
        SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT,
        COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT
    FROM ORDER_DETAIL OD
        JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
        JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    WHERE P.SELLER_NO = 1
      AND O.ORDER_STATUS != '주문취소'
      AND O.ORDER_DATE >= ADD_MONTHS(TRUNC(DATE '2026-09-01', 'MM'), -4)
    GROUP BY TRUNC(O.ORDER_DATE, 'MM')
) S ON S.MONTH_START = D.MONTH_START
ORDER BY D.MONTH_START
```

## 사용 방법
- `DATE '2026-09-01'` : 확인하고 싶은 기준일로 교체 (미지정 시 화면 기본값은 오늘 날짜)
- `P.SELLER_NO = 1` : 확인하고 싶은 판매자 번호로 교체
- `O.ORDER_STATUS != '주문취소'` 조건으로 주문취소 건은 매출/주문수에서 제외됨

# 판매자 대시보드 - KPI 스파크라인(일별 방문자/노출수) Query

`VendorDashboardDAO.getDailyTrafficStat` (project/GoodPang/src/main/java/com/goodpang/dao/VendorDashboardDAO.java:185-238) 에서 사용하는 쿼리.

바인드 변수(`?`) 순서: `targetDate`, `sellerNo`, `targetDate`

매출 일간 차트(`getDailySalesStat`)와 동일한 날짜 스핀(D) + LEFT JOIN(S) 패턴을 `PRODUCT_VIEW_LOG`에 적용.

```sql
SELECT
    D.STAT_DATE,
    NVL(S.VISITOR_COUNT, 0) AS VISITOR_COUNT,
    NVL(S.VIEW_COUNT, 0) AS VIEW_COUNT
FROM (
    SELECT DATE '2026-09-01' - LEVEL + 1 AS STAT_DATE
    FROM DUAL
    CONNECT BY LEVEL <= 7
) D
LEFT JOIN (
    SELECT
        TRUNC(L.VIEW_DATE) AS VIEW_DAY,
        COUNT(DISTINCT L.SESSION_ID) AS VISITOR_COUNT,
        COUNT(*) AS VIEW_COUNT
    FROM PRODUCT_VIEW_LOG L
        JOIN PRODUCT P ON L.PRODUCT_NO = P.PRODUCT_NO
    WHERE P.SELLER_NO = 1
      AND TRUNC(L.VIEW_DATE) >= DATE '2026-09-01' - 6
    GROUP BY TRUNC(L.VIEW_DATE)
) S ON S.VIEW_DAY = D.STAT_DATE
ORDER BY D.STAT_DATE
```

## 사용 방법
- `DATE '2026-09-01'` : 확인하고 싶은 기준일로 교체 (미지정 시 화면 기본값은 오늘 날짜, 최근 7일 표시)
- `P.SELLER_NO = 1` : 확인하고 싶은 판매자 번호로 교체
- 방문자수는 `SESSION_ID` 중복제거(DISTINCT COUNT), 상품노출수는 조회 로그 건수 그대로(COUNT)

# 판매자 대시보드 - 주문/배송 현황 패널 Query

`VendorOrderListDAO.countStats` (project/GoodPang/src/main/java/com/goodpang/dao/VendorOrderListDAO.java:127-141) 에서 사용하는 쿼리. vendor-order.jsp 상단 카드와 공유되는 쿼리.

바인드 변수(`?`) 순서: `sellerNo`

```sql
SELECT
    COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '결제완료' THEN O.ORDER_NO END) AS WAITING_COUNT,
    COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송중' THEN O.ORDER_NO END) AS SHIPPING_COUNT,
    COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송완료' THEN O.ORDER_NO END) AS DELIVERED_COUNT,
    COUNT(DISTINCT CASE WHEN O.ORDER_STATUS = '배송완료'
                          AND D.DELIVERY_END_DATE IS NOT NULL
                          AND TRUNC(D.DELIVERY_END_DATE) = TRUNC(SYSDATE)
                     THEN O.ORDER_NO END) AS DELIVERED_TODAY_COUNT
FROM ORDERS O
    JOIN ORDER_DETAIL OD ON OD.ORDER_NO = O.ORDER_NO
    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    LEFT JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
WHERE P.SELLER_NO = 1  -- 확인할 sellerNo로 교체
```

## 사용 방법
- `P.SELLER_NO = 1` : 확인하고 싶은 판매자 번호로 교체
- WAITING_COUNT(결제완료=배송대기), SHIPPING_COUNT(배송중), DELIVERED_COUNT(배송완료 전체), DELIVERED_TODAY_COUNT(오늘 배송완료)를 한 번에 집계
- `DELIVERED_TODAY_COUNT`는 `DELIVERY.DELIVERY_END_DATE`가 오늘(`SYSDATE`)인 건만 카운트

# 판매자 상품 목록 - VendorProductServlet Query

`ProductListDAO.findBySellerNoAndDisplayYn` (project/GoodPang/src/main/java/com/goodpang/dao/ProductListDAO.java:28-70) 에서 사용하는 쿼리. `findBySellerNo`(전시중, `view` 파라미터 없음)와 `findHiddenBySellerNo`(숨김, `view=hidden`)가 이 쿼리를 공유하며 `displayYn` 값만 다르다.

바인드 변수(`?`) 순서: `sellerNo`, `displayYn`

```sql
SELECT
    P.PRODUCT_NO,
    P.PRODUCT_NAME,
    P.SALE_METHOD,
    P.SALE_STATUS,
    C1.CATEGORY_NAME AS MAIN_CATEGORY_NAME,
    C2.CATEGORY_NAME AS MID_CATEGORY_NAME,
    C3.CATEGORY_NAME AS SUB_CATEGORY_NAME,
    NVL(OPT.MIN_PRICE, 0) + P.PRODUCT_PRICE  AS MIN_PRICE,
    NVL(OPT.MAX_PRICE, 0) + P.PRODUCT_PRICE  AS MAX_PRICE,
    NVL(OPT.TOTAL_QUANTITY, P.QUANTITY)  AS TOTAL_QUANTITY,
    NVL(OPT.OPTION_COUNT, 0)             AS OPTION_COUNT,
    IMG.IMAGE_URL                        AS THUMBNAIL_URL,
    P.CREATED_DATE,
    P.UPDATED_DATE
FROM PRODUCT P
    JOIN CATEGORY C3 ON P.SUB_CATEGORY_NO = C3.CATEGORY_NO
    LEFT JOIN CATEGORY C2 ON C2.CATEGORY_NO = C3.PARENT_CATEGORY_NO
    LEFT JOIN CATEGORY C1 ON C1.CATEGORY_NO = C2.PARENT_CATEGORY_NO
    LEFT JOIN (
        SELECT PRODUCT_NO,
               MIN(PRICE)    AS MIN_PRICE,
               MAX(PRICE)    AS MAX_PRICE,
               SUM(QUANTITY) AS TOTAL_QUANTITY,
               COUNT(*)      AS OPTION_COUNT
        FROM PRODUCT_OPTION
        GROUP BY PRODUCT_NO
    ) OPT ON OPT.PRODUCT_NO = P.PRODUCT_NO
    LEFT JOIN (
        SELECT PRODUCT_NO, IMAGE_URL,
               ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
        FROM PRODUCT_IMAGE
        WHERE IMAGE_PURPOSE = '대표'
    ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
WHERE P.SELLER_NO = 1        -- 확인할 sellerNo로 교체
  AND P.DISPLAY_YN = 'Y'     -- 'Y'=전시중 목록, 'N'=숨김 목록
ORDER BY P.CREATED_DATE DESC
```

## 사용 방법
- `P.SELLER_NO` : 확인하고 싶은 판매자 번호로 교체
- `P.DISPLAY_YN` : `'Y'`면 전시중 상품 목록(기본), `'N'`이면 숨김 상품 목록(`?view=hidden`)
- `CATEGORY`를 대/중/소 3단으로 self-join해서 카테고리명을 함께 조회
- `PRODUCT_OPTION`을 서브쿼리로 집계해 옵션 최저/최고가·총재고·옵션수 산출 (옵션이 없으면 상품 자체 가격/수량을 그대로 사용)
- `PRODUCT_IMAGE`에서 `IMAGE_PURPOSE='대표'`인 이미지 중 `ROW_NUMBER()`로 1건만 썸네일로 사용
- 판매중/품절/판매중지/승인대기 건수는 이 쿼리 결과가 아니라 `VendorProductServlet`에서 `SALE_STATUS` 값을 순회하며 자바 코드로 집계

# 판매자 상품 등록 - VendorProductWriteServlet Query

INSERT는 POST에서 `ProductWriteDAO.insertProduct(dto)` (project/GoodPang/src/main/java/com/goodpang/dao/ProductWriteDAO.java) 가 트랜잭션(`setAutoCommit(false)`) 안에서 실행. 상품 1건 등록 시 시퀀스 번호 + PRODUCT/PRODUCT_OPTION/PRODUCT_IMAGE 3개 테이블 INSERT가 한 트랜잭션으로 커밋되고, 하나라도 실패하면 전체 롤백됨.

## 1. 시퀀스 번호 (`nextVal`, ProductWriteDAO.java:56-62)
상품당 1회(`SEQ_PRODUCT`), 옵션마다 1회(`SEQ_OPTION`), 이미지마다 1회(`SEQ_PRODUCT_IMAGE`)에 재사용
```sql
SELECT SEQ_PRODUCT.NEXTVAL FROM DUAL
```

## 2. 상품 등록 (`insertProductRow`, ProductWriteDAO.java:64-160)
```sql
INSERT INTO PRODUCT (
    product_no, seller_no, sub_category_no,
    sale_method, brand_name, no_brand_yn, product_name, internal_name,
    option_yn, product_price, quantity,
    manufacturer, composition_type, certification_type, parallel_import_yn,
    minor_purchase_yn, max_purchase_yn, max_purchase_qty,
    sale_period_yn, sale_start_date, sale_end_date, vat_type,
    detail_type, product_desc,
    shipping_zipcode, shipping_address, shipping_detail_address, jeju_shipping_yn,
    delivery_service_code, delivery_method, bundle_shipping_yn,
    shipping_fee_type, shipping_fee,
    lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
    return_zipcode, return_address, return_detail_address,
    initial_shipping_fee, return_shipping_fee,
    sale_status, created_date, updated_date
) VALUES (
    ?, ?, ?,
    ?, ?, ?, ?, ?,
    ?, ?, 0,
    ?, ?, ?, ?,
    ?, ?, NULL,
    ?, NULL, NULL, ?,
    ?, NULL,
    ?, ?, ?, ?,
    ?, ?, ?,
    ?, 0,
    ?, ?, ?, ?,
    ?, ?, ?,
    ?, ?,
    ?, SYSDATE, SYSDATE
)
```
- `quantity`는 항상 0, `sale_status`는 항상 `'승인 대기'`로 고정(등록 즉시 노출되지 않음)
- 출고지/반품지 주소는 주소록 기능이 없어 판매자 사업장 주소(`SellerDTO`)를 그대로 복사해서 넣음

## 3. 옵션 등록 (옵션마다 반복) (`insertOptionRow`, ProductWriteDAO.java:162-207)
```sql
INSERT INTO PRODUCT_OPTION (
    OPTION_ID, product_no,
    OPTION1_TYPE, OPTION1_VALUE, OPTION2_TYPE, OPTION2_VALUE, OPTION3_TYPE, OPTION3_VALUE,
    normal_price, PRICE, auto_price_adjust_yn, quantity,
    seller_product_code, model_no, barcode, STATUS
) VALUES (
    ?, ?,
    ?, ?, ?, ?, ?, ?,
    ?, ?, ?, ?,
    ?, ?, ?, ?
)
```
- `STATUS`는 `quantity > 0`이면 `'Y'`, 아니면 `'N'`으로 자바 코드가 계산해서 채움

## 4. 이미지 등록 (옵션 대표/추가 이미지 + 상세설명 이미지마다 반복) (`insertImageRow`, ProductWriteDAO.java:209-235)
```sql
INSERT INTO PRODUCT_IMAGE (
    image_no, product_no, OPTION_ID, image_purpose, image_order, image_url, created_date
) VALUES (?, ?, ?, ?, ?, ?, SYSDATE)
```
- `image_purpose`: 옵션 대표 이미지=`'대표'`, 옵션 추가 이미지=`'추가'`, 상품 상세설명 이미지=`'상세설명'`(이 경우 `OPTION_ID`는 NULL)

## 사용 방법
- 실제 실행 순서: 상품 INSERT 1회 → (옵션 INSERT → 대표/추가 이미지 INSERT) × 옵션 수 → 상세설명 이미지 INSERT × 이미지 수 → COMMIT
- 직접 Query로 데이터를 확인할 때는 방금 등록한 `product_no`(또는 `SELECT SEQ_PRODUCT.CURRVAL FROM DUAL`)로 PRODUCT/PRODUCT_OPTION/PRODUCT_IMAGE를 조회하면 됨

# 판매자 상품 옵션 관리 - /vendor/product/options Query

`VendorProductOptionListServlet` (GET) 이 `VendorProductOptionDAO` (project/GoodPang/src/main/java/com/goodpang/dao/VendorProductOptionDAO.java) 의 쿼리 2개를 사용.

## 1. 옵션 목록 조회 (`findBySellerNo`, VendorProductOptionDAO.java:24-41)

바인드 변수(`?`) 순서: `sellerNo`, `productNo`(nullable), `productNo`(nullable)

```sql
SELECT
    PO.OPTION_ID, PO.PRODUCT_NO, P.PRODUCT_NAME,
    PO.OPTION1_VALUE, PO.OPTION2_VALUE, PO.OPTION3_VALUE,
    NVL(PO.PRICE, 0) AS PRICE, PO.NORMAL_PRICE, PO.QUANTITY, PO.STATUS,
    IMG.IMAGE_URL AS THUMBNAIL_URL
FROM PRODUCT_OPTION PO
    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
    LEFT JOIN (
        SELECT PRODUCT_NO, IMAGE_URL,
               ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
        FROM PRODUCT_IMAGE
        WHERE IMAGE_PURPOSE = '대표'
    ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
WHERE P.SELLER_NO = 1        -- 확인할 sellerNo로 교체
  AND (NULL IS NULL OR P.PRODUCT_NO = NULL)  -- ?productNo= 파라미터가 있으면 그 상품번호로 교체(2군데 동일 값), 없으면 NULL 그대로 두면 전체 조회
ORDER BY P.CREATED_DATE DESC, P.PRODUCT_NO DESC, PO.OPTION_ID
```
- `PRODUCT_IMAGE`에서 `IMAGE_PURPOSE='대표'` 이미지 1건만 `ROW_NUMBER()`로 골라 썸네일로 사용 (상품 목록 쿼리와 동일 패턴)

## 2. 상품 선택 필터 드롭다운용 (`findDistinctProductsBySellerNo`, VendorProductOptionDAO.java:77-83)

바인드 변수(`?`) 순서: `sellerNo`

```sql
SELECT DISTINCT P.PRODUCT_NO, P.PRODUCT_NAME, P.CREATED_DATE
FROM PRODUCT_OPTION PO
    JOIN PRODUCT P ON PO.PRODUCT_NO = P.PRODUCT_NO
WHERE P.SELLER_NO = 1        -- 확인할 sellerNo로 교체
ORDER BY P.CREATED_DATE DESC, P.PRODUCT_NO DESC
```
- 옵션이 하나라도 있는 상품만 중복 없이 뽑아 화면 상단 "상품 선택" 드롭다운을 채움

## 참고: 옵션 수정 (`/vendor/product/option/update`, POST)
목록 화면이 아니라 별도 URL(`VendorProductOptionUpdateServlet`)에서 처리하는 UPDATE. `PRODUCT_OPTION`에는 `SELLER_NO`가 없어서 `PRODUCT`를 통해 소유권을 확인한 뒤에만 수정되게 `EXISTS` 서브쿼리로 검사함 (VendorProductOptionDAO.java:114-128).
(다른 판매자가 옵션을 수정할수없음)
```sql
UPDATE PRODUCT_OPTION
SET PRICE = ?,
    NORMAL_PRICE = ?,
    QUANTITY = ?,
    STATUS = ?
WHERE OPTION_ID = ?
  AND EXISTS (
        SELECT 1 FROM PRODUCT P
        WHERE P.PRODUCT_NO = PRODUCT_OPTION.PRODUCT_NO
          AND P.SELLER_NO = ?
  )
```

# 판매자 주문/배송 관리 - /vendor/order Query

`VendorOrderServlet` (GET) 이 `VendorOrderListDAO` (project/GoodPang/src/main/java/com/goodpang/dao/VendorOrderListDAO.java) 의 `findBySellerNo`(주문 목록)와 `countStats`(상단 통계 카드/도넛, 위 "주문/배송 현황 패널 Query" 항목과 동일 쿼리) 를 사용.

## 주문 목록 조회 (`findBySellerNo`, VendorOrderListDAO.java:35-121)

검색 필터(기간/주문상태/배송상태/결제상태)는 전부 선택사항이라, 파라미터가 있을 때만 해당 WHERE 절이 동적으로 붙음.

```sql
SELECT
    O.ORDER_NO,
    OD.ORDER_DETAIL_NO,
    P.PRODUCT_NAME,
    PO.OPTION1_VALUE,
    PO.OPTION2_VALUE,
    PO.OPTION3_VALUE,
    OD.ORDER_QTY,
    OD.PRICE,
    O.ORDER_STATUS,
    O.ORDER_DATE,
    M.MEMBER_NAME,
    M.PHONE,
    IMG.IMAGE_URL AS THUMBNAIL_URL
FROM ORDER_DETAIL OD
    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
    LEFT JOIN PRODUCT_OPTION PO ON OD.OPTION_ID = PO.OPTION_ID
    LEFT JOIN (
        SELECT PRODUCT_NO, IMAGE_URL,
               ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
        FROM PRODUCT_IMAGE
        WHERE IMAGE_PURPOSE = '대표'
    ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
WHERE P.SELLER_NO = 1                              -- 확인할 sellerNo로 교체
  -- 아래는 검색 파라미터가 있을 때만 각각 추가됨 (없으면 조건절 자체가 안 붙음)
  AND TRUNC(O.ORDER_DATE) >= DATE '2026-08-01'      -- startDate
  AND TRUNC(O.ORDER_DATE) <= DATE '2026-09-02'      -- endDate
  AND O.ORDER_STATUS = '배송중'                      -- orderStatus (결제완료/배송중/배송완료/주문취소)
  -- deliveryStatus: '출고대기'는 내부적으로 '결제완료'로 치환되어 O.ORDER_STATUS = ? 조건으로 추가
  -- paymentStatus: '결제완료'면 O.ORDER_STATUS != '주문취소', '결제취소'면 O.ORDER_STATUS = '주문취소'
ORDER BY O.ORDER_DATE DESC, O.ORDER_NO DESC
```

## 사용 방법
- `P.SELLER_NO` : 확인하고 싶은 판매자 번호로 교체
- orderStatus/deliveryStatus/paymentStatus 세 필터는 전부 같은 컬럼 `O.ORDER_STATUS`를 서로 다른 관점(주문상태/배송진행도/결제여부)으로 매핑한 것이라, 서로 모순되는 값을 동시에 걸면 0건이 나올 수 있음(정상 동작)
- 상단 통계 카드/배송현황 도넛은 이 문서의 "판매자 대시보드 - 주문/배송 현황 패널 Query" 항목과 동일한 `countStats` 쿼리를 그대로 씀

# 판매자 배송 관리 - /vendor/delivery Query

`VendorDeliveryServlet` (GET) 이 `VendorDeliveryDAO.findShippingBySellerNo` (project/GoodPang/src/main/java/com/goodpang/dao/VendorDeliveryDAO.java:20-45) 쿼리 하나만 사용. 배송중인 주문을 모니터링하는 화면이고, 배송완료 처리는 여기서 하지 않음(/admin/deliveries 에서 대행).

바인드 변수(`?`) 순서: `sellerNo`

```sql
SELECT
    D.DELIVERY_NO, D.ORDER_NO, D.DELIVERY_SERVICE_CODE, D.INVOICE_NO,
    D.DELIVERY_START_DATE,
    M.MEMBER_NAME, M.PHONE,
    PN.PRODUCT_NAME, PN.ITEM_COUNT
FROM DELIVERY D
    JOIN ORDERS O ON D.ORDER_NO = O.ORDER_NO
    JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
    JOIN (
        SELECT
            OD.ORDER_NO,
            MIN(P.PRODUCT_NAME) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS PRODUCT_NAME,
            COUNT(*) AS ITEM_COUNT
        FROM ORDER_DETAIL OD
            JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
        WHERE P.SELLER_NO = 1        -- 확인할 sellerNo로 교체
        GROUP BY OD.ORDER_NO
    ) PN ON PN.ORDER_NO = D.ORDER_NO
WHERE D.DELIVERY_STATUS = '배송중'
ORDER BY D.DELIVERY_START_DATE DESC
```

## 사용 방법
- `P.SELLER_NO` : 확인하고 싶은 판매자 번호로 교체
- `DELIVERY` 테이블엔 `SELLER_NO`가 없어서, 서브쿼리 `PN`이 이 판매자 상품이 포함된 주문번호만 뽑아 `INNER JOIN`으로 걸어 소유권을 필터링함
- `PRODUCT_NAME`은 그 주문의 첫 번째 주문상세(`ORDER_DETAIL_NO` 기준) 상품명 하나만 대표로 표시, `ITEM_COUNT`는 그 주문에 포함된 이 판매자 상품 라인 수
- 화면의 "지연" 표시는 이 쿼리 결과가 아니라 자바 `VendorDeliveryDTO.isDelayed()`에서 `DELIVERY_START_DATE` 기준으로 계산

# 판매자 출고/운송장 관리 - /vendor/shipping Query

`VendorShippingServlet` (GET) 이 `VendorShippingDAO.findWaitingBySellerNo` (project/GoodPang/src/main/java/com/goodpang/dao/VendorShippingDAO.java:19-43) 쿼리 하나만 사용. `/vendor/delivery`와 거의 동일한 패턴(주문별 대표 상품명 집계 서브쿼리)이고, `결제완료`(출고대기) 상태 주문만 대상. 실제 송장 등록/출고 처리는 여기서 하지 않고 기존 `/vendor/order/ship`(VendorOrderShipServlet)을 그대로 씀.

바인드 변수(`?`) 순서: `sellerNo`

```sql
SELECT
    O.ORDER_NO, O.ORDER_DATE,
    M.MEMBER_NAME, M.PHONE,
    PN.PRODUCT_NAME, PN.ITEM_COUNT, PN.TOTAL_AMOUNT
FROM ORDERS O
    JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
    JOIN (
        SELECT
            OD.ORDER_NO,
            MIN(P.PRODUCT_NAME) KEEP (DENSE_RANK FIRST ORDER BY OD.ORDER_DETAIL_NO) AS PRODUCT_NAME,
            COUNT(*) AS ITEM_COUNT,
            SUM(OD.PRICE * OD.ORDER_QTY) AS TOTAL_AMOUNT
        FROM ORDER_DETAIL OD
            JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
        WHERE P.SELLER_NO = 1        -- 확인할 sellerNo로 교체
        GROUP BY OD.ORDER_NO
    ) PN ON PN.ORDER_NO = O.ORDER_NO
WHERE O.ORDER_STATUS = '결제완료'
ORDER BY O.ORDER_DATE ASC
```

## 사용 방법
- `P.SELLER_NO` : 확인하고 싶은 판매자 번호로 교체
- `ORDERS`엔 `SELLER_NO`가 없어서, 서브쿼리 `PN`이 이 판매자 상품이 포함된 주문번호만 뽑아 `INNER JOIN`으로 소유권 필터링 (`/vendor/delivery`는 `DELIVERY` 기준, 이건 `ORDERS` 기준이라는 차이만 있음)
- `TOTAL_AMOUNT`는 주문 전체 금액이 아니라 이 판매자 상품 라인들의 `PRICE*ORDER_QTY` 합만 집계
- `ORDER BY ORDER_DATE ASC`로 오래된(출고 지연 임박) 주문이 먼저 나옴
- "지연" 표시는 쿼리 결과가 아니라 자바 `VendorShippingDTO.isDelayed()`에서 계산

# 판매자 정산관리 - /vendor/settlement Query

`VendorSettlementServlet` (GET) 이 `VendorSettlementDAO.findBySellerNo` (project/GoodPang/src/main/java/com/goodpang/dao/VendorSettlementDAO.java:23-41) 쿼리 하나만 사용. 실제 수수료율/정산주기 데이터가 없어 배송완료 매출을 주(week) 단위로 근사 집계하는 방식.

바인드 변수(`?`) 순서: `sellerNo`

```sql
SELECT
    TRUNC(D.DELIVERY_END_DATE, 'IW') AS PERIOD_START,
    COUNT(DISTINCT O.ORDER_NO) AS ORDER_COUNT,
    SUM(OD.PRICE * OD.ORDER_QTY) AS SALES_AMOUNT
FROM ORDER_DETAIL OD
    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    JOIN DELIVERY D ON D.ORDER_NO = O.ORDER_NO
WHERE P.SELLER_NO = 1               -- 확인할 sellerNo로 교체
  AND O.ORDER_STATUS = '배송완료'
  AND D.DELIVERY_END_DATE IS NOT NULL
GROUP BY TRUNC(D.DELIVERY_END_DATE, 'IW')
ORDER BY PERIOD_START DESC
```

## 사용 방법
- `P.SELLER_NO` : 확인하고 싶은 판매자 번호로 교체
- `배송완료` 상태이면서 `DELIVERY_END_DATE`(배송완료일)가 있는 주문 라인만 대상 — 진행 중인 주문은 정산 대상에서 제외
- `TRUNC(.., 'IW')`로 배송완료일이 속한 ISO 주(월요일 시작)별로 묶어 그 주의 주문수/매출을 집계 → 화면의 정산 "기간" 단위
- `SALES_AMOUNT`는 이 판매자 상품 라인들의 `PRICE*ORDER_QTY` 합(수수료 차감 전 총매출 근사치)
- 화면 상단 총 정산금액은 이 쿼리 결과를 자바에서 다시 합산한 값
- 각 기간 행 상세(`/vendor/settlement/detail`)는 이 쿼리를 GROUP BY 없이 풀어서 보여주는 별도 쿼리(`findDetailBySellerNo`)를 씀

# 판매자 취소/반품/교환 관리 - /vendor/return Query

`VendorReturnServlet` (GET) 이 `VendorReturnDAO.findBySellerNo` (project/GoodPang/src/main/java/com/goodpang/dao/VendorReturnDAO.java) 쿼리 하나만 사용. `PRODUCT_RETURN`에는 취소/반품/교환을 구분하는 컬럼이 따로 없고 `RETURN_STATUS`(문자열) 하나뿐이라, `'취소%'`/`'반품%'`/`'교환%'`로 시작하는 상태값을 기준으로 유형을 나눠서 보여줌. 지금은 주문취소(`OrderCancelDAO.cancelOrder`)만 실제로 이 테이블에 `'취소완료'` 상태로 데이터를 쌓고 있고, 반품/교환 신청 화면은 아직 없어 그 두 유형은 데이터가 비어있을 수 있음.

바인드 변수(`?`) 순서: `sellerNo`

```sql
SELECT
    PR.RETURN_NO, PR.ORDER_DETAIL_NO, O.ORDER_NO,
    PR.RETURN_QTY, PR.REFUND_AMOUNT, PR.RETURN_REASON, PR.RETURN_STATUS, PR.REQUEST_DATE,
    CASE
        WHEN PR.RETURN_STATUS LIKE '취소%' THEN '취소'
        WHEN PR.RETURN_STATUS LIKE '반품%' THEN '반품'
        WHEN PR.RETURN_STATUS LIKE '교환%' THEN '교환'
        ELSE '기타'
    END AS RETURN_TYPE,
    P.PRODUCT_NAME,
    PO.OPTION1_VALUE, PO.OPTION2_VALUE, PO.OPTION3_VALUE,
    M.MEMBER_NAME, M.PHONE,
    IMG.IMAGE_URL AS THUMBNAIL_URL
FROM PRODUCT_RETURN PR
    JOIN ORDER_DETAIL OD ON PR.ORDER_DETAIL_NO = OD.ORDER_DETAIL_NO
    JOIN ORDERS O ON OD.ORDER_NO = O.ORDER_NO
    JOIN PRODUCT P ON OD.PRODUCT_NO = P.PRODUCT_NO
    JOIN MEMBER M ON O.MEMBER_NO = M.MEMBER_NO
    LEFT JOIN PRODUCT_OPTION PO ON OD.OPTION_ID = PO.OPTION_ID
    LEFT JOIN (
        SELECT PRODUCT_NO, IMAGE_URL,
               ROW_NUMBER() OVER (PARTITION BY PRODUCT_NO ORDER BY OPTION_ID, IMAGE_ORDER) AS RN
        FROM PRODUCT_IMAGE
        WHERE IMAGE_PURPOSE = '대표'
    ) IMG ON IMG.PRODUCT_NO = P.PRODUCT_NO AND IMG.RN = 1
WHERE P.SELLER_NO = 1        -- 확인할 sellerNo로 교체
ORDER BY PR.REQUEST_DATE DESC
```

## 사용 방법
- `P.SELLER_NO` : 확인하고 싶은 판매자 번호로 교체
- `RETURN_TYPE`은 저장된 컬럼이 아니라 `RETURN_STATUS` 값을 조회 시점에 분류한 계산 컬럼 — 나중에 반품/교환 신청을 추가할 때 `RETURN_STATUS`를 `'반품...'`/`'교환...'`으로 시작하게만 넣으면 이 쿼리가 자동으로 분류함
- 화면 상단 취소/반품/교환 건수 통계는 이 쿼리 결과를 자바(`VendorReturnServlet`)에서 `RETURN_TYPE` 기준으로 세어서 계산
