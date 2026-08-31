<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>와우 멤버십 가입</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/wow_join.css">
</head>
<body>

	<jsp:include page="/inc/header.jsp" />

	<div class="wow-join-page">
		<div class="wow-join-container">

			<div class="wow-join-header">
				<div class="wow-join-logo">WOW!</div>
				<h1>와우 멤버십</h1>
				<p>매일 누리는 특별한 혜택을 만나보세요.</p>
			</div>

			<section class="wow-benefit-section">
				<h2>와우 회원이라면 누릴 수 있어요</h2>

				<div class="wow-benefit-list">
					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🚚</div>
						<div class="wow-benefit-text">
							<strong>로켓배송 무료배송</strong>
							<p>금액 조건 없이 로켓배송 상품을 무료배송으로 받아보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🌙</div>
						<div class="wow-benefit-text">
							<strong>새벽배송 · 당일배송</strong>
							<p>필요한 상품을 주문하고 더 빠르게 받아보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">↩</div>
						<div class="wow-benefit-text">
							<strong>30일 무료반품</strong>
							<p>상품이 마음에 들지 않아도 편리하게 무료반품할 수 있어요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">₩</div>
						<div class="wow-benefit-text">
							<strong>와우 회원 전용 할인</strong>
							<p>다양한 상품의 와우 회원 전용 할인가를 만나보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🥬</div>
						<div class="wow-benefit-text">
							<strong>로켓프레시 혜택</strong>
							<p>신선식품도 빠르고 편리하게 받아보세요.</p>
						</div>
					</div>

					<div class="wow-benefit-item">
						<div class="wow-benefit-icon">🍽</div>
						<div class="wow-benefit-text">
							<strong>쿠팡이츠 혜택</strong>
							<p>와우 회원을 위한 다양한 쿠팡이츠 혜택을 이용할 수 있어요.</p>
						</div>
					</div>
				</div>
			</section>

			<form action="${pageContext.request.contextPath}/wow/join"
				method="post" id="wowJoinForm">

				<section class="wow-payment-section">
					<h2>결제수단</h2>

					<c:choose>
						<c:when test="${not empty paymentMethods}">
							<div class="wow-payment-method-list">
								<c:forEach var="payment" items="${paymentMethods}"
									varStatus="status">
									<label class="wow-payment-method"> <input type="radio"
										name="paymentMethodNo" value="${payment.paymentMethodNo}"
										<c:if test="${status.first}">checked</c:if>>

										<div class="wow-payment-info">
											<c:choose>
												<c:when test="${payment.paymentType eq 'BANK'}">
													<strong>${payment.bankName}</strong>
													<span>계좌 ****${payment.accountLast4}</span>

													<c:if test="${not empty payment.accountHolder}">
														<span>${payment.accountHolder}</span>
													</c:if>
												</c:when>

												<c:when test="${payment.paymentType eq 'CARD'}">
													<strong>${payment.cardCompany}</strong>
													<span>카드 ****${payment.cardLast4}</span>
												</c:when>

												<c:otherwise>
													<strong>${payment.paymentType}</strong>
												</c:otherwise>
											</c:choose>

											<c:if test="${payment.paymentDefault}">
												<span class="wow-default-badge">기본 결제수단</span>
											</c:if>
										</div>
									</label>
								</c:forEach>
							</div>
						</c:when>

						<c:otherwise>
							<div class="wow-no-payment">
								<p>등록된 결제수단이 없습니다.</p>
								<button type="button"
									onclick="location.href='${pageContext.request.contextPath}/payment-method/list'">
									결제수단 등록하기</button>
							</div>
						</c:otherwise>
					</c:choose>
				</section>

				<section class="wow-price-section">
					<h2>멤버십 이용료</h2>

					<div class="wow-price-box">
						<div class="wow-price-row">
							<span>와우 멤버십</span> <strong>월 7,890원</strong>
						</div>

						<p>매월 선택한 결제수단으로 자동 결제됩니다.</p>
					</div>
				</section>

				<section class="wow-agree-section">
					<label class="wow-agree"> <input type="checkbox"
						id="wowAgree"> <span>와우 멤버십 이용 조건 및 월 7,890원 정기결제에
							동의합니다.</span>
					</label>

					<p class="wow-agree-notice">가입 후 언제든지 와우 멤버십 관리 페이지에서 해지할 수
						있습니다.</p>
				</section>

				<button type="submit" id="wowSubmitBtn" class="wow-submit-btn"
					disabled>와우 멤버십 가입하기</button>
			</form>

			<button type="button" class="wow-back-btn" onclick="history.back()">다음에
				할게요</button>

		</div>
	</div>

	<script>
document.addEventListener("DOMContentLoaded", function() {
    const agree = document.getElementById("wowAgree");
    const submitBtn = document.getElementById("wowSubmitBtn");
    const paymentMethods = document.querySelectorAll('input[name="paymentMethodNo"]');

    function checkForm() {
        const selectedPayment = document.querySelector('input[name="paymentMethodNo"]:checked');
        submitBtn.disabled = !(agree.checked && selectedPayment);
    }

    agree.addEventListener("change", checkForm);

    paymentMethods.forEach(function(payment) {
        payment.addEventListener("change", checkForm);
    });

    document.getElementById("wowJoinForm").addEventListener("submit", function(event) {
        const selectedPayment = document.querySelector('input[name="paymentMethodNo"]:checked');

        if (!selectedPayment) {
            event.preventDefault();
            alert("결제수단을 선택해주세요.");
            return;
        }

        if (!agree.checked) {
            event.preventDefault();
            alert("와우 멤버십 이용 조건에 동의해주세요.");
        }
    });

    checkForm();
});
</script>

</body>
</html>