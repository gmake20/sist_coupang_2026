<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vendor_orders.css">
  <title>굿팡 판매자 공지사항</title>

  <style>
    .notice-tag { display: inline-block; padding: 2px 6px; border-radius: 4px; font-size: 11px; font-weight: 700; }
    .tag-notice { background: #fdeceb; color: #f4514a; }
    .tag-info { background: #eef1f6; color: #777; }
  </style>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "notice"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">공지사항</h1>
          <p class="page-desc">쿠팡(굿팡) 운영팀에서 등록한 공지사항을 확인할 수 있습니다.</p>
        </div>

      </div>


      <!-- 공지사항 목록 -->
      <section class="panel table-panel">

        <div class="result-toolbar">
          <p class="result-count">공지사항 <strong>${fn:length(noticeList)}</strong>건</p>
        </div>

        <div class="table-scroll">
          <table class="order-table">
            <thead>
              <tr>
                <th class="col-status">구분</th>
                <th>제목</th>
                <th class="col-date">등록일</th>
              </tr>
            </thead>
            <tbody>

              <c:choose>

                <c:when test="${empty noticeList}">
                  <tr>
                    <td colspan="3" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                      등록된 공지사항이 없습니다.
                    </td>
                  </tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="notice" items="${noticeList}">
                    <tr>
                      <td class="col-status">
                        <span class="notice-tag ${notice.noticeType == '공지' ? 'tag-notice' : 'tag-info'}">${notice.noticeType}</span>
                      </td>
                      <td>
                        <a href="${pageContext.request.contextPath}/vendor/notice/detail?noticeNo=${notice.noticeNo}"
                           style="color:inherit; text-decoration:none;">${notice.title}</a>
                      </td>
                      <td class="col-date">
                        <fmt:formatDate value="${notice.createdDate}" pattern="yyyy-MM-dd" />
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>

              </c:choose>

            </tbody>
          </table>
        </div>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/js/vendor-common.js"></script>

</body>

</html>
