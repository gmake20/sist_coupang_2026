<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GoodPang 로그인</title>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login.css">
</head>
<body>

<div class="login-wrapper">

    <!-- 상단 로고 -->
    <header class="login-header">
        <h1 class="logo">
            <a href="${pageContext.request.contextPath}/" title="GoodPang 홈으로">
                <span class="brand-goodpang">GoodPang</span>
            </a>
        </h1>
    </header>

    <!-- 로그인 탭 -->
    <nav class="login-tabs">
        <button type="button" class="tab-btn active" data-tab="email">
            아이디 로그인
        </button>

        <button type="button" class="tab-btn" data-tab="phone">
            휴대전화번호 로그인
            <span class="badge-n">N</span>
        </button>

        <button type="button" class="tab-btn" data-tab="qr">
            QR코드 로그인
        </button>
    </nav>

    <main class="login-content">

        <!-- Spring Security 로그인 -->
        <form id="form-email"
              class="auth-form active-form"
              method="post"
              action="${pageContext.request.contextPath}/loginProcess">

            <!-- 로그인 실패 -->
            <c:if test="${param.error eq 'true'}">
                <div class="login-error">
                    아이디 또는 비밀번호가 올바르지 않습니다.
                </div>
            </c:if>

            <!-- 로그아웃 완료 -->
            <c:if test="${param.logout eq 'true'}">
                <div class="login-success">
                    로그아웃되었습니다.
                </div>
            </c:if>

            <!-- 아이디 -->
            <div class="input-group">
                <span class="input-icon icon-mail"></span>
                <input type="text"
                       name="memberId"
                       id="memberId"
                       placeholder="아이디"
                       autocomplete="username"
                       required>
                <button type="button" class="btn-clear" tabindex="-1">&times;</button>
            </div>

            <!-- 비밀번호 -->
            <div class="input-group">
                <span class="input-icon icon-lock"></span>
                <input type="password"
                       name="password"
                       id="password"
                       placeholder="비밀번호"
                       autocomplete="current-password"
                       required>
                <button type="button" class="btn-toggle-pw" tabindex="-1"></button>
            </div>

            <!-- CSRF -->
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <!-- 자동 로그인 / 비밀번호 찾기 -->
            <div class="form-options">
                <label class="auto-login-label">
                    <input type="checkbox" name="remember-me">
                    <span class="checkbox-custom"></span>
                    자동 로그인
                </label>

                <a href="#" class="find-link">아이디·비밀번호 찾기</a>
            </div>

            <button type="submit" class="btn-submit btn-blue">로그인</button>

            <a href="${pageContext.request.contextPath}/signup"
               class="btn-submit btn-outline">회원가입</a>

            <div class="divider">
                <span>패스키를 GoodPang에 이미 등록했다면</span>
            </div>

            <button type="button" class="btn-submit btn-passkey">
                패스키 로그인
            </button>
        </form>

        <!-- 휴대전화 로그인 -->
        <form id="form-phone"
              class="auth-form"
              method="post"
              action="#">

            <p class="form-desc">
                GoodPang 계정에 등록된 휴대전화번호를 입력해주세요.
            </p>

            <div class="input-group">
                <span class="input-icon icon-phone"></span>
                <input type="tel"
                       name="userPhone"
                       placeholder="휴대전화번호"
                       required>
            </div>

            <button type="submit" class="btn-submit btn-blue">
                인증번호 발송
            </button>

            <a href="${pageContext.request.contextPath}/signup"
               class="btn-submit btn-outline">회원가입</a>

            <div class="divider">
                <span>패스키를 GoodPang에 이미 등록했다면</span>
            </div>

            <button type="button" class="btn-submit btn-passkey">
                패스키 로그인
            </button>
        </form>

        <!-- QR 로그인 -->
        <div id="form-qr" class="auth-form">
            <div class="qr-container">

                <div class="qr-info">
                    <h3>
                        GoodPang 앱을 통해 바로 로그인하려면<br>
                        다음 단계에 따라 진행해주세요
                    </h3>

                    <ol class="qr-steps">
                        <li>휴대폰 카메라로 QR코드를 스캔하세요.</li>
                        <li>화면에서 아래의 숫자를 선택하면 로그인됩니다.</li>
                    </ol>

                    <div class="qr-number-box">71</div>

                    <p class="qr-notice">
                        ⓘ 최신 버전의 앱에서만 QR 로그인이 가능합니다.
                    </p>
                </div>

                <div class="qr-code-area">
                    <img src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=GoodPangLogin"
                         alt="QR Code"
                         class="qr-img">

                    <span class="qr-timer">
                        남은시간 <strong id="timer">2:57</strong>
                    </span>
                </div>
            </div>

            <a href="${pageContext.request.contextPath}/signup"
               class="btn-submit btn-outline">회원가입</a>
        </div>
    </main>

    <footer class="login-footer">
        <p>&copy; GoodPang Corp. All rights reserved.</p>
    </footer>
</div>

<script src="${pageContext.request.contextPath}/js/login.js"></script>

<script>
(function () {
    let totalSeconds = 2 * 60 + 57;
    const timerElement = document.getElementById("timer");

    if (!timerElement) {
        return;
    }

    const intervalId = setInterval(function () {
        totalSeconds--;

        if (totalSeconds <= 0) {
            clearInterval(intervalId);
            timerElement.textContent = "0:00";
            location.reload();
            return;
        }

        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;
        const formattedSeconds = seconds < 10 ? "0" + seconds : seconds;

        timerElement.textContent = minutes + ":" + formattedSeconds;
    }, 1000);
})();
</script>

</body>
</html>