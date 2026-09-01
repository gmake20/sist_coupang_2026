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

	<%-- 상단 띠배너 — index.html 21~27번 줄과 완전히 동일한 마크업 재사용(2026-09-01).
	     header.jsp 안에는 이 배너가 없고, index.html 이 header include 앞에 직접 넣어둔 걸 그대로 가져옴.
	     CSS(.coupang-top-banner, .top-banner-placeholder)와 배너 이미지(top-1.jpg/top-2.jpg)는
	     common.css/main.css 에 이미 있는 걸 그대로 씀 — 새로 만든 것 없음 --%>
	<div class="coupang-top-banner">
		<div class="banner-middle">
			<a href="#"> <span class="top-banner-placeholder"
				style="background: #b5f3fe; color: #0b3d5c"> 오늘 밤 12시까지 주문해도
					로켓배송은 내일 도착! <i class="arrow"></i>
			</span></a> <a href="#"> <span class="top-banner-placeholder"
				style="background: #80daff; color: #0b2d45"> 김원훈의 모발관리템 <i
					class="arrow"></i></span></a>
		</div>
	</div>

	<jsp:include page="/inc/header.jsp" />

	<%-- 정렬/페이지/평점/가격 링크에 항상 같이 실어야 하는 값들. 새 링크 만들 때마다 이어붙임.
	     색상은 다중선택(2026-09-01)이라 선택된 개수만큼 color= 를 반복해서 실음.
	     <c:param> 으로 URL 인코딩까지 여기서 미리 해둠(한글이라 직접 이어붙이면 깨짐) --%>
	<c:url var="baseUrl" value="/category">
		<c:param name="categoryNo" value="${categoryNo}" />
		<c:forEach var="c" items="${selectedColors}">
			<c:param name="color" value="${c}" />
		</c:forEach>
	</c:url>

	<%-- 필터가 하나라도 선택돼 있는지 — "전체해제" 버튼과 "선택한 필터" 줄을 보여줄지 결정하는 데만 씀 --%>
	<c:set var="hasActiveFilter"
		value="${not empty selectedColors || rating > 0 || minPrice > 0 || not empty maxPrice}" />

	<%-- ==================================================
	     브레드크럼 — 원본 재실측(2026-09-01): 화면 끝까지 이어지는 회색 띠(#F9FAFB,
	     border-bottom #DFE3E8) 안에 있음. .category-page(max-width:1300px) 밖에 따로 둬야
	     배경만 풀폭으로 퍼지고 글자는 그 안에서 1300px 폭으로 맞춰짐 --%>
	<div class="breadcrumb-bar">
		<nav class="breadcrumb" aria-label="브레드크럼">
			<a href="${pageContext.request.contextPath}/">굿팡 홈</a>
			<c:forEach var="crumb" items="${breadcrumb}">
				<span class="sep">&gt;</span>
				<%-- 2026-09-02: 소분류(현재 페이지)도 클릭 가능하게 링크로 바꿈(사용자 요청) —
				     "current" 클래스는 그대로 둬서 색상 등 스타일 구분은 유지 --%>
				<a href="${pageContext.request.contextPath}/category?categoryNo=${crumb.categoryNo}"
					class="${crumb.categoryLevel == 3 ? 'current' : ''}">${crumb.categoryName}</a>
			</c:forEach>
		</nav>
	</div>

	<main class="category-page">

		<div class="category-body">

			<%-- ==================================================
			     왼쪽 필터 사이드바
			     ================================================== --%>
			<aside class="category-filter">

				<div class="filter-title-row">
					<h2 class="filter-title">필터</h2>
					<%-- 실제 쿠팡도 필터 하나라도 고르면 "전체해제" 버튼이 뜸(2026-09-01 Playwright 재확인).
					     색상/평점/가격처럼 서버가 아는 필터만 초기화 대상 — categoryNo 만 남기고 나머지 다 뗀 URL로 이동.
					     ★ 2026-09-01: 브랜드/핏 같은 장식용 체크박스만 골랐을 때도 이 버튼이 떠야 해서(버그 리포트 1번),
					     서버가 모르는 상태라 JS가 관리할 수 있게 항상 DOM에 두고 hidden 속성만 토글하는 식으로 바꿈 --%>
					<a href="${pageContext.request.contextPath}/category?categoryNo=${categoryNo}"
						id="filterClearAll" class="filter-clear-all" ${hasActiveFilter ? '' : 'hidden'}>전체해제</a>
				</div>

				<%-- 원본은 "필터" 제목 바로 아래, 소제목 없이 로켓/무료배송 체크박스가 옴.
				     실제 로켓 배지 이미지는 승인 안 된 도메인이라 못 받아와서 글자만(2026-08-31).
				     ★ 2026-09-01: 이 줄은 DB 연동이 없는 장식용 필터임 — 그래도 "체크박스가 있으면 눌렀을 때
				     체크 표시는 나야 한다"는 요청으로 name 없는 진짜 <input type="checkbox"> 를 넣음. name 이
				     없어서 폼에 실리지도 않고 서버에도 안 감. data-deco-id/data-label 은 category.js 가
				     "선택한 필터" 줄에 칩을 추가하려고 씀(버그 리포트 1번 — 체크만 되고 전체해제/선택한 필터에
				     안 잡히던 문제 수정) --%>
				<ul class="filter-top-row">
					<c:forEach var="item" items="${topFilterItems}" varStatus="st">
						<li><label><input type="checkbox" data-deco-id="top-${st.index}" data-label="${item}"><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
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
				<c:forEach var="entry" items="${beforeColorGroups}" varStatus="gst">
					<section class="filter-group is-inert">
						<h3>${entry.key}</h3>
						<ul>
							<c:forEach var="item" items="${entry.value}" varStatus="ist">
								<li><label><input type="checkbox" data-deco-id="before-${gst.index}-${ist.index}" data-label="${item}"><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
							</c:forEach>
						</ul>
					</section>
				</c:forEach>

				<%-- 색상 — 하드코딩 아님, PRODUCT_OPTION 에 실제 등록된 값만 나옴(2026-08-30 확정).
				     2026-09-01: 다중선택으로 바꿈(실제 쿠팡 Playwright 재확인 — 여러 색 동시 선택 가능,
				     선택한 색상끼리는 OR). 체크박스 하나 바꿀 때마다 폼을 통째로 자동 제출해서 서버에 다시 물어봄
				     (색상 조합 자체가 DB 조회 결과라 클라이언트에서만 처리할 수 없음 — 다른 실동작 필터와 같은 원칙) --%>
				<c:if test="${not empty colorOptions}">
					<section class="filter-group">
						<h3>색상</h3>
						<form id="colorFilterForm" method="get" action="${pageContext.request.contextPath}/category">
							<input type="hidden" name="categoryNo" value="${categoryNo}">
							<input type="hidden" name="sort" value="${sort}">
							<input type="hidden" name="minPrice" value="${minPrice}">
							<input type="hidden" name="maxPrice" value="${maxPrice}">
							<input type="hidden" name="rating" value="${rating}">
							<ul class="filter-chip-list">
								<c:forEach var="color" items="${colorOptions}">
									<li>
										<label>
											<input type="checkbox" name="color" value="${color}"
												onchange="document.getElementById('colorFilterForm').submit()"
												${selectedColorMap[color] ? 'checked' : ''}>
											<i class="filter-function-bar-asset"></i><span>${color}</span>
										</label>
									</li>
								</c:forEach>
							</ul>
						</form>
					</section>
				</c:if>

				<c:forEach var="entry" items="${afterColorGroups}" varStatus="gst">
					<section class="filter-group is-inert">
						<h3>${entry.key}</h3>
						<ul>
							<c:forEach var="item" items="${entry.value}" varStatus="ist">
								<li><label><input type="checkbox" data-deco-id="after-${gst.index}-${ist.index}" data-label="${item}"><i class="filter-function-bar-asset"></i><span>${item}</span></label></li>
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
					<%-- 2026-09-01: 여기 색상 hidden input 이 빠져있어서, 가격을 직접 입력해서 검색하면
					     선택해둔 색상이 통째로 날아가는 버그가 있었음 — 다른 필터 링크들처럼 선택된 색상을
					     그대로 실어서 보냄 --%>
					<form class="filter-price-direct" method="get" action="${pageContext.request.contextPath}/category">
						<input type="hidden" name="categoryNo" value="${categoryNo}">
						<input type="hidden" name="sort" value="${sort}">
						<input type="hidden" name="rating" value="${rating}">
						<c:forEach var="c" items="${selectedColors}">
							<input type="hidden" name="color" value="${c}">
						</c:forEach>
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

				<%-- 선택한 필터 — 실제 쿠팡 재확인(2026-09-01 Playwright): 정렬줄 바로 아래, 상품목록 위에 옴.
				     칩 하나 지우면 그 값만 뺀 URL로 재요청, 나머지 선택은 그대로 유지.
				     ★ 장식용 체크박스(브랜드/핏 등)를 골랐을 때도 여기 칩이 떠야 해서(버그 리포트 1번),
				     서버가 모르는 상태라 항상 DOM에 두고 hidden 만 토글 — data-has-server-filter 는
				     "서버 필터가 있어서 원래도 보여야 하는지"를 category.js 에 알려주는 용도 --%>
				<div id="filterSelectedBar" class="filter-selected-bar" data-has-server-filter="${hasActiveFilter}" ${hasActiveFilter ? '' : 'hidden'}>
					<span class="filter-selected-label">선택한 필터:</span>
					<ul id="filterSelectedChips" class="filter-selected-chips">
						<c:forEach var="color" items="${selectedColors}">
							<c:url var="removeColorUrl" value="/category">
								<c:param name="categoryNo" value="${categoryNo}" />
								<c:param name="sort" value="${sort}" />
								<c:param name="minPrice" value="${minPrice}" />
								<c:param name="maxPrice" value="${maxPrice}" />
								<c:param name="rating" value="${rating}" />
								<c:forEach var="other" items="${selectedColors}">
									<c:if test="${other ne color}">
										<c:param name="color" value="${other}" />
									</c:if>
								</c:forEach>
							</c:url>
							<li><a href="${removeColorUrl}">${color}<span class="remove">삭제</span></a></li>
						</c:forEach>

						<c:if test="${rating > 0}">
							<c:url var="removeRatingUrl" value="/category">
								<c:param name="categoryNo" value="${categoryNo}" />
								<c:param name="sort" value="${sort}" />
								<c:param name="minPrice" value="${minPrice}" />
								<c:param name="maxPrice" value="${maxPrice}" />
								<c:param name="rating" value="0" />
								<c:forEach var="c" items="${selectedColors}"><c:param name="color" value="${c}" /></c:forEach>
							</c:url>
							<li><a href="${removeRatingUrl}">${rating}점 이상<span class="remove">삭제</span></a></li>
						</c:if>

						<c:if test="${minPrice > 0 || not empty maxPrice}">
							<c:url var="removePriceUrl" value="/category">
								<c:param name="categoryNo" value="${categoryNo}" />
								<c:param name="sort" value="${sort}" />
								<c:param name="rating" value="${rating}" />
								<c:param name="minPrice" value="0" />
								<c:forEach var="c" items="${selectedColors}"><c:param name="color" value="${c}" /></c:forEach>
							</c:url>
							<li><a href="${removePriceUrl}">가격<span class="remove">삭제</span></a></li>
						</c:if>
					</ul>
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
										<%-- 적립 — 실제 적립 정책 테이블이 없어서 판매가 1%로 임의 계산(2026-09-01 사용자 요청으로 재추가) --%>
										<p class="cash-reward">적립 <fmt:formatNumber value="${item.cashReward}" pattern="#,###"/>원</p>
										<c:if test="${item.reviewCount > 0}">
											<p class="product-rating">
												<span class="star-rating" aria-label="평점 ${item.avgRating}점"><em style="width:${item.avgRating * 20}%"></em></span>
												(${item.reviewCount})
											</p>
										</c:if>
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
