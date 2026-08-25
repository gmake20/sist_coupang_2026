<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="ko">

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>굿팡</title>

<link rel="preconnect" href="https://fonts.googleapis.com">

<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

<link rel="stylesheet"

	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">

<link rel="stylesheet" href="css/reset.css">

<!-- 1. 브라우저 기본 스타일 지우기 -->

<link rel="stylesheet" href="css/common.css">

<!-- 2. 헤더/푸터 (모든 페이지 공통) -->

<link rel="stylesheet" href="css/main.css">

<!-- 3. 메인페이지 전용 -->

</head>

<body>
<!-- 최상단 배너 -->
	<div class="coupang-top-banner">
		<div class="banner-middle">
			<a href="#"> <span class="top-banner-placeholder"
				style="background: #b5f3fe; color: #0b3d5c"> 오늘 밤 12시까지 주문해도 로켓배송은 내일 도착! <i class="arrow"></i>
			</span></a> <a href="#"> <span class="top-banner-placeholder"
				style="background: #80daff; color: #0b2d45"> <i	class="arrow"></i></span></a>
		</div>
	</div>
	 <!-- HEADER — 페이지 맨 위. 로고 / 검색 / 메뉴 -->
		<jsp:include page="/inc/header.jsp" />

	<!-- ==================================================

       MAIN — 이 페이지의 진짜 본문. 페이지당 딱 1개만!

       쿠팡 원본: div.home-page (본문 컬럼 max-width 1020px)

       ================================================== -->

	<main class="home-page">

		<!-- ===== 히어로 배너 (높이 450px) =====

         원본: section#todaysHot.main-today

         핵심 아이디어: 배경 이미지 6장을 position:absolute 로 "같은 자리에 겹쳐" 쌓아두고

         그중 1장만 보이게 함(.is-on). 나중에 JS가 이 클래스를 옮기면 슬라이드가 됨.

         폭이 두 종류라 상자가 두 겹이야:

           .main-today               ← 화면 전체 폭 (배경이 끝에서 끝까지 깔림)

           .main-today__selected-product ← 가운데 1020px (썸네일이 여기 오른쪽에 붙음)

         ※ 원본은 좌우 화살표와 "1/6" 표시도 CSS엔 있는데 실제 HTML엔 없음.

           안 쓰는 변형이라 안 만듦 -->

		<section class="main-today" id="todaysHot">

			<h2 class="blind">오늘의 추천</h2>

			<div class="main-today__desktop-slide">

				<!-- 배경 6장. 지금은 1번만 .is-on -->

				<img class="main-today__bg is-on" src="images/banners/hero-bg-1.jpg"

					alt=""> <img class="main-today__bg"

					src="images/banners/hero-bg-2.jpg" alt=""> <img

					class="main-today__bg" src="images/banners/hero-bg-3.jpg" alt="">

				<img class="main-today__bg" src="images/banners/hero-bg-4.jpg"

					alt=""> <img class="main-today__bg"

					src="images/banners/hero-bg-5.jpg" alt=""> <img

					class="main-today__bg" src="images/banners/hero-bg-6.jpg" alt="">

				<div class="main-today__selected-product">

					<!-- 배너를 클릭하면 이동할 투명 링크. 배경과 똑같이 6장을 겹쳐두고 1개만 활성.

               가운데 1020px 안에서만 클릭이 먹는 건 원본도 마찬가지 -->

					<div class="main-today__img-container">

						<a href="#" class="today-image is-on"><span class="blind">1번

								배너 바로가기</span></a> <a href="#" class="today-image"><span class="blind">2번

								배너 바로가기</span></a> <a href="#" class="today-image"><span class="blind">3번

								배너 바로가기</span></a> <a href="#" class="today-image"><span class="blind">4번

								배너 바로가기</span></a> <a href="#" class="today-image"><span class="blind">5번

								배너 바로가기</span></a> <a href="#" class="today-image"><span class="blind">6번

								배너 바로가기</span></a>

					</div>

					<!-- 오른쪽 위 썸네일 6개 (180 x 60). 지금 보고 있는 것은 .today-selected -->

					<ul class="main-today--thumb-nails">

						<li class="todayshot-right-thumbnail first-item today-selected">

							<a href="#"><img src="images/banners/hero-thumb-1.jpg"

								width="180" height="60" alt="1번 배너"><span class="mask"></span></a>

						</li>

						<li class="todayshot-right-thumbnail"><a href="#"><img

								src="images/banners/hero-thumb-2.jpg" width="180" height="60"

								alt="2번 배너"><span class="mask"></span></a></li>

						<li class="todayshot-right-thumbnail"><a href="#"><img

								src="images/banners/hero-thumb-3.jpg" width="180" height="60"

								alt="3번 배너"><span class="mask"></span></a></li>

						<li class="todayshot-right-thumbnail"><a href="#"><img

								src="images/banners/hero-thumb-4.jpg" width="180" height="60"

								alt="4번 배너"><span class="mask"></span></a></li>

						<li class="todayshot-right-thumbnail"><a href="#"><img

								src="images/banners/hero-thumb-5.jpg" width="180" height="60"

								alt="5번 배너"><span class="mask"></span></a></li>

						<li class="todayshot-right-thumbnail"><a href="#"><img

								src="images/banners/hero-thumb-6.jpg" width="180" height="60"

								alt="6번 배너"><span class="mask"></span></a></li>

					</ul>

				</div>

			</div>

		</section>

		<!-- ===== 본문 컬럼 + 날개배너 자리 =====

         원본 구조를 그대로 따라감:

           div.fw-flex.fw-justify-center      ← .content-row (가로 배치)

             ├─ div (flex-1, max-width 1020)  ← .content-col (진짜 본문)

             └─ div.right-bar-placeholder     ← 높이 1px 짜리 빈 상자

         빈 상자가 왜 필요하냐 — 날개배너는 position:absolute 라 자리를 안 차지함.

         그대로 두면 본문이 배너 밑으로 깔려서 겹침.

         그래서 본문 옆에 "폭만 차지하는 빈 칸"을 하나 세워두고 그 자리에 배너를 띄우는 것.

         ⚠ 히어로 배너(.main-today)는 이 컬럼 "밖"에 있어야 함 (원본도 그럼).

           전체 폭으로 깔려야 하는데 컬럼 안에 넣으면 1020px 로 묶임 -->

		<div class="content-row">

			<div class="content-col">

				<!-- ==========

           grid-template-columns: repeat(4,1fr) 는 그대로. half-width(2칸) / quarter-width(1칸).

           원본 li 순서(실측): half, half, quarter, quarter, quarter, quarter, half, quarter, quarter

             1행 = half + half         (2+2 = 4칸, 큰 배너 2개)

             2행 = quarter ×4          (1×4 = 4칸, 작은 배너 4개)

             3행 = half + quarter + quarter  (2+1+1 = 4칸, 큰 배너 1개 + 작은 배너 2개)

         ⚠ 진짜 배너 이미지가 없어서 지금은 회색 상자로 자리만 잡음.

           이미지가 생기면 .tti-placeholder 를

           <img class="tti-image" src="..." alt=""> 로 바꾸면 됨 -->

				<section class="today-discovery">

					<div class="title">

						<h2>오늘의 발견</h2>

						<h3>지금 가장 인기가 많은 요즘 HOT한 상품!</h3>

					</div>

					<div class="discovery-list">

						<ul class="banner-list prod-list">

							<li class="half-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="half-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="quarter-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="quarter-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="quarter-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="quarter-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<!-- 3행 시작 — half + quarter + quarter (원본 li 순서 그대로) -->

							<li class="half-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="quarter-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

							<li class="quarter-width"><a href="#"> <span

									class="tti-placeholder"></span>

									<div class="go-btn">

										<p>구매하기</p>

									</div> <span class="mask"> </span>

							</a></li>

						</ul>

					</div>

				</section>

				<!-- ===== 개인화 광고 캐러셀 4개 (#personalizedGW) =====

         클래스(.ad-carousel/.ad-card)로 새로 만듦 (css/main.css "2.5" 절 참고).

         ⚠ 상품명/가격/리뷰수 전부 더미 데이터 (사용자 승인함, 나중에 JSP로 교체 예정) -->

				<section class="personalized-gw">

					<!-- 1. 이 상품 놓치지 마세요 (개인화 광고) -->

					<div class="ad-carousel">

						<div class="ad-carousel-header">

							<h2>이 상품 놓치지 마세요</h2>

							<span class="ad-label">광고</span>

						</div>

						<div class="ad-carousel-body">

							<a href="#" class="move preview">이전 상품</a> <a href="#"

								class="move next">다음 상품</a>

							<ul class="ad-list">

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">저자극 약산성 폼클렌저 300ml, 1개</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>9,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(214)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">보습 크림 앰플 세럼 50ml, 2개</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>18,700원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(1523)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">진정 판테놀 수딩젤 300ml, 1개</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>7,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(8842)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">콜라겐 리프팅 마스크팩 10매, 1box</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>12,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(356)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">무향 저자극 선크림 SPF50+ 50ml</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>15,400원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(4021)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">브라이트닝 앰플 세트 3종</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>29,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(128)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">수분 진정 토너패드 70매</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>11,200원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(967)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">탄력 넥크림 목주름 케어 50ml</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>19,800원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(302)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">저자극 클렌징 오일 200ml</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>13,500원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(715)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">비타민C 브라이트닝 세럼 30ml</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>16,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(2210)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">모공 케어 클레이 마스크 100ml</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>14,300원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(488)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">약산성 바디워시 500ml, 2개</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>17,600원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(1350)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">데일리 립밤 3종 세트</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>8,400원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(622)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">각질 제거 필링 젤 150ml</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>10,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(1780)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">수분 아이크림 30ml, 1개</span> <span

										class="ad-tag ad-tag--free">무료배송</span>

										<div class="ad-price">

											<strong>22,400원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(940)</span>

										</div>

								</a></li>

							</ul>

						</div>

					</div>

					<!-- 2. 지금 이 상품이 필요하신가요? (개인화 광고) -->

					<div class="ad-carousel">

						<div class="ad-carousel-header">

							<h2>지금 이 상품이 필요하신가요?</h2>

							<span class="ad-label">광고</span>

						</div>

						<div class="ad-carousel-body">

							<a href="#" class="move preview">이전 상품</a> <a href="#"

								class="move next">다음 상품</a>

							<ul class="ad-list">

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">휴대용 미니 가습기 350ml, 화이트</span>

										<div class="ad-price">

											<del>22,000원</del>

											<span class="discount">20%</span><strong>17,600원</strong>

										</div>

										<div class="ad-unit-price">10ml당 503원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(890)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">저소음 탁상용 선풍기, 그레이</span>

										<div class="ad-price">

											<del>35,000원</del>

											<span class="discount">15%</span><strong>29,750원</strong>

										</div>

										<div class="ad-unit-price">1개당 29750원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(421)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">무선 충전 마우스패드 세트</span>

										<div class="ad-price">

											<del>28,000원</del>

											<span class="discount">30%</span><strong>19,600원</strong>

										</div>

										<div class="ad-unit-price">1개당 19600원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(156)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">스탠드형 전신 거울 40x150cm</span>

										<div class="ad-price">

											<del>68,000원</del>

											<span class="discount">25%</span><strong>51,000원</strong>

										</div>

										<div class="ad-unit-price">1개당 51000원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(78)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">접이식 캠핑 테이블 미니, 카키</span>

										<div class="ad-price">

											<del>31,000원</del>

											<span class="discount">18%</span><strong>25,420원</strong>

										</div>

										<div class="ad-unit-price">1개당 25420원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(340)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">실리콘 다회용 지퍼백 5종 세트</span>

										<div class="ad-price">

											<del>14,900원</del>

											<span class="discount">10%</span><strong>13,410원</strong>

										</div>

										<div class="ad-unit-price">1개당 13410원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(1204)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">USB 미니 청소기 무선</span>

										<div class="ad-price">

											<del>19,900원</del>

											<span class="discount">22%</span><strong>15,522원</strong>

										</div>

										<div class="ad-unit-price">1개당 15522원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(233)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">휴대용 보조배터리 10000mAh</span>

										<div class="ad-price">

											<del>24,900원</del>

											<span class="discount">12%</span><strong>21,912원</strong>

										</div>

										<div class="ad-unit-price">1개당 21912원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(987)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">원목 트레이 수납함 대형</span>

										<div class="ad-price">

											<del>17,500원</del>

											<span class="discount">8%</span><strong>16,100원</strong>

										</div>

										<div class="ad-unit-price">1개당 16100원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(65)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">전기 요거트 메이커 1L</span>

										<div class="ad-price">

											<del>33,000원</del>

											<span class="discount">28%</span><strong>23,760원</strong>

										</div>

										<div class="ad-unit-price">1개당 23760원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(199)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">접이식 빨래 건조대 대형</span>

										<div class="ad-price">

											<del>42,000원</del>

											<span class="discount">17%</span><strong>34,860원</strong>

										</div>

										<div class="ad-unit-price">1개당 34860원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(512)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">LED 스탠드 조명 3단계 밝기</span>

										<div class="ad-price">

											<del>26,500원</del>

											<span class="discount">24%</span><strong>20,140원</strong>

										</div>

										<div class="ad-unit-price">1개당 20140원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(733)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">욕실 코너 선반 2단 흡착식</span>

										<div class="ad-price">

											<del>15,900원</del>

											<span class="discount">11%</span><strong>14,150원</strong>

										</div>

										<div class="ad-unit-price">1개당 14150원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(289)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">차량용 컵홀더 확장 트레이</span>

										<div class="ad-price">

											<del>12,800원</del>

											<span class="discount">35%</span><strong>8,320원</strong>

										</div>

										<div class="ad-unit-price">1개당 8320원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(1044)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-name">발수 코팅 현관 매트 60x40</span>

										<div class="ad-price">

											<del>18,900원</del>

											<span class="discount">9%</span><strong>17,200원</strong>

										</div>

										<div class="ad-unit-price">1개당 17200원</div>

										<div class="ad-rating">

											<span class="stars">★★★★★</span><span class="count">(176)</span>

										</div>

								</a></li>

							</ul>

						</div>

					</div>

					<!-- 3. 오늘의 판매자 특가 -->

					<div class="ad-carousel ad-carousel--sale">

						<div class="ad-carousel-header">

							<h2>

								오늘의 <em>판매자</em> 특가

							</h2>

							<span class="ad-page"></span>

						</div>

						<div class="ad-carousel-body">

							<a href="#" class="move preview">이전 상품</a> <a href="#"

								class="move next">다음 상품</a>

							<ul class="ad-list">

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">상덕 반려동물 높이조절 2구 식기 스테인리스</span>

										<div class="ad-price">

											<span class="discount">61%</span><strong>11,400원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(69)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">냉동 오징어링 4kg 소사이즈 튀김 짬뽕 절단</span>

										<div class="ad-price">

											<strong>30,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(410)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">라프레슈 카이막 200g 천상의 맛 수제 디저트</span>

										<div class="ad-price">

											<strong>23,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(400)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">갈치 튀김용 10마리 1kg 국내산</span>

										<div class="ad-price">

											<span class="discount">69%</span><strong>10,970원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(256)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">한입 콩가루 크레페 인절미 과자 개별포장 100개</span>

										<div class="ad-price">

											<span class="discount">31%</span><strong>15,350원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(396)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">국내산 손질 오징어 1kg 냉동</span>

										<div class="ad-price">

											<span class="discount">45%</span><strong>13,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(512)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">제주 감귤 5kg 가정용</span>

										<div class="ad-price">

											<span class="discount">20%</span><strong>18,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(233)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">수제 떡갈비 800g 냉동</span>

										<div class="ad-price">

											<span class="discount">38%</span><strong>14,500원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(178)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">건조 미역 300g 국내산</span>

										<div class="ad-price">

											<span class="discount">15%</span><strong>8,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(90)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">냉동 만두 왕교자 1.4kg 2봉</span>

										<div class="ad-price">

											<span class="discount">25%</span><strong>11,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(640)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">무항생제 계란 30구 특란</span>

										<div class="ad-price">

											<span class="discount">12%</span><strong>9,800원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(1023)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">수제 유과 선물세트 1kg</span>

										<div class="ad-price">

											<span class="discount">40%</span><strong>16,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(88)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">훈제오리 슬라이스 500g 2팩</span>

										<div class="ad-price">

											<span class="discount">33%</span><strong>14,200원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(305)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">국산 콩 두부 5모 세트</span>

										<div class="ad-price">

											<span class="discount">18%</span><strong>6,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(421)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<span class="ad-tag ad-tag--sale">특가진행중</span> <span

										class="ad-name">생연어 슬라이스 500g 냉동</span>

										<div class="ad-price">

											<span class="discount">28%</span><strong>19,900원</strong>

										</div>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(267)</span>

										</div>

								</a></li>

							</ul>

						</div>

					</div>

					<!-- 4. 전세계 핫딜 로켓직구 글로벌특가 -->

					<div class="ad-carousel ad-carousel--global">

						<div class="ad-carousel-header">

							<h2>

								전세계 핫딜 <em>로켓직구 글로벌특가</em>

							</h2>

							<span class="ad-page"></span>

						</div>

						<div class="ad-carousel-body">

							<a href="#" class="move preview">이전 상품</a> <a href="#"

								class="move next">다음 상품</a>

							<ul class="ad-list">

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 36% 할인 중</div> <span

										class="ad-name">로지텍 마우스 장패드, 2개, 블랙</span>

										<div class="ad-price">

											<strong>6,330원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(374)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 73% 할인 중</div> <span

										class="ad-name">Siemzon 반영구 스텐 수세미 주방 청소 걸레</span>

										<div class="ad-price">

											<strong>5,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(1646)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 52% 할인 중</div> <span

										class="ad-name">SMABAT 무선 블루투스 이어폰 타입C 노이즈캔슬링</span>

										<div class="ad-price">

											<strong>8,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(108)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 92% 할인 중</div> <span

										class="ad-name">ROUJUN 남여공용 접이식 편광선글라스</span>

										<div class="ad-price">

											<strong>6,990원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(249)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 18% 할인 중</div> <span

										class="ad-name">Tiger Pavilion 3단4단5단원터치 접이식 철제 스탠드 선반</span>

										<div class="ad-price">

											<strong>26,990원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(3894)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 44% 할인 중</div> <span

										class="ad-name">무선 게이밍 키보드 마우스 세트</span>

										<div class="ad-price">

											<strong>19,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(512)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 61% 할인 중</div> <span

										class="ad-name">접이식 우산 자동 3단 방풍</span>

										<div class="ad-price">

											<strong>9,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(2033)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 55% 할인 중</div> <span

										class="ad-name">스마트워치 방수 심박측정</span>

										<div class="ad-price">

											<strong>32,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(780)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 40% 할인 중</div> <span

										class="ad-name">차량용 무선충전 거치대</span>

										<div class="ad-price">

											<strong>11,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(456)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 28% 할인 중</div> <span

										class="ad-name">휴대용 미니 프린터</span>

										<div class="ad-price">

											<strong>24,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(199)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 33% 할인 중</div> <span

										class="ad-name">USB C타입 멀티허브 7in1</span>

										<div class="ad-price">

											<strong>15,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(890)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 47% 할인 중</div> <span

										class="ad-name">블루투스 스피커 방수 IPX7</span>

										<div class="ad-price">

											<strong>18,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(621)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 25% 할인 중</div> <span

										class="ad-name">노트북 파우치 15.6인치</span>

										<div class="ad-price">

											<strong>9,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(345)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 20% 할인 중</div> <span

										class="ad-name">무선 이어폰 케이스 실리콘</span>

										<div class="ad-price">

											<strong>4,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(1120)</span>

										</div>

								</a></li>

								<li class="ad-card"><a href="#"> <span class="ad-thumb"></span>

										<div class="ad-discount-line">지금 38% 할인 중</div> <span

										class="ad-name">접이식 캠핑 의자 초경량</span>

										<div class="ad-price">

											<strong>21,900원</strong>

										</div> <span class="ad-tag ad-tag--rocket">로켓직구</span>

										<div class="ad-rating">

											<span class="stars">★★★★☆</span><span class="count">(267)</span>

										</div>

								</a></li>

							</ul>

						</div>

					</div>

				</section>

				<!-- ===== 띠배너 (.gw-line-banners) =====-->

				<section class="gw-line-banners">

					<a href="#" class="move preview">이전 배너</a> <a href="#"

						class="move next">다음 배너</a> <a href="#" class="line-banner is-on"

						style="background: #f6a623"> <strong>오뚜기 · 집으로 오는 나만의

							맛집</strong> <span class="desc">줄 서지 말고, 집에서 편하게 만나보세요</span>

					</a> <a href="#" class="line-banner" style="background: #2e7d32"> <strong>풀무원

							· 건강한 아침을 여는 습관</strong> <span class="desc">오늘부터 시작하는 그릭요거트 루틴</span>

					</a> <a href="#" class="line-banner" style="background: #5c6bc0"> <strong>피죤

							· 섬유유연제 골라담기</strong> <span class="desc">온 가족이 좋아하는 향 모음전</span>

					</a>

				</section>

				<!-- ===== HOT! TREND (.category-best) =====

         유닛 하나의 구조 (가로 3단):

           ┌────────┬──────────┬─────────────────┐

           │ .left  │.promotion│.best-product-list│

           │ 148px  │  326px   │   나머지(547px)  │

           │카테고리 │ 세로배너  │   상품 3열 x 2줄  │

           │HOT키워드│          │                 │

           └────────┴──────────┴─────────────────┘

         지금은 3개(뷰티/여성패션/식품)만 샘플로 만듦. 나머지는 복사해서 늘리면 됨.

         ⚠ 상품 데이터가 products.json 6개뿐이라 세 카테고리가 같은 상품을 공유함.

           대신 카테고리에 어울리는 것부터 앞에 오도록 순서만 바꿔놨음 -->

				<section class="category-best">

					<h2 class="title">HOT! TREND</h2>

					<!-- 부제목 2줄. 뒤쪽 h3만 파란색(원본 #3b7dff) -->

					<div class="sub-title">

						<h3>카테고리별</h3>

						<h3>추천 광고상품</h3>

					</div>

					<!-- ───────────── 세로 아이콘 레일 (.category-menu) ─────────────

           아이콘은 images/icons/pc_bestcategory_v5.svg 한 장에 21개가 세로로

           들어있는 스프라이트야. 한 칸이 50px이고 오른쪽 50px에는 흰색 버전

           (마우스 올렸을 때용)이 들어있어서 background-position만 바꿔 쓰면 돼.

           바깥 .category-menu-holder 는 "자리를 차지하지 않으면서 섹션 높이만큼

           길쭉한 상자"야. 이게 있어야 안쪽 레일이 position:sticky 로

           이 섹션 안에서만 따라다님 (CSS 4-1절 설명 참고) -->

					<div class="category-menu-holder">

						<div class="category-menu beauty" id="categoryBestMenu">

							<a href="#cat-beauty" class="category-anchor beauty on">뷰티</a> <a

								href="#cat-womanclothe" class="category-anchor womanclothe">여성패션</a>

							<a href="#cat-manclothe" class="category-anchor manclothe">남성패션</a>

							<a href="#cat-digital" class="category-anchor digital">가전/디지털</a>

						</div>

					</div>



					<!-- ───────────── 1. 뷰티 (보라 #7e57c2) ───────────── -->

					<div class="category-best-unit beauty" id="cat-beauty">

						<div class="category-best-content">

							<!-- 왼쪽: 카테고리명 + HOT키워드-->

							<div class="left">

								<dl>

									<dt class="category-title">

										<p class="title">뷰티</p>

										<a href="#" class="go-category">바로가기 ></a>

									</dt>

									<dd class="hot-keyword">

										<strong class="hot-keyword-title">HOT키워드</strong> <a href="#"

											class="keyword">#수분토너</a> <a href="#" class="keyword">#에센스/세럼/앰플</a>

										<a href="#" class="keyword">#아이라이너</a> <a href="#"

											class="keyword">#립틴트</a> <a href="#" class="keyword">#톤업크림</a>

										<a href="#" class="keyword">#클렌징티슈</a>

									</dd>

								</dl>

							</div>

							<!-- 가운데: 세로 광고배너 (원본 325x600 이미지 + 아래쪽 색 캡션) -->

							<div class="promotion">

								<a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">헤어&바디케어 베스트템</strong> <span

										class="promotion-description">지금 구매하기</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">여름 필수템 자외선 차단</strong> <span

										class="promotion-description">선크림 / 선쿠션 모음</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">클렌징 기획전</strong> <span

										class="promotion-description">1+1 인기 상품 모아보기</span>

								</span>

								</a>

								<!-- 좌우 화살표 + 페이지 동그라미. 동그라미 개수는 JS 가 배너 수에 맞춰 만듦 -->

								<a href="#" class="move preview">이전 배너</a> <a href="#"

									class="move next">다음 배너</a>

								<ol class="dot-navi"></ol>

							</div>

							<!-- 오른쪽: 상품 3열 -->

							<div class="best-product-list">

								<!-- 좌우 화살표 — 평소엔 숨어 있다가 목록에 마우스를 올리면 나타남.

                 글자는 CSS text-indent 로 화면 밖에 보내 숨김(화면낭독기는 읽음) -->

								<a href="#" class="move preview">이전 상품</a> <a href="#"

									class="move next">다음 상품</a>

								<ul class="prod-list">

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">글로스앤글로우

												국내생산 핫컬링 뿌리볼륨 롤브러쉬 1호 16파이, 블랙, 1개</span>

											<div class="price">

												<span class="discount">24%</span><strong>8,200원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(356)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">코멧

												3겹 데코 크린 롤화장지, 30m, 30롤</span>

											<div class="price">

												<strong>15,900원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">탐사수

												무라벨 生수, 500ml, 40개</span>

											<div class="price">

												<span class="discount">45%</span><strong>7,690원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">삼다수

												무라벨 生수 2L, 12개</span>

											<div class="price">

												<strong>11,900원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">곰곰

												국내산 백미, 10kg, 1개</span>

											<div class="price">

												<span class="discount">8%</span><strong>29,900원</strong><span

													class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">애플

												2024 맥북 에어 13 M3, 스페이스 그레이, 256GB, 8GB</span>

											<div class="price">

												<strong>1,390,000원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(4021)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">메디힐

												티트리 진정 마스크팩 10매</span>

											<div class="price">

												<span class="discount">18%</span><strong>12,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">보습

												히알루론산 수분크림 50ml, 2개</span>

											<div class="price">

												<strong>18,700원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">데일리

												클렌징 폼 150ml, 3개입</span>

											<div class="price">

												<span class="discount">18%</span><strong>9,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">무향

												저자극 선크림 SPF50+ 50ml</span>

											<div class="price">

												<strong>15,400원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">픽싱

												파우더 팩트 12g, 21호</span>

											<div class="price">

												<span class="discount">8%</span><strong>22,000원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(356)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">손상모발

												케어 헤어 에센스 100ml</span>

											<div class="price">

												<strong>13,800원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(4021)</span>

											</div>

									</a></li>

								</ul>

								<!-- 페이지 동그라미 — 개수는 상품 수에 맞춰 JS 가 만들어 넣음 -->

								<ol class="dot-navi"></ol>

							</div>

						</div>

					</div>



					<!-- ───────────── 2. 여성패션 (분홍 #f6699e) ───────────── -->

					<div class="category-best-unit womanclothe" id="cat-womanclothe">

						<div class="category-best-content">

							<div class="left">

								<dl>

									<dt class="category-title">

										<p class="title">여성패션</p>

										<a href="#" class="go-category">바로가기 ></a>

									</dt>

									<dd class="hot-keyword">

										<strong class="hot-keyword-title">HOT키워드</strong> <a href="#"

											class="keyword">#원피스</a> <a href="#" class="keyword">#블라우스/셔츠</a>

										<a href="#" class="keyword">#티셔츠</a> <a href="#"

											class="keyword">#스커트</a> <a href="#" class="keyword">#스니커즈</a>

										<a href="#" class="keyword">#가방</a>

									</dd>

								</dl>

							</div>

							<div class="promotion">

								<a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">장마 대비 아이템 SALE</strong> <span

										class="promotion-description">레인부츠 / 우산 / 우비 외</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">오늘의 원피스 특가</strong> <span

										class="promotion-description">지금 가장 많이 담은 상품</span>

								</span>

								</a>

								<!-- 좌우 화살표 + 페이지 동그라미. 동그라미 개수는 JS 가 배너 수에 맞춰 만듦 -->

								<a href="#" class="move preview">이전 배너</a> <a href="#"

									class="move next">다음 배너</a>

								<ol class="dot-navi"></ol>

							</div>

							<div class="best-product-list">

								<!-- 좌우 화살표 — 평소엔 숨어 있다가 목록에 마우스를 올리면 나타남.

                 글자는 CSS text-indent 로 화면 밖에 보내 숨김(화면낭독기는 읽음) -->

								<a href="#" class="move preview">이전 상품</a> <a href="#"

									class="move next">다음 상품</a>

								<ul class="prod-list">

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">코멧

												3겹 데코 크린 롤화장지, 30m, 30롤</span>

											<div class="price">

												<span class="discount">45%</span><strong>15,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">글로스앤글로우

												국내생산 핫컬링 뿌리볼륨 롤브러쉬 1호 16파이, 블랙, 1개</span>

											<div class="price">

												<strong>8,200원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(356)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">애플

												2024 맥북 에어 13 M3, 스페이스 그레이, 256GB, 8GB</span>

											<div class="price">

												<span class="discount">18%</span><strong>1,390,000원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(2033)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">삼다수

												무라벨 生수 2L, 12개</span>

											<div class="price">

												<strong>11,900원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">탐사수

												무라벨 生수, 500ml, 40개</span>

											<div class="price">

												<span class="discount">18%</span><strong>7,690원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(8842)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">곰곰

												국내산 백미, 10kg, 1개</span>

											<div class="price">

												<strong>29,900원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(2033)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">여름

												냉감 카라 반팔 니트, 아이보리</span>

											<div class="price">

												<span class="discount">36%</span><strong>21,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(2033)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">하이웨스트

												와이드 슬랙스, 블랙</span>

											<div class="price">

												<strong>26,500원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(890)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">플리츠

												롱스커트, 베이지, 1개</span>

											<div class="price">

												<span class="discount">18%</span><strong>19,800원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">린넨

												셔츠 원피스, 스카이블루</span>

											<div class="price">

												<strong>32,400원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(8842)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">쿠션

												스니커즈 데일리 운동화, 화이트, 235</span>

											<div class="price">

												<span class="discount">36%</span><strong>29,900원</strong><span

													class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(1204)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">여성

												크로스백 미니 숄더백, 브라운</span>

											<div class="price">

												<strong>24,600원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(112)</span>

											</div>

									</a></li>

								</ul>

								<!-- 페이지 동그라미 — 개수는 상품 수에 맞춰 JS 가 만들어 넣음 -->

								<ol class="dot-navi"></ol>

							</div>

						</div>

					</div>



					<!-- ───────────── 3. 남성패션 (파랑 #1992df) ─────────────

           ⚠ 상품명·가격·키워드는 전부 **더미 데이터** -->

					<div class="category-best-unit manclothe" id="cat-manclothe">

						<div class="category-best-content">

							<div class="left">

								<dl>

									<dt class="category-title">

										<p class="title">남성패션</p>

										<a href="#" class="go-category">바로가기 ></a>

									</dt>

									<dd class="hot-keyword">

										<strong class="hot-keyword-title">HOT키워드</strong> <a href="#"

											class="keyword">#반팔티셔츠</a> <a href="#" class="keyword">#반바지</a>

										<a href="#" class="keyword">#셔츠</a> <a href="#"

											class="keyword">#슬랙스</a> <a href="#" class="keyword">#운동화</a>

										<a href="#" class="keyword">#백팩</a>

									</dd>

								</dl>

							</div>

							<div class="promotion">

								<a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">여름 남친룩 완성</strong> <span

										class="promotion-description">티셔츠 / 반바지 최대 50%</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">남성 신발 브랜드전</strong> <span

										class="promotion-description">운동화 / 샌들 최대 40%</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">여름 이너웨어 모음</strong> <span

										class="promotion-description">쿨링 소재 기획전</span>

								</span>

								</a>

								<!-- 좌우 화살표 + 페이지 동그라미. 동그라미 개수는 JS 가 배너 수에 맞춰 만듦 -->

								<a href="#" class="move preview">이전 배너</a> <a href="#"

									class="move next">다음 배너</a>

								<ol class="dot-navi"></ol>

							</div>

							<div class="best-product-list">

								<!-- 좌우 화살표 — 평소엔 숨어 있다가 목록에 마우스를 올리면 나타남.

                 글자는 CSS text-indent 로 화면 밖에 보내 숨김(화면낭독기는 읽음) -->

								<a href="#" class="move preview">이전 상품</a> <a href="#"

									class="move next">다음 상품</a>

								<ul class="prod-list">

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">무형광

												남성 반팔 라운드 티셔츠 3종 세트, 화이트/그레이/블랙</span>

											<div class="price">

												<span class="discount">15%</span><strong>19,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(2033)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">쿨링

												스판 남성 밴딩 슬랙스, 블랙, 1개</span>

											<div class="price">

												<strong>23,800원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(8842)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">데일리

												남성 옥스포드 긴팔 셔츠, 라이트블루</span>

											<div class="price">

												<span class="discount">8%</span><strong>27,500원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">경량

												남성 러닝화 운동화, 다크그레이, 265</span>

											<div class="price">

												<strong>34,900원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(2033)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">남성

												여름 린넨 반바지, 베이지, 1개</span>

											<div class="price">

												<span class="discount">36%</span><strong>16,900원</strong><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(8842)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">대용량

												남성 노트북 백팩 15.6인치, 블랙</span>

											<div class="price">

												<strong>41,200원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">남성

												무지 맨투맨 기모, 차콜, 1개</span>

											<div class="price">

												<span class="discount">36%</span><strong>24,900원</strong><span

													class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">스판

												치노 팬츠 여름용, 베이지</span>

											<div class="price">

												<strong>28,700원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(1204)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">남성

												카라 피케 티셔츠 2종, 네이비/화이트</span>

											<div class="price">

												<span class="discount">20%</span><strong>31,000원</strong><span

													class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(4021)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">가벼운

												남성 여름 자켓, 라이트그레이</span>

											<div class="price">

												<strong>45,800원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">남성

												5족 세트 발목 양말, 블랙</span>

											<div class="price">

												<span class="discount">24%</span><strong>8,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(356)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">가죽

												벨트 자동버클, 브라운</span>

											<div class="price">

												<strong>17,500원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(8842)</span>

											</div>

									</a></li>

								</ul>

								<!-- 페이지 동그라미 — 개수는 상품 수에 맞춰 JS 가 만들어 넣음 -->

								<ol class="dot-navi"></ol>

							</div>

						</div>

					</div>

					<!-- ───────────── 5. 가전디지털 (남보라 #5868be) ─────────────

           ⚠ 상품명·가격·키워드 전부 더미 데이터 -->

					<div class="category-best-unit digital" id="cat-digital">

						<div class="category-best-content">

							<div class="left">

								<dl>

									<dt class="category-title">

										<p class="title">가전디지털</p>

										<a href="#" class="go-category">바로가기 ></a>

									</dt>

									<dd class="hot-keyword">

										<strong class="hot-keyword-title">HOT키워드</strong> <a href="#"

											class="keyword">#무선이어폰</a> <a href="#" class="keyword">#로봇청소기</a>

										<a href="#" class="keyword">#모니터</a> <a href="#"

											class="keyword">#커피머신</a> <a href="#" class="keyword">#선풍기</a>

										<a href="#" class="keyword">#보조배터리</a>

									</dd>

								</dl>

							</div>

							<div class="promotion">

								<a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">여름 필수 가전 특가</strong> <span

										class="promotion-description">에어서큘레이터 / 제습기</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">노트북 / 태블릿 특가</strong> <span

										class="promotion-description">학생 인증 추가 할인</span>

								</span>

								</a> <a href="#" class="promotion-link"> <span

									class="promo-placeholder"></span> <span class="caption">

										<strong class="promotion-title">여름 냉방가전 기획전</strong> <span

										class="promotion-description">에어컨 / 서큘레이터</span>

								</span>

								</a>

								<!-- 좌우 화살표 + 페이지 동그라미. 동그라미 개수는 JS 가 배너 수에 맞춰 만듦 -->

								<a href="#" class="move preview">이전 배너</a> <a href="#"

									class="move next">다음 배너</a>

								<ol class="dot-navi"></ol>

							</div>

							<div class="best-product-list">

								<!-- 좌우 화살표 — 평소엔 숨어 있다가 목록에 마우스를 올리면 나타남.

                 글자는 CSS text-indent 로 화면 밖에 보내 숨김(화면낭독기는 읽음) -->

								<a href="#" class="move preview">이전 상품</a> <a href="#"

									class="move next">다음 상품</a>

								<ul class="prod-list">

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">노이즈캔슬링

												블루투스 무선 이어폰, 화이트</span>

											<div class="price">

												<span class="discount">8%</span><strong>89,000원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(356)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">27인치

												QHD 75Hz 컴퓨터 모니터, 블랙</span>

											<div class="price">

												<strong>168,000원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(2033)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">저소음

												BLDC 에어서큘레이터 선풍기, 아이보리</span>

											<div class="price">

												<span class="discount">15%</span><strong>59,900원</strong><span

													class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">10000mAh

												고속충전 보조배터리 C타입, 그레이</span>

											<div class="price">

												<strong>21,900원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(4021)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">물걸레

												겸용 로봇청소기, 화이트, 1개</span>

											<div class="price">

												<span class="discount">30%</span><strong>239,000원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(4021)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">전자동

												캡슐 커피머신, 블랙</span>

											<div class="price">

												<strong>112,000원</strong><span class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(8842)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">가정용

												미니 제습기 2L, 화이트</span>

											<div class="price">

												<span class="discount">8%</span><strong>79,000원</strong><span

													class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(890)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">USB

												C타입 65W 고속 충전기</span>

											<div class="price">

												<strong>24,900원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★★</span><span class="count">(890)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">게이밍

												기계식 키보드 적축, 블랙</span>

											<div class="price">

												<span class="discount">15%</span><strong>58,000원</strong><span

													class="badge-rocket">로켓배송</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★★☆</span><span class="count">(112)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">무선

												마우스 저소음 블루투스, 그레이</span>

											<div class="price">

												<strong>16,900원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(45)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">휴대용

												블루투스 스피커 방수, 네이비</span>

											<div class="price">

												<span class="discount">8%</span><strong>43,500원</strong>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(356)</span>

											</div>

									</a></li>

									<li><a href="#" class="product-unit"> <span

											class="thumb-placeholder"></span> <span class="name">스마트폰

												거치대 겸용 무선충전 패드</span>

											<div class="price">

												<strong>19,800원</strong><span class="badge-rocket">로켓배송</span><span

													class="badge-tomorrow">내일도착</span>

											</div>

											<div class="ad-rating">

												<span class="stars">★★★☆☆</span><span class="count">(2033)</span>

											</div>

									</a></li>

								</ul>

								<!-- 페이지 동그라미 — 개수는 상품 수에 맞춰 JS 가 만들어 넣음 -->

								<ol class="dot-navi"></ol>

							</div>

						</div>

					</div>

				</section>

				<!-- ===== 맨 아래 CTA 배너 =====

         원본에서는 HOT!TREND 카테고리 18개 중 맨 마지막(여행) 다음에 붙어있던 배너.

         카테고리를 5개만 만들어서 지금은 HOT!TREND 섹션 바로 뒤, 푸터 앞에 둠.

         카테고리를 더 늘리면 "여행" 유닛 바로 뒤로 옮기면 됨 -->

				<a href="#" class="bottom-cta-banner"> <span>쿠팡트래블 메가위크</span> <span

					class="desc">여름휴가 항공·숙소 최대 8% 특가</span> <span class="arrow">></span>

				</a>

			</div>

			<!-- /.content-col -->

			<!-- 날개배너가 들어갈 자리를 비워두는 빈 상자. 높이 1px, 폭만 차지함 -->

			<div class="right-bar-placeholder"></div>

		</div>

		<!-- /.content-row -->

	</main>



	<!-- ==================================================

       ASIDE — 본문 옆 곁다리 내용. 없어도 페이지가 성립하는 것

       쿠팡 원본: article#wa-sidebar (폭 102px, position:absolute)

       ================================================== -->

	<aside class="side-banner">

		<!-- 화면엔 안 보이지만 화면낭독기용 제목. <aside>에도 제목이 있어야 좋음 -->

		<h2 class="blind">추천 배너 및 최근 본 상품</h2>

		<!-- 세로 광고배너 7장 (원본 이미지 크기 102 x 150) -->

		<ul class="promotion-banner">

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">분식집 맛<br>그대로

				</span></a></li>

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">그릭요거트<br>샤베트

				</span></a></li>

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">쿠팡 only</span></a></li>

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">여행 선착순<br>반값 할인

				</span></a></li>

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">쿠팡이 직접<br>수입했어요!

				</span></a></li>

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">금주의<br>특가왕

				</span></a></li>

			<li><a href="#" class="banner-link"><span

					class="wing-placeholder">쿠팡에서<br>판매시작하기

				</span></a></li>

		</ul>

		<!-- ===== 최근 본 상품 =====

         ⚠ 원본도 이 영역은 기본적으로 안 보임 (height:0 + overflow:hidden).

           본 상품이 생기면 JS가 .show 를 붙여서 펼치는 구조야.

           지금은 JS가 없어서 계속 접혀 있는 게 정상 —

           구조를 미리 잡아둔 거라 나중에 .show 만 붙이면 됨 -->

		<section class="recent-view">

			<div class="side-cart">

				<a href="#"><span>장바구니</span><em class="cart-count">0</em></a>

			</div>

			<div class="recently-viewed-products">

				<span>최근본상품</span><em class="total-element">0</em>

			</div>

		</section>

	</aside>



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

		<jsp:include page="/inc/footer.jsp" />



	<!-- JS는 </body> 바로 앞에! HTML을 다 읽은 뒤에 실행되게 하려고 -->

	<script src="js/header.js"></script>

	<script src="js/main.js"></script>

</body>

</html>