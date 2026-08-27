<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>리뷰관리</title>

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

		<!-- 왼쪽 MY쿠팡 -->
		<aside class="mypang-sidebar">
			<div class="mypang-logo">MY쿠팡</div>

			<div class="mypang-menu-group">
				<h3>MY 쇼핑</h3>
				<a href="${pageContext.request.contextPath}/mypage/orders">주문목록/배송조회</a>
				<a href="#">취소/반품/교환/환불내역</a> <a href="#">로켓와우 멤버십 관리</a> <a
					href="#">영수증 조회/출력</a>
			</div>

			<div class="mypang-menu-group">
				<h3>MY 혜택</h3>
				<a href="#">할인쿠폰</a> <a href="#">쿠팡캐시</a>
			</div>

			<div class="mypang-menu-group">
				<h3>MY 활동</h3>
				<a href="#">문의하기</a> <a href="#">문의내역 확인</a> <a
					href="${pageContext.request.contextPath}/review/list"
					class="active">리뷰관리</a> <a href="#">찜 리스트</a>
			</div>

			<div class="mypang-menu-group">
				<h3>MY 정보</h3>
				<a href="${pageContext.request.contextPath}/member/modify">개인정보확인/수정</a>
				<a href="#">결제수단·쿠페이 관리</a> <a
					href="${pageContext.request.contextPath}/address/list">배송지 관리</a> <a
					href="#">회원 탈퇴</a>
			</div>

			<div class="mypang-help">
				<div>📋 쿠팡문의</div>
				<div>📢 고객의 소리</div>
				<div>📦 취소/반품안내</div>
			</div>
		</aside>

		<!-- 중앙 리뷰관리 -->
		<main class="review-page">
			<h1 class="review-title">리뷰관리</h1>

			<!-- 회원 정보 -->
			<div class="review-member-box">
				<div class="member-profile">
					<div class="member-avatar">👤</div>
					<div class="member-name">${sessionScope.loginMember.memberName}</div>
				</div>

				<div class="member-divider"></div>

				<div class="member-stat">
					<span class="member-stat-title">작성한 리뷰</span>
					<div class="member-stat-value">
						<strong>${fn:length(reviewList)}</strong> <span>개</span>
					</div>
				</div>
			</div>

			<!-- 리뷰 메뉴 -->
			<div class="review-tabs">
				<a href="${pageContext.request.contextPath}/review/available">
					    작성 가능한 리뷰
					</a> <a href="${pageContext.request.contextPath}/review/list"
					class="active"> 작성한 리뷰 <span>${fn:length(reviewList)}</span>
				</a>
			</div>

			<!-- 리뷰 목록 -->
			<div class="review-list">
				<c:choose>

					<%-- 작성한 리뷰가 없는 경우 --%>
					<c:when test="${empty reviewList}">
						<div class="review-empty">
							<div class="empty-icon">✎</div>
							<p>작성한 리뷰가 없습니다.</p>

							<a href="${pageContext.request.contextPath}/mypage/orders"
								class="order-btn"> 주문내역 확인하기 </a>
						</div>
					</c:when>

					<%-- 작성한 리뷰가 있는 경우 --%>
					<c:otherwise>
						<c:forEach var="review" items="${reviewList}">

							<article class="review-item">

								<!-- 상품 정보 -->
								<div class="review-product">
									<div class="product-info">

										<a
											href="${pageContext.request.contextPath}/product/detail?productNo=${review.productNo}"
											class="product-name"> ${review.productName} </a>

										<c:if test="${not empty review.optionText}">
											<div class="product-option">${review.optionText}</div>
										</c:if>

									</div>
								</div>


								<!-- 리뷰 내용 -->
								<%-- <div class="review-body">

									<div class="review-top">
										<div class="review-stars">${review.ratingStars}</div>

										<div class="review-date">
											<fmt:formatDate value="${review.reviewDate}"
												pattern="yyyy.MM.dd" />
										</div>
									</div>

									<c:if test="${not empty review.reviewSummary}">
										<div class="review-summary">${review.reviewSummary}</div>
									</c:if>

									<div class="review-content">
										<c:out value="${review.reviewContent}" />
									</div>
									 --%>

								<%-- <div class="mypang-review-body">
									<div class="mypang-review-top">
										<div class="mypang-review-stars">${review.ratingStars}</div>
										<div class="mypang-review-date">
											<fmt:formatDate value="${review.reviewDate}"
												pattern="yyyy.MM.dd" />
										</div>
									</div>
									<c:if test="${not empty review.reviewSummary}">
										<div class="mypang-review-summary">
											<c:out value="${review.reviewSummary}" />
										</div>
									</c:if>
									<div class="mypang-review-content">
										<c:out value="${review.reviewContent}" />
									</div>
									<c:if test="${not empty review.serviceRating}">
										<div class="service-rating">
											서비스 만족도 :
											<c:choose>
												<c:when test="${review.serviceRating == 2}">
								                    👍 만족
								                </c:when>

												<c:otherwise>
								                    👎 불만족
								                </c:otherwise>
											</c:choose>
										</div>
									</c:if>
								</div>


								<!-- 서비스 평가 -->
								<c:if test="${not empty review.serviceRating}">
									<div class="service-rating">
										서비스 만족도 :
										<c:choose>
											<c:when test="${review.serviceRating == 2}">
                                                👍 만족
                                            </c:when>
											<c:otherwise>
                                                👎 불만족
                                            </c:otherwise>
										</c:choose>
									</div>
								</c:if>
								<!-- 리뷰 관리 버튼 -->
								<div class="review-actions">
									<a
										href="${pageContext.request.contextPath}/review/edit?reviewNo=${review.reviewNo}"
										class="review-btn"> 수정 </a>
									<form action="${pageContext.request.contextPath}/review/delete"
										method="post" class="delete-form">

										<input type="hidden" name="reviewNo"
											value="${review.reviewNo}">

										<button type="submit" class="review-btn delete-btn"
											onclick="return confirm('리뷰를 삭제하시겠습니까?');">삭제</button>
									</form>
								</div>
							</div> --%>
								<div class="mypang-review-body">
									<div class="mypang-review-top">
										<div class="mypang-review-stars">${review.ratingStars}</div>
										<div class="mypang-review-date">
											<fmt:formatDate value="${review.reviewDate}"
												pattern="yyyy.MM.dd" />
										</div>
									</div>
									<c:if test="${not empty review.reviewSummary}">
										<div class="mypang-review-summary">
											<c:out value="${review.reviewSummary}" />
										</div>
									</c:if>
									<div class="mypang-review-content">
										<c:out value="${review.reviewContent}" />
									</div>

									<c:if test="${not empty review.serviceRating}">
										<div class="service-rating">
											서비스 만족도 :
											<c:choose>
												<c:when test="${review.serviceRating == 2}">
								                    👍 만족
								                </c:when>
												<c:otherwise>
							                    👎 불만족
							                </c:otherwise>
											</c:choose>
										</div>
									</c:if>


									<div class="review-actions">

										<a
											href="${pageContext.request.contextPath}/review/edit?reviewNo=${review.reviewNo}"
											class="review-btn"> 수정 </a>
										<form
											action="${pageContext.request.contextPath}/review/delete"
											method="post" class="delete-form">

											<input type="hidden" name="reviewNo"
												value="${review.reviewNo}">
											<button type="submit" class="review-btn delete-btn"
												onclick="return confirm('리뷰를 삭제하시겠습니까?');">삭제</button>
										</form>
									</div>
								</div>
							</article>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</div>

		</main>

		<!-- 오른쪽 광고 -->
		<aside class="right-banner" aria-label="프로모션">

			<div class="ad-box ad-only">
				<div class="ad-title">
					<strong>GoodPang</strong> <em>only</em>
				</div>
				<div class="ad-icon">🛍️</div>
			</div>

			<div class="ad-box ad-blue">
				<strong> ~5만원<br> 쿠폰 할인
				</strong>
				<div class="ad-icon">🏖️</div>
			</div>

			<div class="ad-box ad-sky">
				<strong> GoodPang<br> 이 직접<br> 수입했어요!
				</strong>
				<div class="ad-icon">🥤</div>
			</div>

			<div class="ad-box ad-purple">
				<strong> 금주의<br> 특가왕
				</strong>
				<div class="ad-icon">🔔</div>
			</div>

			<div class="ad-box ad-red">
				<strong> GoodPang<br> 에서<br> 판매 시작하기
				</strong>
				<div class="ad-icon">🏪</div>
			</div>

		</aside>
	</div>

	<jsp:include page="/inc/footer.jsp" />

</body>
</html>