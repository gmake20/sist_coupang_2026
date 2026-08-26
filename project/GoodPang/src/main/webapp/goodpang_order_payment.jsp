 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>쿠팡 주문/결제</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/goodpang_order_payment.css">
</head>

<body>

<div class="top-header"></div>

<div class="container">

	<h1 class="logo">
		<a href="${pageContext.request.contextPath}/" title="GoodPang 홈으로">
			<span class="brand-goodpang">GoodPang</span>
		</a>
	</h1>

	<h1 class="page-title">주문/결제</h1>

	<div class="breadcrumb">
		<span>주문결제</span> &gt; 주문완료
	</div>

	<div class="content-wrap">

		<!-- 왼쪽 영역 -->
		<div class="left-section">

			<!-- 배송지 -->
			<div class="section-box">
				<div class="section-header">
					<div>
						<strong>배송지</strong> |
						<span id="currentReceiverName">${address.receiverName}</span>
					</div>

					<button class="btn-outline"
							type="button"
							onclick="openAddressModal()">
						배송지 변경
					</button>
				</div>

				<div class="section-body">
					<div class="address-detail">
						<span id="currentAddress">${address.address}</span><br>
						<span id="currentDetailAddress">${address.detailAddress}</span><br>
						휴대폰 :
						<span id="currentTel">${address.tel}</span>
					</div>

					<c:if test="${address.addressDefault}">
						<span class="tag" id="currentDefaultTag">기본배송지</span>
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
					<span id="currentRequestMessage">
						<c:choose>
							<c:when test="${not empty address.requestMsg}">
								${address.requestMsg}
							</c:when>
							<c:otherwise>
								배송 요청사항이 없습니다.
							</c:otherwise>
						</c:choose>
					</span>
				</div>
			</div>

			<!-- 결제수단 -->
			<div class="section-box payment-section">

				<div class="section-header">
					<strong>결제수단</strong>
				</div>

				<div class="payment-method">

					<!-- 쿠페이 머니 -->
					<label class="pay-radio-row">
						<input type="radio"
							   name="paymentMethod"
							   value="COUPAY_MONEY"
							   form="paymentForm">

						<span class="pay-title">
							쿠페이 머니 :
							<strong>
								<fmt:formatNumber
									value="${checkout.cashUsed}"
									pattern="#,###"/>원
							</strong>
						</span>

						<span class="badge-red">최대 캐시적립</span>
					</label>

					<!-- 계좌이체 -->
					<label class="pay-radio-row account-row">
						<input type="radio"
							   name="paymentMethod"
							   value="BANK_TRANSFER"
							   form="paymentForm"
							   checked>
						<span class="pay-title">계좌이체</span>
					</label>

					<!-- 계좌 선택 -->
					<div class="bank-setting" id="bankSetting">

						<select class="bank-select"
								name="bankCode"
								id="bankCode"
								form="paymentForm">
							<option value="SHINHAN">
								신한은행 / **********4382
							</option>
							<option value="WOORI">
								우리은행 / **********7204
							</option>
							<option value="NH">
								농협은행 / **********9137
							</option>
						</select>

						<label class="default-payment">
							<input type="checkbox"
								   name="defaultPayment"
								   id="defaultPayment"
								   value="Y"
								   form="paymentForm"
								   checked>
							<span>기본 결제 수단으로 사용</span>
						</label>
					</div>

					<div class="payment-divider"></div>

					<!-- 다른 결제수단 -->
					<button class="other-payment-title"
							type="button"
							onclick="toggleOtherPayment()">
						<strong>다른 결제 수단</strong>
						<span id="paymentArrow">︿</span>
					</button>

					<div class="other-payment-list" id="otherPaymentList">

						<!-- 신용/체크카드 -->
						<label class="pay-radio-row other-radio">
							<input type="radio"
								   name="paymentMethod"
								   value="CARD"
								   form="paymentForm">
							<span class="pay-title">신용/체크카드</span>
						</label>

						<!-- 카드사 선택 -->
						<div class="card-setting hidden" id="cardSetting">
							<select class="bank-select"
									name="cardCompany"
									id="cardCompany"
									form="paymentForm"
									disabled>
								<option value="">카드사를 선택하세요</option>
								<option value="SHINHAN">신한카드</option>
								<option value="KB">KB국민카드</option>
								<option value="SAMSUNG">삼성카드</option>
								<option value="HYUNDAI">현대카드</option>
							</select>
						</div>

						<!-- 휴대폰 -->
						<label class="pay-radio-row other-radio">
							<input type="radio"
								   name="paymentMethod"
								   value="MOBILE"
								   form="paymentForm">
							<span class="pay-title">휴대폰</span>
						</label>

						<!-- 무통장입금 -->
						<label class="pay-radio-row other-radio">
							<input type="radio"
								   name="paymentMethod"
								   value="VIRTUAL_ACCOUNT"
								   form="paymentForm">
							<span class="pay-title">무통장입금(가상계좌)</span>
						</label>

					</div>
				</div>
			</div>

			<!-- 배송 / 상품 정보 -->
			<div class="delivery-info">

				<h4>배송 1건 중 1</h4>

				<c:forEach var="item"
						   items="${checkoutItems}"
						   varStatus="status">

					<div class="product-item">
						<div class="product-img"></div>

						<div class="product-info">
							<div class="product-name">
								${item.productName}
							</div>

							<div class="product-option">
								${item.optionName}
							</div>

							<div class="product-price">
								<fmt:formatNumber
									value="${item.price}"
									pattern="#,###"/>원
							</div>

							<div class="product-qty">
								수량 ${item.orderQty}개
							</div>
						</div>
					</div>

				</c:forEach>
			</div>

		</div>

		<!-- 오른쪽 결제 금액 영역 -->
		<div class="right-section">

			<div class="payment-summary">

				<h3>최종 결제 금액</h3>

				<div class="price-row">
					<span>총 상품 가격</span>
					<span>
						<fmt:formatNumber
							value="${checkout.productAmount}"
							pattern="#,###"/>원
					</span>
				</div>

				<div class="price-row">
					<span>즉시할인</span>
					<span class="discount">
						-<fmt:formatNumber
							value="${checkout.instantDiscount}"
							pattern="#,###"/>원
					</span>
				</div>

				<div class="price-row">
					<span>
						쿠폰할인
						<button class="coupon-btn" type="button">변경</button>
					</span>

					<span class="discount">
						-<fmt:formatNumber
							value="${checkout.couponDiscount}"
							pattern="#,###"/>원
					</span>
				</div>

				<div class="price-row">
					<span>배송비</span>
					<span>
						<fmt:formatNumber
							value="${checkout.deliveryFee}"
							pattern="#,###"/>원
					</span>
				</div>

				<div class="price-row">
					<span>쿠팡캐시</span>

					<div class="cash-input">
						<button type="button">전액사용</button>

						<input type="text"
							   name="cashUsed"
							   value="${checkout.cashUsed}"
							   readonly>
						원
					</div>
				</div>

				<div class="divider"></div>

				<div class="total-row">
					<span class="label">총 결제 금액</span>

					<span class="amount">
						<fmt:formatNumber
							value="${checkout.totalPrice}"
							pattern="#,###"/>원
					</span>
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
					* 개별 판매자가 등록한 마켓플레이스(오픈마켓) 상품에 대한 광고,
					상품주문, 배송 및 환불의 의무와 책임은 각 판매자가 부담하고,
					이에 대하여 쿠팡은 통신판매중개자로서 통신판매의 당사자가
					아니므로 일체 책임을 지지 않습니다.
				</div>

				<div class="final-agree">
					위 주문 내용을 확인 하였으며, 회원 본인은 개인정보 이용 및
					제공(해외직구의 경우 국외제공) 및 결제에 동의합니다.
				</div>

				<!-- 결제 FORM -->
				<form id="paymentForm"
					  action="${pageContext.request.contextPath}/order/checkout"
					  method="post"
					  onsubmit="return validatePayment();">

					<input type="hidden"
						   name="checkoutNo"
						   value="${checkoutNo}">

					<input type="hidden"
						   id="selectedAddressNo"
						   name="addressNo"
						   value="${address.addressNo}">

					<button class="btn-pay" type="submit">
						결제하기
					</button>
				</form>

			</div>
		</div>

	</div>
