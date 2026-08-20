<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>GoodPang - 회원정보 수정</title>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/userModify.css">

</head>

<body>

<!-- =========================
     상단 미니 메뉴
========================= -->
<div class="top-bar">

    <div class="top-bar-inner">

        <div class="top-left">
            <a href="#">즐겨찾기</a>

            <a href="#">
                입점신청
                <span class="triangle"></span>
            </a>
        </div>

        <div class="top-right">
            <strong>${member.name}님</strong>
            <a href="${pageContext.request.contextPath}/logout">로그아웃</a>
            <a href="#">고객센터</a>
            <a href="#">판매자 가입</a>
        </div>

    </div>

</div>


<!-- =========================
     메인 헤더
========================= -->
<header class="main-header">

    <div class="header-inner">

        <!-- 카테고리 -->
        <div class="category-box">

            <div class="hamburger">
                <span></span>
                <span></span>
                <span></span>
            </div>

            <span>카테고리</span>

        </div>


        <!-- GoodPang Logo -->
        <a href="${pageContext.request.contextPath}/"
           class="logo">

            <span class="l1">G</span>
            <span class="l2">o</span>
            <span class="l3">o</span>
            <span class="l4">d</span>
            <span class="l5">Pang</span>

        </a>


        <!-- 검색 영역 -->
        <div class="search-box">

            <select>
                <option>전체</option>
                <option>식품</option>
                <option>생활용품</option>
                <option>패션</option>
                <option>전자제품</option>
            </select>

            <input type="text"
                   placeholder="찾고 싶은 상품을 검색해보세요!">

            <button type="button"
                    class="search-button">
                🔍
            </button>

        </div>


        <!-- 마이페이지 -->
        <div class="header-icon">

            <div class="person-icon">
                ♙
            </div>

            <span>마이팡</span>

        </div>


        <!-- 장바구니 -->
        <div class="header-icon cart">

            <span class="cart-count">0</span>

            <div class="cart-icon">
                🛒
            </div>

            <span>장바구니</span>

        </div>

    </div>


    <!-- 서브 메뉴 -->
    <nav class="sub-menu">

        <a href="#">▶ 쿠팡플레이</a>
        <a href="#">🚀 로켓배송</a>
        <a href="#">🚀 로켓프레시</a>
        <a href="#">🏠 다시 구매</a>
        <a href="#">biz-쿠팡비즈</a>
        <a href="#">🔮 로켓직구</a>
        <a href="#">🎁 골드박스</a>
        <a href="#">🛍 이달의신청</a>

    </nav>

</header>


<!-- =========================
     페이지 배경
