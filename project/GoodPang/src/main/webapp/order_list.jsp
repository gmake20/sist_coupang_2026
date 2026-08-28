<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>주문목록/배송조회 - GoodPang</title>

<!-- 기본 초기화 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">

<!-- 공통 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">

<!-- 주문상세/리스트 전용 CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/order_list.css">

<!-- jQuery -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<!-- JSP → JS 로 contextPath 전달 -->
<script>
    var contextPath = "${pageContext.request.contextPath}";
</script>

</head>

<body>
	<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>

	<div class="category-layer" id="wa-pc-category"></div>

	<!-- =========================
         마이페이지 3단 레이아웃 (좌: 메뉴 / 중: 본문 / 우: 배너)
    ========================== -->
	<div class="mypage-container">

		<!-- =========================
             [1열] 왼쪽 MY쿠팡 메뉴
        ========================== -->
		<aside class="mycoupang-side">

			<div class="side-title">MY쿠팡</div>

			<div class="side-section">
				<h3>MY 쇼핑</h3>
				<li class="active"><a
					href="${pageContext.request.contextPath}/order/order_list">주문목록/배송조회</a></li>
				<a href="${pageContext.request.contextPath}/cancel_history.jsp">취소/반품/교환/환불
					내역</a> <a href="#">와우 멤버십</a> <a href="#">구독 서비스 <span class="new">N</span></a>
				<a href="#">로켓프레시 프레시백 <span class="new">N</span></a> <a href="#">영수증
					조회/출력</a>
			</div>

			<div class="side-section">
				<h3>MY 혜택</h3>
				<a href="#">쿠폰 · 이용권</a> <a href="#">쿠팡캐시/기프트카드</a>
			</div>

			<div class="side-section">
				<h3>MY 활동</h3>
				<a href="#">문의하기</a> <a href="#">문의내역 확인</a> <a href="#">리뷰관리</a> <a
					href="#">찜 리스트</a>
			</div>

			<div class="side-section">
				<h3>MY 정보</h3>
				<a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a>
				<a href="#">결제수단·쿠페이 관리</a>
				 <a href="${pageContext.request.contextPath}/address/list">배송지 관리</a> 
				 <a href="#">패스키
					관리</a> <a href="#">회원 탈퇴</a>
			</div>

			<!-- 고객센터 메뉴 -->
			<div class="side-help">
				<a href="#"> <span class="help-icon">📝</span> <span>쿠팡문의</span>
				</a> <a href="#"> <span class="help-icon">📢</span> <span>고객의
						소리<br> <small>제안·칭찬·불편신고</small>
				</span>
				</a> <a href="#"> <span class="help-icon">📦</span> <span>취소/반품
						안내</span>
				</a>
			</div>

		</aside>

		<!-- =========================
             [2열] 중앙 메인 본문
        ========================== -->
		<main class="mypage-main">

			<!-- 쿠페이/쿠팡캐시 배너 -->
			<div class="mypage-cash-bar">
				<div class="cash-col">
					쿠페이 머니 <span class="cash-val">0원</span>
				</div>
				<div class="cash-divider"></div>
				<div class="cash-col">
					쿠팡캐시 <span class="cash-val">0원</span>
				</div>
			</div>

			<h2 class="content-heading">주문목록</h2>

			<!-- 검색 및 연도/기간 선택 필터 -->
			<div class="order-filter-container">
				<div class="search-input-wrap">
					<input type="text" placeholder="주문한 상품을 검색할 수 있어요!">
					<button type="button" class="btn-search-icon">🔍</button>
				</div>
				<div class="year-filter-list">
					<button type="button" class="btn-year btn-period active"
						data-year="recent">최근 6개월</button>
					<button type="button" class="btn-year btn-period" data-year="2026">2026</button>
					<button type="button" class="btn-year btn-period" data-year="2025">2025</button>
					<button type="button" class="btn-year btn-period" data-year="2024">2024</button>
				</div>
			</div>

			<!-- 동적으로 출력될 주문 카드 컨테이너 -->
			<div id="order-card-list">
				<c:choose>
					<c:when test="${not empty orderList}">
						<c:forEach var="item" items="${orderList}">

							<!-- 카드 1개 단위 -->
							<section class="delivery-box"
								style="border: 1px solid #e0e0e0; border-radius: 8px; padding: 16px; margin-bottom: 20px; background: #fff;">

								<!-- 상단 헤더: 주문 날짜 및 상세보기 -->
								<div class="card-header"
									style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">
									<div class="order-info"
										style="font-size: 14px; font-weight: bold; color: #333;">
										<span><fmt:formatDate value="${item.orderDate}"
												pattern="yyyy.MM.dd" /> 주문</span> <span
											style="color: #ccc; margin: 0 8px;">|</span> <span>주문번호
											<strong class="order-number">${item.orderNo}</strong>
										</span>
									</div>
									<a
										href="${pageContext.request.contextPath}/order/order_detail?orderNo=${item.orderNo}"
										class="link-detail"
										style="color: #0073e9; text-decoration: none; font-size: 12px; font-weight: bold;">
										주문 상세보기 &gt; </a>
								</div>

								<!-- 카드 본문 -->
								<div class="delivery-main">
									<div class="delivery-status"
										style="font-size: 16px; font-weight: bold; color: #00891a; margin-bottom: 12px;">
										${item.orderStatus}</div>

									<div class="product-row"
										style="display: flex; align-items: center; justify-content: space-between;">

										<!-- 의류 이미지 / 아이콘 -->
										<div class="product-image"
											style="width: 64px; height: 64px; display: flex; align-items: center; justify-content: center; background: #f5f5f5; border-radius: 6px; font-size: 32px; margin-right: 15px; flex-shrink: 0;">
											<div class="clothes-icon">👕</div>
										</div>

										<!-- 의류 상품 정보 명세 -->
										<div class="product-info" style="flex: 1;">
											<div class="product-name"
												style="font-size: 14px; font-weight: bold; margin-bottom: 4px;">
												<a
													href="${pageContext.request.contextPath}/product/detail?productNo=${item.productNo}"
													style="color: #333; text-decoration: none;"> <span
													class="rocket" style="color: #0073e9;">🚀 로켓배송</span>
													${item.productName}
												</a>
											</div>
											<div class="product-price"
												style="font-size: 13px; color: #333; margin-bottom: 4px;">
												<fmt:formatNumber value="${item.itemPrice * item.quantity}" pattern="#,###" />
												원 <span>·</span> ${item.quantity}개
											</div>
											<c:if
												test="${not empty item.option1Value or not empty item.option2Value}">
												<div class="product-option"
													style="font-size: 12px; color: #666; margin-top: 4px;">
													<span>옵션: </span>
													<c:if test="${not empty item.option1Value}">
														<c:if test="${not empty item.option1Type}">${item.option1Type}: </c:if>${item.option1Value}
                    								</c:if>
													<c:if
														test="${not empty item.option1Value and not empty item.option2Value}"> / </c:if>
													<c:if test="${not empty item.option2Value}">
														<c:if test="${not empty item.option2Type}">${item.option2Type}: </c:if>${item.option2Value}
                    								</c:if>
												</div>
											</c:if>
										</div>
										<!-- // .product-info 닫기 -->
									</div>


									<!-- 3종 하단 액션 버튼 그룹 -->
									<div class="delivery-buttons"
										style="display: flex; gap: 8px; margin-top: 15px; border-top: 1px solid #f0f0f0; padding-top: 12px;">
										<button type="button" class="delivery-btn btn-action primary"
											onclick="location.href='${pageContext.request.contextPath}/order/tracking?orderNo=${item.orderNo}'"
											style="flex: 1; padding: 8px; border: 1px solid #0073e9; color: #0073e9; background: #fff; border-radius: 4px; cursor: pointer;">
											배송 조회</button>
										<button type="button" class="delivery-btn btn-action"
											onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${item.orderNo}'"
											style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
											교환, 반품 신청</button>

										<%-- ========================================
								         현재 상품의 리뷰 작성 여부 확인
								         ======================================== --%>
										<c:set var="isReviewWritten" value="false" />
										<c:forEach var="review" items="${reviewList}">
											<c:if
												test="${review.orderDetailNo eq item.orderDetailNo
                     								and review.reviewWritten}">
												<c:set var="isReviewWritten" value="true" />
											</c:if>
										</c:forEach>
										<%-- 리뷰 버튼 --%>
										<c:choose>
											<%-- 이미 리뷰를 작성한 상품 --%>
											<c:when test="${isReviewWritten}">
												<button type="button" class="delivery-btn btn-action"
													onclick="location.href='${pageContext.request.contextPath}/review/available'"
													style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
													작성한 리뷰 보기</button>
											</c:when>
											<%-- 아직 리뷰를 작성하지 않은 상품 --%>
											<c:otherwise>
												<button type="button" class="delivery-btn btn-action"
													onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${item.orderDetailNo}&productNo=${item.productNo}'"
													style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
													리뷰 작성하기</button>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</section>
						</c:forEach>
					</c:when>

					<c:otherwise>
						<div class="empty-order-container"
							style="text-align: center; padding: 60px 0;">
							<div class="empty-icon"
								style="font-size: 40px; margin-bottom: 10px;">!</div>
							<h3>최근 주문 내역이 없습니다.</h3>
						</div>
					</c:otherwise>
				</c:choose>
			</div>

			<!-- 페이지 이동 버튼 -->
			<div class="pager-box">
				<button type="button" id="btn-page-prev" class="btn-pager">&lt;
					이전</button>
				<button type="button" id="btn-page-next" class="btn-pager">다음
					&gt;</button>
			</div>

			<!-- 배송상품 주문상태 안내 -->
			<div class="delivery-step-box">
				<div class="step-head">
					<span>배송상품 주문상태 안내</span> <a href="#" class="link-more">자세한 내용
						더보기 &gt;</a>
				</div>
				<div class="step-flow">
					<div class="step-item">
						<div class="icon-circle">💳</div>
						<strong>결제완료</strong>
						<p>주문·결제 확인이 완료되었습니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">📦</div>
						<strong>상품준비중</strong>
						<p>판매자가 발송할 상품을 준비중입니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">🚚</div>
						<strong>배송시작</strong>
						<p>상품준비가 완료되어 곧 배송될 예정입니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">🚛</div>
						<strong>배송중</strong>
						<p>상품이 고객님께 배송중입니다.</p>
					</div>
					<span class="step-arrow">&gt;</span>
					<div class="step-item">
						<div class="icon-circle">🎁</div>
						<strong>배송완료</strong>
						<p>상품이 주문자에게 전달완료되었습니다.</p>
					</div>
				</div>
			</div>

			<!-- 취소/반품/교환 안내 -->
			<div class="notice-info-box">
				<p class="notice-title">⚠ 취소/반품/교환 신청전 확인해주세요!</p>

				<div class="notice-sec">
					<h4>취소</h4>
					<ul>
						<li>여행/레저/숙박 상품은 취소 시 수수료가 발생할 수 있으며,</li>
						<li>취소수수료를 확인하여 2일 이내(주말,공휴일 제외 처리결과)를 문자로 안내드립니다.(당일 접수 기준,
							마감시간 오후 4시)</li>
						<li>문화 상품은 사용 전날 24시까지 취소 신청 시 취소수수료가 발생되지 않습니다.</li>
					</ul>
				</div>
			</div>

		</main>

		<!-- =========================
             [3열] 우측 광고 배너 영역
        ========================== -->
		<aside class="right-banner">
			<div class="banner banner-1">
				<strong>쿠팡 only</strong>
				<div class="banner-product">🧻</div>
			</div>
			<div class="banner banner-2">
				<strong>추석연휴 숙소~64%</strong>
				<div class="banner-character">🐰</div>
			</div>
			<div class="banner banner-3">
				<strong>쿠팡이 직접 수입했어요!</strong>
				<div class="banner-shop">🥤</div>
			</div>
			<div class="banner banner-4">
				<strong>금주의 특가왕</strong>
				<div class="banner-bell">🔔</div>
				<div class="badge">1</div>
			</div>
		</aside>

	</div>
	<!-- //.mypage-container -->

	<jsp:include page="/inc/footer.jsp" />

	<%-- <script src="${pageContext.request.contextPath}/js/order_list.js"></script> --%>
</body>
</html>