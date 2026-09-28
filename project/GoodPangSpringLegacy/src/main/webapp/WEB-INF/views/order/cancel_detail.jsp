<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- ★ 팀원들이 만든 커스텀 TLD 태그라이브러리 추가 -->
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld" %>


<script src="${pageContext.request.contextPath}/js/header.js"></script>

<!-- <div class="page-wrap"> -->


    <!-- 중앙 본문 -->
    <main class="content">
        <h1>취소/반품/교환/환불내역 상세</h1>

        <div class="order-meta-info">
            <span>주문일 : <fmt:formatDate value="${cancelInfo2.orderDate}" pattern="yyyy/MM/dd" /></span>
            <span>|</span>
            <span>주문번호 : ${cancelInfo2.orderNo}</span>
        </div>

        <!-- 상품 요약 테이블 -->
        <table class="detail-table">
            <thead>
                <tr>
                    <th style="width: 55%;">상품</th>
                    <th style="width: 20%;">금액</th>
                    <th style="width: 25%;">진행 상태</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${cancelDetailList}">
                    <tr>
                   <td>
    <div class="prod-cell">
          <!-- 상품 이미지 영역 -->
			<div class="prod-img">
                   <img src="${img:url(item.imageUrl)}" alt="${item.productName}" />
            </div>

        <!-- 2. 상품 텍스트 및 옵션 (order_detail.jsp 방식 그대로 적용) -->
        <div class="prod-text" style="margin-left: 12px;">
            <div class="prod-name" style="font-weight: bold; font-size: 14px; margin-bottom: 4px;">
                ${item.productName}
            </div>

            <!-- 옵션 정보 동적 출력 (기존 c:if 구문 적용) -->
            <c:if test="${not empty item.option1Value or not empty item.option2Value}">
                <div class="product-option" style="font-size: 12px; color: #888;">
                    <span>옵션: </span>
                    <c:if test="${not empty item.option1Value}">
                        <c:if test="${not empty item.option1Type}">${item.option1Type}: </c:if>${item.option1Value}
                    </c:if>
                    <c:if test="${not empty item.option1Value and not empty item.option2Value}"> / </c:if>
                    <c:if test="${not empty item.option2Value}">
                        <c:if test="${not empty item.option2Type}">${item.option2Type}: </c:if>${item.option2Value}
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</td>
                        <td style="text-align: center;">
                            <strong>${item.quantity}개</strong><br>
                            <fmt:formatNumber value="${item.itemPrice * item.quantity}" pattern="#,###" />원
                        </td>
                        <td class="status-cell">
                            <strong>${item.orderStatus}</strong>
                            <p>
                                <c:if test="${not empty item.expectedCancelDate}">
                                    <fmt:formatDate value="${item.expectedCancelDate}" pattern="M/d(E)" /> 이내
                                </c:if><br>
                                환불 완료 예정
                            </p>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- 상세정보 -->
        <div class="section-title">상세정보</div>
        <table class="info-grid-table">
            <tr>
                <th>취소접수일자</th>
                <td><fmt:formatDate value="${cancelInfo2.requestDate}" pattern="yyyy/MM/dd" /></td>
            </tr>
            <tr>
                <th>취소접수번호</th>
                <td>${cancelInfo2.returnNo}</td>
            </tr>
            
        </table>

        <!-- 취소 사유 -->
        <div class="section-title">취소 사유</div>
        <table class="info-grid-table">
            <tr>
                <th>취소 사유</th>
                <td>${not empty cancelInfo2.returnReason ? cancelInfo2.returnReason : '품절로 인해 자동취소 되었습니다.'}</td>
            </tr>
        </table>
<!-- =========================================================
     1. JSTL을 활용한 주문건 전체 상품금액 및 최종 환불금액 누적합
     ========================================================= -->
<!-- A. 전체 상품 금액 누적용 변수 초기화 -->
<c:set var="sumItemTotal" value="0" />

<!-- B. 취소 상세 상품 목록을 돌면서 각 상품의 (단가 * 수량)을 누적합 -->
<c:forEach var="item" items="${cancelDetailList}">
    <c:set var="sumItemTotal" value="${sumItemTotal + (item.itemPrice * item.quantity)}" />
</c:forEach>

<!-- 배송비 (null이 들어오면 기본값 0 적용) -->
<c:set var="actualDeliveryFee" value="${not empty cancelInfo2.deliveryFee ? cancelInfo2.deliveryFee : 0}" />

<!-- D. 최종 환불 완료 금액 (누적 총 상품금액 + DB 전달 배송비) -->
<c:set var="finalRefundTotal" value="${sumItemTotal + actualDeliveryFee}" />


<!-- =========================================================
     2. 환불 안내 UI 영역 출력
     ========================================================= -->
<div class="section-title">환불안내</div>
<div class="refund-wrap">
    <div class="refund-left">
        <!-- 상품금액 -->
        <div class="refund-row">
            <span>상품금액</span>
            <strong><fmt:formatNumber value="${sumItemTotal}" pattern="#,###" />원</strong>
        </div>
        
        <!-- 배송비 (DB에서 전달된 deliveryFee 그대로 출력) -->
        <div class="refund-row">
            <span>배송비</span>
            <strong>
                <c:choose>
                    <c:when test="${actualDeliveryFee == 0}">
                        0원
                    </c:when>
                    <c:otherwise>
                        +<fmt:formatNumber value="${actualDeliveryFee}" pattern="#,###" />원
                    </c:otherwise>
                </c:choose>
            </strong>
        </div>
        
        <!-- 반품비 -->
        <div class="refund-row">
            <span>반품비</span>
            <strong>0원</strong>
        </div>
    </div>
    
    <div class="refund-right">
        <!-- 환불 수단 -->
        <div class="refund-row">
            <span>환불 수단</span>
            <strong>
                <c:choose>
                    <c:when test="${not empty cancelInfo.cardCompanyName}">
                        ${cancelInfo.cardCompanyName} / 일시불
                    </c:when>
                    <c:when test="${not empty cancelInfo.bankName}">
                        ${cancelInfo.bankName} / 계좌이체
                    </c:when>
                    <c:otherwise>
                        ${cancelInfo.paymentMethod}
                    </c:otherwise>
                </c:choose>
            </strong>
        </div>
        
        <!-- 환불 완료 총액 -->
        <div class="refund-row total">
            <span>환불 완료</span>
            <strong><fmt:formatNumber value="${finalRefundTotal}" pattern="#,###" />원</strong>
        </div>
    </div>
</div>

        <div class="notice-text">
            ❶ 카드사로 결제 취소 요청이 전달된 후 환불까지 평일 기준 3~7일이 소요됩니다.
        </div>

        <!-- 목록 버튼 -->
        <div class="btn-center-area">
            <a href="${pageContext.request.contextPath}/order/cancel_history" class="list-btn">목록</a>
        </div>

    </main>


<!-- </div> -->

