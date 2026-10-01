-- =====================================================================
-- 팀원1 시드 데이터 (개발/시연용)
--   schema-team1.sql 실행 후 실행
--
-- 고객 계정 (/login)
--   user1  / User1234!   : 본인확인 완료 → 2주차부터 계좌/이체 개발에 사용
--   user2  / User1234!   : 본인확인 전  → 첫 계좌 개설(본인확인 신청) 흐름 테스트
--   user3  / User1234!   : 본인확인 심사중 → 직원 심사 화면 테스트
--     ※ user3의 개설대기(PENDING) 계좌는 팀원2의 ACCOUNT 시드에서 함께 만들어야 함
--
-- 직원 계정 (/admin/login) - 비밀번호 모두 Staff1234!, 개발 편의상 비밀번호 변경 강제 해제
--   E0001 : SYSTEM_ADMIN (본점)     - 직원/지점/로그 관리
--   E0002 : MANAGER      (강남지점) - 승인 업무 (동결, 한도, 대출, 상품, 카드)
--   E0003 : STAFF        (강남지점) - 조회, 본인확인 심사, 고객센터
-- =====================================================================

-- ---------------------------------------------------------------------
-- BRANCH (모의 데이터 - 좌표는 지역 대표 위치, 실제 은행 지점 아님)
--   EMPLOYEE가 참조하므로 가장 먼저 입력
-- ---------------------------------------------------------------------
INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'BRANCH', '모의은행 본점', '서울특별시 중구 을지로 일대', 37.5660000, 126.9910000, '0215880000', '평일 09:00~16:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'BRANCH', '모의은행 시청지점', '서울특별시 중구 세종대로 일대', 37.5663000, 126.9779000, '0215880001', '평일 09:00~16:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'BRANCH', '모의은행 강남지점', '서울특별시 강남구 강남대로 일대', 37.4979000, 127.0276000, '0215880002', '평일 09:00~16:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'BRANCH', '모의은행 여의도지점', '서울특별시 영등포구 여의대로 일대', 37.5219000, 126.9245000, '0215880003', '평일 09:00~16:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'BRANCH', '모의은행 판교지점', '경기도 성남시 분당구 판교역로 일대', 37.3948000, 127.1112000, '0315880004', '평일 09:00~16:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'BRANCH', '모의은행 서면지점', '부산광역시 부산진구 중앙대로 일대', 35.1578000, 129.0600000, '0515880005', '평일 09:00~16:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'ATM', '모의은행 강남역 ATM', '서울특별시 강남구 강남대로 일대 (역사 내)', 37.4981000, 127.0279000, NULL, '24시간', 'Y');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'ATM', '모의은행 홍대입구역 ATM', '서울특별시 마포구 양화로 일대 (역사 내)', 37.5572000, 126.9245000, NULL, '24시간', 'Y');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'ATM', '모의은행 잠실 ATM', '서울특별시 송파구 올림픽로 일대', 37.5133000, 127.1001000, NULL, '07:00~23:00', 'N');

INSERT INTO BRANCH (BRANCH_ID, BRANCH_TYPE, NAME, ADDRESS, LATITUDE, LONGITUDE, PHONE, BUSINESS_HOURS, OPEN_24H_YN)
VALUES (SEQ_BRANCH.NEXTVAL, 'ATM', '모의은행 종로 ATM', '서울특별시 종로구 종로 일대', 37.5704000, 126.9920000, NULL, '24시간', 'Y');

-- ---------------------------------------------------------------------
-- BRANCH_SERVICE (이용 가능 업무)
-- ---------------------------------------------------------------------
-- 모든 지점: 입금, 출금, 이체, 계좌개설, 카드발급
INSERT INTO BRANCH_SERVICE (BRANCH_ID, SERVICE_CODE)
SELECT b.BRANCH_ID, s.CODE
FROM BRANCH b
CROSS JOIN (SELECT 'DEPOSIT' AS CODE FROM DUAL UNION ALL
            SELECT 'WITHDRAW' FROM DUAL UNION ALL
            SELECT 'TRANSFER' FROM DUAL UNION ALL
            SELECT 'ACCOUNT_OPENING' FROM DUAL UNION ALL
            SELECT 'CARD_ISSUANCE' FROM DUAL) s
