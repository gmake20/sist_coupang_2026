<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>쿠팡 주문/결제</title>

<!-- CSS 분리 -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/coupang_order_payment.css">
</head>
<body>

	<div class="top-header"></div>

	<div class="container">

		<h1 class="logo">
			<a href="index.html" title="GoodPang 홈으로"> <span
				class="brand-goodpang">GoodPang</span>
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
							<strong>배송지</strong> | <span id="currentReceiverName">${address.receiverName}</span>
						</div>

						<button class="btn-outline" type="button"
							onclick="openAddressModal()">배송지 변경</button>
					</div>

					<div class="section-body">

						<div class="address-detail">
							<span id="currentAddress">${address.address}</span><br> <span
								id="currentDetailAddress">${address.detailAddress}</span><br>
							휴대폰 : <span id="currentTel">${address.tel}</span>
						</div>

						<c:if test="${address.addressDefault}">
							<span class="tag" id="currentDefaultTag"> 기본배송지 </span>
						</c:if>

					</div>
				</div>

				<input type="hidden" id="selectedAddressNo" name="addressNo"
					value="${address.addressNo}">

				<!-- 배송 요청사항 -->
				<div class="section-box">
					<div class="section-header">
						<strong>배송 요청사항</strong>
						<button class="btn-outline" type="button">변경</button>
					</div>

					<div class="section-body">
						<span id="currentRequestMessage"> <c:choose>
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
						<label class="pay-radio-row"> <input type="radio"
							name="payMethod" value="coupayMoney"> <span
							class="pay-title"> 쿠페이 머니 : <strong> <fmt:formatNumber
										value="${summary.cashUsed}" pattern="#,###" />원
							</strong>
						</span> <span class="badge-red"> 최대 캐시적립 </span>
						</label>


						<!-- 계좌이체 -->
						<label class="pay-radio-row account-row"> <input
							type="radio" name="payMethod" value="bank" checked> <span
							class="pay-title"> 계좌이체 </span>

						</label>


						<!-- 계좌 선택 -->
						<div class="bank-setting">

							<select class="bank-select" name="bankAccount">

								<option value="SHINHAN">신한은행 / **********4382</option>

								<option value="WOORI">우리은행 / **********7204</option>

								<option value="NH">농협은행 / **********9137</option>

							</select> <label class="default-payment"> <input type="checkbox"
								name="defaultPayment" checked> <span> 기본 결제 수단으로
									사용 </span>

							</label>

						</div>


						<div class="payment-divider"></div>


						<!-- 다른 결제수단 -->
						<button class="other-payment-title" type="button"
							onclick="toggleOtherPayment()">

							<strong>다른 결제 수단</strong> <span id="paymentArrow"> ︿ </span>

						</button>


						<div class="other-payment-list" id="otherPaymentList">


							<!-- 신용/체크카드 -->
							<label class="pay-radio-row other-radio"> <input
								type="radio" name="payMethod" value="card"> <span
								class="pay-title"> 신용/체크카드 </span>

							</label>


							<!-- 법인카드 -->
							<label class="pay-radio-row other-radio"> <input
								type="radio" name="payMethod" value="companyCard"> <span
								class="pay-title"> 법인카드 </span>

							</label>


							<!-- 휴대폰 -->
							<label class="pay-radio-row other-radio"> <input
								type="radio" name="payMethod" value="mobile"> <span
								class="pay-title"> 휴대폰 </span>

							</label>


							<!-- 무통장입금 -->
							<label class="pay-radio-row other-radio"> <input
								type="radio" name="payMethod" value="virtualAccount"> <span
								class="pay-title"> 무통장입금(가상계좌) </span>

							</label>

						</div>

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
									<fmt:formatNumber value="${item.salePrice}" pattern="#,###" />
									원
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
						<span>총 상품 가격</span> <span><fmt:formatNumber
								value="${summary.totalProductPrice}" pattern="#,###" />원</span>
					</div>

					<div class="price-row">
						<span>즉시할인</span> <span class="discount">-<fmt:formatNumber
								value="${summary.instantDiscount}" pattern="#,###" />원
						</span>
					</div>

					<div class="price-row">
						<span> 쿠폰할인
							<button class="coupon-btn" type="button">변경</button>
						</span> <span class="discount">-<fmt:formatNumber
								value="${summary.couponDiscount}" pattern="#,###" />원
						</span>
					</div>

					<div class="price-row">
						<span>배송비</span> <span><fmt:formatNumber
								value="${summary.deliveryFee}" pattern="#,###" />원</span>
					</div>

					<div class="price-row">
						<span>쿠팡캐시</span>
						<div class="cash-input">
							<button type="button">전액사용</button>
							<input type="text" value="${summary.cashUsed}"> 원
						</div>
					</div>
					<div
						style="text-align: right; font-size: 12px; color: #888; margin-top: -6px;">
						잔여 :
						<fmt:formatNumber value="${summary.remainCash}" pattern="#,###" />
						원
					</div>

					<div class="divider"></div>

					<div class="total-row">
						<span class="label">총 결제 금액</span> <span class="amount"><fmt:formatNumber
								value="${summary.finalPrice}" pattern="#,###" />원</span>
					</div>

					<div class="agree-links">
						개인정보 제3자 제공 동의 <a href="#">보기</a>
					</div>
					<div class="agree-links">
						개인정보 수집 및 이용 안내 <a href="#">보기</a>
					</div>

					<div class="notice">* 개별 판매자가 등록한 마켓플레이스(오픈마켓) 상품에 대한 광고,
						상품주문, 배송 및 환불의 의무와 책임은 각 판매자가 부담하고, 이에 대하여 쿠팡은 통신판매중개자로서 통신판매의
						당사자가 아니므로 일체 책임을 지지 않습니다.</div>

					<div class="final-agree">위 주문 내용을 확인 하였으며, 회원 본인은 개인정보 이용 및
						제공(해외직구의 경우 국외제공) 및 결제에 동의합니다.</div>

					<button class="btn-pay" type="button" onclick="dummyPay()">결제하기</button>
				</div>
			</div>
		</div>
	</div>
	<!-- ===================================== -->
	<!-- 배송지 선택 모달 -->
	<!-- ===================================== -->
	<div id="addressModalOverlay" class="address-modal-overlay">

		<div class="address-modal" onclick="event.stopPropagation();">

			<!-- 상단 -->
			<div class="address-modal-header">

				<h2>배송지 선택</h2>

				<button type="button" class="address-modal-close"
					onclick="closeAddressModal()">&times;</button>

			</div>


			<!-- 배송지 목록 -->
			<div class="address-modal-body">

				<c:forEach var="addr" items="${addressList}">

					<div
						class="address-card
                    ${addr.addressNo == address.addressNo ? 'selected-address-card' : ''}">

						<!-- 이름 -->
						<div class="address-card-name">${addr.receiverName}</div>


						<!-- 태그 -->
						<div class="address-tags">

							<c:if test="${addr.addressDefault}">
								<span class="address-tag default"> 기본배송지 </span>
							</c:if>

							<span class="address-tag fresh"> 로켓프레시 가능 </span> <span
								class="address-tag rocket"> 로켓와우 가능 </span>

						</div>


						<!-- 주소 -->
						<div class="address-card-detail">

							<div class="address-text">
    							${addr.address}&nbsp;${addr.detailAddress}
							</div>

							<div>${addr.tel}</div>

							<c:choose>

								<c:when test="${not empty addr.requestMsg}">
									<div class="address-request">${addr.requestMsg}</div>
								</c:when>

								<c:otherwise>
									<div class="address-request">배송 요청사항 없음</div>
								</c:otherwise>

							</c:choose>

						</div>


						<!-- 하단 버튼 -->
						<div class="address-card-buttons">

							<button type="button" class="address-edit-btn">수정</button>


							<button type="button" class="address-select-btn"
								data-address-no="${addr.addressNo}"
								data-receiver-name="${fn:escapeXml(addr.receiverName)}"
								data-address="${fn:escapeXml(addr.address)}"
								data-detail-address="${fn:escapeXml(addr.detailAddress)}"
								data-tel="${fn:escapeXml(addr.tel)}"
								data-request-msg="${fn:escapeXml(addr.requestMsg)}"
								data-default="${addr.addressDefault}"
								onclick="selectAddress(this)">선택</button>

						</div>

					</div>

				</c:forEach>


				<!-- 배송지 추가 -->
				<button type="button" class="address-add-btn" onclick="addAddress()">

					<span class="plus-icon">＋</span> 배송지 추가

				</button>

			</div>

		</div>

	</div>
