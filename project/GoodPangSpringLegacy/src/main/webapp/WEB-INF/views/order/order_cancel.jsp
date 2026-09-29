<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!-- ★ 팀원들이 만든 커스텀 TLD 태그라이브러리 추가 -->
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld" %>


<!-- 공통 CSS 및 전용 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/order_cancel.css">


<div class="cancel-wrap">

    <!-- 상단 헤더 -->
    <div class="cancel-header">
        <button type="button" class="back-btn" onclick="history.back();" title="뒤로가기">←</button>
        <h1>주문 취소</h1>
    </div>

    <!-- 주문 상품 정보 -->
    <section class="cancel-section">
        <h2>주문 상품</h2>
        <c:forEach var="cancelInfo" items="${cancelList}">
        <div class="product-box">
         <!-- 상품 이미지 출력 영역 -->
				<div class="product-img">
				    <img src="${img:url(cancelInfo.imageUrl)}" alt="${cancelInfo.productName}" />
				</div>
            <div class="product-info">
                <p class="product-name">
                    <span class="rocket-badge">🚀 로켓배송</span> ${cancelInfo.productName}
                </p>
                <c:if test="${not empty cancelInfo.option1Value or not empty cancelInfo.option2Value}">
                    <div class="product-option">
                        <span>옵션: </span>
                        <c:if test="${not empty cancelInfo.option1Value}">
                            <c:if test="${not empty cancelInfo.option1Type}">${cancelInfo.option1Type}: </c:if>${cancelInfo.option1Value}
                        </c:if>
                        <c:if test="${not empty cancelInfo.option1Value and not empty cancelInfo.option2Value}"> / </c:if>
                        <c:if test="${not empty cancelInfo.option2Value}">
                            <c:if test="${not empty cancelInfo.option2Type}">${cancelInfo.option2Type}: </c:if>${cancelInfo.option2Value}
                        </c:if>
                    </div>
                </c:if>
                <p class="product-count">수량 : ${cancelInfo.quantity}개</p>
                <strong class="product-price">
                    <fmt:formatNumber value="${cancelInfo.itemPrice * cancelInfo.quantity}" pattern="#,###" />원
                </strong>
            </div>
        </div>
        </c:forEach>
    </section>

    <!-- 취소 사유 선택 -->
    <section class="cancel-section">
        <h2>취소 사유</h2>
        <select name="cancelReason" id="cancelReason" class="cancel-select">
            <option value="">취소 사유를 선택해주세요</option>
            <option value="단순 변심">단순 변심</option>
            <option value="상품을 잘못 주문함">상품을 잘못 주문함</option>
            <option value="배송이 너무 늦음">배송이 너무 늦음</option>
            <option value="가격이 마음에 들지 않음">가격이 마음에 들지 않음</option>
            <option value="기타">기타</option>
        </select>
    </section>

    <%-- <!-- 환불 정보 -->
    <section class="cancel-section">
        <h2>환불 정보</h2>
      
        <div class="refund-box">
            <div class="refund-row">
                <span>상품 금액</span>
                <strong><fmt:formatNumber value="${cancelList.totalPrice - cancelList.deliveryFee}" pattern="#,###" />원</strong>
            </div>
            <div class="refund-row">
                <span>배송비</span>
                <strong><fmt:formatNumber value="${cancelList.deliveryFee}" pattern="#,###" />원</strong>
            </div>
            <div class="refund-line"></div>
            <div class="refund-row total">
                <span>환불 예정 금액</span>
                <strong class="total-price"><fmt:formatNumber value="${cancelList.totalPrice}" pattern="#,###" />원</strong>
            </div>
        </div>
    </section>  --%>
    
    <!-- 1. 리스트를 돌면서 총 환불금액 계산 -->
<c:set var="totalProductPrice" value="0" />
<c:set var="deliveryFee" value="${cancelList[0].deliveryFee}" />

<c:forEach var="item" items="${cancelList}">
    <c:set var="totalProductPrice" value="${totalProductPrice + (item.itemPrice * item.quantity)}" />
</c:forEach>

<!-- 2. 환불 정보 영역 출력 -->
<section class="cancel-section">
    <h2>환불 정보</h2>
    <div class="refund-box">
        <div class="refund-row">
            <span>상품 금액</span>
            <strong><fmt:formatNumber value="${totalProductPrice}" pattern="#,###" />원</strong>
        </div>
        <div class="refund-row">
            <span>배송비</span>
            <strong><fmt:formatNumber value="${deliveryFee}" pattern="#,###" />원</strong>
        </div>
        <div class="refund-line"></div>
        <div class="refund-row total">
            <span>환불 예정 금액</span>
            <strong class="total-price"><fmt:formatNumber value="${totalProductPrice + deliveryFee}" pattern="#,###" />원</strong>
        </div>
    </div>
</section>
    
   
    <!-- 주문 취소 안내 -->
    <section class="notice">
        <h3>주문 취소 안내</h3>
        <ul>
            <li>주문 취소 후에는 상품을 다시 주문해야 합니다.</li>
            <li>결제 수단에 따라 환불까지 1~3일(영업일 기준)이 소요될 수 있습니다.</li>
            <li>배송이 시작된 상품은 주문 취소가 제한되며 반품 절차로 진행됩니다.</li>
        </ul>
    </section>

    <!-- 취소 버튼 영역 -->
<div class="button-area">
    <button type="button" class="cancel-submit" onclick="cancelOrder(${cancelList[0].orderNo});">
        주문 취소하기
    </button>
</div>

<!-- 서버로 전송할 Hidden Form (페이지 하단에 위치) -->
<form id="cancelForm" action="${pageContext.request.contextPath}/order/order_cancel" method="post">
    <input type="hidden" id="formOrderNo" name="orderNo" value="" />
    <input type="hidden" id="formCancelReason" name="cancelReason" value="" />
    <!-- Spring Security 사용 시 CSRF 토큰 태그 -->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>

</div>

<!-- 취소 액션 제출용 동적 폼 -->
<%-- 
<form id="cancelForm" action="${pageContext.request.contextPath}/order/cancel_action" method="post">
     Spring Security CSRF 토큰 태그 (사용 중이라면 주석 해제) 
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="orderNo" id="formOrderNo" />
    <input type="hidden" name="memberNo" id="formMemberNo" />
    <input type="hidden" name="cancelReason" id="formCancelReason" />
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>
 --%>

<script>
function cancelOrder(orderNo) {
    const reasonSelect = document.getElementById("cancelReason");
    
    // 1. 유효성 검사
    if (!reasonSelect || !reasonSelect.value) {
        alert("취소 사유를 선택해주세요.");
        if (reasonSelect) reasonSelect.focus();
        return;
    }

    const reasonText = reasonSelect.options[reasonSelect.selectedIndex].text;

    // 2. 컨펌 창
    if (!confirm("정말 주문을 취소하시겠습니까?")) {
        return;
    }

    // 3. Hidden Form에 값 바인딩 후 POST 제출 (백엔드 로직 호출)
    document.getElementById("formOrderNo").value = orderNo;
    // document.getElementById("formMemberNo").value = memberNo;
    document.getElementById("formCancelReason").value = reasonText;

    // ⭕ 폼 제출 -> 컨트롤러(@PostMapping("/order_cancel")) -> DB 3단 처리 -> cancel_confirm 이동
    document.getElementById("cancelForm").submit();
}
</script>
