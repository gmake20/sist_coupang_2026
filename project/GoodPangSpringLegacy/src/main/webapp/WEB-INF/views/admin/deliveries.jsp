<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- =========================================================
     admin/deliveries.jsp — 관리자 배송 관리 (Tiles "admin.deliveries", 단독 화면)
     AdminController.deliveries()가 배송중 목록(deliveryList)을 넘겨준다.
     (기존 GoodPang admin-delivery-list.jsp - 링크를 .htm 경로로, 배송완료 폼에 CSRF 토큰)
========================================================= --%>
<!DOCTYPE html>

<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- 파비콘 설정 -->
  <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">
  <title>배송 관리 - 관리자</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; }
    .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
    h1 { font-size: 20px; margin-bottom: 16px; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; white-space: nowrap; }
    td.col-address { white-space: normal; max-width: 260px; }
    th { background: #fafafa; color: #555; }
    .empty { padding: 40px; text-align: center; color: #999; }
    .btn { padding: 6px 12px; border-radius: 6px; border: none; font-size: 12px; cursor: pointer; }
    .btn-complete { background: #0f7b3c; color: #fff; }
  </style>

</head>

<body>

  <a class="back-link" href="${pageContext.request.contextPath}/admin/dashboard.htm">&larr; 대시보드로</a>

  <h1>배송중 상품 목록 (${fn:length(deliveryList)}건)</h1>

  <c:choose>

    <c:when test="${empty deliveryList}">
      <div class="empty">배송중인 상품이 없습니다.</div>
    </c:when>

    <c:otherwise>

      <table>
        <thead>
          <tr>
            <th>주문번호</th>
            <th>판매자</th>
            <th>상품명</th>
            <th>구매자</th>
            <th>배송주소</th>
            <th>택배사</th>
            <th>송장번호</th>
            <th>배송 시작일시</th>
            <th>처리</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="delivery" items="${deliveryList}">
            <tr>
              <td>${delivery.orderNo}</td>
              <td><c:out value="${delivery.storeName}" /></td>
              <td>
                <c:out value="${delivery.productName}" />
                <c:if test="${delivery.itemCount > 1}"> 외 ${delivery.itemCount - 1}건</c:if>
              </td>
              <td><c:out value="${delivery.buyerName}" /> (<c:out value="${delivery.buyerPhone}" />)</td>
              <td class="col-address">(<c:out value="${delivery.zipcode}" />) <c:out value="${delivery.address}" /> <c:out value="${delivery.detailAddress}" /></td>
              <td><c:out value="${delivery.deliveryServiceCode}" /></td>
              <td><c:out value="${delivery.invoiceNo}" /></td>
              <td><fmt:formatDate value="${delivery.deliveryStartDate}" pattern="yyyy-MM-dd HH:mm" /></td>
              <td>
                <form method="post" action="${pageContext.request.contextPath}/admin/delivery_complete.htm"
                      onsubmit="return confirm('이 주문을 배송완료 처리하시겠습니까?');">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                  <input type="hidden" name="deliveryNo" value="${delivery.deliveryNo}">
                  <button class="btn btn-complete" type="submit">배송완료 처리</button>
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
