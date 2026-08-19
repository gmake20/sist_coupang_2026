<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>쿠팡! - 주문상세</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<style>
/* === 주문상세 전용 페이지 스타일 === */
.order-detail-container {
  display: flex;
  justify-content: center;
  max-width: 1020px;
  margin: 20px auto 40px;
  gap: 20px;
  font-size: 12px;
  color: #333;
}

/* 1. 좌측 마이쿠팡 사이드바 */
.mypage-sidebar {
  width: 160px;
  flex-shrink: 0;
}
.mypage-sidebar .side-box {
  border: 1px solid #e2e2e2;
  background: #fff;
  margin-bottom: 10px;
}
.mypage-sidebar .side-title {
  height: 48px;
  background: #0073e9;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}
.mypage-sidebar .menu-group {
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}
.mypage-sidebar .menu-group:last-child {
  border-bottom: none;
}
.mypage-sidebar .group-title {
  font-weight: bold;
  color: #111;
  margin-bottom: 8px;
}
.mypage-sidebar .menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.mypage-sidebar .menu-list li {
  margin-bottom: 6px;
}
.mypage-sidebar .menu-list li a {
  color: #555;
  text-decoration: none;
}
.mypage-sidebar .menu-list li a.active {
  color: #0073e9;
  font-weight: bold;
}
.mypage-sidebar .quick-btns .btn-q {
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid #e2e2e2;
  background: #fff;
  margin-bottom: 5px;
  color: #333;
  font-weight: bold;
  text-decoration: none;
}

/* 2. 중앙 주문상세 본문 */
.order-detail-content {
  flex: 1;
  min-width: 0;
}

/* 상단 메인 타이틀 & 날짜 */
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}
.order-meta {
  font-size: 13px;
  margin-bottom: 16px;
  padding-bottom: 8px;
}
.order-meta strong {
  font-size: 14px;
}
.order-meta .ord-num {
  color: #666;
  margin-left: 8px;
}

/* 상품 배송 상태 카드 */
.delivery-card {
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
}
.delivery-card .prod-left {
  flex: 1;
}
.delivery-card .status-title {
  font-size: 16px;
  font-weight: bold;
  color: #111;
  margin-bottom: 15px;
}
.delivery-card .status-title .date {
  color: #00891a;
  margin-left: 6px;
}
.delivery-card .prod-box {
  display: flex;
  align-items: center;
  gap: 15px;
}
.delivery-card .prod-img {
  width: 75px;
  height: 75px;
  border: 1px solid #eee;
  object-fit: cover;
}
.delivery-card .prod-info {
  flex: 1;
}
.delivery-card .prod-name {
  font-size: 13px;
  color: #111;
  margin-bottom: 6px;
}
.delivery-card .badge-rocket {
  background: #0073e9;
  color: #fff;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 2px;
  font-weight: bold;
}
.delivery-card .badge-tomorrow {
  color: #0073e9;
  font-size: 11px;
  font-weight: bold;
}
.delivery-card .prod-price {
  font-size: 13px;
  color: #555;
  margin-top: 4px;
}
.delivery-card .btn-cart-in {
  display: inline-block;
  margin-top: 8px;
  padding: 4px 10px;
  border: 1px solid #ccc;
  background: #fff;
  color: #333;
  text-decoration: none;
  font-size: 11px;
}

/* 우측 액션 버튼들 */
.delivery-card .action-btns {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 130px;
  justify-content: center;
  border-left: 1px solid #f0f0f0;
  padding-left: 20px;
}
.delivery-card .action-btns .btn-act {
  display: block;
  text-align: center;
  padding: 8px 0;
  border: 1px solid #ccc;
  background: #fff;
  color: #333;
  text-decoration: none;
  border-radius: 2px;
}
.delivery-card .action-btns .btn-act.primary {
  border-color: #0073e9;
  color: #0073e9;
}