WHERE b.BRANCH_TYPE = 'BRANCH';

-- 대출상담: 판교지점 제외
INSERT INTO BRANCH_SERVICE (BRANCH_ID, SERVICE_CODE)
SELECT BRANCH_ID, 'LOAN_CONSULTING' FROM BRANCH
WHERE BRANCH_TYPE = 'BRANCH' AND NAME <> '모의은행 판교지점';

-- ATM: 입금, 출금, 이체, 잔액조회 (잠실 ATM 제외)
INSERT INTO BRANCH_SERVICE (BRANCH_ID, SERVICE_CODE)
SELECT b.BRANCH_ID, s.CODE
FROM BRANCH b
CROSS JOIN (SELECT 'DEPOSIT' AS CODE FROM DUAL UNION ALL
            SELECT 'WITHDRAW' FROM DUAL UNION ALL
            SELECT 'TRANSFER' FROM DUAL UNION ALL
            SELECT 'BALANCE_INQUIRY' FROM DUAL) s
WHERE b.BRANCH_TYPE = 'ATM' AND b.NAME <> '모의은행 잠실 ATM';

-- 잠실 ATM: 출금, 잔액조회만 (출금 전용 기기)
INSERT INTO BRANCH_SERVICE (BRANCH_ID, SERVICE_CODE)
SELECT b.BRANCH_ID, s.CODE
FROM BRANCH b
CROSS JOIN (SELECT 'WITHDRAW' AS CODE FROM DUAL UNION ALL
            SELECT 'BALANCE_INQUIRY' FROM DUAL) s
WHERE b.NAME = '모의은행 잠실 ATM';

-- ---------------------------------------------------------------------
-- EMPLOYEE (비밀번호: Staff1234!)
-- ---------------------------------------------------------------------
INSERT INTO EMPLOYEE (EMPLOYEE_ID, EMPLOYEE_NO, PASSWORD, NAME, EMAIL, PHONE, BRANCH_ID, POSITION,
                      ROLE, STATUS, TEMP_PW_YN, HIRED_AT)
VALUES (SEQ_EMPLOYEE.NEXTVAL, 'E0001',
        '$2a$10$tjbLDI3KlIrVr/Flis8YAeMfn7vJRFIPin7Gdl6anU3okK/SmnUau',
        '최시스템', 'e0001@mockbank.local', '0215880100',
        (SELECT BRANCH_ID FROM BRANCH WHERE NAME = '모의은행 본점'), '차장',
        'SYSTEM_ADMIN', 'ACTIVE', 'N', DATE '2015-03-02');

INSERT INTO EMPLOYEE (EMPLOYEE_ID, EMPLOYEE_NO, PASSWORD, NAME, EMAIL, PHONE, BRANCH_ID, POSITION,
                      ROLE, STATUS, TEMP_PW_YN, HIRED_AT)
VALUES (SEQ_EMPLOYEE.NEXTVAL, 'E0002',
        '$2a$10$tjbLDI3KlIrVr/Flis8YAeMfn7vJRFIPin7Gdl6anU3okK/SmnUau',
        '정책임', 'e0002@mockbank.local', '0215880201',
        (SELECT BRANCH_ID FROM BRANCH WHERE NAME = '모의은행 강남지점'), '과장',
        'MANAGER', 'ACTIVE', 'N', DATE '2017-07-03');

INSERT INTO EMPLOYEE (EMPLOYEE_ID, EMPLOYEE_NO, PASSWORD, NAME, EMAIL, PHONE, BRANCH_ID, POSITION,
                      ROLE, STATUS, TEMP_PW_YN, HIRED_AT)
VALUES (SEQ_EMPLOYEE.NEXTVAL, 'E0003',
        '$2a$10$tjbLDI3KlIrVr/Flis8YAeMfn7vJRFIPin7Gdl6anU3okK/SmnUau',
        '한창구', 'e0003@mockbank.local', '0215880202',
        (SELECT BRANCH_ID FROM BRANCH WHERE NAME = '모의은행 강남지점'), '대리',
        'STAFF', 'ACTIVE', 'N', DATE '2021-01-04');

