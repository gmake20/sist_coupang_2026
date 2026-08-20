<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>

    <meta charset="UTF-8">

    <title>주문상세</title>


    <!-- 기본 초기화 CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/reset.css">


    <!-- 공통 CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">


    <!-- 주문상세 전용 CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/order_detail.css">


    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>


    <!-- 주문상세 전용 JS -->
    <script src="${pageContext.request.contextPath}/js/order_detail.js"></script>

</head>

<body>
 <jsp:include page="/inc/header.jsp" /> 
<div class="order-detail-wrap">

    <!-- =========================
         왼쪽 MY쿠팡 메뉴
    ========================== -->
    <aside class="mycoupang-side">

        <div class="side-title">
            MY쿠팡
        </div>

        <div class="side-section">
            <h3>MY 쇼핑</h3>

            <a href="#" class="active">주문목록/배송조회</a>
            <a href="#">취소/반품/교환/환불 내역</a>
            <a href="#">와우 멤버십</a>
            <a href="#">구독 서비스 <span class="new">N</span></a>
            <a href="#">로켓프레시 프레시백 <span class="new">N</span></a>
            <a href="#">영수증 조회/출력</a>
        </div>

        <div class="side-section">
            <h3>MY 혜택</h3>

            <a href="#">쿠폰 · 이용권</a>
            <a href="#">쿠팡캐시/기프트카드</a>
        </div>

        <div class="side-section">
            <h3>MY 활동</h3>

            <a href="#">문의하기</a>
            <a href="#">문의내역 확인</a>
            <a href="#">리뷰관리</a>
            <a href="#">찜 리스트</a>
        </div>

        <div class="side-section">
            <h3>MY 정보</h3>

            <a href="#">개인정보확인/수정</a>
            <a href="#">결제수단·쿠페이 관리</a>
            <a href="#">배송지 관리</a>
            <a href="#">패스키 관리</a>
            <a href="#">회원 탈퇴</a>
        </div>


        <!-- 고객센터 메뉴 -->
        <div class="side-help">

            <a href="#">
                <span class="help-icon">📝</span>
                <span>쿠팡문의</span>
            </a>

            <a href="#">
                <span class="help-icon">📢</span>
                <span>
                    고객의 소리<br>
                    <small>제안·칭찬·불편신고</small>
                </span>
            </a>

            <a href="#">
                <span class="help-icon">📦</span>
                <span>취소/반품 안내</span>
            </a>

        </div>

    </aside>


    <!-- =========================
         가운데 본문
    ========================== -->
    <main class="order-content">

        <!-- 주문 상세 제목 -->
        <section class="order-header">

            <h1>주문상세</h1>

            <div class="order-info">
                <strong>2026. 8. 12</strong>
                주문
                <span class="dot">·</span>
                주문번호
                <span class="order-number">5102232456789</span>
            </div>

        </section>


        <!-- =========================
             배송 상품
        ========================== -->
        <section class="delivery-box">

            <div class="delivery-main">

                <div class="delivery-title">
                    배송완료
                    <span>·</span>
                    <strong>8/13(목) 도착</strong>
                </div>

                <div class="product-row">

                    <!-- 상품 이미지 -->
                    <div class="product-image">
                        <div class="clothes-icon">
                            👕
                        </div>
                    </div>


                    <!-- 상품 정보 -->
                    <div class="product-info">

                        <div class="product-name">

                            <span class="rocket">🚀 로켓배송</span>

                            남녀공용 오버핏 레터링 반팔 티셔츠
                            (블루)

                        </div>

                        <div class="product-price">
                            19,800 원
                            <span>·</span>
                            1개
                        </div>

                        <div class="product-option">
                            사이즈: L
                            <span>/</span>
                            색상: 블루
                        </div>

                    </div>


                    <button type="button"
                            class="cart-btn"
                            id="cartBtn">
                        장바구니 담기
                    </button>

                </div>

            </div>


            <!-- 오른쪽 버튼 -->
            <div class="delivery-buttons">

                <button type="button"
                        class="delivery-btn primary"
                        id="deliveryBtn">
                    배송 조회
                </button>

                <button type="button"
                        class="delivery-btn"
                        id="exchangeBtn">
                    교환, 반품 신청
                </button>

                <button type="button"
                        class="delivery-btn"
                        id="reviewBtn">
                    리뷰 작성하기
                </button>

            </div>

        </section>



        <!-- =========================
             받는사람 정보
        ========================== -->
        <section class="detail-section">

            <h2>받는사람 정보</h2>

            <div class="section-line"></div>

            <div class="info-table">

                <div class="info-row">
                    <div class="info-title">받는사람</div>
                    <div class="info-value">김지훈</div>
                </div>

                <div class="info-row">
                    <div class="info-title">연락처</div>
                    <div class="info-value">010-2486-5319</div>
                </div>

                <div class="info-row">
                    <div class="info-title">받는주소</div>
                    <div class="info-value">
                        (06236) 서울특별시 강남구 테헤란로 123,
                        4층 401호
                    </div>
                </div>

                <div class="info-row">
                    <div class="info-title">배송요청사항</div>
                    <div class="info-value">
                        문 앞에 놓아주세요.
                    </div>
                </div>

            </div>

        </section>



        <!-- =========================
             결제 정보
        ========================== -->
        <section class="detail-section payment-section">

            <h2>결제 정보</h2>

            <div class="section-line"></div>

            <div class="payment-box">

                <div class="payment-method">
                    비씨카드&nbsp; / &nbsp;일시불
                </div>

                <div class="payment-price">

                    <div class="price-row">
                        <span>총 상품가격</span>
                        <strong>19,800 원</strong>
                    </div>

                    <div class="price-row">
                        <span>배송비</span>
                        <strong>0 원</strong>
                    </div>

                </div>

            </div>


            <div class="payment-total">

                <div>
                    비씨카드&nbsp; / &nbsp;일시불
                </div>

                <div>
                    <span>총 결제금액</span>
                    <strong>19,800 원</strong>
                </div>

            </div>

        </section>



        <!-- =========================
             결제영수증 정보
        ========================== -->
        <section class="detail-section receipt-section">

            <h2>결제영수증 정보</h2>

            <div class="section-line"></div>


            <div class="receipt-row">

                <span>
                    해당 주문건에 대해 구매 카드영수증 확인이 가능합니다.
                </span>

                <button type="button"
                        class="receipt-btn"
                        id="cardReceiptBtn">
                    카드영수증
                </button>

            </div>


            <div class="receipt-row">

                <span>
                    해당 주문건에 대해 거래명세서 확인이 가능합니다.
                </span>

                <button type="button"
                        class="receipt-btn"
                        id="statementBtn">
                    거래명세서
                </button>

            </div>

        </section>

    </main>



    <!-- =========================
         오른쪽 광고 영역
    ========================== -->
    <aside class="right-banner">

        <div class="banner banner-1">
            <strong>쿠팡 only</strong>
            <span>›</span>
            <div class="banner-product">
                📦
            </div>
        </div>


        <div class="banner banner-2">
            <strong>~5만원<br>쿠폰 할인</strong>
            <span>›</span>
            <div class="banner-character">
                🏝️
            </div>
        </div>


        <div class="banner banner-3">
            <strong>쿠팡이 직접<br>
                <em>수입</em>했어요!
            </strong>

            <span>›</span>

            <div class="banner-product">
                🧴
            </div>
        </div>


        <div class="banner banner-4">
            <strong>
                금주의<br>
                특가왕
            </strong>

            <span>›</span>

            <div class="banner-bell">
                🔔
            </div>

            <b class="badge">1</b>
        </div>


        <div class="banner banner-5">
            <strong>
                쿠팡에서<br>
                판매 시작하기
            </strong>

            <span>›</span>

            <div class="banner-shop">
                🛍️
            </div>
        </div>


        <div class="side-mini-menu">

            <div>
                장바구니
                <strong>2</strong>
            </div>

            <div>
                최근본상품
                <strong>15</strong>
            </div>

        </div>


        <div class="recent-product">
            👟
        </div>

    </aside>

</div>
<jsp:include page="/inc/footer.jsp" />

</body>
</html>