/* 정보 섹션 공통 (받는사람, 결제정보) */
.section-block {
  margin-bottom: 30px;
}
.section-block .sec-title {
  font-size: 15px;
  font-weight: bold;
  border-bottom: 1px solid #111;
  padding-bottom: 8px;
  margin-bottom: 12px;
}
.info-table {
  width: 100%;
  border-collapse: collapse;
}
.info-table th {
  text-align: left;
  padding: 6px 0;
  color: #555;
  font-weight: normal;
  width: 100px;
  vertical-align: top;
}
.info-table td {
  padding: 6px 0;
  color: #111;
}

/* 결제 정보 우측 합계 표 */
.pay-grid {
  display: flex;
  justify-content: space-between;
}
.pay-grid .pay-left, .pay-grid .pay-right {
  width: 48%;
}
.pay-summary-row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  color: #333;
}
.pay-summary-row.total {
  font-weight: bold;
  font-size: 14px;
  border-top: 1px solid #ddd;
  padding-top: 8px;
  margin-top: 4px;
}

/* 영수증 처리 안내 줄 */
.receipt-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
.btn-receipt {
  padding: 4px 10px;
  border: 1px solid #ccc;
  background: #fff;
  color: #333;
  text-decoration: none;
}

/* 3. 우측 윙 배너 */
.wing-banner-area {
  width: 90px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.wing-banner-area img {
  width: 100%;
  border: 1px solid #eee;
}
</style>
</head>
<body>

  <!-- 헤더 영역 -->
  <header id="header">
    <nav class="service-nav">
      <div class="service-inner" style="display: flex; justify-content: center; align-items: center; gap: 20px; height: 40px;">
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge play">쿠팡플레이</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge rocket">🚀 로켓배송</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge fresh">🌱 로켓프레시</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge retry">🔄 다시 구매</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge biz">biz 쿠팡비즈</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge direct">✈️ 로켓직구</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge gold">📦 골드박스</span></a>
        <a href="#" class="svc-item" style="display: inline-flex; align-items: center;"><span class="badge new">✨ 이달의신상</span></a>
      </div>
    </nav>
  </header>

  <!-- 메인 컨테이너 -->
  <div class="order-detail-container">
    
    <!-- 1. 좌측 MY쿠팡 사이드바 -->
    <aside class="mypage-sidebar">
      <div class="side-box">
        <div class="side-title">MY쿠팡</div>
        
        <div class="menu-group">
          <div class="group-title">MY 쇼핑</div>
          <ul class="menu-list">
            <li><a href="#" class="active">주문목록/배송조회</a></li>
            <li><a href="#">취소/반품/교환/환불내역</a></li>
            <li><a href="#">와우 멤버십</a></li>
            <li><a href="#">구독 서비스</a></li>
            <li><a href="#">로켓프레시 프레시백</a></li>
            <li><a href="#">영수증 조회/출력</a></li>
          </ul>
        </div>

        <div class="menu-group">
          <div class="group-title">MY 혜택</div>
          <ul class="menu-list">
            <li><a href="#">쿠폰·이용권</a></li>
            <li><a href="#">쿠팡캐시/기프트카드</a></li>
          </ul>
        </div>

        <div class="menu-group">
          <div class="group-title">MY 활동</div>
          <ul class="menu-list">
            <li><a href="#">문의하기</a></li>
            <li><a href="#">문의내역 확인</a></li>
            <li><a href="#">리뷰관리</a></li>
            <li><a href="#">찜 리스트</a></li>
          </ul>
        </div>

        <div class="menu-group">
          <div class="group-title">MY 정보</div>
          <ul class="menu-list">
            <li><a href="#">개인정보확인/수정</a></li>
            <li><a href="#">결제수단·쿠페이 관리</a></li>
            <li><a href="#">배송지 관리</a></li>
            <li><a href="#">패스키 관리</a></li>
            <li><a href="#">회원 탈퇴</a></li>
          </ul>
        </div>
      </div>

      <div class="quick-btns">
        <a href="#" class="btn-q">❓ 쿠팡문의</a>
        <a href="#" class="btn-q">📣 고객의 소리</a>
        <a href="#" class="btn-q">📦 취소/반품 안내</a>
      </div>
    </aside>

    <!-- 2. 중앙 주문상세 영역 -->
    <main class="order-detail-content">
      <h2 class="page-title">주문상세</h2>
      <div class="order-meta">
        <strong>2026. 8. 12 주문</strong>
        <span class="ord-num">주문번호 5102232376786</span>
      </div>

      <!-- 배송 완료 카드 (상품 정보: 의류 변경) -->
      <div class="delivery-card">
        <div class="prod-left">
          <div class="status-title">
            배송완료 <span class="date">· 8/13(목) 도착</span>
          </div>
          <div class="prod-box">
            <!-- 임시 대체 이미지 또는 실제 의류 이미지 경로 -->
            <img src="${pageContext.request.contextPath}/images/products/wear_sample.jpg" alt="의류 상품" class="prod-img" onerror="this.src='https://via.placeholder.com/75?text=Apparel';">
            <div class="prod-info">
              <div class="prod-name">
                <span class="badge-rocket">🚀 로켓</span> <span class="badge-tomorrow">내일</span>
                [남성용] 데일리 베이직 라운드 반팔 티셔츠 3팩 세트 (블랙/화이트/그레이)
              </div>
              <div class="prod-price">19,800 원 · 1개</div>
              <a href="#" class="btn-cart-in">장바구니 담기</a>
            </div>
          </div>
        </div>
        
        <!-- 우측 액션 버튼 -->
        <div class="action-btns">
          <a href="#" class="btn-act primary">배송 조회</a>
          <a href="#" class="btn-act">교환, 반품 신청</a>
          <a href="#" class="btn-act">리뷰 작성하기</a>
        </div>
      </div>

      <!-- 받는사람 정보 -->
      <section class="section-block">
        <h3 class="sec-title">받는사람 정보</h3>
        <table class="info-table">
          <tr>
            <th>받는사람</th>
            <td>오*빈</td>
          </tr>
          <tr>
            <th>연락처</th>
            <td>010-****-1234</td>
          </tr>
          <tr>
            <th>받는주소</th>
            <td>(22222) 서울특별시 강남구 선릉로 **** </td>
          </tr>
          <tr>
            <th>배송요청사항</th>
            <td>문 앞</td>
          </tr>
        </table>
      </section>

      <!-- 결제 정보 -->
      <section class="section-block">
        <h3 class="sec-title">결제 정보</h3>
        <div class="pay-grid">
          <div class="pay-left">
            <table class="info-table">
              <tr>
                <th>결제수단</th>
                <td>신한카드 / 일시불</td>
              </tr>
            </table>
          </div>
          <div class="pay-right">
            <div class="pay-summary-row">
              <span>총 상품가격</span>
              <span>19,800 원</span>
            </div>
            <div class="pay-summary-row">
              <span>배송비</span>
              <span>0 원</span>
            </div>
            <div class="pay-summary-row total">
              <span>총 결제금액</span>
              <span style="color: #0073e9;">19,800 원</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 결제영수증 정보 -->
      <section class="section-block">
        <h3 class="sec-title">결제영수증 정보</h3>
        <div class="receipt-row">
          <span>해당 주문건에 대해 구매 카드영수증 확인이 가능합니다.</span>
          <a href="#" class="btn-receipt">카드영수증</a>
        </div>
        <div class="receipt-row">
          <span>해당 주문건에 대해 거래명세서 확인이 가능합니다.</span>
          <a href="#" class="btn-receipt">거래명세서</a>
        </div>
      </section>
    </main>

    <!-- 3. 우측 배너 영역 -->
    <aside class="wing-banner-area">
      <img src="https://via.placeholder.com/90x120?text=Coupang+Only" alt="Coupang Only">
      <img src="https://via.placeholder.com/90x120?text=Discount" alt="할인 배너">
      <img src="https://via.placeholder.com/90x120?text=Import" alt="수입 배너">
    </aside>

  </div>

</body>
</html>