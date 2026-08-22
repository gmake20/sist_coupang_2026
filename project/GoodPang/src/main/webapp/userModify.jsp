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

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/userModify.css">
</head>

<body>

<jsp:include page="/inc/header.jsp" />

<div class="user-modify-page">
    <div class="user-modify-layout">

        <main class="member-container">

            <h1 class="member-title">회원정보 수정</h1>
            <div class="title-line"></div>

            <!-- 아이디(이메일) -->
            <div class="info-row">
                <div class="info-title">아이디(이메일)</div>

                <div class="info-content">
                    <strong class="member-value">
                        <c:out value="${member.email}" />
                    </strong>

                    <button type="button"
                            class="small-btn"
                            onclick="toggleForm('emailChangeForm')">
                        이메일 변경
                    </button>

                    <div id="emailChangeForm" class="hidden-form">
                        <input type="email"
                               id="newEmail"
                               name="newEmail"
                               placeholder="새 이메일">
                        <button type="button" class="small-btn">변경</button>
                    </div>
                </div>
            </div>

            <!-- 이름 -->
            <div class="info-row">
                <div class="info-title">이름</div>

                <div class="info-content">
                    <strong class="member-value">
                        <c:out value="${member.memberName}" />
                    </strong>

                    <button type="button" class="small-btn">
                        개명하셨다면? 이름변경 &gt;
                    </button>
                </div>
            </div>

            <!-- 휴대폰 번호 -->
            <div class="info-row">
                <div class="info-title">휴대폰 번호</div>

                <div class="info-content">
                    <strong class="member-value">
                        <c:out value="${member.phone}" />
                    </strong>

                    <button type="button"
                            class="small-btn"
                            onclick="toggleForm('phoneChangeForm')">
                        휴대폰 번호 변경
                    </button>

                    <div id="phoneChangeForm" class="hidden-form">
                        <input type="text"
                               id="newPhone"
                               name="newPhone"
                               placeholder="010-0000-0000">
                        <button type="button" class="small-btn">인증번호 전송</button>
                    </div>
                </div>
            </div>

            <!-- 비밀번호 변경 -->
            <div class="info-row password-row">
                <div class="info-title">비밀번호변경</div>

                <div class="info-content password-content">

                    <div class="password-notice">
                        비밀번호 변경 시, 로그인된 다른 기기에서 로그아웃이 반영되기까지 최대 3분 소요됩니다.
                    </div>

                    <form action="${pageContext.request.contextPath}/member/password"
                          method="post"
                          class="password-form">

                        <div class="password-input-row">
                            <label for="currentPassword">현재 비밀번호</label>
                            <input type="password"
                                   id="currentPassword"
                                   name="currentPassword"
                                   autocomplete="current-password">
                        </div>

                        <div class="password-input-row">
                            <label for="newPassword">새 비밀번호</label>
                            <input type="password"
                                   id="newPassword"
                                   name="newPassword"
                                   autocomplete="new-password">
                        </div>

                        <div class="password-input-row">
                            <label for="confirmPassword">비밀번호 다시 입력</label>
                            <input type="password"
                                   id="confirmPassword"
                                   name="confirmPassword"
                                   autocomplete="new-password">
                        </div>

                        <div class="password-button-row">
                            <button type="submit" class="small-btn password-btn">
                                비밀번호 변경
                            </button>
                        </div>
                    </form>

                </div>
            </div>

            <!-- 배송지 -->
            <div class="info-row">
                <div class="info-title">배송지</div>

                <div class="info-content">
                    배송지 주소 관리는
                    <a href="${pageContext.request.contextPath}/address/list"
                       class="blue-link">
                        [배송지 관리]
                    </a>
                    에서 수정, 등록 합니다.
                </div>
            </div>

            <!-- 수신설정 -->
            <div class="info-row receive-row">
                <div class="info-title">수신설정</div>

                <div class="info-content receive-content">

                    <div class="setting-section">
                        <label class="checkbox-label">
                            <input type="checkbox"
                                   name="marketingAgree"
                                   checked>
                            마케팅 목적의 개인정보 수집 및 이용 동의
                            <span class="date">26.05.01</span>
                        </label>

                        <button type="button" class="detail-view">
                            전문보기 &gt;
                        </button>
                    </div>

                    <div class="setting-section advertising">
                        <div class="advertising-line">

                            <label class="checkbox-label">
                                <input type="checkbox"
                                       name="advertisingAgree"
                                       checked>
                                광고성 정보 수신 동의
                                <span class="date">26.05.01</span>
                            </label>

                            <span class="sub-options">
                                (
                                <label><input type="checkbox"> SMS</label>
                                <label><input type="checkbox"> SNS</label>
                                <label><input type="checkbox"> 이메일</label>
                                <label><input type="checkbox" checked disabled> 푸시 알림</label>
                                )
                            </span>
                        </div>

                        <button type="button" class="detail-view">
                            전문보기 &gt;
                        </button>

                        <p class="setting-guide">
                            *푸시 알림을 받으려면 고객님 기기에서 알림을 허용해주세요.
                        </p>

                        <p class="setting-guide">
                            *위 항목을 모두 동의하셔야 GoodPang 맞춤형 쇼핑혜택(광고)을 받으실 수 있습니다.
                        </p>
                    </div>

                </div>
            </div>

            <div class="member-bottom">
                <button type="button" class="withdraw-btn">회원탈퇴</button>
            </div>

        </main>

        <aside class="right-banner" aria-label="프로모션">

            <div class="ad-box ad-only">
                <strong>GoodPang</strong>
                <em>only</em>
                <div class="ad-icon">🛍️</div>
            </div>

            <div class="ad-box ad-blue">
                <strong>~5만원<br>쿠폰 할인</strong>
                <div class="ad-icon">🏖️</div>
            </div>

            <div class="ad-box ad-sky">
                <strong>GoodPang<br>이 직접<br>수입했어요!</strong>
                <div class="ad-icon">🥤</div>
            </div>

            <div class="ad-box ad-purple">
                <strong>금주의<br>특가왕</strong>
                <div class="ad-icon">🔔</div>
            </div>

            <div class="ad-box ad-red">
                <strong>GoodPang<br>에서<br>판매 시작하기</strong>
                <div class="ad-icon">🏪</div>
            </div>

        </aside>

    </div>
</div>

<jsp:include page="/inc/footer.jsp" />

<script>
function toggleForm(id) {
    const form = document.getElementById(id);
    if (form) {
        form.classList.toggle("show");
    }
}
</script>

</body>
</html>