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

    <link rel="icon"
          href="${pageContext.request.contextPath}/resources/images/favicon.jpg"
          type="image/jpeg">

    <!-- 공통 CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/reset.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/common.css">

    <!-- 배송지 CSS -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/goodpang_addressbook.css">
          
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/address_add.css">

</head>

<body>

    <!-- 헤더 -->
    <tiles:insertAttribute name="header" />

    <main class="addressbook-page">

        <!-- 왼쪽 메뉴 -->
        <tiles:insertAttribute name="leftbanner" />

        <!-- 가운데 -->
        <tiles:insertAttribute name="content" />

        <!-- 오른쪽 -->
        <aside class="address-right-area">
            <tiles:insertAttribute name="rightbanner" />
        </aside>

    </main>

    <!-- 푸터 -->
    <tiles:insertAttribute name="footer" />

</body>
<script src="${pageContext.request.contextPath}/resources/js/header.js"></script>

</html>
