<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title><tiles:getAsString name="title" ignore="true" /></title>

<!-- 파비콘 -->
<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap">


<!-- jQuery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<!-- 1. 브라우저 기본 스타일 지우기 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css">

<!-- 2. 헤더/푸터 (모든 페이지 공통) -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
 <tiles:importAttribute name="css" ignore="true"/>
   <%
       String css = (String) pageContext.getAttribute("css");
       if (css != null && application.getResource(css) != null) {
   %>
     <link rel="stylesheet" href="${pageContext.request.contextPath}${css}">
   <%  } %>  
   
   
</head>
<body>
  
      
    <!-- HEADER -->
    <tiles:insertAttribute name="header" />
    
    <div class="mypage-container">
    
    <tiles:insertAttribute name="leftbanner" />
    <!-- 본문 영역 (tiles.xml에 작성된 content JSP가 여기에 들어옵니다) -->
     <tiles:insertAttribute name="content" />
     
     
     <tiles:insertAttribute name="rightbanner" />
     
     </div>
    
     
     
      
    <!-- FOOTER -->
    <tiles:insertAttribute name="footer" />

    <!-- JS 공통 script -->
    <script src="${pageContext.request.contextPath}/resources/js/header.js"></script>
    
    
</body>
</html>