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

			<!-- 리뷰관리 메뉴 파란색 활성화 -->
	<jsp:include page="/inc/left_banner.jsp">
	    <jsp:param name="activeMenu" value="review" />
	</jsp:include>
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

		 <jsp:include page="/inc/right_banner.jsp" />
	</div>

	<jsp:include page="/inc/footer.jsp" />

</body>
</html>