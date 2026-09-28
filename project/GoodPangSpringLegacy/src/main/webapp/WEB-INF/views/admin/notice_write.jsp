<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- =========================================================
     admin/notice_write.jsp — 관리자 공지사항 등록 (Tiles "admin.notice_write", 단독 화면)
     입력값 검증 실패 시 AdminController가 error를 넘겨주고, 입력했던 값은 요청 파라미터(param)로 다시 채운다.
     (기존 GoodPang admin-notice-write.jsp - 스크립틀릿을 EL로 바꾸고 CSRF 토큰 추가)
========================================================= --%>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- 파비콘 설정 -->
  <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">
  <title>공지사항 등록 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin: 0 0 16px; }
    .error { color: #f4514a; margin-bottom: 12px; font-size: 13px; }
    .field { margin-bottom: 14px; }
    .field label { display: block; margin-bottom: 6px; font-size: 13px; color: #555; }
    .field input[type="text"], .field textarea, .field select {
      width: 100%; max-width: 640px; padding: 10px 12px;
      border: 1px solid #ddd; border-radius: 6px; font-size: 14px; box-sizing: border-box;
    }
    .field textarea { min-height: 240px; resize: vertical; font-family: inherit; }
    .btn { padding: 8px 16px; border-radius: 6px; border: none; font-size: 13px; cursor: pointer; }
    .btn-primary { background: #346aff; color: #fff; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/notices.htm">&larr; 목록으로</a>

  <h1>공지사항 등록</h1>

  <c:if test="${not empty error}">
    <p class="error"><c:out value="${error}" /></p>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/admin/notice_write.htm">

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

    <%-- 처음 열 때는 '안내', 검증 실패로 다시 열 때는 선택했던 값 --%>
    <c:set var="selectedType" value="${empty param.noticeType ? '안내' : param.noticeType}" />

    <div class="field">
      <label>구분</label>
      <select name="noticeType">
        <option value="공지" ${selectedType == '공지' ? 'selected' : ''}>공지</option>
        <option value="안내" ${selectedType == '안내' ? 'selected' : ''}>안내</option>
      </select>
    </div>

    <div class="field">
      <label>제목</label>
      <input type="text" name="title" maxlength="200" required value="<c:out value='${param.title}' />">
    </div>

    <div class="field">
      <label>내용</label>
      <textarea name="content" required><c:out value="${param.content}" /></textarea>
    </div>

    <button class="btn btn-primary" type="submit">등록</button>

  </form>

</body>

</html>
