<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- =========================================================
     sidebar.jsp — 판매자센터 공통 좌측 사이드바 (Tiles "sidebar" 속성)

     현재 메뉴는 Controller에서 request 속성 "menu"로 넘겨줘야 함
       (예) mav.addObject("menu", "dashboard");  /  model.addAttribute("menu", "dashboard");
     Tiles의 insertAttribute는 동적 include라 layout.jsp의 스크립틀릿 지역변수가
     이 파일에서 안 보임 → 반드시 request 속성 + EL(${menu})로 받아야 함

       "dashboard"       // 대시보드
       "products"        // 상품관리 > 상품 목록
       "productWrite"    // 상품관리 > 상품 등록
       "productOptions"  // 상품관리 > 상품 옵션 관리
       "orders"          // 주문/배송관리 > 주문 목록
       "delivery"        // 주문/배송관리 > 배송 관리
       "return"          // 주문/배송관리 > 취소/반품/교환 관리
       "shipping"        // 주문/배송관리 > 출고/운송장 관리
       "settlement"      // 정산관리 > 정산내역 리스트
       "businessInfo"    // 하단 > 판매자 정보관리
       "notice"          // 공지사항

     ⚠ 이 파일은 <div class="layout">과 <aside class="sidebar">를
       열기만 하고 안 닫음. 포함하는 쪽에서
         (이 include) → <main class="main">...내용...</main> → </div>
       순서로 이어서 써야 함 (예전에 각 페이지에 통째로 있던 구조 그대로)
========================================================= --%>
<div class="layout">

  <!-- 사이드바 -->
  <aside class="sidebar" id="sidebar">

    <nav class="side-nav">

      <a href="${pageContext.request.contextPath}/vendor/dashboard.htm" class="side-item ${menu eq 'dashboard' ? 'active' : ''}">
        <svg class="icon"><use href="#ic-home" /></svg>
        <span>대시보드</span>
      </a>

      <button class="side-item side-group-toggle ${(menu eq 'products' or menu eq 'productWrite' or menu eq 'productOptions') ? 'active open' : ''}" type="button">
        <svg class="icon"><use href="#ic-box" /></svg>
        <span>상품관리</span>
        <svg class="icon chevron"><use href="#ic-chevron-down" /></svg>
      </button>
      <div class="side-submenu ${(menu eq 'products' or menu eq 'productWrite' or menu eq 'productOptions') ? 'open' : ''}">
        <a href="${pageContext.request.contextPath}/vendor/product.htm" class="${menu eq 'products' ? 'active' : ''}">상품 목록</a>
        <a href="${pageContext.request.contextPath}/vendor/product/write.htm" class="${menu eq 'productWrite' ? 'active' : ''}">상품 등록</a>
        <a href="${pageContext.request.contextPath}/vendor/product/options.htm" class="${menu eq 'productOptions' ? 'active' : ''}">상품 옵션 관리</a>
      </div>

      <button class="side-item side-group-toggle ${(menu eq 'orders' or menu eq 'delivery' or menu eq 'return' or menu eq 'shipping') ? 'active open' : ''}" type="button">
        <svg class="icon"><use href="#ic-truck" /></svg>
        <span>주문/배송관리</span>
        <svg class="icon chevron"><use href="#ic-chevron-down" /></svg>
      </button>
      <div class="side-submenu ${(menu eq 'orders' or menu eq 'delivery' or menu eq 'return' or menu eq 'shipping') ? 'open' : ''}">
        <a href="${pageContext.request.contextPath}/vendor/order.htm" class="${menu eq 'orders' ? 'active' : ''}">주문 목록</a>
        <a href="${pageContext.request.contextPath}/vendor/delivery.htm" class="${menu eq 'delivery' ? 'active' : ''}">배송 관리</a>
        <a href="${pageContext.request.contextPath}/vendor/return.htm" class="${menu eq 'return' ? 'active' : ''}">취소/반품/교환 관리</a>
        <a href="${pageContext.request.contextPath}/vendor/shipping.htm" class="${menu eq 'shipping' ? 'active' : ''}">출고/운송장 관리</a>
      </div>

      <button class="side-item side-group-toggle ${menu eq 'settlement' ? 'active open' : ''}" type="button">
        <svg class="icon"><use href="#ic-calculator" /></svg>
        <span>정산관리</span>
        <svg class="icon chevron"><use href="#ic-chevron-down" /></svg>
      </button>
      <div class="side-submenu ${menu eq 'settlement' ? 'open' : ''}">
        <a href="${pageContext.request.contextPath}/vendor/settlement.htm" class="${menu eq 'settlement' ? 'active' : ''}">정산내역 리스트</a>
      </div>

      <a href="${pageContext.request.contextPath}/vendor/notice.htm" class="side-item ${menu eq 'notice' ? 'active' : ''}">
        <svg class="icon"><use href="#ic-bell" /></svg>
        <span>공지사항</span>
      </a>

    </nav>

    <div class="side-foot">
      <a href="${pageContext.request.contextPath}/vendor/business-info.htm" class="side-item ${menu eq 'businessInfo' ? 'active' : ''}">
        <svg class="icon"><use href="#ic-gear" /></svg>
        <span>판매자 정보관리</span>
      </a>
      <button class="collapse-btn" id="collapseBtn" type="button">
        <svg class="icon"><use href="#ic-chevron-left" /></svg>
        메뉴 접기
      </button>
    </div>

  </aside>
