<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld" %>

<%-- 검색 결과 페이지 — 구버전 search_list.jsp 를 Tiles 본문 조각으로 옮김 (SearchController 가 "search.list" 리턴).
     <head>/<title>/헤더/푸터/header.js 는 layout_shop.jsp 가 그림. 제목은 Controller 의 pageTitle.
     ★ keyword 는 사용자가 입력한 값이라 화면에 찍을 때 반드시 escape (구버전은 ${keyword} 를 그대로 찍었음) --%>

<%-- 상품 카드(.product-grid/.product-card)는 카테고리 목록과 완전히 같은 모양이라 category.css 를 그대로 재사용 --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/category.css">

<c:set var="keywordText" value="${fn:escapeXml(keyword)}" />

	<main class="category-page">
		<div class="category-body" style="justify-content:center;">
			<section class="category-list">

				<h1 class="category-title">'${keywordText}' 검색결과</h1>
				<p class="search-result-count">총 <fmt:formatNumber value="${totalCount}" pattern="#,###" />개의 상품이 있습니다.</p>

				<%-- 정렬 — 카테고리 목록의 정렬(최신순/낮은가격순/높은가격순/판매량순)과 동일. 쿠팡랭킹순은 검색에서 지원 안 함.
				     c:url + c:param 이 검색어를 URL 인코딩해줌 --%>
				<c:if test="${not empty keyword}">
					<div class="sort-bar">
						<ul>
							<li class="${sort == 'LATEST' ? 'Sort_selected' : ''}">
								<c:url var="sortLatestUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="LATEST" />
								</c:url>
								<a href="${sortLatestUrl}">최신순</a>
							</li>
							<li class="${sort == 'PRICE_ASC' ? 'Sort_selected' : ''}">
								<c:url var="sortPriceAscUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="PRICE_ASC" />
								</c:url>
								<a href="${sortPriceAscUrl}">낮은가격순</a>
							</li>
							<li class="${sort == 'PRICE_DESC' ? 'Sort_selected' : ''}">
								<c:url var="sortPriceDescUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="PRICE_DESC" />
								</c:url>
								<a href="${sortPriceDescUrl}">높은가격순</a>
							</li>
							<li class="${sort == 'SALE_COUNT' ? 'Sort_selected' : ''}">
								<c:url var="sortSaleCountUrl" value="/search">
									<c:param name="keyword" value="${keyword}" />
									<c:param name="sort" value="SALE_COUNT" />
								</c:url>
								<a href="${sortSaleCountUrl}">판매량순</a>
							</li>
						</ul>
					</div>
				</c:if>

				<c:choose>
					<c:when test="${empty products}">
						<p class="empty-message">
							<c:choose>
								<c:when test="${empty keyword}">검색어를 입력해주세요.</c:when>
								<c:otherwise>'${keywordText}'에 대한 검색결과가 없습니다.</c:otherwise>
							</c:choose>
						</p>
					</c:when>
					<c:otherwise>

						<%-- 무료배송 기준 금액 — category_list.jsp 와 같은 값 --%>
						<c:set var="freeShipMin" value="19800" />
						<ul class="product-grid">
							<c:forEach var="item" items="${products}">
								<li class="product-card">
									<%-- 카드 링크 — category_list.jsp 와 같이 /product/{상품번호}?optionId=대표옵션 (옵션이 없으면 안 붙임) --%>
									<a href="${pageContext.request.contextPath}/product/${item.productNo}<c:if test="${item.optionId > 0}">?optionId=${item.optionId}</c:if>">
										<figure>
											<c:if test="${not empty item.thumbnailUrl}">
												<img src="${img:url(item.thumbnailUrl)}" alt="${item.productName}">
											</c:if>
										</figure>
										<p class="free-shipping-badge ${item.salePrice >= freeShipMin ? '' : 'is-empty'}">
											<c:if test="${item.salePrice >= freeShipMin}">무료배송</c:if>
										</p>
										<p class="product-name">${item.productName}</p>
										<p class="product-price">
											<c:if test="${item.discountRate > 0}">
												<span class="discount-rate">${item.discountRate}%</span>
												<span class="normal-price"><fmt:formatNumber value="${item.normalPrice}" pattern="#,###"/>원</span>
											</c:if>
											<span class="price-badge-row ${item.soldOut ? 'is-soldout' : ''}">
												<strong class="sale-price"><fmt:formatNumber value="${item.salePrice}" pattern="#,###"/>원</strong>
												<img class="badge-rocket" src="${pageContext.request.contextPath}/resources/images/icons/logo_rocket_filter_medium.png" alt="로켓배송">
												<img class="badge-tomorrow" src="${pageContext.request.contextPath}/resources/images/icons/badge_199cd481e67.png" alt="내일도착">
											</span>
										</p>
										<c:if test="${item.soldOut}">
											<p class="sold-out-text">일시품절</p>
										</c:if>
										<p class="delivery-date">${deliveryDate} 도착 보장</p>
										<c:if test="${item.reviewCount > 0}">
											<p class="product-rating">
												<span class="star-rating" aria-label="평점 ${item.avgRating}점"><em style="width:${item.avgRating * 20}%"></em></span>
												(${item.reviewCount})
											</p>
										</c:if>
										<p class="cash-reward">
											<img src="${pageContext.request.contextPath}/resources/images/icons/list-cash-icon@2x.png" alt="">
											최대 <fmt:formatNumber value="${item.cashReward}" pattern="#,###"/>원 적립
										</p>
									</a>
								</li>
							</c:forEach>

							<%-- 채움 카드 — 마지막 줄을 4칸으로 채워 밑줄이 끝까지 이어지게 (category_list.jsp 와 같음) --%>
							<c:set var="remainder" value="${fn:length(products) % 4}" />
							<c:if test="${remainder > 0}">
								<c:forEach begin="1" end="${4 - remainder}">
									<li class="product-card product-card--filler" aria-hidden="true"></li>
								</c:forEach>
							</c:if>
						</ul>

						<c:if test="${totalPages > 1}">
							<nav class="pagination" aria-label="페이지">
								<c:if test="${page > 1}">
									<c:url var="prevPageUrl" value="/search">
										<c:param name="keyword" value="${keyword}" />
										<c:param name="sort" value="${sort}" />
										<c:param name="page" value="${page - 1}" />
									</c:url>
									<a href="${prevPageUrl}">이전</a>
								</c:if>
								<c:forEach var="p" begin="1" end="${totalPages}">
									<c:url var="pageUrl" value="/search">
										<c:param name="keyword" value="${keyword}" />
										<c:param name="sort" value="${sort}" />
										<c:param name="page" value="${p}" />
									</c:url>
									<a href="${pageUrl}" class="${p == page ? 'current' : ''}">${p}</a>
								</c:forEach>
								<c:if test="${page < totalPages}">
									<c:url var="nextPageUrl" value="/search">
										<c:param name="keyword" value="${keyword}" />
										<c:param name="sort" value="${sort}" />
										<c:param name="page" value="${page + 1}" />
									</c:url>
									<a href="${nextPageUrl}">다음</a>
								</c:if>
							</nav>
						</c:if>

					</c:otherwise>
				</c:choose>

			</section>
		</div>
	</main>

	<%-- 맨 위로 버튼 — 동작은 layout_shop.jsp 가 읽는 header.js 가 #goto-top 에 붙여줌 --%>
	<button type="button" id="goto-top" class="goto-top">
		<span class="blind">맨 위로</span>
	</button>