-- ---------------------------------------------------------------------
-- MEMBER (비밀번호: User1234!)
-- ---------------------------------------------------------------------
INSERT INTO MEMBER (MEMBER_ID, LOGIN_ID, PASSWORD, NAME, EMAIL, PHONE, BIRTH_DATE,
                    STATUS, VERIFICATION_STATUS, TERMS_AGREED_AT, MARKETING_AGREE_YN)
VALUES (SEQ_MEMBER.NEXTVAL, 'user1',
        '$2a$10$n3/sWtwUkO4SdI0SMtxz7.EFq5ajWIuzj8xS9Cm0YjWUXxlUzzIai',
        '김테스트', 'user1@mockbank.local', '01011111111', DATE '1995-03-15',
        'ACTIVE', 'VERIFIED', SYSTIMESTAMP, 'Y');

INSERT INTO MEMBER (MEMBER_ID, LOGIN_ID, PASSWORD, NAME, EMAIL, PHONE, BIRTH_DATE,
                    STATUS, VERIFICATION_STATUS, TERMS_AGREED_AT)
VALUES (SEQ_MEMBER.NEXTVAL, 'user2',
        '$2a$10$n3/sWtwUkO4SdI0SMtxz7.EFq5ajWIuzj8xS9Cm0YjWUXxlUzzIai',
        '이신규', 'user2@mockbank.local', '01022222222', DATE '1998-07-20',
        'ACTIVE', 'NONE', SYSTIMESTAMP);

INSERT INTO MEMBER (MEMBER_ID, LOGIN_ID, PASSWORD, NAME, EMAIL, PHONE, BIRTH_DATE,
                    STATUS, VERIFICATION_STATUS, TERMS_AGREED_AT)
VALUES (SEQ_MEMBER.NEXTVAL, 'user3',
        '$2a$10$n3/sWtwUkO4SdI0SMtxz7.EFq5ajWIuzj8xS9Cm0YjWUXxlUzzIai',
        '박심사', 'user3@mockbank.local', '01033333333', DATE '2000-11-02',
        'ACTIVE', 'PENDING', SYSTIMESTAMP);

-- ---------------------------------------------------------------------
-- IDENTITY_VERIFICATION
-- ---------------------------------------------------------------------
-- user1: 승인 완료 이력 (STAFF E0003이 심사)
INSERT INTO IDENTITY_VERIFICATION (VERIFICATION_ID, MEMBER_ID, ID_CARD_TYPE, ID_ISSUE_DATE,
                                   AUTH_BANK_NAME, AUTH_ACCOUNT_MASKED, STATUS,
                                   REQUESTED_AT, REVIEWED_EMPLOYEE_ID, REVIEWED_AT)
VALUES (SEQ_IDENTITY_VERIFICATION.NEXTVAL,
        (SELECT MEMBER_ID FROM MEMBER WHERE LOGIN_ID = 'user1'),
        'RESIDENT_CARD', DATE '2018-05-10', '가상은행', '110-***-**1234', 'APPROVED',
        SYSTIMESTAMP - INTERVAL '2' DAY,
        (SELECT EMPLOYEE_ID FROM EMPLOYEE WHERE EMPLOYEE_NO = 'E0003'),
        SYSTIMESTAMP - INTERVAL '1' DAY);

-- user3: 심사 대기
INSERT INTO IDENTITY_VERIFICATION (VERIFICATION_ID, MEMBER_ID, ID_CARD_TYPE, ID_ISSUE_DATE,
                                   AUTH_BANK_NAME, AUTH_ACCOUNT_MASKED, STATUS, REQUESTED_AT)
VALUES (SEQ_IDENTITY_VERIFICATION.NEXTVAL,
        (SELECT MEMBER_ID FROM MEMBER WHERE LOGIN_ID = 'user3'),
        'DRIVER_LICENSE', DATE '2021-02-01', '샘플은행', '333-***-**5678', 'PENDING',
        SYSTIMESTAMP);

COMMIT;
