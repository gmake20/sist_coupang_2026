<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${breadcrumb[2].categoryName} | 굿팡</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/category.css">
</head>

<body class="page-category">
	<jsp:include page="/inc/header.jsp" />

	<%-- 정렬/페이지/평점/가격 링크에 항상 같이 실어야 하는 값들. 새 링크 만들 때마다 이어붙임 --%>
	<c:url var="baseUrl" value="/category">
		<c:param name="categoryNo" value="${categoryNo}" />
	</c:url>

	<main class="category-page">

		<%-- ==================================================
		     브레드크럼
		     ================================================== --%>
		<nav class="breadcrumb" aria-label="브레드크럼">
			<a href="${pageContext.request.contextPath}/">굿팡 홈</a>
			<c:forEach var="crumb" items="${breadcrumb}">
				<span class="sep">&gt;</span>
				<c:choose>
					<c:when test="${crumb.categoryLevel == 3}">
						<span class="current">${crumb.categoryName}</span>
					</c:when>
					<c:otherwise>
						<a href="${pageContext.request.contextPath}/category?categoryNo=${crumb.categoryNo}">${crumb.categoryName}</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</nav>

		<div class="category-body">

			<%-- ==================================================
			     왼쪽 필터 사이드바
			     ================================================== --%>
			<aside class="category-filter">

				<h2 class="filter-title">필터</h2>

				<%-- 원본은 "필터" 제목 바로 아래, 소제목 없이 로켓/무료배송 체크박스가 옴.
				     실제 로켓 배지 이미지는 승인 안 된 도메인이라 못 받아와서 글자만(2026-08-31) --%>
				<ul class="filter-top-row">
					<c:forEach var="item" items="${topFilterItems}">
						<li><label><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
					</c:forEach>
				</ul>

				<%-- 카테고리 필터 — 실제로 동작함(클릭하면 그 카테고리로 이동) --%>
				<section class="filter-group">
					<h3>카테고리</h3>
					<ul>
						<c:forEach var="sib" items="${siblingCategories}">
							<li>
								<a href="${pageContext.request.contextPath}/category?categoryNo=${sib.categoryNo}"
									class="${sib.categoryNo == categoryNo ? 'selected' : ''}">
									${sib.categoryName}
								</a>
							</li>
						</c:forEach>
					</ul>
				</section>

				<%-- 여기부터 실제 DB에 속성 컬럼이 없어서 화면만 있고 동작은 안 하는 필터들
				     (2026-08-30 확정 — 원본과 구성은 맞추되, 데이터가 없는 걸 있는 척 만들지 않음).
				     원본 실측(2026-08-31, STRUCTURE.md) 순서 그대로: 카테고리→브랜드→상품상태→색상→핏→...→별점→가격 --%>
				<c:forEach var="entry" items="${beforeColorGroups}">
					<section class="filter-group is-inert">
						<h3>${entry.key}</h3>
						<ul>
							<c:forEach var="item" items="${entry.value}">
								<li><label><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
							</c:forEach>
						</ul>
					</section>
				</c:forEach>

				<%-- 색상 — 하드코딩 아님, PRODUCT_OPTION 에 실제 등록된 값만 나옴(2026-08-30 확정).
				     클릭 동작은 아직 없음 — 필터링 로직은 나중에 붙일 것 --%>
				<c:if test="${not empty colorOptions}">
					<section class="filter-group">
						<h3>색상</h3>
						<ul class="filter-chip-list">
							<c:forEach var="color" items="${colorOptions}">
								<li><label><i class="filter-function-bar-asset"></i><span>${color}</span></label></li>
							</c:forEach>
						</ul>
					</section>
				</c:if>

				<c:forEach var="entry" items="${afterColorGroups}">
					<section class="filter-group is-inert">
						<h3>${entry.key}</h3>
						<ul>
							<c:forEach var="item" items="${entry.value}">
								<li><label><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
							</c:forEach>
						</ul>
					</section>
				</c:forEach>

				<%-- 평점 — 실제로 동작함. 원본은 맨 끝에서 두 번째 --%>
				<section class="filter-group">
					<h3>별점</h3>
					<%-- JSP EL 은 리스트 리터럴이 없어서 5개를 그냥 하나씩 적음(0=전체) --%>
					<ul>
						<li><a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=0" class="${rating == 0 ? 'selected' : ''}">별점 전체</a></li>
						<li><a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=4" class="${rating == 4 ? 'selected' : ''}">4점 이상</a></li>
						<li><a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=3" class="${rating == 3 ? 'selected' : ''}">3점 이상</a></li>
						<li><a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=2" class="${rating == 2 ? 'selected' : ''}">2점 이상</a></li>
						<li><a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=1" class="${rating == 1 ? 'selected' : ''}">1점 이상</a></li>
					</ul>
				</section>

				<%-- 가격대 — 실제로 동작함. 쿠팡 원본 구간(9천 단위)을 그대로 씀. 원본은 맨 끝 --%>
				<section class="filter-group">
					<h3>가격</h3>
					<ul>
						<li><a href="${baseUrl}&sort=${sort}&rating=${rating}&minPrice=0&maxPrice="
								class="${empty maxPrice ? 'selected' : ''}">전체</a></li>
						<li><a href="${baseUrl}&sort=${sort}&rating=${rating}&minPrice=0&maxPrice=9000"
								class="${maxPrice == 9000 ? 'selected' : ''}">9,000원 이하</a></li>
						<li><a href="${baseUrl}&sort=${sort}&rating=${rating}&minPrice=9000&maxPrice=18000"
								class="${minPrice == 9000 && maxPrice == 18000 ? 'selected' : ''}">9,000원~18,000원</a></li>
						<li><a href="${baseUrl}&sort=${sort}&rating=${rating}&minPrice=18000&maxPrice=27000"
								class="${minPrice == 18000 && maxPrice == 27000 ? 'selected' : ''}">18,000원~27,000원</a></li>
						<li><a href="${baseUrl}&sort=${sort}&rating=${rating}&minPrice=27000&maxPrice=36000"
								class="${minPrice == 27000 && maxPrice == 36000 ? 'selected' : ''}">27,000원~36,000원</a></li>
						<li><a href="${baseUrl}&sort=${sort}&rating=${rating}&minPrice=36000&maxPrice="
								class="${minPrice == 36000 && empty maxPrice ? 'selected' : ''}">36,000원 이상</a></li>
					</ul>
					<form class="filter-price-direct" method="get" action="${pageContext.request.contextPath}/category">
						<input type="hidden" name="categoryNo" value="${categoryNo}">
						<input type="hidden" name="sort" value="${sort}">
						<input type="hidden" name="rating" value="${rating}">
						<input type="number" name="minPrice" placeholder="최소" min="0">
						<span>~</span>
						<input type="number" name="maxPrice" placeholder="최대" min="0">
						<button type="submit">검색</button>
					</form>
				</section>

			</aside>

			<%-- ==================================================
			     오른쪽 상품 목록
			     ================================================== --%>
			<section class="category-list">

				<%-- 이것도 마찬가지로 EL 리스트 리터럴 대신 4개를 그냥 하나씩 적음 --%>
				<div class="sort-bar">
					<ul>
						<li class="${sort == 'LATEST' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&sort=LATEST&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">최신순</a>
						</li>
						<li class="${sort == 'PRICE_ASC' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&sort=PRICE_ASC&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">낮은가격순</a>
						</li>
						<li class="${sort == 'PRICE_DESC' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&sort=PRICE_DESC&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">높은가격순</a>
						</li>
						<li class="${sort == 'SALE_COUNT' ? 'Sort_selected' : ''}">
							<a href="${baseUrl}&sort=SALE_COUNT&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}">판매량순</a>
						</li>
					</ul>
					<%-- 60개 고정(2026-08-30 확정) — 원본처럼 오른쪽에 표시만 함, 드롭다운 아님 --%>
					<span class="list-size">60개씩 보기</span>
				</div>

				<c:choose>
					<c:when test="${empty products}">
						<p class="empty-message">조건에 맞는 상품이 없습니다.</p>
					</c:when>
					<c:otherwise>
						<ul class="product-grid">
							<c:forEach var="item" items="${products}">
								<li class="product-card">
									<a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">
										<figure>
											<c:if test="${not empty item.thumbnailUrl}">
												<img src="${pageContext.request.contextPath}/${item.thumbnailUrl}" alt="${item.productName}">
											</c:if>
										</figure>
										<p class="product-name">${item.productName}</p>
										<p class="product-price">
											<c:if test="${item.discountRate > 0}">
												<span class="discount-rate">${item.discountRate}%</span>
												<span class="normal-price"><fmt:formatNumber value="${item.normalPrice}" pattern="#,###"/>원</span>
											</c:if>
											<strong class="sale-price"><fmt:formatNumber value="${item.salePrice}" pattern="#,###"/>원</strong>
										</p>
										<%-- 배송정보 — 실제 DB에 배송 컬럼이 아직 연동 안 돼서 고정 문구(product.jsp 광고캐러셀과 같은 방식, 4장 4번 참고) --%>
										<p class="delivery-info">
											<span class="delivery-date">내일 도착 보장</span>
											<span class="delivery-free">무료배송 · 무료반품</span>
										</p>
										<c:if test="${item.reviewCount > 0}">
											<p class="product-rating">
												<span class="star-rating" aria-label="평점 ${item.avgRating}점"><em style="width:${item.avgRating * 20}%"></em></span>
												(${item.reviewCount})
											</p>
										</c:if>
										<%-- 적립 — 2026-08-31 보류. 실제 적립 정책 없이 임의 계산(판매가 1%)으로 넣었다가 사용자가 "안 할 수도 있음"이라 뺌 --%>
									</a>
								</li>
							</c:forEach>
						</ul>

						<%-- 페이지네이션 --%>
						<nav class="pagination" aria-label="페이지">
							<c:if test="${page > 1}">
								<a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&page=${page - 1}">이전</a>
							</c:if>
							<c:forEach var="p" begin="1" end="${totalPages}">
								<a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&page=${p}"
									class="${p == page ? 'current' : ''}">${p}</a>
							</c:forEach>
							<c:if test="${page < totalPages}">
								<a href="${baseUrl}&sort=${sort}&minPrice=${minPrice}&maxPrice=${maxPrice}&rating=${rating}&page=${page + 1}">다음</a>
							</c:if>
						</nav>
					</c:otherwise>
				</c:choose>

			</section>
		</div>
	</main>

	<jsp:include page="/inc/footer.jsp" />

	<script src="${pageContext.request.contextPath}/js/header.js"></script>
	<script src="${pageContext.request.contextPath}/js/category.js"></script>
</body>
</html>