</div>


<!-- 배송지 선택 모달 -->
<div id="addressModalOverlay" class="address-modal-overlay">

	<div class="address-modal" onclick="event.stopPropagation();">

		<div class="address-modal-header">
			<h2>배송지 선택</h2>

			<button type="button"
					class="address-modal-close"
					onclick="closeAddressModal()">
				&times;
			</button>
		</div>

		<div class="address-modal-body">

			<c:forEach var="addr" items="${addressList}">

				<div class="address-card
					${addr.addressNo == address.addressNo
					? 'selected-address-card'
					: ''}">

					<div class="address-card-name">
						${addr.receiverName}
					</div>

					<div class="address-tags">

						<c:if test="${addr.addressDefault}">
							<span class="address-tag default">
								기본배송지
							</span>
						</c:if>

						<span class="address-tag fresh">
							로켓프레시 가능
						</span>

						<span class="address-tag rocket">
							로켓와우 가능
						</span>
					</div>

					<div class="address-card-detail">

						<div class="address-text">
							${addr.address}&nbsp;${addr.detailAddress}
						</div>

						<div>${addr.tel}</div>

						<c:choose>
							<c:when test="${not empty addr.requestMsg}">
								<div class="address-request">
									${addr.requestMsg}
								</div>
							</c:when>

							<c:otherwise>
								<div class="address-request">
									배송 요청사항 없음
								</div>
							</c:otherwise>
						</c:choose>

					</div>

					<div class="address-card-buttons">

						<button type="button"
								class="address-edit-btn">
							수정
						</button>

						<button type="button"
								class="address-select-btn"
								data-address-no="${addr.addressNo}"
								data-receiver-name="${fn:escapeXml(addr.receiverName)}"
								data-address="${fn:escapeXml(addr.address)}"
								data-detail-address="${fn:escapeXml(addr.detailAddress)}"
								data-tel="${fn:escapeXml(addr.tel)}"
								data-request-msg="${fn:escapeXml(addr.requestMsg)}"
								data-default="${addr.addressDefault}"
								onclick="selectAddress(this)">
							선택
						</button>

					</div>
				</div>

			</c:forEach>

			<button type="button"
					class="address-add-btn"
					onclick="openAddAddressModal()">

				<span class="plus-icon">＋</span>
				배송지 추가

			</button>

		</div>
	</div>