</body>
<script>
function toggleOtherPayment() {

    const paymentList =
        document.getElementById("otherPaymentList");

    const paymentArrow =
        document.getElementById("paymentArrow");

    paymentList.classList.toggle("hidden");

    // 닫혀있으면 아래 화살표
    if (paymentList.classList.contains("hidden")) {
        paymentArrow.innerHTML = "﹀";
    } else {
        paymentArrow.innerHTML = "︿";
    }
}

    function dummyPay() {
        // 실제 결제 처리 없이 무조건 주문완료 페이지로 이동
        location.href = "${pageContext.request.contextPath}/coupang_order_complete.jsp";
    }
    
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


    /*
     * 배송지 선택
     */
    function selectAddress(button) {

        const addressNo =
            button.dataset.addressNo;

        const receiverName =
            button.dataset.receiverName;

        const address =
            button.dataset.address;

        const detailAddress =
            button.dataset.detailAddress;

        const tel =
            button.dataset.tel;

        const requestMsg =
            button.dataset.requestMsg;

        const isDefault =
            button.dataset.default === "true";


        /*
         * 주문페이지 배송지 변경
         */
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


        /*
         * 실제 주문 때 사용할 ADDRESS_NO
         */
        document.getElementById(
            "selectedAddressNo"
        ).value = addressNo;


        /*
         * 배송 요청사항도 변경
         */
        const requestArea =
            document.getElementById(
                "currentRequestMessage"
            );

        if (requestArea) {

            if (
                requestMsg !== null &&
                requestMsg.trim() !== ""
            ) {

                requestArea.textContent =
                    requestMsg;

                requestArea.style.color =
                    "#333";

            } else {

                requestArea.textContent =
                    "배송 요청사항이 없습니다.";

                requestArea.style.color =
                    "#aaa";
            }
        }


        /*
         * 선택된 카드 파란 테두리
         */
        document
            .querySelectorAll(".address-card")
            .forEach(card => {

                card.classList.remove(
                    "selected-address-card"
                );

            });


        button
            .closest(".address-card")
            .classList.add(
                "selected-address-card"
            );


        /*
         * 모달 닫기
         */
        closeAddressModal();
    }


    /*
     * 배송지 추가
     */
    function addAddress() {

        location.href =
            "${pageContext.request.contextPath}/address/add";
    }


    /*
     * 모달의 어두운 배경 클릭 시 닫기
     */
    document
        .getElementById("addressModalOverlay")
        .addEventListener(
            "click",
            closeAddressModal
        );


    /*
     * ESC로 닫기
     */
    document.addEventListener(
        "keydown",
        function(event) {

            if (event.key === "Escape") {

                closeAddressModal();

            }

        }
    );
</script>
</html>