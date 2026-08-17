<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>쿠팡 주문/결제</title>
    
    <!-- CSS 분리 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/coupang_order_payment.css">
</head>
<body>

<div class="top-header"></div>

<div class="container">
    
    <!-- 로고 -->
      <h1 class="logo">
        <a href="#" title="GoodPang 홈으로">
            <span class="l1">G</span>
            <span class="l1">o</span>
            <span class="l1">o</span>
            <span class="l2">d</span>
            <span class="l3">P</span>
            <span class="l4">a</span>
            <span class="l5">n</span>
            <span class="l5">g</span>
        </a>
    </h1>

    <h1 class="page-title">주문/결제</h1>
    <div class="breadcrumb">
        <span>주문결제</span> &gt; 주문완료
    </div>

    <div class="content-wrap">
        <!-- ========== 왼쪽 영역 ========== -->
        <div class="left-section">

            <!-- 배송지 -->
            <div class="section-box">
                <div class="section-header">
                    <div>
                        <strong>배송지</strong> | ${address.name}
                    </div>
                    <button class="btn-outline" type="button">배송지 변경</button>
                </div>
                <div class="section-body">
                    <div class="address-detail">
                        ${address.roadAddress}<br>
                        ${address.detailAddress}<br>
                        ${address.phone}
                    </div>
                    <c:if test="${address.defaultAddress}">
                        <span class="tag">기본배송지</span>
                    </c:if>
                </div>
            </div>

            <!-- 배송 요청사항 -->
            <div class="section-box">
                <div class="section-header">
                    <strong>배송 요청사항</strong>
                    <button class="btn-outline" type="button">변경</button>
                </div>
                <div class="section-body">
                    <c:choose>
                        <c:when test="${not empty deliveryRequest}">
                            ${deliveryRequest}
                        </c:when>
                        <c:otherwise>
                            <span style="color:#aaa;">배송 요청사항이 없습니다.</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- 결제수단 -->
            <div class="section-box">
                <div class="section-header">
                    <strong>결제수단</strong>
                </div>
                <div class="payment-method">
                    <div class="radio-item">
                        <input type="radio" name="payMethod" id="pay1" value="coupangPay" checked>
                        <label for="pay1">
                            쿠페이 머니 : <fmt:formatNumber value="${summary.usedCash}" pattern="#,###"/>원
                            <span class="badge-red">최대 캐시적립</span>
                        </label>
                    </div>
                    <div class="radio-item">
                        <input type="radio" name="payMethod" id="pay2" value="other">
                        <label for="pay2"></label>
                    </div>
                    <div class="other-payment">다른 결제 수단 ▾</div>
                </div>
            </div>

            <!-- 배송 / 상품 정보 -->
            <div class="delivery-info">
                <h4>배송 1건 중 1</h4>

                <c:forEach var="item" items="${orderItems}" varStatus="status">
                    <div class="product-item">
                        <div class="product-img"></div>
                        <div class="product-info">
                            <div class="product-name">${item.productName}</div>
                            <div class="product-option">${item.optionName}</div>
                            <div class="product-price">
                                <fmt:formatNumber value="${item.salePrice}" pattern="#,###"/>원
                            </div>
                            <div class="product-qty">
                                수량 ${item.quantity}개
                                <c:if test="${item.freeDelivery}"> / 무료배송</c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

        </div>

        <!-- ========== 오른쪽 결제 금액 영역 ========== -->
        <div class="right-section">
            <div class="payment-summary">
                <h3>최종 결제 금액</h3>

                <div class="price-row">
                    <span>총 상품 가격</span>
                    <span><fmt:formatNumber value="${summary.totalProductPrice}" pattern="#,###"/>원</span>
                </div>

                <div class="price-row">
                    <span>즉시할인</span>
                    <span class="discount">-<fmt:formatNumber value="${summary.instantDiscount}" pattern="#,###"/>원</span>
                </div>

                <div class="price-row">
                    <span>
                        쿠폰할인
                        <button class="coupon-btn" type="button">변경</button>
                    </span>
                    <span class="discount">-<fmt:formatNumber value="${summary.couponDiscount}" pattern="#,###"/>원</span>
                </div>

                <div class="price-row">
                    <span>배송비</span>
                    <span><fmt:formatNumber value="${summary.deliveryFee}" pattern="#,###"/>원</span>
                </div>

                <div class="price-row">
                    <span>쿠팡캐시</span>
                    <div class="cash-input">
                        <button type="button">전액사용</button>
                        <input type="text" value="${summary.usedCash}"> 원
                    </div>
                </div>
                <div style="text-align: right; font-size: 12px; color: #888; margin-top: -6px;">
                    잔여 : <fmt:formatNumber value="${summary.remainCash}" pattern="#,###"/>원
                </div>

                <div class="divider"></div>

                <div class="total-row">
                    <span class="label">총 결제 금액</span>
                    <span class="amount"><fmt:formatNumber value="${summary.finalPrice}" pattern="#,###"/>원</span>
                </div>

                <div class="agree-links">
                    개인정보 제3자 제공 동의
                    <a href="#">보기</a>
                </div>
                <div class="agree-links">
                    개인정보 수집 및 이용 안내
                    <a href="#">보기</a>
                </div>

                <div class="notice">
                    * 개별 판매자가 등록한 마켓플레이스(오픈마켓) 상품에 대한 광고, 상품주문, 배송 및 환불의 의무와 책임은 각 판매자가 부담하고, 이에 대하여 쿠팡은 통신판매중개자로서 통신판매의 당사자가 아니므로 일체 책임을 지지 않습니다.
                </div>

                <div class="final-agree">
                    위 주문 내용을 확인 하였으며, 회원 본인은 개인정보 이용 및 제공(해외직구의 경우 국외제공) 및 결제에 동의합니다.
                </div>

                <button class="btn-pay" type="button">결제하기</button>
            </div>
        </div>
    </div>
</div>

</body>
</html>