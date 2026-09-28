<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="tiles"
    uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html>
<html lang="ko">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        <tiles:getAsString name="title" />
    </title>

    <!-- favicon -->
    <link rel="icon"
        href="${pageContext.request.contextPath}/resources/images/favicon.jpg"
        type="image/jpeg">

    <!-- 공통 CSS -->
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/resources/css/reset.css">

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/resources/css/common.css">

    <!-- 리뷰 CSS -->
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/resources/css/review_list.css">

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/resources/css/review_available.css">
        
    <link rel="stylesheet"
    	href="${pageContext.request.contextPath}/resources/css/review_write.css">

</head>

<body>

    <!-- 헤더 -->
    <tiles:insertAttribute name="header" />

    <div class="mypang-layout">

        <!-- 왼쪽 메뉴 -->
        <tiles:insertAttribute name="leftbanner" />

        <!-- 리뷰 본문 -->
        <tiles:insertAttribute name="content" />

        <!-- 오른쪽 배너 -->
        <aside class="review-right-banner">
            <tiles:insertAttribute name="rightbanner" />
        </aside>

    </div>

    <!-- 푸터 -->
    <tiles:insertAttribute name="footer" />

</body>

</html>