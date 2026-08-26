-- =========================================================
-- product_table_insert.sql
-- 상품(PRODUCT) 샘플 데이터 — 5개 상품 전체 시나리오
-- 출처: docs/상품.md 14절 (카테고리 번호는 실제 category_table_insert.sql 데이터에
--       맞춰 재검증한 값으로 교체함 — 자세한 매핑은 상품.md 상단 표 참고)
--
-- ⚠ 전제조건
--   1. product_table_create.sql을 먼저 실행해서 PRODUCT/PRODUCT_OPTION/시퀀스가
--      "방금 새로 만들어진, 비어있는" 상태여야 합니다. 이 파일은 SEQ_PRODUCT/SEQ_OPTION이
--      1부터 시작한다고 가정하고 product_no/OPTION_ID를 리터럴 숫자로 직접 씁니다
--      (테이블이 비어있지 않은 상태에서 실행하면 번호가 꼬입니다).
--   2. SELLER 테이블에 seller_no = 1, 5, 8 데이터가 이미 있어야 합니다 (FK).
--   3. CATEGORY 테이블에 category_no = 10301, 10308, 40601, 10202, 41205가 이미 있어야
--      합니다 (docs/category_table_insert.sql 실행 후라면 문제 없음).
--
-- ⚠ 상품3(생수)은 option_yn='N'이라 PRODUCT_OPTION 행을 아예 만들지 않습니다. 그래서
-- 옛날처럼 PRODUCT_IN + 트리거로 입고 처리를 할 수 없어서, quantity를 직접 UPDATE
-- 했습니다. OPTION_ID 번호도 상품3의 더미 옵션 행이 빠지면서 상품4·상품5부터 한 칸씩
-- 앞당겨졌습니다 (상품4: 12~19 → 11~18, 상품5: 20~22 → 19~21).
--
-- ⚠ 상품.md 10절에 있던 "남성 긴팔티 골프 이너웨어" 2개짜리 간단 예시는 이 5개 상품
-- 시나리오와 내용이 겹쳐서 여기엔 포함하지 않았습니다. 그 예시가 따로 필요하면
-- 상품.md 10절을 참고해서 직접 실행하세요.
-- =========================================================


-- PRODUCT 컬럼순서는 아래 모든 INSERT에서 동일. 배송/반품 값은 데모용 기본값
-- (로젠택배·일반배송·무료배송 등, vendor_product_write.jsp 화면 기본값과 동일)으로 통일.

-- 상품1: 남성 반팔 티셔츠 — 사이즈 단일 옵션 / 29,000원 (option_yn='Y')
-- 카테고리: 패션의류/잡화 > 남녀공용의류 > 티셔츠 (10301)
INSERT INTO PRODUCT (
    product_no, seller_no, sub_category_no,
    sale_method, brand_name, no_brand_yn, product_name, internal_name,
    option_yn, product_price, quantity,
    manufacturer, composition_type, certification_type, parallel_import_yn,
    minor_purchase_yn, max_purchase_yn, max_purchase_qty,
    sale_period_yn, sale_start_date, sale_end_date, vat_type,
    detail_type, product_desc,
    shipping_zipcode, shipping_address, shipping_detail_address,
    jeju_shipping_yn, delivery_service_code, delivery_method,
    bundle_shipping_yn, shipping_fee_type, shipping_fee,
    lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
    return_zipcode, return_address, return_detail_address,
    initial_shipping_fee, return_shipping_fee,
    sale_status, created_date, updated_date
) VALUES (
    SEQ_PRODUCT.NEXTVAL, 1, 10301,
    '판매자배송', NULL, 'Y', '남성 반팔 티셔츠', NULL,
    'Y', NULL, NULL,
    NULL, '동일한 상품으로 구성됨', '인증·신고 대상 아님', 'N',
    'Y', 'N', NULL, 'N', NULL, NULL, '과세',
    'HTML 작성', '기본 코튼 소재의 남성 반팔 티셔츠',
    '04524', '서울시 중구 세종대로', NULL,
    'Y', '로젠택배', '일반배송', 'Y', '무료배송', 0,
    '기본 입력', 1, 'Y', '12:00',
    '04524', '서울시 중구 세종대로', NULL, 3000, 3000,
    '판매 중', SYSDATE, SYSDATE
);

