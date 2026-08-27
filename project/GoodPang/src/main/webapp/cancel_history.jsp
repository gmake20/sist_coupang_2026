<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">

<title>취소/반품/교환/환불 내역</title>

<link rel="stylesheet" href="css/reset.css"> 

<style>

* {
    box-sizing: border-box;
}

body {
    margin: 0;
    font-family: Arial, "Malgun Gothic", sans-serif;
    color: #111;
    background: #fff;
}

a {
    text-decoration: none;
    color: inherit;
}


/* =========================
   HEADER 자리
   ========================= */
.header{


}

/* =========================
   전체 영역
   ========================= */

.page-wrap {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    min-height: 750px;
}


/* =========================
   왼쪽 MY쿠팡 메뉴
   ========================= */

.side-menu {
    width: 180px;
    flex-shrink: 0;
    border: 1px solid #ddd;
    margin-top: 20px;
    margin-bottom: 30px;
}

.side-title {
    height: 54px;
    background: #2878c8;
    color: #fff;

    display: flex;
    align-items: center;
    padding-left: 20px;

    font-size: 18px;
    font-weight: bold;
}

.side-section {
    padding: 20px 15px;
    border-bottom: 1px solid #ddd;
}

.side-section h3 {
    margin: 0 0 12px;
    font-size: 14px;
}

.side-section a {
    display: block;
    margin-bottom: 8px;
    font-size: 13px;
    line-height: 1.3;
}

.side-section a:hover {
    color: #2878c8;
}

.side-section a.active {
    color: #2878c8;
    font-weight: bold;
}


/* =========================
   오른쪽 본문
   ========================= */

.content {
    width: 920px;
    padding: 50px 25px 80px;
}

.content h1 {
    margin: 0 0 25px;
    font-size: 25px;
    font-weight: 700;
}


/* =========================
   탭
   ========================= */

.tab-menu {
    width: 100%;
    display: flex;
    border-bottom: 1px solid #aaa;
    margin-bottom: 25px;
}

.tab-menu a {
    width: 50%;
    height: 48px;

    display: flex;
    align-items: center;
    justify-content: center;

    border: 1px solid #ccc;
    border-bottom: none;

    font-size: 15px;
}

.tab-menu a + a {
    border-left: none;
}

.tab-menu a.active {
    color: #2878c8;
    border: 1px solid #2878c8;
    border-bottom: 1px solid #fff;
    font-weight: bold;
}


/* =========================
   안내 문구
   ========================= */

.info-area {
    margin-bottom: 20px;
    font-size: 12px;
    color: #666;
    line-height: 1.8;
}

.info-area span {
    color: #2878c8;
}


/* =========================
   주문 하나
   ========================= */

.order-box {
    border: 1px solid #d5d5d5;
    margin-bottom: 15px;
}


/* 주문 상단 */

.order-header {
    height: 42px;
    padding: 0 15px;

    display: flex;
    align-items: center;

    background: #fafafa;
    border-bottom: 1px solid #ddd;

    font-size: 13px;
}

.order-header span {
    margin-right: 12px;
}

.order-header .bar {
    color: #ccc;
    margin-right: 12px;
}


/* 주문 상품 영역 */

.order-body {
    min-height: 105px;
    display: flex;
}


/* 상품 정보 */

.product-info {
    width: 55%;
    padding: 20px;
    display: flex;
    align-items: center;
}

.product-img {
    width: 75px;
    height: 75px;

    margin-right: 15px;

    background: #f5f5f5;

    display: flex;
    align-items: center;
    justify-content: center;

    overflow: hidden;
}

.product-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.product-text {
    flex: 1;
}

.product-name {
    margin: 0 0 8px;
    font-size: 14px;
    font-weight: bold;
}

.product-option {
    margin: 0;
    color: #888;
    font-size: 12px;
    line-height: 1.5;
}


/* 수량 / 금액 */

.product-price {
    width: 15%;
    border-left: 1px solid #ddd;
    border-right: 1px solid #ddd;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    font-size: 13px;
}

.product-price strong {
    margin-top: 8px;
    font-size: 14px;
}


