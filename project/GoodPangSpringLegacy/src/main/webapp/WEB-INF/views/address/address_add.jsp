<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="sec"
    uri="http://www.springframework.org/security/tags" %>


<main class="address-add-page">

    <section class="address-add-box">

        <div class="address-add-header">
            <h1>배송지 추가</h1>
            <p>배송에 필요한 정보를 입력해주세요.</p>
        </div>


        <c:if test="${not empty error}">
            <div class="form-error">
                <c:out value="${error}" />
            </div>
        </c:if>


        <form id="addressForm"
            action="${pageContext.request.contextPath}/address/add"
            method="post"
            class="address-form">

            <sec:csrfInput/>


            <div class="form-row">

                <label for="receiverName">
                    받는 사람
                    <span class="required">*</span>
                </label>

                <input type="text"
                    id="receiverName"
                    name="receiverName"
                    class="form-input"
                    maxlength="30"
                    value="${param.receiverName}"
                    placeholder="받는 사람 이름"
                    required>

            </div>


            <div class="form-row">

                <label>
                    휴대폰 번호
                    <span class="required">*</span>
                </label>

                <div class="phone-row">

                    <input type="text"
                        id="phone1"
                        class="form-input phone-input"
                        maxlength="3"
                        inputmode="numeric"
                        required>

                    <span class="phone-hyphen">-</span>

                    <input type="text"
                        id="phone2"
                        class="form-input phone-input"
                        maxlength="4"
                        inputmode="numeric"
                        placeholder="1234"
                        required>

                    <span class="phone-hyphen">-</span>

                    <input type="text"
                        id="phone3"
                        class="form-input phone-input"
                        maxlength="4"
                        inputmode="numeric"
                        placeholder="5678"
                        required>

                </div>

                <%-- 실제 서버로 전달되는 전화번호 --%>
                <input type="hidden"
                    id="tel"
                    name="tel"
                    value="${param.tel}">

            </div>


            <div class="form-row">

                <label for="zipcode">
                    우편번호
                    <span class="required">*</span>
                </label>

                <div class="zipcode-row">

                    <input type="text"
                        id="zipcode"
                        name="zipcode"
                        class="form-input zipcode-input"
                        maxlength="10"
                        value="${param.zipcode}"
                        placeholder="우편번호"
                        required>

                    <button type="button"
                        class="zipcode-button"
                        id="zipcodeButton">
                        우편번호 찾기
                    </button>

                </div>

            </div>


            <div class="form-row">

                <label for="address">
                    주소
                    <span class="required">*</span>
                </label>

                <input type="text"
                    id="address"
                    name="address"
                    class="form-input"
                    maxlength="200"
                    value="${param.address}"
                    placeholder="기본 주소"
                    required>

            </div>


            <div class="form-row">

                <label for="detailAddress">
                    상세주소
                </label>

                <input type="text"
                    id="detailAddress"
                    name="detailAddress"
                    class="form-input"
                    maxlength="200"
                    value="${param.detailAddress}"
                    placeholder="상세주소를 입력해주세요.">

            </div>


            <div class="form-row">

                <label for="requestMsg">
                    배송 요청사항
                </label>

                <select id="requestMsg"
                    name="requestMsg"
                    class="form-select">

                    <option value="">
                        배송 요청사항을 선택해주세요.
                    </option>

                    <option value="문 앞"
                        ${param.requestMsg eq '문 앞' ? 'selected' : ''}>
                        문 앞
                    </option>

                    <option value="직접 받고 부재 시 문 앞"
                        ${param.requestMsg eq '직접 받고 부재 시 문 앞' ? 'selected' : ''}>
                        직접 받고 부재 시 문 앞
                    </option>

                    <option value="경비실"
                        ${param.requestMsg eq '경비실' ? 'selected' : ''}>
                        경비실
                    </option>

                    <option value="택배함"
                        ${param.requestMsg eq '택배함' ? 'selected' : ''}>
                        택배함
                    </option>

                </select>

            </div>


            <div class="default-row">

                <label class="default-label">

                    <input type="checkbox"
                        name="addressDefault"
                        value="Y"
                        class="default-check"
                        ${param.addressDefault eq 'Y' ? 'checked' : ''}>

                    <span>
                        기본 배송지로 설정
                    </span>

                </label>

            </div>


            <div class="form-actions">

                <a href="${pageContext.request.contextPath}/address/list"
                    class="cancel-button">
                    취소
                </a>

                <button type="submit"
                    class="submit-button">
                    배송지 저장
                </button>

            </div>

        </form>

    </section>

</main>


<script
    src="//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js">
</script>


<script>

document.getElementById("zipcodeButton")
    .addEventListener("click", function () {

        new kakao.Postcode({

            oncomplete: function(data) {

                let addr = "";

                if (data.userSelectedType === "R") {

                    addr = data.roadAddress;

                } else {

                    addr = data.jibunAddress;
                }

                document.getElementById("zipcode").value =
                    data.zonecode;

                document.getElementById("address").value =
                    addr;

                document.getElementById("detailAddress").focus();
            }

        }).open();

    });


const phone1 =
    document.getElementById("phone1");

const phone2 =
    document.getElementById("phone2");

const phone3 =
    document.getElementById("phone3");

const tel =
    document.getElementById("tel");


function onlyNumber(input) {

    input.value =
        input.value.replace(/[^0-9]/g, "");
}


function combinePhoneNumber() {

    const p1 =
        phone1.value.trim();

    const p2 =
        phone2.value.trim();

    const p3 =
        phone3.value.trim();

    if (p1 && p2 && p3) {

        tel.value =
            p1 + "-" + p2 + "-" + p3;

    } else {

        tel.value = "";
    }
}


/* 기존 param.tel이 있다면 전화번호 분리 */
(function initPhoneNumber() {

    const savedTel =
        tel.value || "";

    const numbers =
        savedTel.replace(/[^0-9]/g, "");

    if (numbers.length === 11) {

        phone1.value =
            numbers.substring(0, 3);

        phone2.value =
            numbers.substring(3, 7);

        phone3.value =
            numbers.substring(7, 11);

    } else if (numbers.length === 10) {

        phone1.value =
            numbers.substring(0, 3);

        phone2.value =
            numbers.substring(3, 6);

        phone3.value =
            numbers.substring(6, 10);
    }

    combinePhoneNumber();

})();


phone1.addEventListener("input", function() {

    onlyNumber(this);

    if (this.value.length === 3) {
        phone2.focus();
    }

    combinePhoneNumber();
});


phone2.addEventListener("input", function() {

    onlyNumber(this);

    if (this.value.length === 4) {
        phone3.focus();
    }

    combinePhoneNumber();
});


phone3.addEventListener("input", function() {

    onlyNumber(this);

    combinePhoneNumber();
});


phone2.addEventListener("keydown", function(event) {

    if (
        event.key === "Backspace" &&
        this.value === ""
    ) {
        phone1.focus();
    }
});


phone3.addEventListener("keydown", function(event) {

    if (
        event.key === "Backspace" &&
        this.value === ""
    ) {
        phone2.focus();
    }
});


document.getElementById("addressForm")
    .addEventListener("submit", function(event) {

        combinePhoneNumber();

        if (
            phone1.value.length !== 3 ||
            phone2.value.length < 3 ||
            phone3.value.length !== 4
        ) {

            event.preventDefault();

            alert("휴대폰 번호를 정확히 입력해주세요.");

            phone1.focus();
        }
    });

</script>