</div>


<!-- 배송지 추가 모달 -->
<div id="addAddressModalOverlay" class="address-add-modal-overlay">

	<div class="address-add-modal" onclick="event.stopPropagation();">

		<div class="address-add-header">
			<h2>배송지 추가</h2>

			<button type="button"
					class="address-add-close"
					onclick="closeAddAddressModal()">
				×
			</button>
		</div>

		<form id="addAddressForm"
			  action="${pageContext.request.contextPath}/address/add"
			  method="post">

			<div class="address-add-body">

				<div class="add-address-row">
					<div class="add-address-icon">♙</div>

					<input type="text"
						   name="receiverName"
						   id="newReceiverName"
						   placeholder="받는 사람"
						   autocomplete="off">
				</div>

				<div class="add-address-row postcode-row">
					<div class="add-address-icon">◉</div>

					<button type="button"
							class="postcode-search-btn"
							onclick="findPostcode()">
						우편번호 찾기
					</button>

					<input type="hidden"
						   name="zipcode"
						   id="newZipcode">
				</div>

				<div class="add-address-row address-input-row">
					<div class="add-address-icon">⌂</div>

					<input type="text"
						   name="address"
						   id="newAddress"
						   placeholder="주소"
						   readonly>
				</div>

				<div class="add-address-row">
					<div class="add-address-icon">⌂</div>

					<input type="text"
						   name="detailAddress"
						   id="newDetailAddress"
						   placeholder="상세주소">
				</div>

				<div class="add-address-row phone-row">
					<div class="add-address-icon">▣</div>

					<input type="text"
						   name="tel"
						   id="newTel"
						   placeholder="휴대폰 번호">

					<span class="phone-plus">＋</span>
				</div>

				<button type="button" class="delivery-option-row">
					<div class="delivery-option-icon">▦</div>
					<span>일반배송 정보를 선택해 주세요.</span>
					<strong>〉</strong>
				</button>

				<button type="button" class="delivery-option-row">
					<div class="delivery-option-icon">▦</div>
					<span>새벽배송 정보를 선택해 주세요.</span>
					<strong>〉</strong>
				</button>

				<label class="default-address-check">
					<input type="checkbox"
						   name="addressDefault"
						   value="Y">

					<span class="custom-check"></span>
					기본 배송지로 선택
				</label>

				<button type="submit" class="address-save-btn">
					저장
				</button>

			</div>
		</form>

	</div>
