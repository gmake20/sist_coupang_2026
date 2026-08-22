<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>GoodPang | 배송지 수정</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/reset.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <!-- address_add.jsp와 동일한 폼 스타일 재사용 -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/address_add.css">
</head>
<body>

<jsp:include page="/inc/header.jsp" />

<main class="address-add-page">

    <section class="address-add-box">

        <div class="address-add-header">
            <h1>배송지 수정</h1>
            <p>배송지 정보를 수정해주세요.</p>
        </div>

        <c:if test="${not empty error}">
            <div class="form-error">
                ${error}
            </div>
        </c:if>

        <form
            action="${pageContext.request.contextPath}/address/edit"
            method="post"
            class="address-form"
        >

            <!-- 어떤 배송지를 수정할지 식별 -->
            <input
                type="hidden"
                name="addressNo"
                value="${address.addressNo}"
            >

            <div class="form-row">
                <label for="receiverName">
                    받는 사람
                    <span class="required">*</span>
                </label>

                <input
                    type="text"
                    id="receiverName"
                    name="receiverName"
                    class="form-input"
                    maxlength="30"
                    value="${address.receiverName}"
                    placeholder="받는 사람 이름"
                    required
                >
            </div>

            <div class="form-row">
                <label for="tel">
                    휴대폰 번호
                    <span class="required">*</span>
                </label>

                <input
                    type="tel"
                    id="tel"
                    name="tel"
                    class="form-input"
                    maxlength="20"
                    value="${address.tel}"
                    placeholder="010-1234-5678"
                    required
                >
            </div>

            <div class="form-row">
                <label for="zipcode">
                    우편번호
                    <span class="required">*</span>
                </label>

                <div class="zipcode-row">
                    <input
                        type="text"
                        id="zipcode"
                        name="zipcode"
                        class="form-input zipcode-input"
                        maxlength="10"
                        value="${address.zipcode}"
                        placeholder="우편번호"
                        required
                    >

                    <button
                        type="button"
                        class="zipcode-button"
                        id="zipcodeButton"
                    >
                        우편번호 찾기
                    </button>
                </div>
            </div>

            <div class="form-row">
                <label for="address">
                    주소
                    <span class="required">*</span>
                </label>

                <input
                    type="text"
                    id="address"
                    name="address"
                    class="form-input"
                    maxlength="200"
                    value="${address.address}"
                    placeholder="기본 주소"
                    required
                >
            </div>

            <div class="form-row">
                <label for="detailAddress">
                    상세주소
                </label>

                <input
                    type="text"
                    id="detailAddress"
                    name="detailAddress"
                    class="form-input"
                    maxlength="200"
                    value="${address.detailAddress}"
                    placeholder="상세주소를 입력해주세요."
                >
            </div>

            <div class="form-row">
                <label for="requestMsg">
                    배송 요청사항
                </label>

                <select
                    id="requestMsg"
                    name="requestMsg"
                    class="form-select"
                >
                    <option value=""
                        ${empty address.requestMsg ? 'selected' : ''}>
                        배송 요청사항을 선택해주세요.
                    </option>

                    <option value="문 앞"
                        ${address.requestMsg eq '문 앞' ? 'selected' : ''}>
                        문 앞
                    </option>

                    <option value="직접 받고 부재 시 문 앞"
                        ${address.requestMsg eq '직접 받고 부재 시 문 앞' ? 'selected' : ''}>
                        직접 받고 부재 시 문 앞
                    </option>

                    <option value="경비실"
                        ${address.requestMsg eq '경비실' ? 'selected' : ''}>
                        경비실
                    </option>

                    <option value="택배함"
                        ${address.requestMsg eq '택배함' ? 'selected' : ''}>
                        택배함
                    </option>
                </select>
            </div>

            <div class="default-row">
                <label class="default-label">
                    <input
                        type="checkbox"
                        name="addressDefault"
                        value="Y"
                        class="default-check"
                        ${address.addressDefault eq 'Y' ? 'checked' : ''}
                    >

                    <span>
                        기본 배송지로 설정
                    </span>
                </label>
            </div>

            <div class="form-actions">
                <a
                    href="${pageContext.request.contextPath}/address/list"
                    class="cancel-button"
                >
                    취소
                </a>

                <button
                    type="submit"
                    class="submit-button"
                >
                    수정 완료
                </button>
            </div>

        </form>

    </section>

</main>

<jsp:include page="/inc/footer.jsp" />

<script>
document.getElementById("zipcodeButton").addEventListener("click", function () {
    alert("우편번호 검색 API를 연결하면 됩니다.");
});
</script>

</body>
</html>