-- 상품2: 여성 롱패딩 — 색상 × 사이즈 2개 옵션 / 189,000원 (option_yn='Y')
-- 카테고리: 패션의류/잡화 > 남녀공용의류 > 아우터 (10308, ⚠ 여성 전용 리프가 없어 임시매핑)
INSERT INTO PRODUCT (
    product_no, seller_no, sub_category_no,
    sale_method, brand_name, no_brand_yn, product_name, internal_name,
    option_yn, product_price, quantity,
    manufacturer, composition_type, certification_type, parallel_import_yn,
    minor_purchase_yn, max_purchase_yn, max_purchase_qty,
    sale_period_yn, sale_start_date, sale_end_date, vat_type,
    detail_type, product_desc,
    shipping_zipcode, shipping_address, shipping_detail_address,
    jeju_shipping_yn, delivery_service_code, delivery_method,
    bundle_shipping_yn, shipping_fee_type, shipping_fee,
    lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
    return_zipcode, return_address, return_detail_address,
    initial_shipping_fee, return_shipping_fee,
    sale_status, created_date, updated_date
) VALUES (
    SEQ_PRODUCT.NEXTVAL, 1, 10308,
    '판매자배송', NULL, 'Y', '여성 롱패딩', NULL,
    'Y', NULL, NULL,
    NULL, '동일한 상품으로 구성됨', '인증·신고 대상 아님', 'N',
    'Y', 'N', NULL, 'N', NULL, NULL, '과세',
    'HTML 작성', '방한용 여성 롱패딩, 덕다운 충전재',
    '04524', '서울시 중구 세종대로', NULL,
    'Y', '로젠택배', '일반배송', 'Y', '무료배송', 0,
    '기본 입력', 1, 'Y', '12:00',
    '04524', '서울시 중구 세종대로', NULL, 3000, 3000,
    '판매 중', SYSDATE, SYSDATE
);

-- 상품3: 생수 2L(6입) — 옵션 없는 상품 (option_yn='N') → product_price=4,500원, quantity는 아래 UPDATE로 채움
-- 카테고리: 식품 > 생수/음료 > 생수 (40601)
INSERT INTO PRODUCT (
    product_no, seller_no, sub_category_no,
    sale_method, brand_name, no_brand_yn, product_name, internal_name,
    option_yn, product_price, quantity,
    manufacturer, composition_type, certification_type, parallel_import_yn,
    minor_purchase_yn, max_purchase_yn, max_purchase_qty,
    sale_period_yn, sale_start_date, sale_end_date, vat_type,
    detail_type, product_desc,
    shipping_zipcode, shipping_address, shipping_detail_address,
    jeju_shipping_yn, delivery_service_code, delivery_method,
    bundle_shipping_yn, shipping_fee_type, shipping_fee,
    lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
    return_zipcode, return_address, return_detail_address,
    initial_shipping_fee, return_shipping_fee,
    sale_status, created_date, updated_date
) VALUES (
    SEQ_PRODUCT.NEXTVAL, 5, 40601,
    '판매자배송', NULL, 'Y', '생수 2L(6입)', NULL,
    'N', 4500, 0,
    NULL, '동일한 상품으로 구성됨', '인증·신고 대상 아님', 'N',
    'Y', 'N', NULL, 'N', NULL, NULL, '면세',
    'HTML 작성', '청정 생수 2L 6병 묶음',
    '21554', '인천시 연수구 송도과학로', NULL,
    'Y', '로젠택배', '일반배송', 'Y', '무료배송', 0,
    '기본 입력', 1, 'Y', '12:00',
    '21554', '인천시 연수구 송도과학로', NULL, 3000, 3000,
    '판매 중', SYSDATE, SYSDATE
);

-- 상품4: 남성 운동화 — 색상 × 사이즈 × 소재 3개 옵션 / 일반 89,000원 / 방수 94,000원 (option_yn='Y')
-- 카테고리: 패션의류/잡화 > 남성패션 > 신발 (10202)
INSERT INTO PRODUCT (
    product_no, seller_no, sub_category_no,
    sale_method, brand_name, no_brand_yn, product_name, internal_name,
    option_yn, product_price, quantity,
    manufacturer, composition_type, certification_type, parallel_import_yn,
    minor_purchase_yn, max_purchase_yn, max_purchase_qty,
    sale_period_yn, sale_start_date, sale_end_date, vat_type,
    detail_type, product_desc,
    shipping_zipcode, shipping_address, shipping_detail_address,
    jeju_shipping_yn, delivery_service_code, delivery_method,
    bundle_shipping_yn, shipping_fee_type, shipping_fee,
    lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
    return_zipcode, return_address, return_detail_address,
    initial_shipping_fee, return_shipping_fee,
    sale_status, created_date, updated_date
) VALUES (
    SEQ_PRODUCT.NEXTVAL, 8, 10202,
    '판매자배송', NULL, 'Y', '남성 운동화', NULL,
    'Y', NULL, NULL,
    NULL, '동일한 상품으로 구성됨', '인증·신고 대상 아님', 'N',
    'Y', 'N', NULL, 'N', NULL, NULL, '과세',
    'HTML 작성', '경량 메쉬 소재 남성 운동화',
    '48058', '부산시 해운대구 센텀중앙로', NULL,
    'Y', '로젠택배', '일반배송', 'Y', '무료배송', 0,
    '기본 입력', 1, 'Y', '12:00',
    '48058', '부산시 해운대구 센텀중앙로', NULL, 3000, 3000,
    '판매 중', SYSDATE, SYSDATE
);

