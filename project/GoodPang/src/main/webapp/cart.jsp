<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>GoodPang | 장바구니</title>
    <!-- 상대경로로 CSS 연결 -->
    <link rel="stylesheet" href="css/cart.css">
</head>
<body>
    <!-- 장바구니 헤더 -->
    <header class="cart-header">
        <div class="header-inner">
            <h1 class="logo">
                <a href="index.html" title="GoodPang 홈으로">
                    <span class="brand-goodpang">GoodPang</span>
                </a>
            </h1>
            <div class="cart-step">
                <span class="step">01 옵션선택</span> &gt;
                <span class="step active">02 장바구니</span> &gt;
                <span class="step">03 주문/결제</span> &gt;
                <span class="step">04 주문완료</span>
            </div>
        </div>
    </header>

    <main class="cart-container">
        <!-- 메인 타이틀 -->
        <h2 class="cart-title">&lt; 장바구니(<span id="total-count">2</span>)</h2>

        <!-- 메인 2열 컨텐츠 -->
        <div class="cart-content">
            <!-- 왼쪽: 장바구니 상품 목록 -->
            <section class="cart-left">
                <div class="cart-box">
                    <div class="box-header">
                        <span class="rocket-badge">로켓배송 상품</span>
                        <span class="sub-text">무료배송 - 19,800원 이상 주문 가능</span>
                    </div>

                    <!-- 상품 아이템 1 -->
                    <div class="cart-item">
                        <input type="checkbox" class="item-chk" checked>
                        <div class="item-img-placeholder">이미지</div>
                        <div class="item-info">
                            <p class="item-title">별의 바다 UV 99.9% 차단양 자외선차단 4세대 3단 접이식 자동 우산</p>
                            <p class="item-option">옵션: 다크 블루, 95cm</p>
                            <p class="delivery-date">내일(목) 도착</p>
                            <div class="price-wrap">
                                <span class="discount">76%</span>
                                <span class="price">9,590원</span>
                            </div>
                            <div class="quantity-control">
                                <button type="button" class="btn-qty">-</button>
                                <input type="text" value="1" readonly>
                                <button type="button" class="btn-qty">+</button>
                            </div>
                        </div>
                        <button type="button" class="btn-delete">삭제</button>
                    </div>

                    <!-- 상품 아이템 2 -->
                    <div class="cart-item">
                        <input type="checkbox" class="item-chk" checked>
                        <div class="item-img-placeholder">이미지</div>
                        <div class="item-info">
                            <p class="item-title">더베이직 여성용 카바 무시 중목 양말 10켤레</p>
                            <p class="item-option">옵션: 화이트, 230~250mm</p>
                            <p class="delivery-date">내일(목) 도착</p>
                            <div class="price-wrap">
                                <span class="discount">51%</span>
                                <span class="price">19,320원</span>
                            </div>
                            <div class="quantity-control">
                                <button type="button" class="btn-qty">-</button>
                                <input type="text" value="2" readonly>
                                <button type="button" class="btn-qty">+</button>
                            </div>
                        </div>
                        <button type="button" class="btn-delete">삭제</button>
                    </div>
                </div>

                <!-- 일괄 선택 및 삭제 영역 -->
                <div class="cart-select-actions">
                    <label><input type="checkbox" id="chk-all" checked> 전체 선택 (<span class="selected-count">2</span>/<span class="total-count">2</span>)</label>
                    <button type="button" class="btn-action">선택삭제</button>
                </div>
            </section>

            <!-- 오른쪽: 주문 예상 금액 사이드바 -->
            <aside class="cart-right">
                <div class="order-summary-box">
                    <h3>주문 예상 금액</h3>
                    <div class="summary-row">
                        <span>총 상품 가격</span>
                        <span id="price-products">28,910원</span>
                    </div>
                    <div class="summary-row">
                        <span>총 배송비</span>
                        <span id="price-delivery">+0원</span>
                    </div>
                    <hr>
                    <div class="summary-row total">
                        <span>총 결제예상금액</span>
                        <span id="price-total">28,910원</span>
                    </div>
                    <button type="button" class="btn-order">구매하기</button>
                </div>
            </aside>
        </div>

        <!-- 하단 추천 상품 영역 -->
        <section class="recommend-section">
            <h3>같이 보면 좋은 상품</h3>
            <div class="product-grid">
                <div class="product-card">
                    <div class="card-img">상품이미지</div>
                    <p class="card-title">자동 우산 UV차단 초경량 양산</p>
                    <p class="card-price">13,700원</p>
                </div>
                <div class="product-card">
                    <div class="card-img">상품이미지</div>
                    <p class="card-title">3단 자동 우양산 겸용 자외선 차단</p>
                    <p class="card-price">9,900원</p>
                </div>
                <div class="product-card">
                    <div class="card-img">상품이미지</div>
                    <p class="card-title">디시엘로 3단 자동 우산</p>
                    <p class="card-price">12,800원</p>
                </div>
                <div class="product-card">
                    <div class="card-img">상품이미지</div>
                    <p class="card-title">초경량 5단 접이식 미니 우산</p>
                    <p class="card-price">9,900원</p>
                </div>
            </div>

            <h3>결제하려 했던 상품</h3>
            <div class="product-grid">
                <div class="product-card">
                    <div class="card-img">상품이미지</div>
                    <p class="card-title">순면 자루형 베개커버 2장</p>
                    <p class="card-price">12,800원</p>
                </div>
            </div>
        </section>
    </main>

    <!-- 상대경로로 JS 연결 -->
    <script src="js/cart.js"></script>
</body>
</html>