/* 취소 상태 */

.cancel-status {
    width: 30%;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    text-align: center;
}

.cancel-status strong {
    color: #222;
    font-size: 14px;
    margin-bottom: 7px;
}

.cancel-status p {
    margin: 0 0 10px;
    color: #777;
    font-size: 11px;
    line-height: 1.5;
}


/* 상세 버튼 */

.detail-btn {
    padding: 6px 12px;

    border: 1px solid #2878c8;
    background: #fff;
    color: #2878c8;

    font-size: 11px;
    cursor: pointer;
}

.detail-btn:hover {
    background: #2878c8;
    color: #fff;
}


/* =========================
   페이징
   ========================= */

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;

    margin-top: 35px;
}

.pagination a {
    width: 32px;
    height: 32px;

    display: flex;
    align-items: center;
    justify-content: center;

    border: 1px solid #ddd;

    margin: 0 2px;

    font-size: 12px;
}

.pagination a.active {
    background: #2878c8;
    border-color: #2878c8;
    color: #fff;
}


/* =========================
   FOOTER 자리
   ========================= */

footer {
    /* 기존 footer include 넣을 자리 */
}

</style>

</head>

<body>


<!-- =========================
     HEADER
     ========================= -->

<header>
   <jsp:include page="/inc/header.jsp" />
</header>


<!-- =========================
     본문
     ========================= -->

