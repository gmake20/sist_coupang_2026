<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
  </style>

</head>

<body>

  <h1>관리자 대시보드</h1>

  <div class="menu-grid">

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/products">
      <h2>상품 승인 관리</h2>
      <p>등록된 상품을 확인하고 승인/판매중지 처리합니다.</p>
    </a>

    <a class="menu-card" href="${pageContext.request.contextPath}/admin/sellers">
      <h2>판매자 입점 심사</h2>
      <p>입점 신청한 판매자 목록을 확인하고 승인/반려 처리합니다.</p>
    </a>

  </div>

</body>

</html>
