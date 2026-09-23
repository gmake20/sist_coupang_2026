<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html lang="ko">
<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<%-- <title><tiles:getAsString name="title"/></title> --%>
<%-- 제목은 두 군데서 옴.
     ① 컨트롤러가 pageTitle 을 넣으면 그걸 씀 (상품명처럼 값이 페이지마다 달라질 때)
     ② 없으면 tiles.xml 의 title 을 씀 (고정 문구) --%>
<c:set var="tilesTitle"><tiles:getAsString name="title" ignore="true"/></c:set>
<title>${not empty pageTitle ? pageTitle : tilesTitle}</title>

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
<body class="${bodyClass}" data-sale-status="${saleStatus}">
	 <%-- 최상단 배너 — 정의한 페이지에만 나옴. 없으면 아무것도 안 나옴 --%>
      <tiles:insertAttribute name="topBanner" ignore="true" />
      
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
