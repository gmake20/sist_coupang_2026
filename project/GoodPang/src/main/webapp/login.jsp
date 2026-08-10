<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>쿠팡 로그인</title>
 
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
    <div class="login-wrapper">
        <!-- 상단 로고 -->
        <header class="login-header">
            <a href="${pageContext.request.contextPath}/index.html">
                <!-- 로고 이미지 경로에 맞춰 src를 수정하세요 -->
                <img src="${pageContext.request.contextPath}/images/coupang_logo.png" alt="coupang" class="logo-img">
            </a>
        </header>

        <!-- 탭 메뉴 -->
        <nav class="login-tabs">
            <button type="button" class="tab-btn active" data-tab="email">이메일 로그인</button>
            <button type="button" class="tab-btn" data-tab="phone">
                휴대전화번호 로그인 <span class="badge-n">N</span>
            </button>
            <button type="button" class="tab-btn" data-tab="qr">QR코드 로그인</button>
        </nav>

        <!-- 메인 콘텐츠 영역 -->
        <main class="login-content">
            <!-- 1. 이메일 로그인 폼 -->
            <form id="form-email" class="auth-form active-form" method="post" action="loginAction.jsp">
                <div class="input-group">
                    <span class="input-icon icon-mail"></span>
                    <input type="email" name="userEmail" id="userEmail" placeholder="아이디(이메일)" required>
                    <button type="button" class="btn-clear" tabindex="-1">&times;</button>
                </div>
                <div class="input-group">
                    <span class="input-icon icon-lock"></span>
                    <input type="password" name="userPw" id="userPw" placeholder="비밀번호" required>
                    <button type="button" class="btn-toggle-pw" tabindex="-1"></button>
                </div>

                <div class="form-options">
                    <label class="auto-login-label">
                        <input type="checkbox" name="autoLogin">
                        <span class="checkbox-custom"></span>
                        자동 로그인
                    </label>
                    <a href="#" class="find-link">아이디·비밀번호 찾기</a>
                </div>

                <button type="submit" class="btn-submit btn-blue">로그인</button>
                <a href="#" class="btn-submit btn-outline">회원가입</a>

                <div class="divider">
                    <span>패스키를 쿠팡에 이미 등록했다면</span>
                </div>

                <button type="button" class="btn-submit btn-passkey">패스키 로그인</button>
            </form>

            <!-- 2. 휴대전화번호 로그인 폼 -->
            <form id="form-phone" class="auth-form" method="post" action="phoneLoginAction.jsp">
                <p class="form-desc">쿠팡 계정에 등록된 휴대전화번호를 입력해주세요.</p>
                
                <div class="input-group">
                    <span class="input-icon icon-phone"></span>
                    <input type="tel" name="userPhone" placeholder="휴대전화번호" required>
                </div>

                <button type="submit" class="btn-submit btn-blue">인증번호 발송</button>
                <a href="#" class="btn-submit btn-outline">회원가입</a>

                <div class="divider">
                    <span>패스키를 쿠팡에 이미 등록했다면</span>
                </div>

                <button type="button" class="btn-submit btn-passkey">패스키 로그인</button>
            </form>

            <!-- 3. QR코드 로그인 폼 -->
            <div id="form-qr" class="auth-form">
                <div class="qr-container">
                    <div class="qr-info">
                        <h3>쿠팡 앱을 통해 바로 로그인하려면<br>다음 단계에 따라 진행해주세요</h3>
                        <ol class="qr-steps">
                            <li>휴대폰 카메라로 QR코드를 스캔하세요.</li>
                            <li>화면에서 아래의 숫자를 선택하면 로그인됩니다.</li>
                        </ol>
                        <div class="qr-number-box">71</div>
                        <p class="qr-notice">ⓘ 최신 버전의 앱에서만 QR로그인이 가능합니다.</p>
                    </div>
                    <div class="qr-code-area">
                        <!-- QR 이미지 예시 -->
                        <img src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=CoupangLogin" alt="QR Code" class="qr-img">
                        <span class="qr-timer">남은시간 <strong id="timer">2:57</strong></span>
                    </div>
                </div>

                <a href="#" class="btn-submit btn-outline">회원가입</a>
            </div>
        </main>

        <!-- 하단 푸터 -->
        <footer class="login-footer">
            <p>&copy;Coupang Corp. All rights reserved.</p>
        </footer>
    </div>

    <script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>