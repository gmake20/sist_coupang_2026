document.addEventListener("DOMContentLoaded", function () {

    const btnOrder =
        document.getElementById("btn-order");

    const checkoutForm =
        document.getElementById("checkout-form");

    if (btnOrder && checkoutForm) {

        btnOrder.addEventListener(
            "click",
            function () {

                const checkedItems =
                    document.querySelectorAll(
                        ".item-chk:checked"
                    );

                if (checkedItems.length === 0) {

                    alert(
                        "구매할 상품을 선택해주세요."
                    );

                    return;
                }


                /*
                 * 기존에 JS로 추가했던
                 * optionId / quantity 제거
                 *
                 * CSRF input은 제거하지 않음
                 */
                checkoutForm
                    .querySelectorAll(
                        ".checkout-param"
                    )
                    .forEach(function (input) {

                        input.remove();

                    });


                // 선택한 상품 정보 추가
                checkedItems.forEach(
                    function (checkbox) {

                        const item =
                            checkbox.closest(
                                ".cart-item"
                            );

                        if (!item) {
                            return;
                        }


                        const optionId =
                            item.dataset.optionId;


                        const quantityInput =
                            item.querySelector(
                                ".cart-quantity"
                            );


                        if (!optionId
                                || !quantityInput) {

                            return;
                        }


                        // optionId
                        const optionInput =
                            document.createElement(
                                "input"
                            );

                        optionInput.type =
                            "hidden";

                        optionInput.name =
                            "optionId";

                        optionInput.value =
                            optionId;

                        optionInput.className =
                            "checkout-param";


                        checkoutForm.appendChild(
                            optionInput
                        );


                        // quantity
                        const quantity =
                            document.createElement(
                                "input"
                            );

                        quantity.type =
                            "hidden";

                        quantity.name =
                            "quantity";

                        quantity.value =
                            quantityInput.value;

                        quantity.className =
                            "checkout-param";


                        checkoutForm.appendChild(
                            quantity
                        );
                    }
                );


                /*
                 * 첫 클릭에서 바로 제출
                 */
                checkoutForm.submit();
            }
        );
    }
    const chkAll =
        document.getElementById(
            "chk-all"
        );

    const itemChks =
        document.querySelectorAll(
            ".item-chk"
        );

    const cartItems =
        document.querySelectorAll(
            ".cart-item"
        );


    function getUnitPrice(item) {

        const price =
            Number(
                item.dataset.price
            );

        return Number.isNaN(price)
            ? 0
            : price;
    }


    function getQuantity(item) {

        const input =
            item.querySelector(
                ".cart-quantity"
            );

        if (!input) {
            return 0;
        }

        const quantity =
            parseInt(
                input.value,
                10
            );

        return Number.isNaN(quantity)
            ? 0
            : quantity;
    }


    function calculateTotal() {

        let totalPrice = 0;
        let selectedCount = 0;

        const totalCount =
            itemChks.length;


        cartItems.forEach(
            function (item) {

                const chk =
                    item.querySelector(
                        ".item-chk"
                    );

                if (!chk
                        || !chk.checked) {

                    return;
                }


                const unitPrice =
                    getUnitPrice(item);

                const quantity =
                    getQuantity(item);


                totalPrice +=
                    unitPrice
                    * quantity;

                selectedCount++;
            }
        );


        const deliveryFee =
            (
                isWowMember
                || totalPrice >= 19800
                || totalPrice === 0
            )
                ? 0
                : 3000;


        const grandTotal =
            totalPrice
            + deliveryFee;


        const priceProducts =
            document.getElementById(
                "price-products"
            );

        const priceDelivery =
            document.getElementById(
                "price-delivery"
            );

        const priceTotal =
            document.getElementById(
                "price-total"
            );

        const selectedCountEl =
            document.querySelector(
                ".selected-count"
            );


        if (priceProducts) {

            priceProducts.textContent =
                totalPrice
                    .toLocaleString()
                + "원";
        }


        if (priceDelivery) {

            priceDelivery.textContent =
                deliveryFee === 0
                    ? "+0원"
                    : "+"
                    + deliveryFee
                        .toLocaleString()
                    + "원";
        }


        if (priceTotal) {

            priceTotal.textContent =
                grandTotal
                    .toLocaleString()
                + "원";
        }


        if (selectedCountEl) {

            selectedCountEl.textContent =
                selectedCount;
        }


        if (chkAll) {

            chkAll.checked =
                selectedCount === totalCount
                && totalCount > 0;
        }
    }

    if (chkAll) {

        chkAll.addEventListener(
            "change",
            function () {

                itemChks.forEach(
                    function (chk) {

                        chk.checked =
                            chkAll.checked;
                    }
                );

                calculateTotal();
            }
        );
    }

    itemChks.forEach(
        function (chk) {

            chk.addEventListener(
                "change",
                calculateTotal
            );
        }
    );


    // 초기 계산
    calculateTotal();
});

window.addEventListener(
    "pageshow",
    function (event) {

        const navigation =
            performance
                .getEntriesByType(
                    "navigation"
                )[0];


        if (
            event.persisted
            ||
            (
                navigation
                && navigation.type
                    === "back_forward"
            )
        ) {

            window.location.reload();
        }
    }
);