<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%-- =========================================================
     order_detail.jsp — 판매자센터 주문 상세 (Tiles "vendor.order_detail"의 content)
     VendorController.orderDetail()이 order(VendorOrderDetailDTO)를 넘겨준다.
     (기존 GoodPang vendor-order-detail.jsp의 본문 부분)

     기존 페이지는 사이드바 없이 단독으로 뜨는 화면이라 h1/table/th/td 같은 전역 스타일을 썼는데,
     판매자센터 레이아웃(topbar/sidebar) 안에 들어오면 다른 영역까지 바뀌므로
     전부 .order-detail 아래로만 적용되게 범위를 좁혔다 (body 스타일은 레이아웃 것을 그대로 씀).
========================================================= --%>
<style>
  .order-detail .back-link { display: inline-block; margin-bottom: 16px; color: #555; text-decoration: none; }
  .order-detail h1 { font-size: 22px; margin: 0 0 4px; }
  .order-detail .sub { color: #888; margin-bottom: 20px; }

  .order-detail .section { background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 20px; margin-bottom: 16px; }
  .order-detail .section h2 { font-size: 15px; margin: 0 0 14px; color: #333; }

  .order-detail .grid { display: grid; grid-template-columns: 140px 1fr; row-gap: 10px; column-gap: 12px; font-size: 14px; }
  .order-detail .grid dt { color: #888; }
  .order-detail .grid dd { margin: 0; }

  .order-detail table { width: 100%; border-collapse: collapse; font-size: 13px; }
  .order-detail th, .order-detail td { padding: 10px 12px; border-bottom: 1px solid #eee; text-align: left; }
  .order-detail th { background: #fafafa; color: #555; }

  .order-detail .badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; }
  .order-detail .badge-paid { background: #e8f0fe; color: #1a56db; }
  .order-detail .badge-shipping { background: #fff4e5; color: #a15c00; }
  .order-detail .badge-done { background: #e6f7ec; color: #0f7b3c; }
  .order-detail .badge-etc { background: #eee; color: #666; }
</style>

    <!-- 메인 -->
    <main class="main order-detail">

      <a class="back-link" href="${pageContext.request.contextPath}/vendor/order.htm">&larr; 목록으로</a>

      <h1>주문번호 ${order.orderNo}</h1>
      <p class="sub">
        <c:choose>
          <c:when test="${order.orderStatus == '결제완료'}"><span class="badge badge-paid">결제완료</span></c:when>
          <c:when test="${order.orderStatus == '배송중'}"><span class="badge badge-shipping">배송중</span></c:when>
          <c:when test="${order.orderStatus == '주문완료'}"><span class="badge badge-done">주문완료</span></c:when>
          <c:otherwise><span class="badge badge-etc"><c:out value="${order.orderStatus}" /></span></c:otherwise>
        </c:choose>
        <fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss" /> 주문
      </p>

      <div class="section">
        <h2>주문상품</h2>
        <table>
          <thead>
            <tr>
              <th>상품명</th>
              <th>옵션</th>
              <th>수량</th>
              <th>단가</th>
              <th>금액</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="item" items="${order.items}">
              <tr>
                <td><c:out value="${item.productName}" /></td>
                <td><c:out value="${not empty item.optionLabel ? item.optionLabel : '-'}" /></td>
                <td>${item.orderQty}</td>
                <td><fmt:formatNumber value="${item.price}" pattern="#,##0" />원</td>
                <td><fmt:formatNumber value="${item.price * item.orderQty}" pattern="#,##0" />원</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <div class="section">
        <h2>결제 정보</h2>
        <dl class="grid">
          <dt>상품금액</dt>
          <dd><fmt:formatNumber value="${order.productAmount}" pattern="#,##0" />원</dd>
          <dt>배송비</dt>
          <dd><fmt:formatNumber value="${order.deliveryFee}" pattern="#,##0" />원</dd>
          <dt>즉시할인</dt>
          <dd>-<fmt:formatNumber value="${order.instantDiscount}" pattern="#,##0" />원</dd>
          <dt>쿠폰할인</dt>
          <dd>-<fmt:formatNumber value="${order.couponDiscount}" pattern="#,##0" />원</dd>
          <dt>적립금 사용</dt>
          <dd>-<fmt:formatNumber value="${order.cashUsed}" pattern="#,##0" />원</dd>
          <dt>최종 결제금액</dt>
          <dd><strong><fmt:formatNumber value="${order.totalPrice}" pattern="#,##0" />원</strong></dd>
        </dl>
      </div>

      <div class="section">
        <h2>구매자 / 배송지</h2>
        <dl class="grid">
          <dt>주문자</dt>
          <dd><c:out value="${order.buyerName}" /> (<c:out value="${order.buyerPhone}" />)</dd>
          <dt>수령인</dt>
          <dd><c:out value="${order.receiverName}" /> (<c:out value="${order.receiverTel}" />)</dd>
          <dt>배송지 주소</dt>
          <dd>(<c:out value="${order.zipcode}" />) <c:out value="${order.address}" /> <c:out value="${order.detailAddress}" /></dd>
          <dt>배송 요청사항</dt>
          <dd><c:out value="${not empty order.requestMsg ? order.requestMsg : '-'}" /></dd>
        </dl>
      </div>

      <div class="section">
        <h2>배송 현황</h2>
        <c:choose>
          <c:when test="${not empty order.invoiceNo}">
            <dl class="grid">
              <dt>택배사</dt>
              <dd><c:out value="${order.deliveryServiceCode}" /></dd>
              <dt>송장번호</dt>
              <dd><c:out value="${order.invoiceNo}" /></dd>
              <dt>배송상태</dt>
              <dd><c:out value="${order.deliveryStatus}" /></dd>
              <dt>배송 시작일시</dt>
              <dd><fmt:formatDate value="${order.deliveryStartDate}" pattern="yyyy-MM-dd HH:mm" /></dd>
              <dt>배송 완료일시</dt>
              <dd>
                <c:choose>
                  <c:when test="${not empty order.deliveryEndDate}"><fmt:formatDate value="${order.deliveryEndDate}" pattern="yyyy-MM-dd HH:mm" /></c:when>
                  <c:otherwise>-</c:otherwise>
                </c:choose>
              </dd>
            </dl>
          </c:when>
          <c:otherwise>
            <p>아직 배송이 시작되지 않았습니다.</p>
          </c:otherwise>
        </c:choose>
      </div>

    </main>

  </div>

  <script src="${pageContext.request.contextPath}/resources/js/vendor-common.js"></script>
