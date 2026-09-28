<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="org.doit.goodpang.domain.SellerDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld"%>

     <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">취소/반품/교환 관리</h1>
          <p class="page-desc">내 상품에 대한 취소·반품·교환 신청 내역을 확인할 수 있습니다.</p>
        </div>

      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">취소</span>
            <span class="stat-icon stat-icon-red"><svg class="icon"><use href="#ic-ban" /></svg></span>
          </div>
          <div class="stat-value">${cancelCount} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">반품</span>
            <span class="stat-icon stat-icon-orange"><svg class="icon"><use href="#ic-return" /></svg></span>
          </div>
          <div class="stat-value">${returnCount} <small>건</small></div>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">교환</span>
            <span class="stat-icon stat-icon-violet"><svg class="icon"><use href="#ic-box" /></svg></span>
          </div>
          <div class="stat-value">${exchangeCount} <small>건</small></div>
        </article>

      </section>


      <!-- 취소/반품/교환 목록 -->
      <section class="panel table-panel">

        <div class="result-toolbar">
          <p class="result-count">전체 <strong>${fn:length(returnList)}</strong>건</p>
        </div>

        <div class="table-scroll">
          <table class="order-table">
            <thead>
              <tr>
                <th class="col-order">주문 정보</th>
                <th class="col-info">상품 정보</th>
                <th class="col-status">유형</th>
                <th class="col-info">사유</th>
                <th class="col-price">환불 금액</th>
                <th class="col-date">신청일</th>
                <th class="col-manage">상태</th>
              </tr>
            </thead>
            <tbody>

              <c:choose>

                <c:when test="${empty returnList}">
                  <tr>
                    <td colspan="7" class="empty" style="text-align: center; padding: 60px 0; color: #999;">
                      취소/반품/교환 신청 내역이 없습니다.
                    </td>
                  </tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="item" items="${returnList}">
                    <tr>
                      <td class="col-order">
                        <p class="order-no">주문번호<br><strong>${item.orderNo}</strong></p>
                        <p class="order-buyer">${item.buyerName} (${item.buyerPhone})</p>
                      </td>
                      <td class="col-info">
                        ${item.productName}
                        <c:if test="${not empty item.optionLabel}"><br>${item.optionLabel}</c:if>
                        <br>수량 ${item.returnQty}개
                      </td>
                      <td class="col-status">
                        <c:choose>
                          <c:when test="${item.returnType == '취소'}">
                            <span class="return-type-badge return-type-cancel">취소</span>
                          </c:when>
                          <c:when test="${item.returnType == '반품'}">
                            <span class="return-type-badge return-type-return">반품</span>
                          </c:when>
                          <c:when test="${item.returnType == '교환'}">
                            <span class="return-type-badge return-type-exchange">교환</span>
                          </c:when>
                          <c:otherwise>
                            <span class="return-type-badge return-type-etc">${item.returnType}</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="col-info">${item.returnReason}</td>
                      <td class="col-price">
                        <p class="price"><fmt:formatNumber value="${item.refundAmount}" pattern="#,##0" />원</p>
                      </td>
                      <td class="col-date">
                        <fmt:formatDate value="${item.requestDate}" pattern="yyyy-MM-dd" /><br>
                        <span class="time"><fmt:formatDate value="${item.requestDate}" pattern="HH:mm" /></span>
                      </td>
                      <td class="col-manage">${item.returnStatus}</td>
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


  <script src="${pageContext.request.contextPath}/resources/js/vendor-common.js"></script>
