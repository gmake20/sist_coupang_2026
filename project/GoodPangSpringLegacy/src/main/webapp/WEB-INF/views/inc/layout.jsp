<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><tiles:getAsString name="title"/></title>
<link href="${pageContext.request.contextPath }/index.css" type="text/css" rel="stylesheet" />
</head>
<body>
	<!-- header 부분 시작 -->
	<tiles:insertAttribute name="header" />
	<!-- heade 부분 끝 -->
	<!-- visual 부분 시작 -->
	<!-- visual 부분 끝 -->
	<div id="main">
		<!-- main의 영역 시작 -->
		<tiles:insertAttribute name="content" />
		<!-- main의 영역 끝 -->
	</div>
	<!-- footer 부분 시작 -->
	<tiles:insertAttribute name="footer" />
	<!-- footer 부분 끝 -->
</body>
</html>
