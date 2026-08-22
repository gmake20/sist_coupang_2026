<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>GoodPang | 주문목록</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/order_list.css">
    <link rel="stylesheet" href="css/reset.css">
</head>
<body>
  
  <jsp:include page="/inc/header.jsp" />

    <!-- 2. 메인 3열 레이아웃 -->
    <div class="mypage-wrapper">
        
        <!-- [좌측] 사이드바 메뉴 -->
        <aside class="mypage-sidebar">
            <div class="sidebar-group">
                <h3>MY 쇼핑</h3>
                <ul>
                    <li class="active"><a href="order_list.jsp">주문목록/배송조회</a></li>
                    <li><a href="cancel_history.jsp">취소/반품/교환/환불 내역</a></li>
                    <li><a href="#">와우 멤버십</a></li>
                    <li><a href="#">구독 서비스 <span class="badge-n">N</span></a></li>
                    <li><a href="#">로켓프레시 프레시백 <span class="badge-n">N</span></a></li>
                    <li><a href="#">영수증 조회/출력</a></li>
                </ul>
            </div>
            <div class="sidebar-group">
                <h3>MY 혜택</h3>
                <ul>
                    <li><a href="#">쿠폰 · 이용권</a></li>
                    <li><a href="#">쿠팡캐시/기프트카드</a></li>
                </ul>
            </div>
            <div class="sidebar-group">
                <h3>MY 활동</h3>
                <ul>
                    <li><a href="#">문의하기</a></li>
                    <li><a href="#">문의내역 확인</a></li>
                    <li><a href="#">리뷰 관리</a></li>
                    <li><a href="#">찜 리스트</a></li>
                </ul>
            </div>
            <div class="sidebar-group">
                <h3>MY 정보</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a></li>
                    <li><a href="#">결제수단·쿠페이 관리</a></li>
                    <li><a href="${pageContext.request.contextPath}/address/list">배송지 관리</a></li>
                    <li><a href="#">패스키 관리</a></li>
                    <li><a href="#">회원 탈퇴</a></li>
                </ul>
            </div>
        </aside>

        <!-- [중앙] 메인 본문 -->
        <main class="mypage-main">
            <!-- 쿠페이/쿠팡캐시 배너 -->
            <div class="cash-banner">
                <div class="cash-item">쿠페이 머니 <span class="price">0원</span></div>
                <div class="cash-item">쿠팡캐시 <span class="price">0원</span></div>
            </div>

            <h2 class="page-title">주문목록</h2>

            <!-- 검색 및 연도/기간 선택 필터 -->
            <div class="search-filter-box">
                <div class="order-search">
                    <input type="text" placeholder="주문한 상품을 검색할 수 있어요!">
                    <button type="button" class="btn-order-search">🔍</button>
                </div>
                <div class="period-buttons">
                    <button type="button" class="btn-period active" data-year="recent">최근 6개월</button>
                    <button type="button" class="btn-period" data-year="2026">2026</button>
                    <button type="button" class="btn-period" data-year="2025">2025</button>
                    <button type="button" class="btn-period" data-year="2024">2024</button>
                    
                  
                </div>
            </div>

            <!-- 동적으로 변경될 주문 카드 컨테이너 -->
            <div id="order-card-list">
                <!-- JS로 자동 생성 및 필터링됩니다 -->
            </div>
        </main>

        <!-- [우측] 고정형(Sticky) 세로 긴 배너 -->
        <aside class="right-banner-aside">
            <div class="sticky-banner-container">
                <div class="banner-box ad-card">
                    <span class="tag-only">GoodPang Only</span>
                    <img src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=120" alt="추천의류">
                    <p class="banner-desc">기획전 특가 의류</p>
                </div>
                
                <div class="banner-box sale-card">
                    <p class="sale-title">~5만원<br>쿠폰 할인!</p>
                    <div class="sale-bg">여름맞이<br>옷장세일</div>
                </div>

                <div class="banner-box import-card">
                    <p class="import-title">직수입 브랜드<br>단독 특가</p>
                    <img src="https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=120" alt="브랜드상품">
                </div>

                <div class="banner-box yellow-card">
                    <div class="bell-icon">🔔<span>1</span></div>
                    <p>금주의<br>의류 특가왕</p>
                </div>

                <div class="banner-box app-down-card">
                    <p>GoodPang 앱<br>다운로드 시<br><strong>3,000p 지급</strong></p>
                </div>
            </div>
        </aside>

    </div>

    <script src="js/order_list.js"></script>
    
    <jsp:include page="/inc/footer.jsp" />
</body>
</html>