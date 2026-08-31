
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

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
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>


<!-- 주문상세 전용 JS -->
<script src="${pageContext.request.contextPath}/js/order_detail.js"></script>

</head>

<body>
	<jsp:include page="/inc/header.jsp" />
	<script src="${pageContext.request.contextPath}/js/header.js"></script>
	<div class="order-detail-wrap">

		<!-- =========================
         왼쪽 MY쿠팡 메뉴
    ========================== -->
		<aside class="mycoupang-side">

			<div class="side-title">MY쿠팡</div>

			<div class="side-section">
				<h3>MY 쇼핑</h3>

				<a href="${pageContext.request.contextPath}/order/order_list" class="active">주문목록/배송조회</a> <a
					href="${pageContext.request.contextPath}/order/cancel_history">취소/반품/교환/환불 내역</a> <a href="#">와우 멤버십</a>
				<a href="#">구독 서비스 <span class="new">N</span></a> <a href="#">로켓프레시
					프레시백 <span class="new">N</span>
				</a> <a href="#">영수증 조회/출력</a>
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

				<a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a> <a href="#">결제수단·쿠페이 관리</a> <a href="#">배송지
					관리</a> <a href="#">패스키 관리</a> <a href="#">회원 탈퇴</a>
			</div>


			<!-- 고객센터 메뉴 -->
			<div class="side-help">

				<a href="#"> <span class="help-icon">📝</span> <span>쿠팡문의</span>
				</a> <a href="#"> <span class="help-icon">📢</span> <span>
						고객의 소리<br> <small>제안·칭찬·불편신고</small>
				</span>
				</a> <a href="#"> <span class="help-icon">📦</span> <span>취소/반품
						안내</span>
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
					<strong><fmt:formatDate value="${orderInfo.orderDate}"
							pattern="yyyy. M. d" /></strong></strong> 주문 <span class="dot">·</span> 주문번호 <span
						class="order-number">${orderInfo.orderNo}</span>
				</div>

			</section>


			<!-- =========================
     배송 상품 리스트 (JSTL 반복문)