-- 상품5: 벌꿀 — 수량 옵션 (option_yn='Y')
-- 카테고리: 식품 > 가루/조미료/오일 > 천연조미료 (41205, ⚠ 꿀 전용 리프가 없어 임시매핑)
INSERT INTO PRODUCT (
    product_no, seller_no, sub_category_no,
    sale_method, brand_name, no_brand_yn, product_name, internal_name,
    option_yn, product_price, quantity,
    manufacturer, composition_type, certification_type, parallel_import_yn,
    minor_purchase_yn, max_purchase_yn, max_purchase_qty,
    sale_period_yn, sale_start_date, sale_end_date, vat_type,
    detail_type, product_desc,
    shipping_zipcode, shipping_address, shipping_detail_address,
    jeju_shipping_yn, delivery_service_code, delivery_method,
    bundle_shipping_yn, shipping_fee_type, shipping_fee,
    lead_time_input_type, lead_time_days, same_day_ship_yn, same_day_cutoff_time,
    return_zipcode, return_address, return_detail_address,
    initial_shipping_fee, return_shipping_fee,
    sale_status, created_date, updated_date
) VALUES (
    SEQ_PRODUCT.NEXTVAL, 5, 41205,
    '판매자배송', NULL, 'Y', '벌꿀', NULL,
    'Y', NULL, NULL,
    NULL, '동일한 상품으로 구성됨', '인증·신고 대상 아님', 'N',
    'Y', 'N', NULL, 'N', NULL, NULL, '면세',
    'HTML 작성', '국내산 아카시아 벌꿀 (1개 기준가)',
    '21554', '인천시 연수구 송도과학로', NULL,
    'Y', '로젠택배', '일반배송', 'Y', '무료배송', 0,
    '기본 입력', 1, 'Y', '12:00',
    '21554', '인천시 연수구 송도과학로', NULL, 3000, 3000,
    '판매 중', SYSDATE, SYSDATE
);


-- PRODUCT_OPTION 컬럼순서:
-- OPTION_ID, product_no, OPTION1_TYPE, OPTION1_VALUE, OPTION2_TYPE, OPTION2_VALUE, OPTION3_TYPE, OPTION3_VALUE,
-- normal_price, PRICE, auto_price_adjust_yn, quantity, seller_product_code, model_no, barcode, STATUS

-- 상품1: 사이즈 단일 옵션 / 29,000원
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 1, '사이즈','S',  NULL,NULL, NULL,NULL, NULL, 29000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 1, '사이즈','M',  NULL,NULL, NULL,NULL, NULL, 29000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 1, '사이즈','L',  NULL,NULL, NULL,NULL, NULL, 29000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 1, '사이즈','XL', NULL,NULL, NULL,NULL, NULL, 29000, 'N', 0, NULL, NULL, NULL, 'N');

-- 상품2: 색상 × 사이즈 2개 옵션 / 189,000원
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 2, '색상','블랙',   '사이즈','S', NULL,NULL, NULL, 189000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 2, '색상','블랙',   '사이즈','M', NULL,NULL, NULL, 189000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 2, '색상','블랙',   '사이즈','L', NULL,NULL, NULL, 189000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 2, '색상','베이지', '사이즈','S', NULL,NULL, NULL, 189000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 2, '색상','베이지', '사이즈','M', NULL,NULL, NULL, 189000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 2, '색상','베이지', '사이즈','L', NULL,NULL, NULL, 189000, 'N', 0, NULL, NULL, NULL, 'N');

-- 상품3(생수): option_yn='N'이라 PRODUCT_OPTION 행 없음 (product_price=4,500원을 그대로 사용)

