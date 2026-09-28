<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="org.doit.goodpang.domain.SellerDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld"%>


    <!-- 메인 -->
    <main class="main">

      <section class="panel notice-detail" style="padding: 24px;">

        <a class="back-link" href="${pageContext.request.contextPath}/vendor/notice.htm">&larr; 목록으로</a>

        <h1><span class="notice-tag ${notice.noticeType == '공지' ? 'tag-notice' : 'tag-info'}">${notice.noticeType}</span>${notice.title}</h1>
        <p class="meta">
          ${notice.adminName} · <fmt:formatDate value="${notice.createdDate}" pattern="yyyy-MM-dd HH:mm" />
        </p>

        <div class="content">${notice.content}</div>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/resources/js/vendor-common.js"></script>