========================= -->
<div class="page-background">


    <div class="content-wrapper">


        <!-- =====================
             회원정보 수정
        ====================== -->
        <main class="member-container">

            <h1>회원정보 수정</h1>

            <div class="title-line"></div>


            <!-- ===================
                 아이디 / 이메일
            ==================== -->
            <div class="info-row">

                <div class="info-title">
                    아이디(이메일)
                </div>

                <div class="info-content">

                    <strong>
                        <c:out value="${member.email}" />
                    </strong>

                    <button type="button"
                            class="small-btn"
                            onclick="showEmailForm()">
                        이메일 변경
                    </button>


                    <!-- 이메일 변경 -->
                    <div id="emailChangeForm"
                         class="hidden-form">

                        <input type="email"
                               id="newEmail"
                               placeholder="새 이메일">

                        <button type="button"
                                class="small-btn">
                            변경
                        </button>

                    </div>

                </div>

            </div>


            <!-- ===================
                 이름
            ==================== -->
            <div class="info-row">

                <div class="info-title">
                    이름
                </div>

                <div class="info-content">

                    <strong>
                        <c:out value="${member.name}" />
                    </strong>

                    <button type="button"
                            class="small-btn">
                        개명하셨다면? 이름변경 &gt;
                    </button>

                </div>

            </div>


            <!-- ===================
                 휴대폰 번호
            ==================== -->
            <div class="info-row">

                <div class="info-title">
                    휴대폰 번호
                </div>

                <div class="info-content">

                    <strong>
                        <c:out value="${member.phone}" />
                    </strong>

                    <button type="button"
                            class="small-btn"
                            onclick="showPhoneForm()">
                        휴대폰 번호 변경
                    </button>


                    <div id="phoneChangeForm"
                         class="hidden-form">

                        <input type="text"
                               id="newPhone"
                               placeholder="010-0000-0000">

                        <button type="button"
                                class="small-btn">
                            인증번호 전송
                        </button>

                    </div>

                </div>

            </div>


            <!-- ===================
                 비밀번호 변경
            ==================== -->
            <div class="info-row password-row">

                <div class="info-title">
                    비밀번호변경
                </div>


                <div class="info-content password-content">

                    <div class="password-notice">

                        비밀번호 변경 시,
                        로그인된 다른 기기에서
                        로그아웃이 반영되기까지
                        최대 3분 소요됩니다.

                    </div>


                    <form
                        action="${pageContext.request.contextPath}/member/password"
                        method="post"
                        class="password-form">

                        <div class="password-input-row">

                            <label for="currentPassword">
                                현재 비밀번호
                            </label>

                            <input type="password"
                                   id="currentPassword"
                                   name="currentPassword"
                                   autocomplete="current-password">

                        </div>


                        <div class="password-input-row">

                            <label for="newPassword">
                                새 비밀번호
                            </label>

                            <input type="password"
                                   id="newPassword"
                                   name="newPassword"
                                   autocomplete="new-password">

                        </div>


                        <div class="password-input-row">

                            <label for="confirmPassword">
                                비밀번호 다시 입력
                            </label>

                            <input type="password"
                                   id="confirmPassword"
                                   name="confirmPassword"
                                   autocomplete="new-password">

                        </div>


                        <div class="password-button-row">

                            <button type="submit"
                                    class="small-btn password-btn">
                                비밀번호 변경
                            </button>

                        </div>

                    </form>

                </div>

            </div>


            <!-- ===================
                 배송지
            ==================== -->
            <div class="info-row">

                <div class="info-title">
                    배송지
                </div>

                <div class="info-content">

                    배송지 주소 관리는

                    <a href="${pageContext.request.contextPath}/address/list"
                       class="blue-link">
                        [배송지 관리]
                    </a>

                    에서 수정, 등록 합니다.

                </div>

            </div>


            <!-- ===================
                 수신 설정
            ==================== -->
            <div class="info-row receive-row">

                <div class="info-title">
                    수신설정
                </div>

                <div class="info-content receive-content">


                    <!-- 마케팅 동의 -->
                    <div class="setting-section">

                        <label class="checkbox-label">

                            <input type="checkbox"
                                   name="marketingAgree"
                                   checked>

                            마케팅 목적의 개인정보 수집 및 이용 동의

                            <span class="date">
                                26.05.01
                            </span>

                        </label>

                        <div class="detail-view">
                            전문보기 &gt;
                        </div>

                    </div>


                    <!-- 광고성 정보 동의 -->
                    <div class="setting-section advertising">

                        <div>

                            <label class="checkbox-label">

                                <input type="checkbox"
                                       name="advertisingAgree"
                                       checked>

                                광고성 정보 수신 동의

                                <span class="date">
                                    26.05.01
                                </span>

                            </label>


                            <span class="sub-options">

                                (

                                <label>
                                    <input type="checkbox">
                                    SMS
                                </label>

                                <label>
                                    <input type="checkbox">
                                    SNS
                                </label>

                                <label>
                                    <input type="checkbox">
                                    이메일
                                </label>

                                <label>
                                    <input type="checkbox"
                                           checked
                                           disabled>
                                    푸시 알림
                                </label>

                                )

                            </span>

                        </div>


                        <div class="detail-view">
                            전문보기 &gt;
                        </div>


                        <p class="setting-guide">
                            *푸시 알림을 받으려면 고객님 기기에서
                            알림을 허용해주세요.
                        </p>

                        <p class="setting-guide">
                            *위 항목을 모두 동의하셔야
                            GoodPang 맞춤형 쇼핑혜택(광고)을
                            받으실 수 있습니다.
                        </p>

                    </div>


                </div>

            </div>


            <!-- 회원탈퇴 -->
            <div class="member-bottom">

                <button type="button"
                        class="withdraw-btn">
                    회원탈퇴
                </button>

            </div>


        </main>


        <!-- =====================
             오른쪽 광고 영역
        ====================== -->
        <aside class="right-banner">


            <div class="ad-box ad-only">

                <strong>
                    GoodPang
                    <span>only</span>
                </strong>

                <div class="ad-product">
                    🛍️
                </div>

            </div>


            <div class="ad-box ad-blue">

                <strong>
                    ~5만원<br>
                    쿠폰 할인
                </strong>

                <div class="ad-emoji">
                    🏖️
                </div>

            </div>


            <div class="ad-box ad-sky">

                <strong>
                    GoodPang이 직접<br>
                    수입했어요!
                </strong>

                <div class="ad-emoji">
                    🥤
                </div>

            </div>


            <div class="ad-box ad-purple">

                <strong>
                    금주의<br>
                    특가왕
                </strong>

                <div class="ad-emoji">
                    🔔
                </div>

            </div>


            <div class="ad-box ad-red">

                <strong>
                    GoodPang에서<br>
                    판매 시작하기
                </strong>

                <div class="ad-emoji">
                    🏪
                </div>

            </div>


        </aside>


    </div>

</div>


<script>

    function showEmailForm() {

        const form =
            document.getElementById("emailChangeForm");

        form.classList.toggle("show");

    }


    function showPhoneForm() {

        const form =
            document.getElementById("phoneChangeForm");

        form.classList.toggle("show");

    }

</script>

</body>
</html>