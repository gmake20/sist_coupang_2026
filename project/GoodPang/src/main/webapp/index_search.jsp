<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>티셔츠 검색결과 - 달팡</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">
	<link rel="stylesheet" href="css/reset.css">
	<!-- 1. 브라우저 기본 스타일 지우기 -->
	<link rel="stylesheet" href="css/common.css">
	<!-- 2. 헤더/푸터 (모든 페이지 공통) -->
	<link rel="stylesheet" href="css/search.css">
	<!-- 3. 검색결과 페이지 전용 -->
	<link rel="stylesheet" href="css/common.css">	
</head>

<body>


	<div class="coupang-top-banner">
		<div class="banner-middle">
			<a href="#"> <span class="top-banner-placeholder" style="background: #b5f3fe; color: #0b3d5c"> 오늘 밤 12시까지
					주문해도
					로켓배송은 내일 도착! <i class="arrow"></i>
				</span>
			</a> <a href="#"> <span class="top-banner-placeholder" style="background: #80daff; color: #0b2d45"> 김원훈의
					모발관리템 <i class="arrow"></i>
				</span>
			</a>
		</div>
	</div>

	<!-- ==================================================
	     HEADER — index.html과 완전히 동일한 마크업.
	     헤더/푸터는 모든 페이지가 함께 쓰는 공통 컴포넌트라
	     여기서도 그대로 재사용함 (css/common.css, js/header.js)
	     ================================================== -->
	<jsp:include page="/inc/header.jsp" />  
	   


	<!-- ==================================================
	     MAIN — 검색결과 본문
	     쿠팡 원본: /np/search?component=&q=검색어 (div.search-page)
	     ================================================== -->
	<main class="search-page">

		<nav class="search-breadcrumb" aria-label="현재 위치">
			<a href="${pageContext.request.contextPath}/">홈</a><span class="sep">›</span><a href="index_search.html">검색결과</a><span
				class="sep">›</span><span>티셔츠</span>
		</nav>

		<div class="search-heading">
			<h2 class="keyword">
				<strong>'티셔츠'</strong> 검색결과
			</h2>
			<span class="count">총 128,540개의 상품이 있어요</span>
		</div>

		<ul class="related-keywords">
			<li><a href="#">브랜드 티셔츠</a></li>
			<li><a href="#">무지 티셔츠</a></li>
			<li><a href="#">반팔 티셔츠</a></li>
			<li><a href="#">여성 티셔츠</a></li>
			<li><a href="#">남성 티셔츠</a></li>
			<li><a href="#">빅사이즈 티셔츠</a></li>
			<li><a href="#">오버핏 티셔츠</a></li>
			<li><a href="#">커플 티셔츠</a></li>
		</ul>

		<div class="quick-filters">
			<button type="button" class="quick-filter" data-filter="rocket">
				<span class="ic-rocket">로켓</span>배송
			</button>
			<button type="button" class="quick-filter" data-filter="free">무료배송</button>
			<button type="button" class="quick-filter" data-filter="coupon">쿠폰적용</button>
			<button type="button" class="quick-filter" data-filter="today">오늘출발</button>
			<button type="button" class="quick-filter" data-filter="cardDiscount">카드할인</button>
		</div>

		<div class="search-body">

			<!-- ===== 왼쪽 필터 사이드바 =====
			     <details>/<summary> 로 접고 펴는 동작을 JS 없이 구현 -->
			<aside class="search-filter">
				<h2 class="blind">검색 필터</h2>

				<details class="filter-group" open>
					<summary>카테고리</summary>
					<div class="filter-group-body">
						<label class="filter-check"><input type="checkbox">여성패션 &gt; 티셔츠<span
								class="cnt">(52,310)</span></label>
						<label class="filter-check"><input type="checkbox">남성패션 &gt; 티셔츠<span
								class="cnt">(48,920)</span></label>
						<label class="filter-check"><input type="checkbox">유아동패션 &gt; 티셔츠<span
								class="cnt">(15,760)</span></label>
						<label class="filter-check"><input type="checkbox">스포츠웨어 &gt; 기능성 티셔츠<span
								class="cnt">(11,550)</span></label>
					</div>
				</details>

				<details class="filter-group" open>
					<summary>배송 방법</summary>
					<div class="filter-group-body">
						<label class="filter-check"><input type="checkbox">로켓배송<span class="cnt">(64,200)</span></label>
						<label class="filter-check"><input type="checkbox">로켓프레시<span class="cnt">(1,120)</span></label>
						<label class="filter-check"><input type="checkbox">판매자로켓<span
								class="cnt">(22,980)</span></label>
						<label class="filter-check"><input type="checkbox">일반배송<span class="cnt">(40,240)</span></label>
					</div>
				</details>

				<details class="filter-group">
					<summary>가격</summary>
					<div class="filter-group-body">
						<div class="filter-price-row">
							<input type="text" inputmode="numeric" placeholder="0">
							<span>~</span>
							<input type="text" inputmode="numeric" placeholder="500,000">
							<span>원</span>
						</div>
						<button type="button" class="filter-price-apply">적용</button>
						<label class="filter-check" style="margin-top: 12px;"><input type="checkbox">1만원 이하<span
								class="cnt">(18,400)</span></label>
						<label class="filter-check"><input type="checkbox">1만원 ~ 2만원<span
								class="cnt">(41,230)</span></label>
						<label class="filter-check"><input type="checkbox">2만원 ~ 3만원<span
								class="cnt">(35,600)</span></label>
						<label class="filter-check"><input type="checkbox">3만원 이상<span
								class="cnt">(33,310)</span></label>
					</div>
				</details>

				<details class="filter-group">
					<summary>브랜드</summary>
					<div class="filter-group-body">
						<label class="filter-check"><input type="checkbox">베이직루트<span class="cnt">(4,210)</span></label>
						<label class="filter-check"><input type="checkbox">코튼랩<span class="cnt">(3,850)</span></label>
						<label class="filter-check"><input type="checkbox">데일리웨어<span class="cnt">(3,120)</span></label>
						<label class="filter-check"><input type="checkbox">액티브핏<span class="cnt">(2,640)</span></label>
						<label class="filter-check"><input type="checkbox">스트릿모먼트<span
								class="cnt">(1,980)</span></label>
						<label class="filter-check"><input type="checkbox">그린필드<span class="cnt">(1,430)</span></label>
					</div>
				</details>

				<details class="filter-group">
					<summary>할인율</summary>
					<div class="filter-group-body">
						<label class="filter-check"><input type="checkbox">10% 이상<span
								class="cnt">(58,900)</span></label>
						<label class="filter-check"><input type="checkbox">30% 이상<span
								class="cnt">(29,400)</span></label>
						<label class="filter-check"><input type="checkbox">50% 이상<span
								class="cnt">(9,120)</span></label>
					</div>
				</details>

				<details class="filter-group">
					<summary>리뷰 별점</summary>
					<div class="filter-group-body">
						<label class="filter-check"><input type="checkbox">★★★★☆ 4점대 이상<span
								class="cnt">(96,300)</span></label>
						<label class="filter-check"><input type="checkbox">★★★☆☆ 3점대 이상<span
								class="cnt">(112,700)</span></label>
					</div>
				</details>

				<button type="button" class="filter-reset">필터 초기화</button>
			</aside>


			<!-- ===== 오른쪽 상품 목록 ===== -->
			<div class="search-content">

				<div class="search-toolbar">
					<div class="sort-tabs">
						<button type="button" class="sort-tab is-active" data-sort="rank">랭킹순</button>
						<button type="button" class="sort-tab" data-sort="price-asc">낮은가격순</button>
						<button type="button" class="sort-tab" data-sort="price-desc">높은가격순</button>
						<button type="button" class="sort-tab" data-sort="review-desc">리뷰많은순</button>
					</div>
					<span class="search-result-count">상품 24개</span>
				</div>

				<!-- 상품 카드는 이제 여기에 직접 안 적혀 있음.
				     js/search.js 가 data/search-products.json 을 fetch로 읽어와서
				     이 목록 태그 안에 search-product-card 항목을 직접 그림 -->
				<ul class="search-product-list"></ul>


				<p class="search-empty">선택하신 조건에 맞는 상품이 없습니다. 필터를 조정해보세요.</p>

				<nav class="search-pagination" aria-label="검색결과 페이지 이동">
					<a href="#" class="arrow">‹</a>
					<span class="current">1</span>
					<a href="#">2</a>
					<a href="#">3</a>
					<a href="#">4</a>
					<a href="#">5</a>
					<a href="#">6</a>
					<a href="#">7</a>
					<a href="#">8</a>
					<a href="#">9</a>
					<a href="#">10</a>
					<a href="#" class="arrow">›</a>
				</nav>

			</div>

		</div>

	</main>


	<!-- ==================================================
	     ASIDE — 본문 옆 날개배너 (index.html과 동일한 구조)
	     ================================================== -->
	<aside class="side-banner">
		<h2 class="blind">추천 배너 및 최근 본 상품</h2>

		<ul class="promotion-banner">
			<li><a href="#" class="banner-link"><img class="wing-image" src="./pds/banner_1.png" alt="여름 반팔 기획전"></a>
			</li>
			<li><a href="#" class="banner-link"><img class="wing-image" src="./pds/banner_2.jpg" alt="쿠팡 only"></a></li>
			<li><a href="#" class="banner-link"><img class="wing-image" src="./pds/banner_3.png" alt="골라입는 티셔츠 특가"></a>
			</li>
			<li><a href="#" class="banner-link"><img class="wing-image" src="./pds/banner_1.png" alt="쿠팡에서 판매시작하기"></a>
			</li>
		</ul>

		<section class="recent-view">
			<div class="side-cart">
				<a href="#"><span>장바구니</span><em class="cart-count">0</em></a>
			</div>
			<div class="recently-viewed-products">
				<span>최근본상품</span><em class="total-element">0</em>
			</div>
		</section>
	</aside>


	<button type="button" id="goto-top" class="goto-top">
		<span class="blind">맨 위로</span>
	</button>


	<!-- ==================================================
	     FOOTER — index.html과 완전히 동일한 마크업
	     ================================================== -->
	<jsp:include page="/inc/footer.jsp" />  


	<!-- JS는 </body> 바로 앞에! HTML을 다 읽은 뒤에 실행되게 하려고 -->
	<script src="js/header.js"></script>
	<script src="js/search.js"></script>

</body>

</html>

</html>