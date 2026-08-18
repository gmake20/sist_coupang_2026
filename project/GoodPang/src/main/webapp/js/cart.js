document.addEventListener("DOMContentLoaded", function () {
    const chkAll = document.getElementById("chk-all");
    const itemChks = document.querySelectorAll(".item-chk");
    const cartItems = document.querySelectorAll(".cart-item");

    // 각 상품의 단가(원) 파싱 (쉼표 및 '원' 제거)
    function getUnitPrice(item) {
        const priceText = item.querySelector(".price").textContent;
        return parseInt(priceText.replace(/[^0-9]/g, ""), 10);
    }

    // 총 금액 및 선택 개수 실시간 계산 함수
    function calculateTotal() {
        let totalPrice = 0;
        let selectedCount = 0;
        const totalCount = itemChks.length;

        cartItems.forEach(item => {
            const chk = item.querySelector(".item-chk");
            if (chk && chk.checked) {
                const unitPrice = getUnitPrice(item);
                const qty = parseInt(item.querySelector(".quantity-control input").value, 10);
                totalPrice += unitPrice * qty;
                selectedCount++;
            }
        });

        // 19,800원 이상 시 배송비 0원 (로켓배송 조건 적용 예시)
        const deliveryFee = (totalPrice >= 19800 || totalPrice === 0) ? 0 : 3000;
        const grandTotal = totalPrice + deliveryFee;

        // DOM 업데이트
        document.getElementById("price-products").textContent = totalPrice.toLocaleString() + "원";
        document.getElementById("price-delivery").textContent = (deliveryFee === 0 ? "+0원" : "+" + deliveryFee.toLocaleString() + "원");
        document.getElementById("price-total").textContent = grandTotal.toLocaleString() + "원";

        // 선택 개수 표시 업데이트
        const selectedCountEl = document.querySelector(".selected-count");
        if (selectedCountEl) selectedCountEl.textContent = selectedCount;

        // 전체 선택 체크박스 상태 동기화
        if (chkAll) {
            chkAll.checked = (selectedCount === totalCount && totalCount > 0);
        }
    }

    // 1. 전체 선택 / 해제
    if (chkAll) {
        chkAll.addEventListener("change", function () {
            itemChks.forEach(chk => {
                chk.checked = chkAll.checked;
            });
            calculateTotal();
        });
    }

    // 2. 개별 체크박스 변경
    itemChks.forEach(chk => {
        chk.addEventListener("change", calculateTotal);
    });

    // 3. 수량 변경 버튼 (+, -)
    const qtyBtns = document.querySelectorAll(".btn-qty");
    qtyBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            const input = this.parentElement.querySelector("input");
            let val = parseInt(input.value, 10);

            if (this.textContent === "+") {
                input.value = val + 1;
            } else if (this.textContent === "-" && val > 1) {
                input.value = val - 1;
            }

            calculateTotal(); // 수량 변경 후 즉시 금액 재계산
        });
    });

    // 4. 상품 삭제 기능
    const deleteBtns = document.querySelectorAll(".btn-delete");
    deleteBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            const item = this.closest(".cart-item");
            if (item) {
                item.remove();
                calculateTotal(); // 삭제 후 즉시 금액 및 개수 재계산
            }
        });
    });

    // 페이지 진입 시 초기 금액 계산 실행
    calculateTotal();
});