========================== -->
			<c:forEach var="item" items="${detailList}">
				<section class="delivery-box">

					<div class="delivery-main">

						<div class="delivery-title">
							배송완료 <span>·</span> <strong>${item.orderStatus}</strong>
						</div>

						<div class="product-row">

							<!-- 상품 이미지 -->
							<div class="product-image">
								<div class="clothes-icon">👕</div>
							</div>

							<!-- 상품 정보 -->
							<div class="product-info">

								<div class="product-name">

									<span class="rocket">🚀 로켓배송</span> ${item.productName}
								</div>

								<div class="product-price">
									<fmt:formatNumber value="${item.totalPrice}" pattern="#,###" />
									원 <span>·</span> ${item.quantity}개
								</div>

								<!-- 옵션 정보 동적 출력 -->
								<c:if
									test="${not empty item.option1Value or not empty item.option2Value}">
									<div class="product-option">
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

							<button type="button" class="cart-btn"
								onclick="addCart('${item.productNo}')">장바구니 담기</button>

						</div>

					</div>


					<div class="delivery-buttons">

						<button type="button" class="delivery-btn primary"
							id="deliveryBtn" onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${item.orderNo}'">배송 조회</button>

						<button type="button" class="delivery-btn" id="exchangeBtn" 
             onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${item.orderNo}'">교환, 반품 신청</button>

						<button type="button" class="delivery-btn" id="reviewBtn">
							리뷰 작성하기</button>

					</div>

				</section>
			</c:forEach>


			<!-- =========================
             받는사람 정보
        ========================== -->
			<section class="detail-section">

				<h2>받는사람 정보</h2>

				<div class="section-line"></div>

				<div class="info-table">

					<div class="info-row">
						<div class="info-title">받는사람</div>
						<div class="info-value">${orderInfo.memberName}</div>
					</div>

					<div class="info-row">
						<div class="info-title">연락처</div>
						<div class="info-value">${orderInfo.phone}</div>
					</div>

					<div class="info-row">
						<div class="info-title">받는주소</div>
						<div class="info-value">${orderInfo.address}
							${orderInfo.detailAddress}</div>
					</div>

					<div class="info-row">
						<div class="info-title">배송요청사항</div>
						<div class="info-value">${not empty orderInfo.requestMsg ? orderInfo.requestMsg : '요청사항 없음'}</div>
					</div>

				</div>

			</section>

			<section class="detail-section payment-section">
				<h2>결제 정보</h2>
				<div class="section-line"></div>

				<div class="payment-box">

					<div class="payment-method">
						<c:choose>
							<c:when test="${orderInfo.paymentMethod eq 'CARD'}">
								${orderInfo.cardCompanyName} / 일시불
								</c:when>
							<c:when test="${orderInfo.paymentMethod eq 'BANK_TRANSFER'}">
								${orderInfo.bankName} / 계좌이체
								</c:when>
							<c:otherwise>
								${orderInfo.paymentMethod}
								</c:otherwise>
						</c:choose>
					</div>
					<div class="payment-price">
						<div class="price-row">
							<span>총 상품가격</span> <strong> <fmt:formatNumber
									value="${orderInfo.totalPrice}" pattern="#,###" /> 원
							</strong>
						</div>
						<div class="price-row">
							<span>배송비</span> <strong> <fmt:formatNumber
									value="${orderInfo.deliveryFee}" pattern="#,###" /> 원
							</strong>
						</div>
					</div>
				</div>
				<div class="payment-total">
					<div>
						<c:choose>
							<c:when test="${orderInfo.paymentMethod eq 'CARD'}">
								${orderInfo.cardCompanyName} / 일시불
								</c:when>
							<c:when test="${orderInfo.paymentMethod eq 'BANK_TRANSFER'}">
								${orderInfo.bankName} / 계좌이체
								</c:when>
							<c:otherwise>
								${orderInfo.paymentMethod}
								</c:otherwise>
						</c:choose>
					</div>

					<div>
						<span>총 결제금액</span> <strong> <fmt:formatNumber
								value="${orderInfo.totalPrice + orderInfo.deliveryFee}"
								pattern="#,###" /> 원
						</strong>
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

					<span> 해당 주문건에 대해 구매 카드영수증 확인이 가능합니다. </span>

					<button type="button" class="receipt-btn" id="cardReceiptBtn">
						카드영수증</button>

				</div>


				<div class="receipt-row">

					<span> 해당 주문건에 대해 거래명세서 확인이 가능합니다. </span>

					<button type="button" class="receipt-btn" id="statementBtn">
						거래명세서</button>

				</div>

			</section>




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
         오른쪽 광고 영역
    ========================== -->
		<aside class="right-banner">

			<div class="banner banner-1">
				<strong>쿠팡 only</strong> <span>›</span>
				<div class="banner-product">📦</div>
			</div>


			<div class="banner banner-2">
				<strong>~5만원<br>쿠폰 할인
				</strong> <span>›</span>
				<div class="banner-character">🏝️</div>
			</div>


			<div class="banner banner-3">
				<strong>쿠팡이 직접<br> <em>수입</em>했어요!
				</strong> <span>›</span>

				<div class="banner-product">🧴</div>
			</div>


			<div class="banner banner-4">
				<strong> 금주의<br> 특가왕
				</strong> <span>›</span>

				<div class="banner-bell">🔔</div>

				<b class="badge">1</b>
			</div>


			<div class="banner banner-5">
				<strong> 쿠팡에서<br> 판매 시작하기
				</strong> <span>›</span>

				<div class="banner-shop">🛍️</div>
			</div>


			<div class="side-mini-menu">

				<div>
					장바구니 <strong>2</strong>
				</div>

				<div>
					최근본상품 <strong>15</strong>
				</div>

			</div>


			<div class="recent-product">👟</div>

		</aside>

	</div>
	<jsp:include page="/inc/footer.jsp" />


</body>
</html>