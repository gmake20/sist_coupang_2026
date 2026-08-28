<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<title>GoodPang | 장바구니</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/cart.css">
</head>

<body>
	<header class="cart-header">
		<div class="header-inner">
			<h1 class="logo">
				<a href="${pageContext.request.contextPath}/" title="GoodPang 홈으로">
					<span class="brand-goodpang">GoodPang</span>
				</a>
			</h1>
			<div class="cart-step">
				<span class="step">01 옵션선택</span> &gt; <span class="step active">02
					장바구니</span> &gt; <span class="step">03 주문/결제</span> &gt; <span
					class="step">04 주문완료</span>
			</div>
		</div>
	</header>
	<main class="cart-container">

		<h2 class="cart-title">
			&lt; 장바구니(<span id="total-count">${cartCount}</span>)
		</h2>
		<div class="cart-content">
			<section class="cart-left">
				<div class="cart-box">
					<div class="box-header">
						<span class="rocket-badge"> 로켓배송 상품 </span> <span class="sub-text">
							무료배송 - 19,800원 이상 주문 가능 </span>
					</div>
					<c:if test="${empty cartItems}">
						<div class="empty-cart">
							<p>장바구니에 담긴 상품이 없습니다.</p>
						</div>
					</c:if>
					<c:forEach var="item" items="${cartItems}">
						<div class="cart-item" data-option-id="${item.optionId}"
							data-price="${item.unitPrice}">
							<input type="checkbox" class="item-chk" value="${item.optionId}"
								checked>
							<div class="item-img-placeholder">이미지</div>
							<div class="item-info">
								<p class="item-title">${item.productName}</p>
								<p class="item-option">
									옵션:
									<c:if test="${not empty item.option1Value}">
										${item.option1Type}: ${item.option1Value}
										</c:if>
									<c:if test="${not empty item.option2Value}">
										/ ${item.option2Type}: ${item.option2Value}
										</c:if>
									<c:if test="${not empty item.option3Value}">
										/ ${item.option3Type}: ${item.option3Value}
										</c:if>
								</p>
								<p class="delivery-date">배송 예정</p>
								<div class="price-wrap">

									<span class="price"> <fmt:formatNumber
											value="${item.unitPrice}" pattern="#,###" />원
									</span>
								</div>
								<form method="post"
									action="${pageContext.request.contextPath}/cart/update"
									class="quantity-control">
									<input type="hidden" name="optionId" value="${item.optionId}">
									<button type="submit" class="btn-qty" name="quantity"
										value="${item.quantity - 1}"
										${item.quantity <= 1 ? 'disabled' : ''}>-</button>
									<input type="text" class="cart-quantity"
										value="${item.quantity}" readonly>
									<button type="submit" class="btn-qty" name="quantity"
										value="${item.quantity + 1}">+</button>
								</form>
							</div>
							<form method="post"
								action="${pageContext.request.contextPath}/cart/delete">

								<input type="hidden" name="optionId" value="${item.optionId}">
								<button type="submit" class="btn-delete">삭제</button>
							</form>
						</div>
					</c:forEach>
				</div>

				<c:if test="${not empty cartItems}">
					<div class="cart-select-actions">
						<label> <input type="checkbox" id="chk-all" checked>
							전체 선택 ( <span class="selected-count"> ${cartCount} </span> / <span
							class="total-count"> ${cartCount} </span> )
						</label>
						
						<button type="button" class="btn-action" id="btn-delete-selected">
							선택삭제</button>
							
					</div>
				</c:if>
			</section>

			<aside class="cart-right">
				<div class="order-summary-box">
					<h3>주문 예상 금액</h3>
					<div class="summary-row">
						<span> 총 상품 가격 </span> <span id="price-products"> <fmt:formatNumber
								value="${totalPrice}" pattern="#,###" />원
						</span>
					</div>
					<div class="summary-row">
						<span> 총 배송비 </span> <span id="price-delivery"> +0원 </span>
					</div>
					<hr>
					<div class="summary-row total">
						<span> 총 결제예상금액 </span> <span id="price-total"> <fmt:formatNumber
								value="${totalPrice}" pattern="#,###" />원
						</span>
					</div>
					<button type="button"
						class="btn-order"
						id="btn-order"
						${empty cartItems ? 'disabled' : ''}>
						구매하기
						</button>
				</div>
			</aside>
		</div>

		<section class="recommend-section">
			<h3>같이 보면 좋은 상품</h3>
			<div class="product-grid">
				<div class="product-card">
					<div class="card-img">상품이미지</div>
					<p class="card-title">자동 우산 UV차단 초경량 양산</p>
					<p class="card-price">13,700원</p>
				</div>
				<div class="product-card">
					<div class="card-img">상품이미지</div>
					<p class="card-title">3단 자동 우양산 겸용 자외선 차단</p>
					<p class="card-price">9,900원</p>
				</div>
				<div class="product-card">
					<div class="card-img">상품이미지</div>
					<p class="card-title">디시엘로 3단 자동 우산</p>
					<p class="card-price">12,800원</p>
				</div>
				<div class="product-card">
					<div class="card-img">상품이미지</div>
					<p class="card-title">초경량 5단 접이식 미니 우산</p>
					<p class="card-price">9,900원</p>
				</div>
			</div>
			<h3>결제하려 했던 상품</h3>
			<div class="product-grid">
				<div class="product-card">
					<div class="card-img">상품이미지</div>
					<p class="card-title">순면 자루형 베개커버 2장</p>
					<p class="card-price">12,800원</p>
				</div>
			</div>
		</section>
	</main>
	
	<script>
	const contextPath = "${pageContext.request.contextPath}";
	
	const checkAll =
	    document.getElementById("chk-all");

	const itemCheckboxes =
	    document.querySelectorAll(".item-chk");

	if (checkAll) {

	    checkAll.addEventListener(
	        "change",
	        function() {

	            itemCheckboxes.forEach(
	                function(item) {

	                    item.checked =
	                        checkAll.checked;
	                }
	            );

	            updateSelectedCount();
	        }
	    );
	}
	
	const deleteSelectedBtn =
	    document.getElementById("btn-delete-selected");

	if (deleteSelectedBtn) {

	    deleteSelectedBtn.addEventListener(
	        "click",
	        function() {

	            const checkedItems =
	                document.querySelectorAll(
	                    ".item-chk:checked"
	                );

	            if (checkedItems.length === 0) {
	                alert("삭제할 상품을 선택해주세요.");
	                return;
	            }

	            if (!confirm(
	                    "선택한 상품을 삭제하시겠습니까?"
	            )) {
	                return;
	            }

	            const form =
	                document.createElement("form");

	            form.method = "post";
	            form.action =
	                contextPath
	                + "/cart/delete-selected";

	            checkedItems.forEach(
	                function(item) {

	                    const input =
	                        document.createElement(
	                            "input"
	                        );

	                    input.type = "hidden";
	                    input.name = "optionId";
	                    input.value = item.value;

	                    form.appendChild(input);
	                }
	            );

	            document.body.appendChild(form);

	            form.submit();
	        }
	    );
	}
</script>


	<script src="${pageContext.request.contextPath}/js/cart.js"></script>

</body>

</html>