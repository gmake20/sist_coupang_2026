$(function () {

    /*
     * 배송 조회
     */
    $("#deliveryBtn").on("click", function () {

		           alert("교환/반품 신청 페이지로 이동합니다.");
		       
    });


    /*
     * 교환 / 반품
     */
    $("#exchangeBtn").on("click", function () {

        const result = confirm(
            "교환 또는 반품 신청을 진행하시겠습니까?"
        );

        if (result) {
            alert("교환/반품 신청 페이지로 이동합니다.");
        }

    });


    /*
     * 리뷰 작성
     */
    $("#reviewBtn").on("click", function () {

        alert(
            "리뷰 작성 페이지로 이동합니다."
        );

    });


    /*
     * 장바구니 담기
     */
    $("#cartBtn").on("click", function () {

        const result = confirm(
            "상품을 장바구니에 담으시겠습니까?"
        );

        if (result) {

            alert(
                "장바구니에 상품이 담겼습니다."
            );

        }

    });


    /*
     * 카드영수증
     */
    $("#cardReceiptBtn").on("click", function () {

        alert(
            "카드영수증을 확인합니다."
        );

    });


    /*
     * 거래명세서
     */
    $("#statementBtn").on("click", function () {

        alert(
            "거래명세서를 확인합니다."
        );

    });


    /*
     * 사이드 메뉴 클릭
     *
     * 실제 페이지 연결 전까지
     * 기본 이동을 막음
     */
    $(".mycoupang-side a").on("click", function (e) {

        const href = $(this).attr("href");

        if (href === "#") {
            e.preventDefault();
        }

    });


    /*
     * 오른쪽 광고 클릭 효과
     */
    $(".banner").on("click", function () {

        $(this).css({
            cursor: "pointer"
        });

    });

});