<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
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
	href="${pageContext.request.contextPath}/css/review_available.css">
</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="mypang-layout">

		<aside class="mypang-sidebar">
			<div class="mypang-title">MY쿠팡</div>

			<div class="menu-section">
				<h3>MY 쇼핑</h3>
				<a href="${pageContext.request.contextPath}/order/order_list">주문목록/배송조회</a>
				<a href="#">취소/반품/교환/환불내역</a> <a href="#">로켓와우 멤버십 관리</a> <a
					href="#">영수증 조회/출력</a>
			</div>

			<div class="menu-section">
				<h3>MY 혜택</h3>
				<a href="#">할인쿠폰</a> <a href="#">쿠팡캐시</a>
			</div>

			<div class="menu-section">
				<h3>MY 활동</h3>
				<a href="#">문의하기</a> <a href="#">문의내역 확인</a> <a
					href="${pageContext.request.contextPath}/review/available"
					class="active">리뷰관리</a> <a href="#">찜 리스트</a>
			</div>

			<div class="menu-section">
				<h3>MY 정보</h3>
				<a href="#">개인정보확인/수정</a> <a href="#">결제수단·쿠페이 관리</a> <a href="#">배송지
					관리</a> <a href="#">회원 탈퇴</a>
			</div>
		</aside>

		<main class="review-main">

			<div class="review-header">
				<h1>리뷰관리</h1>
				<div class="review-header-links">
					<a href="#">리뷰 작성 안내</a> <span>|</span> <a href="#">내 리뷰 설정</a>
				</div>
			</div>

			<section class="review-profile">
				<div class="profile-user">
					<div class="profile-circle">👤</div>
					<div class="profile-name">
						<c:choose>
							<c:when test="${not empty sessionScope.loginMember}">
${sessionScope.loginMember.memberName}
</c:when>
							<c:otherwise>회원</c:otherwise>
						</c:choose>
					</div>
				</div>

				<div class="profile-divider"></div>

				<div class="profile-stat">
					<span class="stat-label">도움</span>
					<div class="stat-value">
						<strong>0</strong> <span>명</span>
					</div>
				</div>

				<div class="profile-divider"></div>

				<div class="profile-stat">
					<span class="stat-label">랭킹</span>
					<div class="ranking-value">
						10,099,765 <span>등</span>
					</div>
				</div>
			</section>

			<div class="review-tabs">
				<button type="button" class="review-tab active" id="availableTab"
					onclick="showReviewTab('available')">
					리뷰 작성 <span class="tab-count">${availableCount}</span>
				</button>

				<button type="button" class="review-tab" id="writtenTab"
					onclick="showReviewTab('written')">
					작성한 리뷰 <span class="tab-count">${writtenCount}</span>
				</button>
			</div>

			<!-- 리뷰 작성 가능한 상품 -->
			<div id="availableReviewList" class="review-content">

				<c:set var="hasAvailable" value="false" />

				<c:forEach var="review" items="${reviewList}">
					<c:if test="${not review.reviewWritten}">

						<c:set var="hasAvailable" value="true" />

						<article class="review-item">

							<div class="product-area">

								<div class="product-image">
									<img
										src="${pageContext.request.contextPath}/images/product/default-product.png"
										alt="${review.productName}"
										onerror="this.style.display='none'; this.parentElement.classList.add('image-error');">
								</div>

								<div class="product-info">
									<div class="purchase-date">구매 상품</div>
									<div class="product-name">${review.productName}</div>
									<div class="product-option">상품번호 ${review.productNo}</div>
									<div class="delivery-date">배송 완료</div>
								</div>

							</div>

							<div class="review-action">
								<button type="button" class="btn-review-write"
									onclick="location.href='${pageContext.request.contextPath}/review/write?orderDetailNo=${review.orderDetailNo}&productNo=${review.productNo}'">
									리뷰 작성하기</button>

								<button type="button" class="btn-hide">숨기기</button>
							</div>

						</article>

					</c:if>
				</c:forEach>

				<c:if test="${not hasAvailable}">
					<div class="empty-review">
						<div class="empty-icon">✎</div>
						<div class="empty-title">작성 가능한 리뷰가 없습니다.</div>
						<div class="empty-description">상품 구매 후 리뷰를 작성할 수 있습니다.</div>
					</div>
				</c:if>

			</div>

			<!-- 작성한 리뷰 -->
			<div id="writtenReviewList" class="review-content"
				style="display: none;">

				<c:set var="hasWritten" value="false" />

				<c:forEach var="review" items="${reviewList}">
					<c:if test="${review.reviewWritten}">

						<c:set var="hasWritten" value="true" />

						<article class="review-item">

							<div class="product-area">

								<div class="product-image">
									<img
										src="${pageContext.request.contextPath}/images/product/default-product.png"
										alt="${review.productName}"
										onerror="this.style.display='none'; this.parentElement.classList.add('image-error');">
								</div>

								<div class="product-info">
									<div class="purchase-date">작성한 리뷰</div>
									<div class="product-name">${review.productName}</div>
									<div class="product-option">상품번호 ${review.productNo}</div>
									<div class="delivery-date">리뷰 작성 완료</div>
								</div>

							</div>

							<div class="review-action">

								<button type="button" class="btn-review-write"
									onclick="location.href='${pageContext.request.contextPath}/review/detail?orderDetailNo=${review.orderDetailNo}'">
									리뷰 보기</button>

								<button type="button" class="btn-hide"
									onclick="location.href='${pageContext.request.contextPath}/review/edit?orderDetailNo=${review.orderDetailNo}'">
									수정하기</button>

							</div>

						</article>

					</c:if>
				</c:forEach>

				<c:if test="${not hasWritten}">
					<div class="empty-review">
						<div class="empty-icon">✎</div>
						<div class="empty-title">작성한 리뷰가 없습니다.</div>
						<div class="empty-description">작성한 리뷰가 여기에 표시됩니다.</div>
					</div>
				</c:if>

			</div>

		</main>

		<aside class="review-ad">

			<div class="ad-box ad-only">
				<strong>쿠팡 <span>only</span></strong>
				<div class="ad-placeholder">
					추천<br>상품
				</div>
			</div>

			<div class="ad-box blue-ad">
				<strong>추천 상품</strong>
				<p>
					특별한 상품을<br> 만나보세요
				</p>
			</div>

			<div class="ad-box yellow-ad">
				<strong> 금주의<br> 특가 상품
				</strong>
			</div>

		</aside>

	</div>

	<jsp:include page="/inc/footer.jsp" />

	<script>
function showReviewTab(tab) {

const availableList = document.getElementById("availableReviewList");
const writtenList = document.getElementById("writtenReviewList");

const availableTab = document.getElementById("availableTab");
const writtenTab = document.getElementById("writtenTab");

if (tab === "available") {

availableList.style.display = "block";
writtenList.style.display = "none";

availableTab.classList.add("active");
writtenTab.classList.remove("active");

} else {

availableList.style.display = "none";
writtenList.style.display = "block";

availableTab.classList.remove("active");
writtenTab.classList.add("active");

}

}
</script>

</body>
</html>