-- 상품4: 색상 × 사이즈 × 소재 3개 옵션 / 일반 89,000원 / 방수 94,000원
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','화이트', '사이즈','260', '소재','일반', NULL, 89000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','화이트', '사이즈','260', '소재','방수', NULL, 94000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','화이트', '사이즈','270', '소재','일반', NULL, 89000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','화이트', '사이즈','270', '소재','방수', NULL, 94000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','블랙',   '사이즈','260', '소재','일반', NULL, 89000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','블랙',   '사이즈','260', '소재','방수', NULL, 94000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','블랙',   '사이즈','270', '소재','일반', NULL, 89000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 4, '색상','블랙',   '사이즈','270', '소재','방수', NULL, 94000, 'N', 0, NULL, NULL, NULL, 'N');

-- 상품5(벌꿀): 수량 옵션
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 5, '수량','1개', NULL,NULL, NULL,NULL, NULL, 15000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 5, '수량','2개', NULL,NULL, NULL,NULL, NULL, 28000, 'N', 0, NULL, NULL, NULL, 'N');
INSERT INTO PRODUCT_OPTION VALUES (SEQ_OPTION.NEXTVAL, 5, '수량','5개', NULL,NULL, NULL,NULL, NULL, 65000, 'N', 0, NULL, NULL, NULL, 'N');


-- 상품1 초기 입고 (OPTION_ID 1~4)
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 50, SYSDATE, 1); -- S: 50
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 30, SYSDATE, 2); -- M: 30
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 10, SYSDATE, 3); -- L: 10
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 20, SYSDATE, 4); -- XL: 20
-- → TRIGGER: OPTION 1~4 QUANTITY = 50/30/10/20, STATUS = 'Y'

-- 상품1 L사이즈 2차 입고
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 5, SYSDATE, 3); -- L: +5 → 15
-- → TRIGGER: OPTION 3 QUANTITY = 15, STATUS = 'Y'

-- 상품2 초기 입고 (OPTION_ID 5~10)
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 20, SYSDATE, 5);  -- 블랙S: 20
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 15, SYSDATE, 6);  -- 블랙M: 15
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 10, SYSDATE, 7);  -- 블랙L: 10
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 8,  SYSDATE, 8);  -- 베이지S: 8
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 5,  SYSDATE, 9);  -- 베이지M: 5
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 10, SYSDATE, 10); -- 베이지L: 10
-- → TRIGGER: OPTION 5~10 QUANTITY = 20/15/10/8/5/10, STATUS = 'Y'

-- 상품3(생수)은 옵션이 없어서 PRODUCT_IN/트리거 대신 PRODUCT.quantity를 직접 채움
UPDATE PRODUCT SET quantity = 200 WHERE product_no = 3; -- 생수: 200

-- 상품4 초기 입고 (OPTION_ID 11~18, 상품3 더미옵션이 빠지면서 12~19 → 11~18로 한 칸 당겨짐)
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 15, SYSDATE, 11); -- 화이트260일반: 15
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 10, SYSDATE, 12); -- 화이트260방수: 10
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 8,  SYSDATE, 13); -- 화이트270일반: 8
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 10, SYSDATE, 14); -- 화이트270방수: 10
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 20, SYSDATE, 15); -- 블랙260일반: 20
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 12, SYSDATE, 16); -- 블랙260방수: 12
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 5,  SYSDATE, 17); -- 블랙270일반: 5
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 10, SYSDATE, 18); -- 블랙270방수: 10
-- → TRIGGER: OPTION 11~18 QUANTITY 자동 반영, STATUS = 'Y'

-- 상품5(벌꿀) 입고 (OPTION_ID 19~21, 20~22 → 19~21로 한 칸 당겨짐)
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 100, SYSDATE, 19); -- 1개 묶음: 100
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 50,  SYSDATE, 20); -- 2개 묶음: 50
INSERT INTO PRODUCT_IN VALUES (SEQ_PRODUCT_IN.NEXTVAL, 30,  SYSDATE, 21); -- 5개 묶음: 30


COMMIT;


-- =========================================================
-- 실행 후 확인해볼 만한 쿼리 (참고용 — 자리표시자 없이 바로 실행 가능)
-- =========================================================
-- SELECT * FROM PRODUCT ORDER BY product_no;
-- SELECT * FROM PRODUCT_OPTION ORDER BY product_no, OPTION_ID;
-- SELECT * FROM PRODUCT WHERE product_no = 3; -- quantity=200 확인
-- EXEC UP_PRODUCT_IN(1, 10); -- 상품1 S사이즈 추가 입고 예시