</div>


<script>

function toggleOtherPayment() {

	const paymentList =
		document.getElementById("otherPaymentList");

	const paymentArrow =
		document.getElementById("paymentArrow");

	paymentList.classList.toggle("hidden");

	if (paymentList.classList.contains("hidden")) {
		paymentArrow.innerHTML = "﹀";
	} else {
		paymentArrow.innerHTML = "︿";
	}
}


/* 결제수단 변경 */
document
	.querySelectorAll('input[name="paymentMethod"]')
	.forEach(function(radio) {

		radio.addEventListener("change", function() {
			changePaymentMethod(this.value);
		});

	});


function changePaymentMethod(paymentMethod) {

	const bankSetting =
		document.getElementById("bankSetting");

	const bankCode =
		document.getElementById("bankCode");

	const defaultPayment =
		document.getElementById("defaultPayment");

	const cardSetting =
		document.getElementById("cardSetting");

	const cardCompany =
		document.getElementById("cardCompany");


	if (paymentMethod === "BANK_TRANSFER") {

		bankSetting.classList.remove("hidden");

		bankCode.disabled = false;
		defaultPayment.disabled = false;

	} else {

		bankSetting.classList.add("hidden");

		bankCode.disabled = true;
		defaultPayment.disabled = true;
	}


	if (paymentMethod === "CARD") {

		cardSetting.classList.remove("hidden");
		cardCompany.disabled = false;

	} else {

		cardSetting.classList.add("hidden");
		cardCompany.disabled = true;
	}
}


/* 결제 검증 */
function validatePayment() {

	const addressNo =
		document.getElementById("selectedAddressNo").value;

	const paymentMethod =
		document.querySelector(
			'input[name="paymentMethod"]:checked'
		);

	if (!addressNo || addressNo.trim() === "") {
		alert("배송지를 선택해주세요.");
		return false;
	}

	if (!paymentMethod) {
		alert("결제수단을 선택해주세요.");
		return false;
	}

	if (paymentMethod.value === "BANK_TRANSFER") {

		const bankCode =
			document.getElementById("bankCode").value;

		if (!bankCode || bankCode.trim() === "") {
			alert("은행을 선택해주세요.");
			return false;
		}
	}

	if (paymentMethod.value === "CARD") {

		const cardCompany =
			document.getElementById("cardCompany").value;

		if (!cardCompany || cardCompany.trim() === "") {
			alert("카드사를 선택해주세요.");
			return false;
		}
	}

	const payButton =
		document.querySelector(".btn-pay");

	payButton.disabled = true;
	payButton.textContent = "결제 처리중...";

	return true;
}


