<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- =========================================================
     admin/login.jsp — 관리자 로그인 (Tiles "admin.login", 레이아웃 없는 단독 화면)
     폼은 /admin/loginProcess 로 POST → Spring Security(관리자 전용 <http>)가 인증 처리.
     실패/로그아웃/권한없음 메시지는 AdminController.login()이 error/message로 넘겨준다.
     (기존 GoodPang admin-login.jsp - 스크립틀릿을 EL로 바꾸고 CSRF 토큰 추가)
========================================================= --%>
<!DOCTYPE html>

<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- 파비콘 설정 -->
  <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">
  <title>관리자 로그인</title>

  <style>
    body {
      font-family: Arial, "Malgun Gothic", sans-serif;
      color: #111;
      background: #f7f8fa;
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      margin: 0;
    }

    .login-box {
      width: 320px;
      background: #fff;
      border: 1px solid #eee;
      border-radius: 10px;
      padding: 32px 28px;
    }

    h1 { font-size: 18px; margin: 0 0 24px; text-align: center; }

    .field { margin-bottom: 12px; }

    .field input {
      width: 100%;
      box-sizing: border-box;
      padding: 10px 12px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 14px;
    }

    .error {
      color: #c0392b;
      font-size: 13px;
      margin: 0 0 12px;
    }

    .info {
      color: #1a56db;
      font-size: 13px;
      margin: 0 0 12px;
    }

    .btn-login {
      width: 100%;
      padding: 11px;
      border: none;
      border-radius: 6px;
      background: #346aff;
      color: #fff;
      font-size: 14px;
      cursor: pointer;
      margin-top: 4px;
    }
  </style>

</head>

<body>

  <div class="login-box">

    <h1>관리자 로그인</h1>

    <c:if test="${not empty error}">
      <p class="error"><c:out value="${error}" /></p>
    </c:if>
    <c:if test="${not empty message}">
      <p class="info"><c:out value="${message}" /></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/admin/loginProcess">

      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

      <div class="field">
        <input type="text" name="adminId" placeholder="관리자 아이디" autocomplete="username" required autofocus>
      </div>

      <div class="field">
        <input type="password" name="password" placeholder="비밀번호" autocomplete="current-password" required>
      </div>

      <button class="btn-login" type="submit">로그인</button>

    </form>

  </div>

</body>

</html>
