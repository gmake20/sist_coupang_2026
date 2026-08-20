<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>주문 취소</title>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/order_cancel.css">

</head>

<body>

<div class="cancel-wrap">

    <!-- 상단 -->
    <div class="cancel-header">
        <button type="button"
                class="back-btn"
                onclick="history.back();">
            ←
        </button>

        <h1>주문 취소</h1>
    </div>


    <!-- 주문 상품 -->
    <section class="cancel-section">

        <h2>주문 상품</h2>

        <div class="product-box">

            <div class="product-img">
                <img src="${pageContext.request.contextPath}/images/product.jpg"
                     alt="상품 이미지">
            </div>

            <div class="product-info">

                <p class="product-name">
                   시원한 면 티
                </p>

                <p class="product-option">
                    옵션 : 아이보리
                </p>

                <p class="product-count">
                    수량 : 1개
                </p>

                <strong class="product-price">
                    20,000원
                </strong>

            </div>

        </div>

    </section>


    <!-- 취소 사유 -->
    <section class="cancel-section">

        <h2>취소 사유</h2>

        <select name="cancelReason"
                id="cancelReason">

            <option value="">취소 사유를 선택해주세요</option>
            <option value="change">단순 변심</option>
            <option value="wrong">상품을 잘못 주문함</option>
            <option value="delivery">배송이 너무 늦음</option>
            <option value="price">가격이 마음에 들지 않음</option>
            <option value="etc">기타</option>

        </select>

    </section>


    <!-- 환불 정보 -->
    <section class="cancel-section">

        <h2>환불 정보</h2>

        <div class="refund-box">

            <div class="refund-row">
                <span>상품 금액</span>
                <strong>29,900원</strong>
            </div>

            <div class="refund-row">
                <span>배송비</span>
                <strong>0원</strong>
            </div>

            <div class="refund-line"></div>

            <div class="refund-row total">
                <span>환불 예정 금액</span>
                <strong>29,900원</strong>
            </div>

        </div>

    </section>


    <!-- 안내 -->
    <section class="notice">

        <h3>주문 취소 안내</h3>

        <ul>
            <li>주문 취소 후에는 상품을 다시 주문해야 합니다.</li>
            <li>결제 수단에 따라 환불까지 시간이 걸릴 수 있습니다.</li>
            <li>배송이 시작된 상품은 주문 취소가 제한될 수 있습니다.</li>
        </ul>

    </section>


    <!-- 버튼 -->
    <div class="button-area">

        <button type="button"
                class="cancel-submit"
                onclick="cancelOrder();">
            주문 취소하기
        </button>

    </div>

</div>


<script>

function cancelOrder() {

    const reason = document.getElementById("cancelReason").value;

    if (reason === "") {
        alert("취소 사유를 선택해주세요.");
        return;
    }

    if (!confirm("주문을 취소하시겠습니까?")) {
        return;
    }

    location.href =
        "${pageContext.request.contextPath}/order_cancel_complete.jsp";
}

</script>

</body>
</html>
