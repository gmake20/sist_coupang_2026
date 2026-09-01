<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
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
				<a href="${pageContext.request.contextPath}/order/cancel_history">취소/반품/교환/환불
					내역</a> <a href="${pageContext.request.contextPath}/wow/membership">와우 멤버십</a> <a href="#">구독 서비스 <span class="new">N</span></a>
				<a href="#">로켓프레시 프레시백 <span class="new">N</span></a> <a href="#">영수증
					조회/출력</a>
			</div>

			<div class="side-section">
				<h3>MY 혜택</h3>
				<a href="#">쿠폰 · 이용권</a> <a href="#">쿠팡캐시/기프트카드</a>
			</div>

			<div class="side-section">
				<h3>MY 활동</h3>
				<a href="#">문의하기</a> <a href="#">문의내역 확인</a> <a href="${pageContext.request.contextPath}/review/list">리뷰관리</a> <a
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
				<!-- 연도 필터 버튼 -->
				<div class="year-filter-list">
					<button type="button" class="btn-year btn-period ${empty yearFilter or yearFilter eq 'recent' ? 'active' : ''}" data-year="recent">최근 6개월</button>
					<button type="button" class="btn-year btn-period ${yearFilter eq '2026' ? 'active' : ''}" data-year="2026">2026</button>
					<button type="button" class="btn-year btn-period ${yearFilter eq '2025' ? 'active' : ''}" data-year="2025">2025</button>
					<button type="button" class="btn-year btn-period ${yearFilter eq '2024' ? 'active' : ''}" data-year="2024">2024</button>
				</div>
			</div>

			<!-- 동적으로 출력될 주문 카드 컨테이너 -->
			<div id="order-card-list">
				<c:choose>
					<c:when test="${not empty orderList}">
						
						<%-- 이전 주문번호를 저장할 변수 초기화 --%>
						<c:set var="prevOrderNo" value="" />
						<c:set var="prevItem" value="" />

						<c:forEach var="item" items="${orderList}" varStatus="status">

							<%-- 1. 새로운 주문번호가 시작될 때 이전 주문 카드 박스 및 하단 3개 버튼 닫기 --%>
							<c:if test="${not empty prevOrderNo and prevOrderNo ne item.orderNo}">
										</div> <!-- // .product-list-wrap 닫기 -->
										
										



										<!-- 3종 하단 액션 버튼 그룹 (가로 3개 나란히 배치) -->
										<div class="delivery-buttons"
											style="display: flex; gap: 8px; margin-top: 15px; border-top: 1px solid #f0f0f0; padding-top: 12px;">
											
											<!-- [1] 배송조회 -->
											<button type="button" class="delivery-btn btn-action primary"
												onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${prevOrderNo}'"
												style="flex: 1; padding: 8px; border: 1px solid #0073e9; color: #0073e9; background: #fff; border-radius: 4px; cursor: pointer;">
												배송 조회
											</button>

											<!-- [2] 교환, 반품 신청 -->
											<button type="button" class="delivery-btn btn-action"
												onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${prevOrderNo}'"
												style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
												교환, 반품 신청
											</button>

											<!-- [3] 리뷰 작성 / 작성한 리뷰 보기 -->
											<c:set var="isReviewWritten" value="false" />
											<c:forEach var="review" items="${reviewList}">
												<c:if test="${review.orderDetailNo eq prevItem.orderDetailNo and review.reviewWritten}">
													<c:set var="isReviewWritten" value="true" />
												</c:if>
											</c:forEach>
											
											<c:choose>
												<c:when test="${isReviewWritten}">
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/review/available'"
														style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
														작성한 리뷰 보기
													</button>
												</c:when>
												<c:otherwise>
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${prevItem.orderDetailNo}&productNo=${prevItem.productNo}'"
														style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
														리뷰 작성하기
													</button>
												</c:otherwise>
											</c:choose>

										</div> <!-- // .delivery-buttons 닫기 -->
									</div> <!-- // .delivery-main 닫기 -->
								</section> <!-- // .delivery-box 닫기 -->
							</c:if>

							<%-- 2. 새로운 주문번호일 때만 신규 카드 상자(box)와 헤더 출력 --%>
							<c:if test="${prevOrderNo ne item.orderNo}">
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

										<!-- 동일 주문 상품 목록 감싸기 -->
										<div class="product-list-wrap">
							</c:if>

											<!-- 동일한 주문번호면 이 product-row 영역만 누적 반복 출력됨 -->
											<div class="product-row"
												style="display: flex; align-items: center; justify-content: space-between; padding: 10px 0; border-bottom: 1px dashed #f0f0f0;">

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
														<c:choose>
															<c:when test="${not empty item.totalPrice}">
																<fmt:formatNumber value="${item.itemPrice * item.quantity + item.deliveryFee}" pattern="#,###" />원
															</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${item.itemPrice * item.quantity+ item.deliveryFee}" pattern="#,###" />원
															</c:otherwise>
														</c:choose>
														<span>·</span> ${item.quantity}개
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

											</div> <!-- // .product-row 닫기 -->

							<%-- 현재 주문번호 및 아이템 정보 업데이트 --%>
							<c:set var="prevOrderNo" value="${item.orderNo}" />
							<c:set var="prevItem" value="${item}" />

							<%-- 3. 전체 목록의 맨 마지막 상품일 때 마지막 카드 상자 닫기 및 하단 3개 버튼 출력 --%>
							<c:if test="${status.last}">
										</div> <!-- // .product-list-wrap 닫기 -->

										<!-- 3종 하단 액션 버튼 그룹 (마지막 주문건) -->
										<div class="delivery-buttons"
											style="display: flex; gap: 8px; margin-top: 15px; border-top: 1px solid #f0f0f0; padding-top: 12px;">
											
											<!-- [1] 배송조회 -->
											<button type="button" class="delivery-btn btn-action primary"
												onclick="location.href='${pageContext.request.contextPath}/order/order_tracking?orderNo=${item.orderNo}'"
												style="flex: 1; padding: 8px; border: 1px solid #0073e9; color: #0073e9; background: #fff; border-radius: 4px; cursor: pointer;">
												배송 조회
											</button>

											<!-- [2] 교환, 반품 신청 -->
											<button type="button" class="delivery-btn btn-action"
												onclick="location.href='${pageContext.request.contextPath}/order/order_cancel?orderNo=${item.orderNo}'"
												style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
												교환, 반품 신청
											</button>

											<!-- [3] 리뷰 작성 / 작성한 리뷰 보기 -->
											<c:set var="isReviewWritten" value="false" />
											<c:forEach var="review" items="${reviewList}">
												<c:if test="${review.orderDetailNo eq item.orderDetailNo and review.reviewWritten}">
													<c:set var="isReviewWritten" value="true" />
												</c:if>
											</c:forEach>

											<c:choose>
												<c:when test="${isReviewWritten}">
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/review/available'"
														style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
														작성한 리뷰 보기
													</button>
												</c:when>
												<c:otherwise>
													<button type="button" class="delivery-btn btn-action"
														onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${item.orderDetailNo}&productNo=${item.productNo}'"
														style="flex: 1; padding: 8px; border: 1px solid #ccc; background: #fff; border-radius: 4px; cursor: pointer;">
														리뷰 작성하기
													</button>
												</c:otherwise>
											</c:choose>

										</div> <!-- // .delivery-buttons 닫기 -->
									</div> <!-- // .delivery-main 닫기 -->
								</section> <!-- // .delivery-box 닫기 -->
							</c:if>

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
				<button type="button" id="btn-page-prev" class="btn-pager">&lt; 이전</button>
				<button type="button" id="btn-page-next" class="btn-pager">다음 &gt;</button>
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

        <!-- 우측 날개 배너 모듈 include -->
        <jsp:include page="/inc/right_banner.jsp" />

	</div>
	<!-- //.mypage-container -->

	<jsp:include page="/inc/footer.jsp" />

	<!-- 연도 선택 및 페이징 이벤트 스크립트 -->
	<script>
	$(document).ready(function() {
	    var currentYear = "${empty yearFilter ? 'recent' : yearFilter}";
	    var currentPage = parseInt("${empty curPage ? 1 : curPage}");
	    var totalPages = parseInt("${empty totalPages ? 1 : totalPages}");

	    // 년도 필터 클릭 이벤트 (클릭 시 1페이지로 이동)
	    $('.btn-period').on('click', function() {
	        var selectedYear = $(this).data('year');
	        location.href = contextPath + "/order/order_list?year=" + selectedYear + "&page=1";
	    });

	    // 페이징 이전 버튼 클릭
	    $('#btn-page-prev').on('click', function() {
	        if (currentPage > 1) {
	            location.href = contextPath + "/order/order_list?year=" + currentYear + "&page=" + (currentPage - 1);
	        } else {
	            alert("첫 번째 페이지입니다.");
	        }
	    });

	    // 페이징 다음 버튼 클릭
	    $('#btn-page-next').on('click', function() {
	        if (currentPage < totalPages) {
	            location.href = contextPath + "/order/order_list?year=" + currentYear + "&page=" + (currentPage + 1);
	        } else {
	            alert("마지막 페이지입니다.");
	        }
	    });
	});
	</script>

</body>
</html>