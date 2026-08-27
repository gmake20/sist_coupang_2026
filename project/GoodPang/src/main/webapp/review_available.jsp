<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>작성 가능한 리뷰</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/review_list.css">
</head>
<body>
	<jsp:include page="/inc/header.jsp" />

	<div class="mypang-layout">

		<aside class="mypang-sidebar">
			<div class="mypang-logo">MY쿠팡</div>

			<div class="mypang-menu-group">
				<h3>MY 쇼핑</h3>
				<a href="${pageContext.request.contextPath}/mypage/orders">주문목록/배송조회</a>
				<a href="#">취소/반품/교환/환불내역</a>
			</div>

			<div class="mypang-menu-group">
				<h3>MY 활동</h3>
				<a href="#">문의하기</a> <a href="#">문의내역 확인</a> <a
					href="${pageContext.request.contextPath}/review/available"
					class="active">리뷰관리</a>
			</div>

			<div class="mypang-menu-group">
				<h3>MY 정보</h3>
				<a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a>
				<a href="${pageContext.request.contextPath}/address/list">배송지 관리</a>
			</div>
		</aside>

		<main class="review-page">
			<h1 class="review-title">리뷰관리</h1>

			<div class="review-member-box">
				<div class="member-profile">
					<div class="member-avatar">👤</div>
					<div class="member-name">${sessionScope.loginMember.memberName}</div>
				</div>

				<div class="member-divider"></div>

				<div class="member-stat">
					<span class="member-stat-title">작성한 리뷰</span>
					<div class="member-stat-value">
						<strong>${reviewCount}</strong> <span>개</span>
					</div>
				</div>
			</div>

			<div class="review-tabs">
				<a href="${pageContext.request.contextPath}/review/available"
					class="active"> 작성 가능한 리뷰 <span>${fn:length(availableList)}</span>
				</a> <a href="${pageContext.request.contextPath}/review/list"> 작성한
					리뷰 <span>${reviewCount}</span>
				</a>
			</div>

			<div class="review-list">
				<c:choose>

					<c:when test="${empty availableList}">
						<div class="review-empty">
							<div class="empty-icon">✎</div>
							<p>작성 가능한 리뷰가 없습니다.</p>
							<a href="${pageContext.request.contextPath}/mypage/orders"
								class="order-btn">주문내역 확인하기</a>
						</div>
					</c:when>

					<c:otherwise>
						<c:forEach var="item" items="${availableList}">

							<article class="review-item">

								<div class="review-product">
									<div class="product-info">

										<a
											href="${pageContext.request.contextPath}/product/detail?productNo=${item.productNo}"
											class="product-name"><c:out value="${item.productName}" /></a>

										<c:if test="${not empty item.optionName}">
											<div class="product-option">
												<c:out value="${item.optionName}" />
											</div>
										</c:if>

									</div>
								</div>

								<div class="available-review-actions">
									<a
										href="${pageContext.request.contextPath}/review/write?orderDetailNo=${item.orderDetailNo}&productNo=${item.productNo}"
										class="write-review-btn">리뷰 작성하기</a>
								</div>

							</article>

						</c:forEach>
					</c:otherwise>

				</c:choose>
			</div>
		</main>

		<aside class="right-banner">

			<div class="ad-box ad-only">
				<div class="ad-title">
					<strong>GoodPang</strong> <em>only</em>
				</div>
				<div class="ad-icon">🛍️</div>
			</div>

			<div class="ad-box ad-blue">
				<strong>~5만원<br>쿠폰 할인
				</strong>
				<div class="ad-icon">🏖️</div>
			</div>

			<div class="ad-box ad-sky">
				<strong>GoodPang<br>이 직접<br>수입했어요!
				</strong>
				<div class="ad-icon">🥤</div>
			</div>

		</aside>

	</div>

	<jsp:include page="/inc/footer.jsp" />
</body>
</html>