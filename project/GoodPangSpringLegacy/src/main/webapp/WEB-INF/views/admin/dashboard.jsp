<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- =========================================================
     admin/dashboard.jsp — 관리자 대시보드 (Tiles "admin.dashboard", 레이아웃 없는 단독 화면)
     각 관리 메뉴로 가는 카드 목록. (기존 GoodPang admin-dashboard.jsp)
     메뉴 링크는 판매자센터와 같은 규칙(/admin/xxx.htm, 단어 구분은 _)으로 미리 바꿔둠 -
     해당 관리자 화면을 옮기기 전까지는 404.
========================================================= --%>
<!DOCTYPE html>

<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- 파비콘 설정 -->
  <link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.jpg" type="image/jpeg">
  <title>관리자 대시보드</title>

  <style>
    body { font-family: Arial, "Malgun Gothic", sans-serif; margin: 24px; color: #111; background: #f7f8fa; }
    h1 { font-size: 20px; margin-bottom: 24px; }

    .menu-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 16px;
      max-width: 900px;
    }

    .menu-card {
      display: block;
      padding: 20px;
      background: #fff;
      border: 1px solid #eee;
      border-radius: 10px;
      text-decoration: none;
      color: inherit;
    }

    .menu-card:hover { border-color: #346aff; box-shadow: 0 2px 10px rgba(52,106,255,.12); }

    .menu-card h2 { margin: 0 0 6px; font-size: 15px; color: #111; }
    .menu-card p { margin: 0; font-size: 12px; color: #888; }

    .top-bar { display: flex; align-items: center; justify-content: space-between; max-width: 900px; margin-bottom: 24px; }
    .top-bar h1 { margin: 0; }
    .top-bar .admin-name { font-size: 13px; color: #555; }
    .top-bar .logout-form { display: inline; margin: 0; }
    .top-bar .logout-link { margin-left: 12px; color: #888; text-decoration: none; background: none; border: 0; padding: 0; font: inherit; font-size: 13px; cursor: pointer; }
    .top-bar .logout-link:hover { text-decoration: underline; }
  </style>

</head>

<body>

  <div class="top-bar">
    <h1>관리자 대시보드</h1>
    <div>
      <span class="admin-name"><c:out value="${sessionScope.adminName}" />님</span>
      <%-- Spring Security 로그아웃은 CSRF 보호 때문에 POST만 받음 → 링크 대신 form + 토큰 --%>
      <form class="logout-form" method="post" action="${pageContext.request.contextPath}/admin/logout.htm">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <button class="logout-link" type="submit">로그아웃</button>
      </form>
    </div>
  </div>

  <div class="menu-grid">

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/products.htm">
      <h2>상품 승인 관리</h2>
      <p>등록된 상품을 확인하고 승인/판매중지 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/sellers.htm">
      <h2>판매자 입점 심사</h2>
      <p>입점 신청한 판매자 목록을 확인하고 승인/반려 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/deliveries.htm">
      <h2>배송 관리</h2>
      <p>배송중인 주문 목록을 확인하고 배송완료 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/notices.htm">
      <h2>공지사항 관리</h2>
      <p>판매자에게 노출되는 공지사항을 등록/수정/삭제합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/action_logs.htm">
      <h2>액션 로그</h2>
      <p>관리자가 수행한 승인/반려/정지/삭제 등의 처리 이력을 확인합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/vendor_action_logs.htm">
      <h2>판매자 액션 로그</h2>
      <p>판매자가 상품 등록/노출전환/판매중지/옵션수정/배송처리 등에서 수행한 작업 이력을 확인합니다.</p>
    </a>

  </div>

</body>

</html>
