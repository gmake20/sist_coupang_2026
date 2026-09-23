<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!DOCTYPE html>

<html lang="ko">
<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><tiles:getAsString name="title"/></title>

<!-- 파비콘 -->
<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">

<!-- 1. 브라우저 기본 스타일 지우기 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css">

<!-- 2. 헤더/푸터 (모든 페이지 공통) -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

<!-- 3. 페이지별 CSS — 각 content JSP 안에서 추가 -->

</head>
<body>
	 <!-- HEADER — 페이지 맨 위. 로고 / 검색 / 메뉴 -->
      <tiles:insertAttribute name="header" />

      <!-- 본문 -->
      <tiles:insertAttribute name="content" />

      <!-- FOOTER -->
      <tiles:insertAttribute name="footer" />

      <!-- JS는 맨 뒤에. HTML 다 읽은 뒤 실행되게 -->
	<script src="${pageContext.request.contextPath}/resources/js/header.js"></script>
</body>
</html>