<div class="page-wrap">


    <!-- 왼쪽 메뉴 -->

    <aside class="side-menu">

        <div class="side-title">
            MY쿠팡
        </div>


        <div class="side-section">

            <h3>MY 쇼핑</h3>

            <a href="${pageContext.request.contextPath}/order/order_list">
                주문목록/배송조회
            </a>

            <a href="${pageContext.request.contextPath}/cancel_history.jsp"
               class="active">
                취소/반품/교환/환불 내역
            </a>

            <a href="#">
                정기배송 관리
            </a>

            <a href="#">
                영수증 조회/출력
            </a>

        </div>


        <div class="side-section">

            <h3>MY 혜택</h3>

            <a href="#">쿠폰</a>
            <a href="#">쿠팡캐시</a>

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
            <a href="#">결제수단 관리</a>
            <a href="#">배송지 관리</a>
            <a href="#">회원 탈퇴</a>

        </div>

    </aside>



    <!-- =========================
         본문
         ========================= -->

    <main class="content">

        <h1>취소/반품/교환/환불 내역</h1>


        <!-- 탭 -->

        <div class="tab-menu">

            <a href="${pageContext.request.contextPath}/order_list.jsp">
                주문목록/배송조회
            </a>

            <a href="${pageContext.request.contextPath}/cancel_history.jsp"
               class="active">
                취소/반품/교환
            </a>

        </div>


        <!-- 안내 -->

        <div class="info-area">

            <div>
                - 취소/반품/교환 신청한 내역을 확인할 수 있습니다.
            </div>

            <div>
                - 상품에 대한 자세한 문의는
                <span>고객센터</span>를 이용해주세요.
            </div>

        </div>



        <!-- =========================
             주문 1
             ========================= -->

        <div class="order-box">

            <div class="order-header">

                <span>취소접수일 : 2026/08/18</span>

                <span class="bar">|</span>

                <span>주문일 : 2026/08/17</span>

                <span class="bar">|</span>

                <span>주문번호 : 2026081712847392</span>

            </div>


            <div class="order-body">


                <div class="product-info">

                    <div class="product-img">

                        <img src="${pageContext.request.contextPath}/images/clothes1.jpg"
                             alt="여성 니트">

                    </div>


                    <div class="product-text">

                        <p class="product-name">
                            여성 데일리 라운드 니트
                        </p>

                        <p class="product-option">
                            아이보리 / FREE / 1개
                        </p>

                    </div>

                </div>


                <div class="product-price">

                    <span>1개</span>

                    <strong>29,900원</strong>

                </div>


                <div class="cancel-status">

                    <strong>취소완료</strong>

                    <p>
                        8/18(화) 취소 완료
                    </p>

                    <button type="button"
                            class="detail-btn">
                        취소상세
                    </button>

                </div>

            </div>

        </div>



        <!-- =========================
             주문 2
             ========================= -->

        <div class="order-box">

            <div class="order-header">

                <span>취소접수일 : 2026/08/12</span>

                <span class="bar">|</span>

                <span>주문일 : 2026/08/11</span>

                <span class="bar">|</span>

                <span>주문번호 : 2026081119375026</span>

            </div>


            <div class="order-body">


                <div class="product-info">

                    <div class="product-img">

                        <img src="${pageContext.request.contextPath}/images/clothes2.jpg"
                             alt="남성 셔츠">

                    </div>


                    <div class="product-text">

                        <p class="product-name">
                            남성 오버핏 스트라이프 셔츠
                        </p>

                        <p class="product-option">
                            블루 / L / 1개
                        </p>

                    </div>

                </div>


                <div class="product-price">

                    <span>1개</span>

                    <strong>34,500원</strong>

                </div>


                <div class="cancel-status">

                    <strong>취소완료</strong>

                    <p>
                        8/12(수) 취소 완료
                    </p>

                    <button type="button"
                            class="detail-btn">
                        취소상세
                    </button>

                </div>

            </div>

        </div>



        <!-- =========================
             주문 3
             ========================= -->

        <div class="order-box">

            <div class="order-header">

                <span>취소접수일 : 2026/08/05</span>

                <span class="bar">|</span>

                <span>주문일 : 2026/08/04</span>

                <span class="bar">|</span>

                <span>주문번호 : 2026080415629084</span>

            </div>


            <div class="order-body">


                <div class="product-info">

                    <div class="product-img">

                        <img src="${pageContext.request.contextPath}/images/clothes3.jpg"
                             alt="여성 청바지">

                    </div>


                    <div class="product-text">

                        <p class="product-name">
                            여성 스트레이트 데님 팬츠
                        </p>

                        <p class="product-option">
                            중청 / M / 1개
                        </p>

                    </div>

                </div>


                <div class="product-price">

                    <span>1개</span>

                    <strong>42,000원</strong>

                </div>


                <div class="cancel-status">

                    <strong>취소완료</strong>

                    <p>
                        8/05(수) 취소 완료
                    </p>

                    <button type="button"
                            class="detail-btn">
                        취소상세
                    </button>

                </div>

            </div>

        </div>



        <!-- =========================
             주문 4
             ========================= -->

        <div class="order-box">

            <div class="order-header">

                <span>취소접수일 : 2026/07/28</span>

                <span class="bar">|</span>

                <span>주문일 : 2026/07/27</span>

                <span class="bar">|</span>

                <span>주문번호 : 2026072718473165</span>

            </div>


            <div class="order-body">


                <div class="product-info">

                    <div class="product-img">

                        <img src="${pageContext.request.contextPath}/images/clothes4.jpg"
                             alt="여성 린넨 원피스">

                    </div>


                    <div class="product-text">

                        <p class="product-name">
                            여성 여름 린넨 셔츠 원피스
                        </p>

                        <p class="product-option">
                            베이지 / FREE / 1개
                        </p>

                    </div>

                </div>


                <div class="product-price">

                    <span>1개</span>

                    <strong>51,900원</strong>

                </div>


                <div class="cancel-status">

                    <strong>취소완료</strong>

                    <p>
                        7/28(화) 취소 완료
                    </p>

                    <button type="button"
                            class="detail-btn">
                        취소상세
                    </button>

                </div>

            </div>

        </div>



        <!-- =========================
             페이지
             ========================= -->

        <div class="pagination">

            <a href="#">&lt;</a>
            <a href="#" class="active">1</a>
            <a href="#">2</a>
            <a href="#">3</a>
            <a href="#">4</a>
            <a href="#">5</a>
            <a href="#">&gt;</a>

        </div>

    </main>

</div>



<!-- =========================
     FOOTER
     ========================= -->
<footer>
<jsp:include page="/inc/footer.jsp" />
</footer>

</body>
</html>