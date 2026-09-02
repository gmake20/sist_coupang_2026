<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>공지사항 관리 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    .top-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
    h1 { font-size: 20px; margin: 0; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; }
    th { background: #fafafa; color: #555; }
    .empty { padding: 40px; text-align: center; color: #999; }
    .btn { padding: 6px 12px; border-radius: 6px; border: none; font-size: 12px; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn-primary { background: #346aff; color: #fff; }
    .btn-outline { background: #fff; color: #333; border: 1px solid #ddd; }
    .btn-danger { background: #f4514a; color: #fff; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/dashboard">&larr; 대시보드로</a>

  <div class="top-row">
    <h1>공지사항 관리 (${fn:length(noticeList)}건)</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/notice/write">새 공지 등록</a>
  </div>

  <c:choose>

    <c:when test="${empty noticeList}">
      <div class="empty">등록된 공지사항이 없습니다.</div>
    </c:when>

    <c:otherwise>

      <table>
        <thead>
          <tr>
            <th>번호</th>
            <th>구분</th>
            <th>제목</th>
            <th>작성자</th>
            <th>등록일</th>
            <th>수정일</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="notice" items="${noticeList}">
            <tr>
              <td>${notice.noticeNo}</td>
              <td>${notice.noticeType}</td>
              <td>${notice.title}</td>
              <td>${notice.adminName}</td>
              <td><fmt:formatDate value="${notice.createdDate}" pattern="yyyy-MM-dd HH:mm" /></td>
              <td><fmt:formatDate value="${notice.updatedDate}" pattern="yyyy-MM-dd HH:mm" /></td>
              <td>
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/notice/edit?noticeNo=${notice.noticeNo}">수정</a>
                <form method="post" action="${pageContext.request.contextPath}/admin/notice/delete"
                      style="display:inline;" onsubmit="return confirm('이 공지사항을 삭제하시겠습니까?');">
                  <input type="hidden" name="noticeNo" value="${notice.noticeNo}">
                  <button class="btn btn-danger" type="submit">삭제</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

    </c:otherwise>

  </c:choose>

</body>

</html>
