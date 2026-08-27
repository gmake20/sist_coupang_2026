<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${p.productName}- ${p.subCategoryName} | 굿팡</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<!-- 1. 브라우저 기본 스타일 지우기 -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<!-- 2. 헤더/푸터 (모든 페이지 공통) -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/product.css">
<!-- 4. 상세페이지 전용 -->
</head>

<body class="page-product">
	<!-- ==================================================
       HEADER — 페이지 맨 위. 로고 / 검색 / 메뉴
       ================================================== -->
	<!-- ★ 이 헤더는 inc/header.jsp 에서 그대로 옮겨온 것 (2026-08-24).
	     손으로 고치지 말 것 — 고쳐야 하면 inc/header.jsp 를 고치고 여기로 다시 옮길 것.
	     JSP 판(product.jsp)에서는 이 자리가 &lt;jsp:include page="inc/header.jsp"/&gt; 한 줄임 -->
	<jsp:include page="inc/header.jsp" />

	<!-- ==================================================
       MAIN — 상품 상세 페이지 본문
       쿠팡 원본: div.prod-atf (상품이미지 650 + 구매박스 650)

       ★ 이 페이지는 "틀 하나 + 상품 데이터 N개" 구조를 염두에 두고 만듦.
         지금은 값을 직접 써넣었지만, 나중에 JSP 로 바꿀 때
         값이 들어갈 자리만 {p.name} 같은 걸로 갈아끼우면 됨.
         그래서 값이 들어가는 자리마다 ▶JSP 주석을 달아둠.

       ★ 클래스 이름은 전부 .product-page 안에서만 쓰도록 CSS 를 잡았음.
         index.html 과 이름이 겹쳐도 안 부딪히게 하려는 것 (CLAUDE.md 7장 규칙)
       ================================================== -->
	<main class="product-page">
		<div class="product-page__inner">

			<!-- ── 빵부스러기(breadcrumb) — 지금 내가 어느 카테고리에 있는지 ──
           원본은 "쿠팡 홈 > 쿠팡수입 > 패션의류/잡화 > ..." 형태.
            <nav> 으로 감싸는 이유: 이건 글이 아니라 "길 안내"라서

           ▶JSP: ProductServlet 이 request 에 담아준 "p"(ProductDTO) 의
             mainCategoryName / midCategoryName / subCategoryName 을 그대로 씀
             (ProductDAO 가 PRODUCT→SUB_CATEGORY→MID_CATEGORY→MAIN_CATEGORY 조인해서 채워둔 값).
             JS 는 필요 없음 — 요청마다 서버(JSP)가 이미 값을 들고 있어서 EL 로 바로 찍으면 됨.
             (카테고리 "메뉴판" 을 그리는 category/getinfo + header.js 와는 다른 데이터임 — 그건
             사이트 전체 카테고리 트리고, 여긴 "이 상품 하나"가 속한 카테고리 경로임)
           href="#" 는 그대로 둠 — 카테고리별 상품목록 페이지가 아직 없어서. 나중에 생기면
             href="category?categoryNo=..." 식으로 채울 것 -->
			<nav class="breadcrumb">
				<ol>
					<li><a href="index.jsp">굿팡 홈</a></li>
					<!--   
					<li><a href="#">남성패션</a></li>
					<li><a href="#">의류</a></li>
					<li><a href="#">티셔츠</a></li>
					 -->
					<li><a href="#">${p.mainCategoryName}</a></li>
					<li><a href="#">${p.midCategoryName}</a></li>
					<li><a href="#">${p.subCategoryName}</a></li>

				</ol>
			</nav>

			<!-- ===== 상품 위쪽 영역 (ATF) =====
           ATF = Above The Fold. 스크롤 안 내려도 바로 보이는 부분이라는 뜻.
           왼쪽 사진 / 오른쪽 구매박스 를 반반으로 나눔 -->
			<div class="prod-atf">

				<!-- ── 왼쪽: 상품 이미지 ────────────────────────── -->
				<div class="product-image">

					<!-- 썸네일 세로 목록 (원본 실측 70 x 70, 아래 여백 4px)
               사진 파일은 images/product-detail/photo-1~3.jpg (1000x1000).
                 썸네일과 큰 이미지가 **같은 파일**을 씀 — 누르면 JS 가 큰 이미지의 src 를 갈아끼움.
                 alt="" 인 이유: 옆의 큰 이미지가 같은 사진이라 화면낭독기가 두 번 읽으면 방해됨
                 ※ 상세페이지 사진은 images/products/ 가 아니라 images/product-detail/ 를 씀.
                   images/products/ 는 메인페이지 상품카드 전용 (2026-08-20 분리) -->
					<ul class="product-image__thumbs">
						<li class="is-on"><a href="#"><img
								src="${pageContext.request.contextPath}/images/product-detail/photo-1.jpg"
								alt=""></a></li>
						<li><a href="#"><img
								src="${pageContext.request.contextPath}/images/product-detail/photo-2.jpg"
								alt=""></a></li>
						<li><a href="#"><img
								src="${pageContext.request.contextPath}/images/product-detail/photo-3.jpg"
								alt=""></a></li>
					</ul>

					<!-- 큰 이미지 (정사각형)
               ★ 임시 ★ 위와 같음 -->
					<div class="product-image__main">
						<img
							src="${pageContext.request.contextPath}/images/product-detail/photo-1.jpg"
							alt="무형광 남성 반팔 라운드 티셔츠 3종 세트">
					</div>
				</div>

				<!-- ── 오른쪽: 구매박스 ────────────────────────── -->
				<div class="prod-atf-contents">

					<!-- ① 머리부분 — 재고경고 / 브랜드 / 상품명 / 별점 -->
					<div class="product-buy-header">

						<!-- 브랜드명 — 파란 글씨(#346aff). ▶JSP:  -->
						<!-- <a href="#" class="brand-info">베이직스</a> -->
						<a href="#" class="brand-info">${p.storeName}</a>

						<div class="product-title-row">
							<!-- 상품명. 페이지에서 제일 중요한 제목이라 h1.
                   ※ index.html 은 h1 이 로고였는데, 이 페이지의 주인공은 상품이라
                     여기서는 상품명이 h1 이 되는 게 맞음 -->
							<!-- <h1 class="product-title">무형광 남성 반팔 라운드 티셔츠 3종 세트, 화이트/그레이/블랙</h1> -->
							<h1 class="product-title">${p.productName}</h1>

							<!-- 찜 / 공유 버튼 — 원본은 SVG 아이콘이라 우리도 SVG 로 그림.
                   글자(♡ ↗)로 대신하면 모양이 원본과 다르고 폰트에 따라 달라짐.
                   stroke=선으로 그리기 / fill=none 이면 속이 빈 아이콘 -->
							<div class="wish-and-share">
								<button type="button" class="btn-wish">
									<svg viewBox="0 0 24 24" aria-hidden="true">
										<path
											d="M12 20.3 4.6 13a4.6 4.6 0 0 1 6.5-6.5l.9.9.9-.9A4.6 4.6 0 0 1 19.4 13z" />
									</svg>
									<span class="blind">찜하기</span>
								</button>
								<button type="button" class="btn-share">
									<!-- 점 3개 + 연결선 = 공유 아이콘 -->
									<svg viewBox="0 0 24 24" aria-hidden="true">
										<circle cx="18" cy="5" r="2.6" />
										<circle cx="6" cy="12" r="2.6" />
										<circle cx="18" cy="19" r="2.6" />
										<line x1="8.3" y1="10.8" x2="15.7" y2="6.2" />
										<line x1="8.3" y1="13.2" x2="15.7" y2="17.8" />
									</svg>
									<span class="blind">공유하기</span>
								</button>
							</div>
						</div>

						<!-- 별점 + 리뷰수. 리뷰 영역으로 이동하는 링크(#reviews)
                 ▶JSP: 별은 평점으로 계산, 숫자는 {p.reviewCount} -->
						<div class="review-atf">
							<span class="stars">★★★★☆</span> <a href="#reviews" class="count">(${reviewCount})</a>
						</div>
					</div>

					<!-- ② 가격 -->
					<div class="price-container">
						<div class="price-now">
							<!-- <span class="discount">15%</span> <strong class="total-price" data-unit-price="19900">19,900원</strong> -->
							<span class="discount">15%</span> <strong class="total-price"
								data-unit-price="${p.productPrice}">${p.productPrice}원</strong>
							<span class="badge-rocket">로켓배송</span> <span
								class="badge-tomorrow">내일도착</span>
						</div>
						<!-- 원가 취소선 (#768695 + line-through) -->
						<div class="price-origin">
							<span class="origin-price">23,400원</span>
							<!-- 원본에 있는 ⓘ — 눌러도 아무 일 없는 안내 아이콘. 이미지 없이 글자로 그림 -->
							<button type="button" class="price-info">
								<span class="blind">가격 안내</span>
							</button>
						</div>

						<!-- 품절일 때만 보이는 줄. 평소엔 CSS 가 감춤 (body 에 .is-soldout 이 붙어야 나옴)
						     원본 실측: 14px / 700 / rgb(170,181,192) -->
						<p class="soldout-text">품절</p>
					</div>

					<hr class="price-bottom-divider">

					<!-- ③ 배송 정보 -->
					<div class="delivery-container">
						<p class="shipping-fee">
							<em class="txt-bold">무료배송</em> (로켓배송 상품 19,800원 이상 구매 시)
						</p>
						<p class="delivery-date">

							<em class="txt-green">${deliveryDate}</em> <em
								class="txt-green-normal">도착 보장</em> <span class="txt-sub">(11시간
								20분 내 주문 시 / 서울·경기 기준)</span>
						</p>

						<!-- 배송 방법 선택 (2026-08-21 추가)
						     원본에 실제로 있는 라디오 버튼 2개. input[type=radio] 가 아니라
						     span 을 CSS 로 동그랗게 그린 커스텀 라디오 (원본 클래스명 그대로 씀).
						     ★ 둘째 항목(로켓와우)을 고르면 아래 [장바구니 담기][바로구매] 두 칸이
						     [로켓와우로 무료배송 >] 한 칸으로 바뀜 — 원본을 Playwright 로 직접 클릭해서 확인함.
						     ▶JSP: 지금은 앞쪽(로켓배송)이 기본 선택. 로그인/와우 여부에 따라 서버가
						       기본값을 정하게 될 자리 -->
						<ul class="radio-group">
							<li class="radio-item is-on"><span class="radio"></span> <span
								class="radio-text">로켓배송 상품 19,800원 이상 무료배송</span></li>
							<li class="radio-item"><span class="radio"></span> <span
								class="radio-text">무료배송 + 무료반품 <span class="separator">|</span>
									로켓와우 신청시
							</span>
								<button type="button" class="radio-info">
									<span class="blind">안내</span>
								</button></li>
						</ul>
					</div>

					<!-- ④ 옵션 — 원본은 section 을 옵션 종류만큼 반복함
               ▶JSP: &lt;c:forEach items="{p.options}"&gt; 로 감쌀 자리.
                 그래서 두 덩어리를 일부러 똑같은 모양으로 만들어둠 -->
					<div class="fashion-option">

						<!-- 옵션 1: 굵기 — 원본은 select 처럼 생긴 상자(실측 93 x 36) -->
						<section class="option-row">
							<div class="option-label">사이즈</div>
							<div class="option-select">
								<select>
									<option>95</option>
									<option>100</option>
									<option>105</option>
									<option>110</option>
								</select>
							</div>
						</section>

						<!-- 옵션 2: 색상 — 48 x 48 칩. 지금 고른 것에 .is-on -->
						<section class="option-row">
							<div class="option-label">
								색상: <span class="option-value">화이트</span>
							</div>
							<ul class="option-chips">
								<li data-color="블랙"><a href="#"><img
										src="${pageContext.request.contextPath}/images/product-detail/option-1.jpg"
										alt="블랙"></a></li>
								<li data-color="네이비"><a href="#"><img
										src="${pageContext.request.contextPath}/images/product-detail/option-2.jpg"
										alt="네이비"></a></li>
								<li class="is-on" data-color="화이트"><a href="#"><img
										src="${pageContext.request.contextPath}/images/product-detail/option-3.jpg"
										alt="화이트"></a></li>
							</ul>
						</section>
					</div>

					<!-- ⑤ 적립 혜택 -->
					<div class="conditional-benefits">
						<div class="benefit-row">
							<span class="benefit-label">적립</span> <span class="benefit-text">
								<em>최대 ${rewardCash}원 </em> <u>굿팡캐시 적립</u> · 굿페이 머니 결제시
							</span> <a href="#" class="benefit-more">혜택보기</a>
						</div>
						<div class="benefit-row pay-methods">
							<strong>PC에서도 간편한 결제</strong> <span class="pay-chip">굿페이머니</span>
							<span class="pay-chip">카드</span> <span class="pay-chip">계좌이체</span>
						</div>
					</div>

					<!-- ⑥ 수량 + 구매 버튼 (원본 실측: 전부 높이 42px) -->
					<form class="prod-buy-quantity-and-footer" method="post"
						action="${pageContext.request.contextPath}/cart/add">

						<!-- 옵션 넘버 들어와야 할 곳 -->
						<!-- 실제 CART에 저장할 OPTION_ID -->
						<input type="hidden" name="optionId" id="selectedOptionId"
							value="25">

						<!-- 화면 표시용 선택 옵션 -->
						<!-- 상품번호 -->
						<input type="hidden" name="productNo" value="${p.productNo}">
						<input type="hidden" name="color" id="selectedColor" value="화이트">

						<!-- 수량 -->
						<div class="product-quantity">

							<input type="text" class="qty-input" name="quantity" value="1"
								readonly>
							<div class="qty-spin">
								<button type="button" class="qty-plus">
									<span class="blind"> 수량 더하기 </span>
								</button>
								<button type="button" class="qty-minus">
									<span class="blind"> 수량 빼기 </span>
								</button>
							</div>
						</div>

						<!-- 장바구니 -->
						<button type="submit" class="prod-cart-btn">장바구니 담기</button>

						<!-- 바로구매 -->
						<button type="submit" class="prod-buy-btn"
							formaction="${pageContext.request.contextPath}/order/buy"
							formmethod="post">

							바로구매 <i class="arrow-right"></i>

						</button>

						<!-- 로켓와우 -->
						<button type="submit" class="prod-wow-btn"
							formaction="${pageContext.request.contextPath}/order/buy"
							formmethod="post">

							로켓와우로 무료배송 <i class="arrow-right"></i>

						</button>

						<!-- 품절 -->
						<button type="button" class="prod-soldout-btn" disabled>
							품절</button>

					</form>

					<!-- ⑦ 맨 아래 작은 글씨 색상계열, 굿팡상품번호 텍스트만 삭제, 통째로 삭제하면 배치 달라짐-->
					<div class="product-description">
						<ul>
							<li></li>
							<li></li>
						</ul>
					</div>

				</div>
				<!-- //.prod-atf-contents -->
			</div>
			<!-- //.prod-atf -->

			<!-- ===== 광고 캐러셀 ① 함께 비교하면 좋을 상품 =====
			     원본: div.sdp-ads > .recommendation (높이 446px)
			     실측: 위 여백 35 / 아래 40 / 제목 22px·700 / 카드 140px + 오른쪽 여백 16 / 사진 140x140
			
			     ★ 메인페이지의 .ad-carousel 과 "비슷하지만 다름" — 카드가 훨씬 좁고(140 vs 188)
			       한 번에 5개씩 넘기는 게 아니라 가로로 쭉 이어지다 잘림. 그래서 새로 만듦.
			       (css/main.css 를 불러오면 재사용할 수 있지만, 거기엔 메인 본문 전용 58KB 가
			        같이 딸려와서 상세페이지에는 안 불러오기로 함 — CLAUDE.md 참고) -->
			<section class="sdp-ads">
				<div class="sdp-ads__head">
					<h2>함께 비교하면 좋을 상품</h2>
					<span class="ad-label">광고</span>
				</div>

				<!-- .is-cut = 넘치는 부분을 잘라내는 상자. 안쪽 ul 이 옆으로 밀리며 움직임 -->
				<div class="sdp-ads__body">
					<ul class="sdp-ads__list">
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-1.jpg"
									alt=""></span> <span class="ad-item__name">베스티하루 로카티 ROKA
									반팔 티셔츠</span> <span class="ad-item__price"> <span class="was">할인
										<del>23,900</del>
								</span> <span class="now"><em class="rate">58%</em> <strong>9,900</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(63)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-2.jpg"
									alt=""></span> <span class="ad-item__name">3장 세트 ROKA 기능성
									냉감 쿨링 남자 여자 반팔티 로카티</span> <span class="ad-item__price"> <span
									class="now"><strong>18,900</strong></span>
							</span> <span class="ad-item__tag">무료배송</span> <span
								class="ad-item__ship">모레(금) 도착 예정</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(30)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-3.jpg"
									alt=""></span> <span class="ad-item__name">워크존 로카티 코리아아미
									쿨링 반팔 티셔츠 남녀공용</span> <span class="ad-item__price"> <span
									class="was">할인 <del>18,300</del></span> <span class="now"><em
										class="rate">60%</em> <strong>7,290</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(398)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-4.jpg"
									alt=""></span> <span class="ad-item__name">밀리랩 로카티 ROKA
									반팔 티셔츠 2P</span> <span class="ad-item__price"> <span
									class="was">할인 <del>25,800</del></span> <span class="now"><em
										class="rate">36%</em> <strong>16,500</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(6,543)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-5.jpg"
									alt=""></span> <span class="ad-item__name">5장 모던프로 스포츠
									기능성 드라이 라운드 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>120,000</del></span> <span class="now"><em
										class="rate">87%</em> <strong>14,800</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(357)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-6.jpg"
									alt=""></span> <span class="ad-item__name">NEOX 네옥스 쿨론
									자카드 검정 라운드넥 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>34,900</del></span> <span class="now"><em
										class="rate">77%</em> <strong>7,910</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(76)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-7.jpg"
									alt=""></span> <span class="ad-item__name">쿨링 ROKA 로카티 반팔
									단체 티셔츠 반티 체육대회 유니폼</span> <span class="ad-item__price"> <span
									class="was">할인 <del>20,000</del></span> <span class="now"><em
										class="rate">56%</em> <strong>8,800</strong></span>
							</span> <span class="ad-item__tag">무료반품</span> <span
								class="ad-item__ship">내일(목) 도착 보장</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(6,384)</span>
						</a></li>
						<li class="ad-item"><a href="#"> <span
								class="ad-item__thumb"><img
									src="${pageContext.request.contextPath}/images/product-detail/ad-8.jpg"
									alt=""></span> <span class="ad-item__name">아소트 냉감 통기성 메쉬
									로카티 코리아 아미 반팔 티셔츠</span> <span class="ad-item__price"> <span
									class="was">할인 <del>21,900</del></span> <span class="now"><em
										class="rate">54%</em> <strong>9,920</strong></span>
							</span> <span class="ad-item__tag">무료배송</span> <span
								class="ad-item__ship">모레(금) 도착 예정</span> <span
								class="ad-item__rating"><em class="stars">★★★★☆</em>(682)</span>


								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-1.jpg"
											alt=""></span> <span class="ad-item__name">베스티하루 로카티
											ROKA 반팔 티셔츠</span> <span class="ad-item__price"> <span
											class="was">할인 <del>23,900</del></span> <span class="now"><em
												class="rate">58%</em> <strong>9,900</strong></span>
									</span> <span class="ad-item__tag">무료반품</span> <span
										class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(63)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-2.jpg"
											alt=""></span> <span class="ad-item__name">3장 세트 ROKA
											기능성 냉감 쿨링 남자 여자 반팔티 로카티</span> <span class="ad-item__price">
											<span class="now"><strong>18,900</strong></span>
									</span> <span class="ad-item__tag">무료배송</span> <span
										class="ad-item__ship">${deliveryDate} 도착 예정</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(30)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-3.jpg"
											alt=""></span> <span class="ad-item__name">워크존 로카티
											코리아아미 쿨링 반팔 티셔츠 남녀공용</span> <span class="ad-item__price"> <span
											class="was">할인 <del>18,300</del></span> <span class="now"><em
												class="rate">60%</em> <strong>7,290</strong></span>
									</span> <span class="ad-item__tag">무료반품</span> <span
										class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(398)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-4.jpg"
											alt=""></span> <span class="ad-item__name">밀리랩 로카티 ROKA
											반팔 티셔츠 2P</span> <span class="ad-item__price"> <span
											class="was">할인 <del>25,800</del></span> <span class="now"><em
												class="rate">36%</em> <strong>16,500</strong></span>
									</span> <span class="ad-item__tag">무료반품</span> <span
										class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(6,543)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-5.jpg"
											alt=""></span> <span class="ad-item__name">5장 모던프로 스포츠
											기능성 드라이 라운드 반팔 티셔츠</span> <span class="ad-item__price"> <span
											class="was">할인 <del>120,000</del></span> <span class="now"><em
												class="rate">87%</em> <strong>14,800</strong></span>
									</span> <span class="ad-item__tag">무료반품</span> <span
										class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(357)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-6.jpg"
											alt=""></span> <span class="ad-item__name">NEOX 네옥스 쿨론
											자카드 검정 라운드넥 반팔 티셔츠</span> <span class="ad-item__price"> <span
											class="was">할인 <del>34,900</del></span> <span class="now"><em
												class="rate">77%</em> <strong>7,910</strong></span>
									</span> <span class="ad-item__tag">무료반품</span> <span
										class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(76)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-7.jpg"
											alt=""></span> <span class="ad-item__name">쿨링 ROKA 로카티
											반팔 단체 티셔츠 반티 체육대회 유니폼</span> <span class="ad-item__price"> <span
											class="was">할인 <del>20,000</del></span> <span class="now"><em
												class="rate">56%</em> <strong>8,800</strong></span>
									</span> <span class="ad-item__tag">무료반품</span> <span
										class="ad-item__ship">${deliveryDate} 도착 보장</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(6,384)</span>
								</a></li>
								<li class="ad-item"><a href="#"> <span
										class="ad-item__thumb"><img
											src="${pageContext.request.contextPath}/images/product-detail/ad-8.jpg"
											alt=""></span> <span class="ad-item__name">아소트 냉감 통기성
											메쉬 로카티 코리아 아미 반팔 티셔츠</span> <span class="ad-item__price"> <span
											class="was">할인 <del>21,900</del></span> <span class="now"><em
												class="rate">54%</em> <strong>9,920</strong></span>
									</span> <span class="ad-item__tag">무료배송</span> <span
										class="ad-item__ship">${deliveryDate} 도착 예정</span> <span
										class="ad-item__rating"><em class="stars">★★★★☆</em>(682)</span>
								</a></li>
					</ul>
					<button type="button" class="sdp-ads__next">
						<span class="blind">다음 상품</span>
					</button>
				</div>
			</section>

			<!-- ===== 광고 캐러셀 ② 오늘의 판매자 특가 =====
			     원본: div.personalizedGW (높이 496px) — 메인페이지에도 같은 이름의 섹션이 있음
			     실측: 카드 225px / 사진 160x160 / 제목 가운데 + 오른쪽에 1/5
			     ★ 메인과 다른 점: 카드마다 "N % 남음" 재고 게이지가 붙음 -->
			<section class="gw-deal">
				<div class="gw-deal__head">
					<h2>
						오늘의 <em>판매자 특가</em>
					</h2>
					<span class="gw-deal__page">1/5</span>
				</div>

				<ul class="gw-deal__list">
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-1.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">REPUBLIC OF KOREA 쿨링 반팔 티셔츠 RT002 -
								헬스 운동 기능성</span> <span class="gw-card__was">할인 <em>43%</em> <del>15,000</del></span>
							<strong class="gw-card__price">8,550원</strong> <span
							class="gw-card__ship">내일(목) 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(60)</span> <!-- 재고 게이지 — 길이가 데이터라서 인라인 style. ▶JSP: style="width:{d.left}%%" -->
							<span class="gw-card__stock"><span class="bar"><i
									style="width: 99%"></i></span>99 % 남음</span>
					</a></li>


					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-1.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">REPUBLIC OF KOREA 쿨링 반팔 티셔츠 RT002 -
								헬스 운동 기능성</span> <span class="gw-card__was">할인 <em>43%</em> <del>15,000</del></span>
							<strong class="gw-card__price">8,550원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(60)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-2.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">NEOX 네옥스 밀리터리 디지털 기능성 V넥 쿨론 반팔 티셔츠</span> <span
							class="gw-card__was">할인 <em>68%</em> <del>24,500</del></span> <strong
							class="gw-card__price">7,670원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(44)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-3.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">인디오 남성용 빅사이즈 밀리터리 카모플라쥬 쿨링 반팔티셔츠</span> <span
							class="gw-card__was">할인 <em>10%</em> <del>10,800</del></span> <strong
							class="gw-card__price">9,720원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(26)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-4.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">NEOX 네옥스 쿨론 밀리터리 디지털 라운드넥 로카 런닝</span> <span
							class="gw-card__was">할인 <em>80%</em> <del>34,900</del></span> <strong
							class="gw-card__price">6,950원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(14)</span>
					</a></li>
					<li class="gw-card"><a href="#"> <span
							class="gw-card__thumb"><img
								src="${pageContext.request.contextPath}/images/product-detail/gw-5.jpg"
								alt=""></span> <span class="gw-card__tag">특가진행중</span> <span
							class="gw-card__name">FLY 남녀공용 기능성 드라이 스포츠 반팔 티셔츠 헬스 러닝</span> <span
							class="gw-card__was">할인 <em>11%</em> <del>6,490</del></span> <strong
							class="gw-card__price">5,770원</strong> <span
							class="gw-card__ship">${deliveryDate} 도착 보장</span> <span
							class="gw-card__rating"><em class="stars">★★★★☆</em>(216)</span>
					</a></li>
				</ul>
			</section>

			<!-- ===== 탭줄 (상품상세 / 상품평 / 상품문의 / 배송·교환·반품) =====
           원본 실측: 4칸 균등, 칸 높이 49px, 위 여백 50px,
                     글자 16px/700/#555, 테두리 1px #ccc

           ★ 원본은 스크롤 위치를 따라 활성 탭이 자동으로 바뀌는 방식(scroll-spy)임.
             (페이지 맨 위에서 재보면 활성 탭이 아예 없어서 그걸로 확인함)
             지금은 JS 없이 "상품상세"에 .is-on 을 고정으로 붙여둠.
             나머지 3개는 아직 그 섹션을 안 만들어서 링크가 갈 곳이 없음 —
             다음 단계에서 리뷰·문의·배송 섹션을 만들면 그때 살아남 -->
			<!-- ★ 스크롤을 내려도 화면 위에 붙어 있음(sticky) + 지금 보고 있는 구간에 따라
			     활성 탭이 저절로 바뀜(scroll-spy). 원본이 그렇게 동작함 (2026-08-21 반영) -->
			<nav class="detail-tabs">

				<a href="#detail" class="is-on">상품상세</a>
				<!-- <a href="#reviews">상품평 (2,033)</a> -->
				<a href="#reviews">상품평 (${reviewCount})</a> <a href="#qna">상품문의</a>
				<a href="#delivery">배송/교환/반품 안내</a>
			</nav>

			<!-- ===== 필수 표기 정보 =====
           원본: div#itemBrief — 법으로 표기가 의무인 항목들.
           원본도 값이 전부 "상품 상세페이지 참조"로만 돼 있음 (판매자가 안 채운 것) -->
			<div class="item-brief" id="detail">
				<p class="item-brief__title">필수 표기 정보</p>

				<!-- 한 줄에 [라벨][값][라벨][값] 4칸씩 들어감.
             ▶JSP: &lt;c:forEach&gt; 로 감쌀 자리 — 항목 수가 카테고리마다 다름
                   (의류는 소재·치수, 식품은 원산지·유통기한 …) -->
				<table class="item-brief__table">
					<tbody>
						<tr>
							<th>제품 소재</th>
							<td>상품 상세페이지 참조</td>
							<th>색상</th>
							<td>상품 상세페이지 참조</td>
						</tr>
						<tr>
							<th>치수</th>
							<td>상품 상세페이지 참조</td>
							<th>제조자(수입자)</th>
							<td>상품 상세페이지 참조</td>
						</tr>
						<tr>
							<th>제조국</th>
							<td>상품 상세페이지 참조</td>
							<th>세탁방법 및 취급시 주의사항</th>
							<td>상품 상세페이지 참조</td>
						</tr>

						<!-- 여기부터는 "더보기" 를 눌러야 보이는 줄들.
                 처음엔 CSS 로 감춰두고(.is-folded), JS 가 클래스를 떼면 나타남 -->
						<tr class="is-folded">
							<th>제조연월</th>
							<td>상품 상세페이지 참조</td>
							<th>품질보증기준</th>
							<td>관련 법 및 소비자 분쟁 해결 기준에 따름</td>
						</tr>
						<tr class="is-folded">
							<th>A/S 책임자와 전화번호</th>
							<td>판매자에게 문의</td>
							<th>취급시 주의사항</th>
							<td>상품 상세페이지 참조</td>
						</tr>
					</tbody>
				</table>

				<!-- 눌러도 아직 아무 일 없음. 나중에 접힌 줄을 펴는 자리 -->
				<!-- 눌러서 접힌 줄을 펴고 접음. 글자도 같이 바뀜 (js/product.js) -->
				<button type="button" class="item-brief__more" aria-expanded="false">필수
					표기 정보 더보기</button>
			</div>

			<!-- ===== 사기 거래 경고 =====
           원본 실측: 배경 #fce9e6 / 글자 #e12705 / 14px / padding 18px 16px / 모서리 4px
           ! 아이콘은 이미지가 아니라 CSS 로 그림(빨간 동그라미 + 느낌표) -->
			<p class="fraud-notice">판매자가 현금거래를 요구하면 거부하시고 즉시 사기 거래 신고센터
				(1670-9832)에 신고하시기 바랍니다.</p>

			<!-- ===== 상세설명 =====
           원본: div.product-detail-content — 이 페이지에서 제일 긴 영역(10,140px)인데
                 구조는 제일 단순함. 판매자가 올린 **긴 이미지 3장**이 전부.
                 폭 780px 고정 + 가운데 정렬(margin:0 auto).

           ★ 임시 ★ 이미지가 없어서 회색 상자로 대신함.
             원본 이미지는 780 x 3661 / 780 x 3661 / 780 x 2818 이었음.
             우리 상자는 780 x 1200 으로 잡아둠 (그대로 흉내내면 화면이 너무 길어져서 확인이 힘듦)
             ▶JSP: &lt;c:forEach items="{p.detailImages}"&gt; 로 감쌀 자리 —
                   상세설명 이미지는 상품마다 장수가 다름 -->
			<div class="product-detail-content">
				<div class="detail-image">
					<img
						src="${pageContext.request.contextPath}/images/product-detail/detail-1.jpg"
						alt="">
				</div>
				<div class="detail-image">
					<img
						src="${pageContext.request.contextPath}/images/product-detail/detail-2.jpg"
						alt="">
				</div>
				<div class="detail-image">
					<img
						src="${pageContext.request.contextPath}/images/product-detail/detail-3.jpg"
						alt="">
				</div>
			</div>



			<!-- ===== 상품 리뷰 =====
           원본: div#sdpReview.product-review (높이 3,320px)
           실측: 위에 border-top 1px #333 / 위 여백 30px
                 좌우 2단 — 왼쪽 요약 360 / 오른쪽 목록 821 / 사이 24 (원본 폭 1205 기준)
                 우리는 본문이 1020 이라 왼쪽 300 고정 + 오른쪽은 남는 만큼 -->
			<section class="product-review" id="reviews">
				<h2 class="review-title">상품 리뷰</h2>

				<div class="review-body">

					<!-- ── 왼쪽: 요약 ───────────────────────── -->
					<div class="review-summary">

						<!-- 별점 + 리뷰 개수
                 ★ 옆의 큰 숫자는 "평균 평점"이 아니라 **리뷰 개수**임.
                   원본이 `★★★★☆ 17` 인데 그 상품 평점은 4.2 이고 리뷰가 17개였음
                   (JSON-LD 의 ratingValue 4.2 / ratingCount 17 로 확인).
                   별 그림이 평점을, 숫자가 개수를 나타내는 구조 -->
						<div class="review-score">
							<span class="stars">★★★★☆</span> <strong>${reviewCount}</strong>
						</div>

						<!-- 별점 분포 막대
                 ★ 막대 길이를 style="width:78%" 처럼 인라인으로 준 이유:
                   이 값만 데이터에 따라 매번 달라지기 때문. CSS 파일에는 못 적음.
                   ▶JSP 로 가면 style="width:{r.percent}%" 가 될 자리 -->
						<ul class="score-graph">
							<li><span class="label">최고</span> <span class="bar"><i
									style="width: 78%"></i></span> <span class="pct">78%</span></li>
							<li><span class="label">좋음</span> <span class="bar"><i
									style="width: 5%"></i></span> <span class="pct">5%</span></li>
							<li><span class="label">보통</span> <span class="bar"><i
									style="width: 0%"></i></span> <span class="pct">0%</span></li>
							<li><span class="label">별로</span> <span class="bar"><i
									style="width: 0%"></i></span> <span class="pct">0%</span></li>
							<li><span class="label">나쁨</span> <span class="bar"><i
									style="width: 17%"></i></span> <span class="pct">17%</span></li>
						</ul>

						<!-- 구매자 설문 요약 -->
						<dl class="review-survey">
							<div class="survey-row">
								<dt>사이즈</dt>
								<dd>정사이즈예요</dd>
								<span class="pct">80%</span>
							</div>
							<div class="survey-row">
								<dt>색상</dt>
								<dd>화면과 비슷해요</dd>
								<span class="pct">40%</span>
							</div>
						</dl>

						<!-- ★ "자세히 보기" 를 누르면 열리는 항목별 전체 분포 (2026-08-24 추가)
						     원본을 Playwright 로 직접 열어서 확인함 — 모달이 아니라 **그 자리에서 아래로 펼쳐짐**.
						     원본 실측: 항목명 110px 고정 / 막대 8px·radius 4px / 퍼센트 38px 우측정렬 /
						               사이 gap 12px, 그룹 안 gap 8px, 그룹 사이 gap 24px, 글자 14px/17px
						     ★ 1등 항목만 막대가 시안색(#34CAE2), 나머지는 회색 — 원본이 그렇게 구분함
						       (그래서 클래스 .is-top 을 1등에만 붙임)
						     평소엔 CSS 가 감춰두고, .is-open 이 붙으면 나타남 (js/product.js setupSurveyMore)
						     ▶JSP: &lt;c:forEach items="{survey}"&gt; 로 감쌀 자리 — 설문 항목 수가 카테고리마다 다름
						       (의류는 사이즈·색상, 식품은 맛·양 …). 퍼센트는 style="width:{o.percent}%" -->
						<div class="survey-detail">
							<div class="survey-group">
								<p class="survey-group__title">사이즈</p>
								<div class="survey-opt is-top">
									<span class="name">정사이즈예요</span> <span class="bar"><i
										style="width: 80%"></i></span> <span class="pct">80%</span>
								</div>
								<div class="survey-opt">
									<span class="name">생각보다 커요</span> <span class="bar"><i
										style="width: 20%"></i></span> <span class="pct">20%</span>
								</div>
							</div>
							<div class="survey-group">
								<p class="survey-group__title">색상</p>
								<div class="survey-opt is-top">
									<span class="name">화면과 비슷해요</span> <span class="bar"><i
										style="width: 40%"></i></span> <span class="pct">40%</span>
								</div>
								<div class="survey-opt">
									<span class="name">화면과 같아요</span> <span class="bar"><i
										style="width: 40%"></i></span> <span class="pct">40%</span>
								</div>
								<div class="survey-opt">
									<span class="name">화면과 달라요</span> <span class="bar"><i
										style="width: 20%"></i></span> <span class="pct">20%</span>
								</div>
							</div>
						</div>

						<!-- 글자가 "자세히 보기" ↔ "접기" 로 바뀜 (원본과 동일). 화살표는 CSS ::after 로 그림 -->
						<button type="button" class="survey-more" aria-expanded="false">자세히
							보기</button>

						<!-- 안내 문구 — 리뷰가 상품(productId)에 붙지 판매자에 붙는 게 아니라는 뜻.
                 나중에 DB 설계할 때 리뷰 테이블이 어디에 연결되는지와 같은 얘기 -->
						<p class="review-notice">동일한 상품에 대해 작성된 상품 리뷰로, 판매자는 다를 수
							있습니다.</p>
						<a href="#" class="review-policy">상품리뷰 운영원칙</a>
					</div>

					<!-- ── 오른쪽: 목록 ───────────────────────── -->
					<div class="review-list">

						<!-- 리뷰 사진 모아보기 — 누르면 갤러리 모달이 열림 (js/product.js setupReviewGallery)

						     ★ 안이 비어 있는 게 맞음. JS 가 **아래 리뷰 카드들의 .review-photos 를 훑어서**
						       사진을 모아 여기에 채움.
						     ★ 왜 이렇게 바꿨나 (2026-08-24):
						       전에는 여기에 사진 목록을 손으로 적고 data-review 로 주인을 표시했음.
						       그런데 "어느 사진이 누구 리뷰인지" 가 갤러리와 리뷰 카드 **두 군데**에 적히는 구조라
						       실제로 어긋났음 — 갤러리는 review-4 를 최*영 것이라 했는데
						       최*영 카드에는 사진이 없어서, 전체보기로 가면 사진이 없었음.
						       이제 사진의 출처가 **리뷰 카드 하나뿐**이라 어긋날 수가 없음.
						     ▶JSP: 카드 안 사진만 &lt;c:forEach&gt; 로 찍어내면 이 줄은 그대로 두면 됨 -->
						<ul class="review-gallery"></ul>

						<!-- 정렬 / 검색 / 별점 필터 (2026-08-21 실제로 동작하게 만듦 — js/product.js setupReviewTools)
						     ★ 원본에 "별점순"은 없음. 베스트순·최신순 두 개뿐이고, 별점은 정렬이 아니라
						       옆의 select 로 "필터"만 함 (vp_04_review.jpeg 로 확인). 지금 구조 그대로 맞음.
						     각 .review-item 의 data-helpful 은 "도움이 돼요" 누적 수 — 화면엔 숫자가 안 보이지만
						     베스트순 정렬 기준으로 씀. 지금은 지어낸 더미 값.
						     ▶JSP: 정렬·필터·검색 전부 서버 쿼리(ORDER BY / WHERE)로 넘어갈 자리.
						       지금 JS 는 그 전까지 화면에서만 흉내내는 것 -->
						<div class="review-tools">
							<div class="review-sort">
								<a href="#" class="is-on" data-sort="best">베스트순</a> <a href="#"
									data-sort="latest">최신순</a>
							</div>
							<input type="text" class="review-search" placeholder="검색어를 입력하세요">
							<select class="review-filter">
								<option value="">모든 별점</option>
								<option value="5">5점</option>
								<option value="4">4점</option>
								<option value="3">3점</option>
								<option value="2">2점</option>
								<option value="1">1점</option>
							</select>
						</div>

						<!-- 리뷰 카드
                 ▶JSP: &lt;c:forEach items="{reviews}" var="r"&gt; 로 감쌀 자리.
                   그래서 3개를 **완전히 같은 구조**로 만들어둠 (안에 든 글자만 다름)
                   
                 ★ 원본은 리뷰 카드가 10개라 이 영역이 3,320px 인데 우리는 3개라 1,447px (44%).
                   **모자란 게 아니라 개수만 다른 것.** 카드 하나의 구조·크기는 원본과 같음.
                   DB 를 붙이면 &lt;c:forEach&gt; 가 리뷰 수만큼 찍어내므로 저절로 채워짐 —
                   지금 개수를 늘리려고 카드를 복붙할 필요 없음 (2026-08-21 확인) -->
						<c:choose>
							<c:when test="${not empty reviews}">
								<c:forEach items="${reviews}" var="r">
									<article class="review-item" data-rating="${r.productRating}"
										data-review-no="${r.reviewNo}">
										<!-- 작성자 -->
										<div class="review-head">
											<span class="review-avatar"></span>
											<div class="review-writer">
												<strong class="name"> <c:out
														value="${r.maskedName}" />
												</strong>
												<div class="meta">
													<span class="stars"> ${r.ratingStars} </span> <span
														class="date"> <fmt:formatDate
															value="${r.reviewDate}" pattern="yyyy.MM.dd" />
													</span>
												</div>
											</div>
										</div>
										<!-- 구매 옵션 -->
										<c:if test="${not empty r.optionText}">
											<p class="review-option">
												<c:out value="${r.optionText}" />
											</p>
										</c:if>
										<!-- 한줄 요약 -->
										<c:if test="${not empty r.reviewSummary}">
											<strong class="review-summary-text"> <c:out
													value="${r.reviewSummary}" />
											</strong>
										</c:if>
										<!-- 상세 리뷰 -->
										<p class="review-text">
											<c:out value="${r.reviewContent}" />
										</p>
										<!-- 하단 -->
										<div class="review-foot">

											<button type="button" class="btn-helpful"
												data-review-no="${r.reviewNo}">도움이 돼요</button>
											<a href="#" class="btn-report" data-review-no="${r.reviewNo}">
												신고하기 </a>
										</div>
									</article>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<p class="review-empty">등록된 상품 리뷰가 없습니다.</p>
							</c:otherwise>
						</c:choose>

						<!-- 페이지네이션 — 원본 실측(ref/product/vp_05_review2.jpeg): "‹ (1) 2 ›" 모양,
						     지금 고른 페이지만 파란 원 테두리.
						     번호 버튼은 JS(setupReviewTools)가 리뷰 개수를 보고 그때그때 새로 그림 —
						     정렬·검색·별점 필터를 걸면 보이는 개수가 바뀌니까 페이지 수도 같이 바뀌어야 함.
						     ▶JSP: 총 페이지 수 = 서버가 리뷰 개수(2,033) ÷ 페이지당 개수로 계산해서 내려줄 자리.
						       지금은 더미 리뷰 5개를 3개씩 나눠 2페이지로 흉내냄 (js/product.js PAGE_SIZE). -->
						<c:if test="${fn:length(reviews) > 3}">
							    <nav class="review-pagination">
							        <button type="button"
							                class="page-prev"
							                aria-label="이전 페이지"
							                disabled>
							            ‹
							        </button>
							        <span class="page-numbers"></span>
							        <button type="button"
							                class="page-next"
							                aria-label="다음 페이지">
							            ›
							        </button>
							    </nav>
							</c:if>
					<!-- //.review-list -->
				</div>
			</section>


			<!-- ===== 상품문의 =====
           원본: div#btf-qna (높이 500px)
           실측: 사방 테두리 1px #e5e7eb / 위아래 여백 26 / 안쪽 여백 30px 16px -->
			<section class="prod-qna" id="qna">
				<div class="qna-head">
					<h2 class="qna-title">상품문의</h2>
					<button type="button" class="btn-ask">문의하기</button>
				</div>

				<!-- 안내 5줄 — 실측 13px / #555. 일부만 굵게 (원본 그대로) -->
				<ul class="qna-guide">
					<li>구매한 상품의 <strong>취소/반품은 마이굿팡 구매내역에서 신청</strong> 가능합니다.
					</li>
					<li>상품문의 및 후기게시판을 통해 취소나 환불, 반품 등은 처리되지 않습니다.</li>
					<li><strong>가격, 판매자, 교환/환불 및 배송 등 해당 상품 자체와 관련 없는 문의는
							고객센터 내 1:1 문의하기</strong>를 이용해주세요.</li>
					<li><strong>"해당 상품 자체"와 관계없는 글, 양도, 광고성, 욕설, 비방, 도배
							등의 글은 예고 없이 이동, 노출제한, 삭제 등의 조치가 취해질 수 있습니다.</strong></li>
					<li>공개 게시판이므로 전화번호, 메일 주소 등 고객님의 소중한 개인정보는 절대 남기지 말아주세요.</li>
				</ul>

				<!-- 검색창 — 원본은 테두리가 유난히 진하고 두꺼움(#212b36 2px).
             돋보기는 이미지가 아니라 CSS 로 그림 (동그라미 + 45도 손잡이) -->
				<div class="qna-search">
					<input type="text" placeholder="궁금한 내용을 단어나 키워드로 검색하세요.">
					<button type="button" class="btn-search-qna">
						<span class="blind">검색</span>
					</button>
				</div>

				<p class="qna-empty">아직 문의가 없습니다.</p>

				<div class="qna-report">
					<span>판매 부적격 상품 또는 허위과장광고 및 지식재산권을 침해하는 상품의 경우 신고하여 주시기
						바랍니다.</span>
					<button type="button" class="btn-report-qna">신고하기</button>
				</div>
			</section>

			<!-- ===== 배송/교환/반품 안내 + 판매자 정보 =====
           원본: div#sdpEtc (높이 1,377px)

           ★ 여기 글은 거의 전부 **모든 상품이 똑같이 쓰는 고정 문구**임.
             상품마다 달라지는 건 맨 아래 "판매자 정보" 표뿐.
             ▶JSP 로 가면 이 절 전체를 &lt;jsp:include page="/WEB-INF/views/etc-policy.jsp"/&gt;
               로 빼고, 판매자 정보만 {seller.xxx} 로 채우면 됨 -->
			<section class="prod-etc" id="delivery">

				<!-- ── 배송정보 ───────────────────────── -->
				<h3 class="etc-title">배송정보</h3>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>배송방법</th>
							<td>순차배송</td>
							<!-- rowspan=2 : 세로로 두 칸을 하나로 합침.
                   왼쪽 두 줄(배송방법/묶음배송)에 배송비 한 칸이 걸쳐 있는 모양 -->
							<th rowspan="2">배송비</th>
							<td rowspan="2">무료배송<br> - 로켓배송 상품 중 19,800원 이상 구매 시
								무료배송<br> - 도서산간 지역 추가비용 없음
							</td>
						</tr>
						<tr>
							<th>묶음배송 여부</th>
							<td>가능</td>
						</tr>
						<tr>
							<th>배송기간</th>
							<!-- colspan=3 : 가로로 세 칸을 하나로 합침 -->
							<td colspan="3">• 굿팡친구 배송 지역 : 주문 및 결제 완료 후, 1-2일 이내 도착<br>
								• 굿팡친구 미배송 지역 : 주문 및 결제 완료 후, 2-3일 이내 도착<br> - 도서 산간 지역 등은
								하루가 더 소요될 수 있습니다.<br> • 천재지변, 물량 수급 변동 등 예외적인 사유 발생 시, 다소
								지연될 수 있는 점 양해 부탁드립니다.
							</td>
						</tr>
					</tbody>
				</table>

				<!-- ── 교환/반품 안내 ──────────────────── -->
				<h3 class="etc-title">교환/반품 안내</h3>
				<p class="etc-note">
					• 교환/반품에 관한 일반적인 사항은 판매자가 제시사항보다 관계법령이 우선합니다.<br> 다만, 판매자의
					제시사항이 관계법령보다 소비자에게 유리한 경우에는 판매자 제시사항이 적용됩니다.
				</p>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>교환/반품 비용</th>
							<td>• 와우멤버십 회원: 무료로 반품/교환 가능<br> • 와우멤버십 회원 아닌 경우:<br>
								1) [총 주문금액] - [반품 상품금액] = 19,800원 미만인 경우 반품비 5,000원<br> 2)
								[총 주문금액] - [반품 상품금액] = 19,800원 이상인 경우 반품비 2,500원
							</td>
						</tr>
						<tr>
							<th>교환/반품 신청 기준일</th>
							<td>• 단순변심에 의한 로켓배송 상품의 교환/반품은 제품 수령 후 30일 이내까지, 교환/반품 제한사항에
								해당하지 않는 경우에만 가능 (교환/반품 비용 고객부담)<br> • 상품의 내용이 표시·광고의 내용과 다른
								경우에는 상품을 수령한 날부터 3개월 이내, 그 사실을 안 날 또는 알 수 있었던 날부터 30일 이내에 청약철회
								가능
							</td>
						</tr>
					</tbody>
				</table>

				<!-- ── 교환/반품 제한사항 ──────────────── -->
				<h3 class="etc-title">교환/반품 제한사항</h3>
				<ul class="etc-list">
					<li>주문/제작 상품의 경우, 상품의 제작이 이미 진행된 경우</li>
					<li>상품 포장을 개봉하여 사용 또는 설치 완료되어 상품의 가치가 훼손된 경우 (단, 내용 확인을 위한 포장
						개봉의 경우는 예외)</li>
					<li>고객의 사용, 시간경과, 일부 소비에 의하여 상품의 가치가 현저히 감소한 경우</li>
					<li>세트상품 일부 사용, 구성품을 분실하였거나 취급 부주의로 인한 파손/고장/오염으로 재판매 불가한 경우</li>
					<li>모니터 해상도의 차이로 인해 색상이나 이미지가 실제와 달라, 고객이 단순 변심으로 교환/반품을 무료로
						요청하는 경우</li>
					<li>제조사의 사정 (신모델 출시 등) 및 부품 가격 변동 등에 의해 무료 교환/반품으로 요청하는 경우</li>
				</ul>
				<p class="etc-note">※ 각 상품별로 아래와 같은 사유로 취소/반품이 제한될 수 있습니다.</p>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>의류/잡화/수입명품</th>
							<td>• 상품의 택(TAG) 및 라벨의 멸실 또는 훼손, 상품의 사용 또는 훼손, 구성품 누락으로 상품의
								가치가 현저히 감소된 경우</td>
						</tr>
						<tr>
							<th>계절상품/식품/화장품</th>
							<td>• 신선/냉장/냉동 상품의 단순변심의 경우<br> • 뷰티 상품 이용 시 트러블(알러지,
								붉은 반점, 가려움, 따가움)이 발생하는 경우, 진료 확인서 및 소견서 등을 증빙하면 환불이 가능 (제반비용
								고객부담)
							</td>
						</tr>
						<tr>
							<th>전자/가전/설치상품</th>
							<td>• 전자제품 특성상 정품 스티커가 제거되었거나, 설치 또는 사용 이후에 단순변심인 경우<br>
								• 설치 또는 사용하여 재판매가 어려운 경우, 액정이 있는 상품의 전원을 켠 경우<br> • 홀로그램 등을
								분리, 분실, 훼손하여 상품의 가치가 현저히 감소하여 재판매가 불가할 경우 (노트북, 데스크탑 PC 등)
							</td>
						</tr>
						<tr>
							<th>자동차용품</th>
							<td>• 상품을 개봉하여 장착한 이후 단순변심인 경우</td>
						</tr>
						<tr>
							<th>CD/DVD/GAME/BOOK</th>
							<td>• 복제가 가능한 상품의 포장 등을 훼손한 경우</td>
						</tr>
					</tbody>
				</table>

				<!-- ── 판매자 정보 ────────────────────
             ★ 이 표만 상품(정확히는 판매자)마다 달라짐. 나머지는 전부 고정 문구.
               ▶JSP: {seller.name} {seller.tel} … 로 바뀔 자리 -->
				<h3 class="etc-title">판매자 정보</h3>
				<table class="policy-table">
					<tbody>
						<tr>
							<th>소비자상담 및 교환반품 문의</th>
							<td colspan="3">굿팡고객센터 1577-0000<br> <span class="sub">배송
									및 환불/교환에 대한 문의는 마이굿팡-고객센터 내 상담하기를 이용해주세요.</span>
							</td>
						</tr>
						<!-- <tr>
							<th>상호/대표자</th>
							<td>베이직스 / 김미영</td>
							<th>사업장 소재지</th>
							<td>경기도 부천시 원미구 중동로 108 114동 1904호</td>
						</tr>
						<tr>
							<th>e-mail</th>
							<td>help@basics.co.kr</td>
							<th>연락처</th>
							<td>010-0000-0000</td>
						</tr>
						<tr>
							<th>통신판매업 신고번호</th>
							<td>2026-부천원미-1026</td>
							<th>사업자번호</th>
							<td>886-34-01859</td>
						</tr> -->
						<tr>
							<th>상호/대표자</th>
							<td>${p.storeName}/ ${p.ceoName}</td>
							<th>사업장 소재지</th>
							<td>${p.businessAddress}${p.businessDetailAddress}</td>
						</tr>
						<tr>
							<th>e-mail</th>
							<td>${p.email}</td>
							<th>연락처</th>
							<td>${p.phone}</td>
						</tr>
						<tr>
							<th>통신판매업 신고번호</th>
							<td>${p.mailOrderNo}</td>
							<th>사업자번호</th>
							<td>${p.businessNo}</td>
						</tr>
						<tr>
							<th>구매안전 서비스</th>
							<td colspan="3">02-006-00042 <a href="#" class="link">서비스
									가입사실 확인 &gt;</a><br> <span class="sub">본 판매자는 고객님의
									안전거래를 위해 관련 법률에 의거하여 굿팡페이의 구매안전서비스를 적용하고 있습니다.</span>
							</td>
						</tr>

					</tbody>
				</table>

				<p class="etc-footnote">미성년자가 체결한 계약은 법정대리인이 동의하지 않는 경우 본인 또는
					법정대리인이 취소할 수 있습니다. 굿팡은 통신판매중개자로서 통신판매의 당사자가 아니며, 광고, 고시정보, 상품 주문,
					배송 및 환불의 의무와 책임은 각 판매자에게 있습니다.</p>
			</section>

		</div>
	</main>


	<!-- ★ 어사이드(날개배너) 없음 — 2026-08-20 판단
	     원본 쿠팡 상세페이지에도 **안 나옴**. DOM 에는 <article id="wa-sidebar"> 가 있지만
	     class 가 `fw-absolute fw-hidden` 뿐이라 숨겨져 있음 (Tailwind 의 fw-hidden = display:none).
	     메인페이지 원본은 같은 자리에 `s600:!fw-flex s1024:fw-block` 이 더 붙어서 되살아남.
	     → 쿠팡은 두 페이지에 같은 껍데기를 쓰되 상세페이지에서는 꺼버리는 것.
	     스크린샷(ref/product/vp_01_atf.jpeg) 오른쪽이 완전히 빈 것으로도 확인함.
	     ※ 이걸 빼면서 "날개배너가 푸터 아래로 삐져나오던 문제"도 같이 사라짐 -->


	<!-- 맨 위로 버튼 (스크롤 내리면 나타남)
       ↑ 화살표는 이미지가 아니라 CSS 로 그림 (테두리 2개만 남기고 45도 회전 —
         장바구니 ›, 오늘의 발견 > 와 같은 기법). 글자는 화면낭독기용으로만 남겨둠 -->
	<button type="button" id="goto-top" class="goto-top">
		<span class="blind">맨 위로</span>
	</button>


	<!-- ==================================================
       FOOTER — 페이지 맨 아래. 회사정보 / 약관 / 고객센터
       쿠팡 원본: footer#wa-footer (높이 428px)
       ================================================== -->
	<!-- ★ 이 푸터는 inc/footer.jsp 에서 그대로 옮겨온 것. 위 헤더와 같은 규칙 -->
	<jsp:include page="inc/footer.jsp" />


	<!-- ==================================================
	     리뷰 사진 갤러리 — 2단 구조 (2026-08-24)
	     원본을 Playwright 로 직접 열어서 확인한 흐름:
	       ① 리뷰 사진 썸네일 클릭 → "갤러리" 모달 (사진 전체를 격자로)
	       ② 그 안의 썸네일을 또 클릭 → "사진 뷰어" (큰 사진 + 좌우 화살표 + 리뷰 글)
	     둘 다 여기 미리 만들어두고 평소엔 CSS 가 감춰둠. 안의 내용은 JS 가 채움
	     (js/product.js setupReviewGallery).

	     ★ 왜 body 끝에 두는가: 모달은 화면 전체를 덮어야 하는데, 리뷰 섹션 안에 두면
	       부모의 overflow·position·z-index 에 갇혀서 잘리거나 뒤에 깔릴 수 있음.
	       화면 전체를 덮는 것은 문서 맨 끝에 두는 게 정석
	     ================================================== -->

	<!-- ── ① 갤러리 모달 ── 원본 실측: 흰 패널 982px / 덮개 rgba(17,17,17,0.87) / z-index 9999 -->
	<div class="review-gallery-modal" hidden>
		<div class="gallery-panel" role="dialog" aria-modal="true"
			aria-label="리뷰 사진 갤러리">
			<div class="gallery-head">
				<strong class="gallery-title">갤러리</strong> <span class="gallery-sub">섬네일을
					클릭하면 더 많은 상품평 정보를 볼 수 있어요</span>
				<button type="button" class="gallery-close">
					<span class="blind">갤러리 닫기</span>
				</button>
			</div>
			<div class="gallery-body">
				<!-- 사진 개수 — JS 가 숫자를 채움 -->
				<p class="gallery-count">
					이미지 <strong>0</strong>
				</p>
				<!-- 격자 — JS 가 li 를 만들어 넣음 (원본 실측 140x140, radius 4px) -->
				<ul class="gallery-grid"></ul>
			</div>
		</div>
	</div>

	<!-- ── ② 사진 뷰어 ── 원본 실측: 어두운 패널 rgb(34,34,34) / 982px -->
	<div class="photo-viewer" hidden>
		<div class="viewer-panel" role="dialog" aria-modal="true"
			aria-label="리뷰 사진 크게 보기">
			<!-- 위: 누가 쓴 리뷰인지 -->
			<div class="viewer-head">
				<span class="viewer-avatar"></span>
				<div class="viewer-writer">
					<strong class="name"></strong>
					<div class="meta">
						<span class="stars"></span> <span class="date"></span>
					</div>
				</div>
				<button type="button" class="viewer-close">
					<span class="blind">닫기</span>
				</button>
			</div>

			<!-- 가운데: 큰 사진 + 좌우 화살표 -->
			<div class="viewer-stage">
				<button type="button" class="viewer-prev">
					<span class="blind">이전 사진</span>
				</button>
				<img class="viewer-img" src="" alt="">
				<button type="button" class="viewer-next">
					<span class="blind">다음 사진</span>
				</button>
			</div>

			<!-- 사진 아래: 작은 썸네일 줄 (지금 보는 것에 파란 테두리). JS 가 채움 -->
			<ul class="viewer-thumbs"></ul>

			<!-- 맨 아래: 옵션 + 리뷰 글 + 전체보기 -->
			<div class="viewer-foot">
				<div class="viewer-text-wrap">
					<p class="viewer-option"></p>
					<p class="viewer-text"></p>
				</div>
				<button type="button" class="viewer-all">전체보기</button>
			</div>
		</div>
	</div>

	<!-- JS는 </body> 바로 앞에! HTML을 다 읽은 뒤에 실행되게 하려고 -->
	<script src="${pageContext.request.contextPath}/js/header.js"></script>
	<script src="${pageContext.request.contextPath}/js/product.js"></script>

</body>

</html>

