<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="org.doit.goodpang.domain.SellerDTO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>

<html lang="ko">
<head>
    <!-- 파비콘 설정 -->
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">

</head>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_dashboard.css">
  <title>굿팡 판매자 대시보드</title>

</head>

<body>

	<tiles:insertAttribute name="iconsprite"/>

  <% String sellerGrade = null; %>
	<tiles:insertAttribute name="topbar"/>

  <% String menu = "dashboard"; %>
	<tiles:insertAttribute name="sidebar"/>


    <!-- 메인 -->
    <tiles:insertAttribute name="content"/>


</body>

</html>
