<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<link rel="stylesheet" href="css/common.css"> 
<header id="header">
	<!-- ===== 회색 유틸바 (높이 33px, 배경 #f0f0f0) ===== 
         원본: div#wa-top-bar
         왼쪽  즐겨찾기 / 입점신청▼
         오른쪽 로그인 / 회원가입 / 고객센터 / 판매자 가입 -->
	<div class="top-bar">
		<div class="top-bar-inner">

			<!-- 왼쪽 메뉴 (원본 menu#subscribeHeader) -->
			<ul class="top-bar-left">
				<li class="new-header"><a href="#" class="bookmark">즐겨찾기</a></li>

				<li class="vendor-join">
					<!-- i.ic = 아래쪽 삼각형 ▼. 이미지가 아니라 CSS border로 그림 --> <a href="#">입점신청<i
						class="ic"></i></a> <!-- 마우스 올리면 나오는 드롭다운. JS 없이 CSS :hover로 처리 -->
					<ul class="vendor-join-menu">
						<li><a href="#">오픈마켓</a></li>
						<li><a href="#">여행·티켓</a></li>
						<li><a href="#">로켓배송</a></li>
						<li><a href="#">제휴마케팅</a></li>
						<li><a href="#">로켓그로스</a></li>
					</ul>
				</li>
			</ul>

			<!-- 오른쪽 메뉴 (원본 menu, 클래스 없음)
             ★ 여기 li들은 float:right라서 "쓴 순서의 반대"로 화면에 배치됨.
               제일 먼저 쓴 판매자 가입이 제일 오른쪽에 붙고,그 다음 것들이 왼쪽으로 하나씩 밀려 들어옴.
               → 화면에는 로그인 · 회원가입 · 고객센터 · 판매자 가입 순으로 보임 -->
			<ul class="top-bar-right">
				<li><a href="vendor-signup.html">판매자 가입</a></li>

				<li class="cs-center"><a href="#">고객센터</a>
					<ul class="cs-menu">
						<li><a href="#">자주묻는 질문</a></li>
						<li><a href="#">1:1 채팅문의</a></li>
						<li><a href="#">고객의 소리</a></li>
						<li><a href="#">취소 / 반품 안내</a></li>
					</ul></li>

				<li><a href="./signup.html">회원가입</a></li>
				<li><a href="./login.jsp">로그인</a></li>
			</ul>

		</div>
	</div>

	<!-- ===== 헤더 본체 (높이 119px) =====
         ★ 구조 주의: 파란 카테고리 박스가 "로고줄 + GNB줄"을 통째로 왼쪽에서 걸침.
           그래서 로고줄과 GNB가 형제가 아니라, 둘을 .header-col 로 한 번 묶고
           그 묶음과 카테고리 박스를 가로로 나란히 놓는 구조.

               ┌──────────┬──────────────────────────────┐
               │          │  로고 · 검색폼 · 아이콘 (59px) │
               │ 카테고리  ├──────────────────────────────┤
               │  110px   │  GNB (32px)                   │
               └──────────┴──────────────────────────────┘
                          ← .header-col (909px) →          -->

	<div class="header-body">
		<div class="header-inner">

			<!-- ===== 파란 카테고리 버튼 + 펼쳐지는 대분류 패널 =====
             ⚠ 패널(.category-layer)을 <button> 안에 넣을 수 없어서
               (button 안에는 div 같은 걸 못 넣음 — HTML 규칙)
               둘을 .category-wrap 으로 감쌌음.

             ★ 이 패널은 JS가 없어도 동작함. CSS :hover 만으로 열림
               (원본도 `.menu>li:hover .depth { display:block }` 방식) -->
			<div class="category-wrap">

				<!-- 파란 카테고리 버튼 110 x 119 -->
				<button type="button" class="category-btn">
					<!-- 햄버거 막대 3개. 이미지가 아니라 빈 태그에 배경색만 준 것 -->
					<span class="category-bars"><i></i><i></i><i></i></span> <span
						class="category-text">카테고리</span>
				</button>


				<div class="category-layer" id="wa-pc-category">
					<ul class="menu">

						<li><a href="#">뷰티<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">스킨케어</a></li>
									<li><a href="#">메이크업</a></li>
									<li><a href="#">클렌징</a></li>
									<li><a href="#">헤어케어</a></li>
									<li><a href="#">바디케어</a></li>
									<li><a href="#">향수</a></li>
								</ul>
							</div></li>

						<li><a href="#">여성패션<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">원피스</a></li>
									<li><a href="#">블라우스/셔츠</a></li>
									<li><a href="#">티셔츠</a></li>
									<li><a href="#">팬츠</a></li>
									<li><a href="#">스커트</a></li>
									<li><a href="#">아우터</a></li>
								</ul>
							</div></li>

						<li><a href="#">남성패션<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">티셔츠</a></li>
									<li><a href="#">셔츠</a></li>
									<li><a href="#">팬츠</a></li>
									<li><a href="#">아우터</a></li>
									<li><a href="#">정장</a></li>
									<li><a href="#">언더웨어</a></li>
								</ul>
							</div></li>

						<li><a href="#">식품<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">신선식품</a></li>
									<li><a href="#">정육/계란</a></li>
									<li><a href="#">쌀/잡곡</a></li>
									<li><a href="#">간편식</a></li>
									<li><a href="#">과자/간식</a></li>
									<li><a href="#">음료/생수</a></li>
								</ul>
							</div></li>

						<li><a href="#">가구/홈인테리어<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">침대/매트리스</a></li>
									<li><a href="#">소파</a></li>
									<li><a href="#">책상/의자</a></li>
									<li><a href="#">수납/정리</a></li>
									<li><a href="#">조명</a></li>
									<li><a href="#">커튼/블라인드</a></li>
								</ul>
							</div></li>

						<li><a href="#">가전/디지털<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">노트북</a></li>
									<li><a href="#">모니터</a></li>
									<li><a href="#">TV</a></li>
									<li><a href="#">냉장고</a></li>
									<li><a href="#">세탁기</a></li>
									<li><a href="#">이어폰/헤드폰</a></li>
								</ul>
							</div></li>

						<li><a href="#">문구/오피스<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">노트/필기구</a></li>
									<li><a href="#">사무용지</a></li>
									<li><a href="#">파일/바인더</a></li>
									<li><a href="#">프린터용품</a></li>
									<li><a href="#">사무가구</a></li>
									<li><a href="#">포장용품</a></li>
								</ul>
							</div></li>

						<li><a href="#">생활용품<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">화장지/물티슈</a></li>
									<li><a href="#">세제</a></li>
									<li><a href="#">청소용품</a></li>
									<li><a href="#">욕실용품</a></li>
									<li><a href="#">수납/정리</a></li>
									<li><a href="#">벌레퇴치</a></li>
								</ul>
							</div></li>

						<li><a href="#">스포츠/레저<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">운동화</a></li>
									<li><a href="#">등산/캠핑</a></li>
									<li><a href="#">헬스/요가</a></li>
									<li><a href="#">자전거</a></li>
									<li><a href="#">골프</a></li>
									<li><a href="#">수영</a></li>
								</ul>
							</div></li>

						<li><a href="#">헬스/건강식품<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">비타민</a></li>
									<li><a href="#">홍삼</a></li>
									<li><a href="#">유산균</a></li>
									<li><a href="#">오메가3</a></li>
									<li><a href="#">단백질보충제</a></li>
									<li><a href="#">다이어트</a></li>
								</ul>
							</div></li>

						<li><a href="#">출산/유아동<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">기저귀</a></li>
									<li><a href="#">분유/이유식</a></li>
									<li><a href="#">물티슈</a></li>
									<li><a href="#">유모차</a></li>
									<li><a href="#">카시트</a></li>
									<li><a href="#">젖병/수유</a></li>
								</ul>
							</div></li>

						<li><a href="#">유아동패션<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">아기옷</a></li>
									<li><a href="#">아동복</a></li>
									<li><a href="#">아동신발</a></li>
									<li><a href="#">내복/잠옷</a></li>
									<li><a href="#">외출복</a></li>
									<li><a href="#">모자/양말</a></li>
								</ul>
							</div></li>

						<li><a href="#">주방용품<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">냄비/프라이팬</a></li>
									<li><a href="#">식기/그릇</a></li>
									<li><a href="#">컵/텀블러</a></li>
									<li><a href="#">조리도구</a></li>
									<li><a href="#">밀폐용기</a></li>
									<li><a href="#">주방수납</a></li>
								</ul>
							</div></li>

						<li><a href="#">반려동물용품<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">강아지사료</a></li>
									<li><a href="#">고양이사료</a></li>
									<li><a href="#">간식</a></li>
									<li><a href="#">배변용품</a></li>
									<li><a href="#">장난감</a></li>
									<li><a href="#">미용/목욕</a></li>
								</ul>
							</div></li>

						<li><a href="#">완구/취미<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">블록/조립</a></li>
									<li><a href="#">인형</a></li>
									<li><a href="#">보드게임</a></li>
									<li><a href="#">프라모델</a></li>
									<li><a href="#">악기</a></li>
									<li><a href="#">그림/공예</a></li>
								</ul>
							</div></li>

						<li><a href="#">자동차용품<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">타이어</a></li>
									<li><a href="#">엔진오일</a></li>
									<li><a href="#">블랙박스</a></li>
									<li><a href="#">세차용품</a></li>
									<li><a href="#">방향제</a></li>
									<li><a href="#">실내용품</a></li>
								</ul>
							</div></li>

						<li><a href="#">도서/CD/DVD<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">소설/에세이</a></li>
									<li><a href="#">자기계발</a></li>
									<li><a href="#">어린이</a></li>
									<li><a href="#">만화</a></li>
									<li><a href="#">외국어</a></li>
									<li><a href="#">음반/DVD</a></li>
								</ul>
							</div></li>

						<li><a href="#">여행<i class="si"></i></a>
							<div class="depth">
								<ul>
									<li><a href="#">국내숙소</a></li>
									<li><a href="#">해외숙소</a></li>
									<li><a href="#">항공권</a></li>
									<li><a href="#">렌터카</a></li>
									<li><a href="#">투어/티켓</a></li>
									<li><a href="#">캠핑/글램핑</a></li>
								</ul>
							</div></li>

					</ul>
				</div>
			</div>

			<!-- 오른쪽 세로 묶음: 로고줄 + GNB줄 -->
			<div class="header-col">

				<!-- ----- 로고 + 검색폼 + 아이콘메뉴 (59px) ----- -->
				<div class="header-main">

					<h1 class="logo">
						<a href="index.html" class="brand-goodpang">GoodPang</a>
					</h1>

					<!-- 검색폼 (높이 41px, 파란 테두리 2px) -->
					<form class="search-form" action="#" method="get">

						<!-- 왼쪽 "전체" 드롭다운 (135 x 37) -->
						<div class="select--category">
							<a href="#" class="select--category__current">전체</a> <span
								class="select--category--button"></span>
						</div>

						<!-- 실제 검색창 -->
						<div class="header-search">
							<!-- label 은 화면엔 안 보이지만 화면낭독기가 읽어줌 -->
							<label for="search-keyword" class="blind">상품 검색</label> <input
								type="text" id="search-keyword" name="q" class="search-keyword"
								placeholder="찾고 싶은 상품을 검색해보세요!" maxlength="150"
								autocomplete="off">

							<!-- ===== 자동완성 / 최근검색어 (원본 .headerPopupWords.popularity-words) =====
                     상태 2가지:
                       ① 기본        — 최근 검색어 목록 + 아래 회색 버튼줄
                       ② .auto-search — 글자를 치면 추천어 목록으로 바뀌고 버튼줄은 숨김
                     목록 내용은 JS 가 채움 (원본도 서버에서 받아와 채우는 자리라 비어 있었음) -->
							<div class="headerPopupWords popularity-words">
								<div class="autocomplete_wrap"></div>
								<div class="history-btns">
									<span class="delete-all-kwdhistory del-button">전체삭제</span> <span
										class="history-on-off on">최근검색어끄기</span>
								</div>
							</div>
						</div>

						<!-- 마이크는 <img> 가 아니라 배경이미지로 넣음.
                   mic.png 한 장에 마이크가 위아래로 2개(회색/파랑) 들어있어서,
                   그중 한 개만 잘라 보여줘야 함. 자세한 건 common.css 참고 -->
						<a href="#" class="speech-mic"><span class="blind">음성검색</span></a>

						<button type="submit" class="search-btn" title="검색">
							<img src="images/icons/search.png" width="20" height="21"
								alt="검색">
						</button>
					</form>

					<ul class="icon-menus">
						<li class="my-coupang"><a href="order_list.jsp"> <img
								src="images/icons/person.png" width="44" height="44" alt="">
								<span class="icon-label">마이쿠팡</span>
						</a>
							<p class="my-coupang-menu">
								<span class="wrapper"> <i class="arrow"></i> <a href="#">주문목록</a>
									<a href="#">취소/반품</a> <a href="#">찜 리스트</a>
								</span>
							</p></li>

						<li class="cart">
							<!-- 상대경로 cart.jsp 적용 --> <a href="cart.jsp"> <img
								src="images/icons/cart.png" width="44" height="44" alt="">
								<em class="cart-count">0</em> <span class="icon-label">장바구니</span>
						</a>
							<div class="cart-preview">
								<span class="wrapper"> <i class="arrow"></i>
									<ul>
										<li class="empty-cart">장바구니에 담은 상품이 없습니다.</li>
									</ul> <!-- 상대경로 cart.jsp 적용 --> <a href="cart.jsp" class="cart-btn">
										<span>장바구니 전체보기<i class="blue-arrow"></i></span>
								</a>
								</span>
							</div>
						</li>
					</ul>

				</div>

				<!-- ===== 태블릿용 검색줄 (1024px 이하에서만 보임) =====

               원본도 검색폼을 "두 개" 두고 화면 크기에 따라 하나만 보여줌:
                 #wa-search-form         … fw-hidden s1024:fw-flex  → 1025px 이상, 로고 줄 "안"
                 #wa-search-form-tablet  … 부모가 s1024:fw-hidden   → 1024px 이하, 로고 줄 "아래" 별도 줄

               왜 폼을 하나 더 만드냐 — CSS 로는 요소를 다른 상자로 옮길 수 없기 때문.
               원본도 같은 이유로 두 벌을 둠.

               ⚠ id 는 페이지에 하나만 있어야 해서 위 검색폼과 겹치지 않게 -tablet 을 붙였음 -->
				<div class="search-row">
					<form class="search-form" action="#" method="get">

						<div class="select--category">
							<a href="#" class="select--category__current">전체</a> <span
								class="select--category--button"></span>
						</div>

						<div class="header-search">
							<label for="search-keyword-tablet" class="blind">상품 검색</label> <input
								type="text" id="search-keyword-tablet" name="q"
								class="search-keyword" placeholder="찾고 싶은 상품을 검색해보세요!"
								maxlength="150" autocomplete="off">

							<!-- 태블릿 검색폼에도 같은 자동완성 (JS 가 두 개 다 알아서 처리함) -->
							<div class="headerPopupWords popularity-words">
								<div class="autocomplete_wrap"></div>
								<div class="history-btns">
									<span class="delete-all-kwdhistory del-button">전체삭제</span> <span
										class="history-on-off on">최근검색어끄기</span>
								</div>
							</div>
						</div>

						<a href="#" class="speech-mic"><span class="blind">음성검색</span></a>

						<button type="submit" class="search-btn" title="검색">
							<img src="images/icons/search.png" width="20" height="21"
								alt="검색">
						</button>
					</form>
				</div>

				<!-- ----- GNB = Global Navigation Bar (전체 메뉴). 높이 32px -----
               메뉴가 16개라 가로로 넘쳐서, 좌우 화살표로 밀어 보는 구조야.

               핵심 장치 3개:
                 .gnb-menu-container  실제로 보이는 창. overflow:hidden 으로 넘친 건 자름
                 .gnb-menu-viewport   폭 0짜리 빈 상자. 아래 ul 이 여기 갇히지 않고
                                      자유롭게 오른쪽으로 뻗어나가게 하는 용도 (원본 방식)
                 .gnb-menu-scroll     진짜 목록. 폭을 9999px 로 크게 잡아두고
                                      left 값을 JS로 바꿔서 좌우로 밀어냄 -->
				<nav class="gnb">
					<h2 class="blind">주요 서비스 바로가기</h2>

					<div class="gnb-menu-container">

						<!-- 처음엔 왼쪽으로 밀 데가 없으므로 active 를 빼둠(연회색).
                   JS 가 스크롤 위치에 따라 이 클래스를 붙였다 뗐다 함 -->
						<button type="button" class="gnb-menu-btn gnb-menu-btn-left"
							disabled>
							<span class="blind">이전 메뉴 보기</span>
						</button>

						<div class="gnb-menu-viewport">
							<ul class="gnb-menu-scroll">
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/coupang-play.png" alt=""><span>쿠팡플레이</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/rocket-delivery.png" alt=""><span>로켓배송</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/rocket-fresh.png" alt=""><span>로켓프레시</span></a></li>
								<!-- i.in = 빨간 N 배지. 이미지가 아니라 CSS로 그림.
                       화면낭독기에는 "신규"로 읽히게 blind 텍스트를 같이 넣음 -->
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/fbi_icon_3x.png" alt=""><span>다시
											구매</span><i class="in">N<span class="blind">신규</span></i></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/biz.png" alt=""><span>쿠팡비즈</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/oversea-delivery.png" alt=""><span>로켓직구</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/gold-box.png" alt=""><span>골드박스</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/new-item-of-month.png" alt=""><span>이달의
											신상</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/sell-on-coupang.png" alt=""><span>입점신청</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/omp.png" alt=""><span>판매자특가</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/wow.png" alt=""><span>와우회원 할인</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/benefit.png" alt=""><span>이벤트/쿠폰</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/returned-market.png" alt=""><span>반품마켓</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/sustainable-market.png" alt=""><span>착한상점</span><i
										class="in">N<span class="blind">신규</span></i></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/event.png" alt=""><span>기획전</span></a></li>
								<li class="gnb-menu-item"><a href="#"><img
										src="images/gnb/travel.png" alt=""><span>쿠팡트래블</span><i
										class="in">N<span class="blind">신규</span></i></a></li>
							</ul>
						</div>

						<button type="button"
							class="gnb-menu-btn gnb-menu-btn-right gnb-menu-btn-active">
							<span class="blind">다음 메뉴 보기</span>
						</button>

					</div>
				</nav>

			</div>
		</div>
	</div>

</header>

<%--
    [사용방법]

    각 페이지에서 아래와 같이 호출하세요.
    
    <link rel="stylesheet" href="css/reset.css"> 

    <jsp:include page="/inc/header.jsp" />

    header.jsp 자체에서는 위 include 코드를 실행하지 않습니다.
--%>