<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- =========================================================
     product_write_locked.jsp — 상품 등록 안내 (Tiles "vendor.product_write_locked"의 content)
     입점 승인('승인')이 안 된 판매자가 상품 등록 메뉴에 들어왔을 때 보여준다.
     VendorController.productWrite()가 sellerApprovalStatus를 넘겨줌.
     (기존 GoodPang vendor-product-write-locked.jsp의 본문 부분)
========================================================= --%>
<style>
  .approval-guard {
    max-width: 420px;
    margin: 100px auto;
    text-align: center;
  }
  .approval-guard .icon-lock {
    width: 48px; height: 48px;
    margin-bottom: 16px;
    color: #bbb;
  }
  .approval-guard h1 {
    font-size: 18px;
    margin-bottom: 10px;
  }
  .approval-guard p {
    color: #666;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 24px;
  }
  .approval-guard .status-badge {
    display: inline-block;
    padding: 3px 10px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 700;
    background: #eef1f7;
    color: #555;
  }
  .approval-guard .btn-link {
    display: inline-block;
    padding: 10px 20px;
    border-radius: 6px;
    background: #346aff;
    color: #fff;
    text-decoration: none;
    font-weight: 600;
    font-size: 14px;
  }
</style>

    <!-- 메인 -->
    <main class="main">

      <div class="approval-guard">

        <svg class="icon-lock" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="5" y="11" width="14" height="9" rx="2" />
          <path d="M8 11V7a4 4 0 0 1 8 0v4" />
        </svg>

        <h1>입점 승인 후 상품을 등록할 수 있습니다</h1>

        <p>
          현재 판매자 상태는 <span class="status-badge"><c:out value="${sellerApprovalStatus}" /></span> 입니다.<br>
          입점 심사가 완료되어 '승인' 상태가 되면 상품 등록 메뉴를 이용할 수 있습니다.
        </p>

        <a class="btn-link" href="${pageContext.request.contextPath}/vendor/business_info.htm">판매자 정보 확인하기</a>

      </div>

    </main>

  </div>

  <script src="${pageContext.request.contextPath}/resources/js/vendor-common.js"></script>