/* 배송지 모달 */
function openAddressModal() {

	const modal =
		document.getElementById("addressModalOverlay");

	modal.classList.add("show");
	document.body.classList.add("modal-open");
}


function closeAddressModal() {

	const modal =
		document.getElementById("addressModalOverlay");

	modal.classList.remove("show");
	document.body.classList.remove("modal-open");
}


/* 배송지 선택 */
function selectAddress(button) {

	const addressNo = button.dataset.addressNo;
	const receiverName = button.dataset.receiverName;
	const address = button.dataset.address;
	const detailAddress = button.dataset.detailAddress;
	const tel = button.dataset.tel;
	const requestMsg = button.dataset.requestMsg;
	const isDefault = button.dataset.default === "true";

	document.getElementById(
		"currentReceiverName"
	).textContent = receiverName;

	document.getElementById(
		"currentAddress"
	).textContent = address;

	document.getElementById(
		"currentDetailAddress"
	).textContent = detailAddress;

	document.getElementById(
		"currentTel"
	).textContent = tel;

	document.getElementById(
		"selectedAddressNo"
	).value = addressNo;


	const requestArea =
		document.getElementById(
			"currentRequestMessage"
		);

	if (requestArea) {

		if (
			requestMsg !== null &&
			requestMsg !== undefined &&
			requestMsg.trim() !== ""
		) {

			requestArea.textContent = requestMsg;
			requestArea.style.color = "#333";

		} else {

			requestArea.textContent =
				"배송 요청사항이 없습니다.";

			requestArea.style.color = "#aaa";
		}
	}


	const defaultTag =
		document.getElementById(
			"currentDefaultTag"
		);

	if (defaultTag) {

		if (isDefault) {
			defaultTag.style.display = "inline-block";
		} else {
			defaultTag.style.display = "none";
		}
	}


	document
		.querySelectorAll(".address-card")
		.forEach(function(card) {
			card.classList.remove(
				"selected-address-card"
			);
		});


	button
		.closest(".address-card")
		.classList
		.add("selected-address-card");

	closeAddressModal();
}


/* 배송지 추가 */
function openAddAddressModal() {

	const selectModal =
		document.getElementById(
			"addressModalOverlay"
		);

	selectModal.classList.remove("show");


	const addModal =
		document.getElementById(
			"addAddressModalOverlay"
		);

	addModal.classList.add("show");

	document.body.classList.add("modal-open");
}


function closeAddAddressModal() {

	const addModal =
		document.getElementById(
			"addAddressModalOverlay"
		);

	addModal.classList.remove("show");


	const selectModal =
		document.getElementById(
			"addressModalOverlay"
		);

	selectModal.classList.add("show");
}


function findPostcode() {
	alert("우편번호 검색 API를 연결하면 됩니다.");
}


/* 모달 배경 클릭 */
document
	.getElementById("addressModalOverlay")
	.addEventListener("click", function(event) {

		if (event.target === this) {
			closeAddressModal();
		}

	});


document
	.getElementById("addAddressModalOverlay")
	.addEventListener("click", function(event) {

		if (event.target === this) {
			closeAddAddressModal();
		}

	});


/* ESC 닫기 */
document.addEventListener(
	"keydown",
	function(event) {

		if (event.key === "Escape") {

			const addModal =
				document.getElementById(
					"addAddressModalOverlay"
				);

			if (addModal.classList.contains("show")) {
				closeAddAddressModal();
			} else {
				closeAddressModal();
			}
		}

	}
);


/* 최초 결제수단 상태 */
const initialPaymentMethod =
	document.querySelector(
		'input[name="paymentMethod"]:checked'
	);

if (initialPaymentMethod) {
	changePaymentMethod(
		initialPaymentMethod.value
	);
}

</script>

</body>
</html>