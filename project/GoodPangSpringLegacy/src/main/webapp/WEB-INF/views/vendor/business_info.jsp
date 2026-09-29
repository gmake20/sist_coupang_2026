<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="img" uri="/WEB-INF/goodpang-functions.tld"%>
<%-- =========================================================
     business_info.jsp — 판매자 정보관리 (Tiles "vendor.business_info"의 content)
     '입점 대기'/'반려' 판매자는 입점 심사용 정보를 제출하고, '승인' 판매자는 같은 정보를 수정한다.
     값은 세션의 loginSeller에서 채우고, 제출 실패 시 VendorController가 error 메시지를,
     저장 성공 시에는 redirect 후 flash 속성으로 message를 넘겨준다.
     (기존 GoodPang vendor-business-info.jsp의 본문 부분 - 스크립틀릿을 EL로 바꿈)
========================================================= --%>
<c:set var="seller" value="${sessionScope.loginSeller}" />

    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">판매자 정보관리</h1>
          <p class="page-desc">입점 심사를 위해 사업장 정보, 정산계좌, 서류를 등록해주세요.</p>
        </div>

      </div>

      <c:if test="${not empty error}">
        <p class="message error show"><c:out value="${error}" /></p>
      </c:if>
      <c:if test="${not empty message}">
        <p class="message success show"><c:out value="${message}" /></p>
      </c:if>

      <section class="panel business-info-card">

        <%-- multipart 폼은 Spring Security의 CsrfFilter가 본문(_csrf 파라미터)을 읽지 못하므로 토큰을 action URL에 붙인다 --%>
        <form class="form" id="businessInfoForm" novalidate method="post" enctype="multipart/form-data"
          action="${pageContext.request.contextPath}/vendor/business_info.htm?${_csrf.parameterName}=${_csrf.token}">

          <!-- 사업장 주소 -->

          <section class="field-group">

            <h2 class="group-title">사업장 정보</h2>

            <div class="field">
              <label class="label" for="zipcode">우편번호</label>
              <div class="inline-row">
                <input class="input" id="zipcode" name="zipcode" type="text" placeholder="우편번호"
                  value="<c:out value='${seller.zipcode}' />">
                <button class="check-button" id="zipcodeButton" type="button">우편번호 찾기</button>
              </div>
            </div>

            <div class="field">
              <label class="label" for="businessAddress">사업장 주소</label>
              <input class="input" id="businessAddress" name="businessAddress" type="text" placeholder="기본주소"
                value="<c:out value='${seller.businessAddress}' />">
            </div>

            <div class="field">
              <label class="label" for="businessDetailAddress">상세주소</label>
              <input class="input" id="businessDetailAddress" name="businessDetailAddress" type="text" placeholder="상세주소"
                value="<c:out value='${seller.businessDetailAddress}' />">
            </div>

            <div class="field">
              <label class="label" for="mailOrderNo">통신판매업신고번호</label>
              <input class="input" id="mailOrderNo" name="mailOrderNo" type="text" placeholder="예) 2024-서울강남-00000"
                value="<c:out value='${seller.mailOrderNo}' />">
            </div>

            <div class="field">
              <label class="label" for="categoryNo">대표 판매 카테고리</label>
              <select class="input" id="categoryNo" name="categoryNo">
                <option value="">선택해주세요</option>
              </select>
            </div>

          </section>

          <!-- 정산계좌 -->

          <section class="field-group">

            <h2 class="group-title">정산계좌</h2>

            <div class="field">
              <label class="label" for="bankName">은행명</label>
              <input class="input" id="bankName" name="bankName" type="text" placeholder="은행명"
                value="<c:out value='${seller.bankName}' />">
            </div>

            <div class="field">
              <label class="label" for="accountNo">계좌번호</label>
              <input class="input" id="accountNo" name="accountNo" type="text" placeholder="-없이 입력"
                value="<c:out value='${seller.accountNo}' />">
            </div>

            <div class="field">
              <label class="label" for="accountHolder">예금주명</label>
              <input class="input" id="accountHolder" name="accountHolder" type="text" placeholder="예금주명"
                value="<c:out value='${seller.accountHolder}' />">
            </div>

          </section>

          <!-- 서류첨부 -->

          <section class="field-group">

            <h2 class="group-title">서류첨부</h2>

            <p class="agreement-all-desc">
              이미지 파일(jpg, jpeg, png)만 첨부 가능하며, 파일당 최대 5MB까지 첨부할 수 있습니다.
            </p>

            <div class="field">
              <label class="label" for="businessCert">사업자등록증</label>
              <input class="input" id="businessCert" name="businessCert" type="file" accept=".jpg,.jpeg,.png">
              <c:if test="${not empty seller.businessCertUrl}">
                <%-- .message는 show가 있어야 보인다 (없으면 display:none) --%>
                <p class="message show">기존 첨부파일이 있습니다. 새로 첨부하지 않으면 기존 파일이 유지됩니다.</p>
                <a class="doc-preview" href="${img:url(seller.businessCertUrl)}" target="_blank" rel="noopener">
                  <img src="${img:url(seller.businessCertUrl)}" alt="현재 등록된 사업자등록증">
                  <span>현재 등록된 사업자등록증 보기</span>
                </a>
              </c:if>
            </div>

            <div class="field">
              <label class="label" for="mailOrderCert">통신판매신고증</label>
              <input class="input" id="mailOrderCert" name="mailOrderCert" type="file" accept=".jpg,.jpeg,.png">
              <c:if test="${not empty seller.mailOrderCertUrl}">
                <p class="message show">기존 첨부파일이 있습니다. 새로 첨부하지 않으면 기존 파일이 유지됩니다.</p>
                <a class="doc-preview" href="${img:url(seller.mailOrderCertUrl)}" target="_blank" rel="noopener">
                  <img src="${img:url(seller.mailOrderCertUrl)}" alt="현재 등록된 통신판매신고증">
                  <span>현재 등록된 통신판매신고증 보기</span>
                </a>
              </c:if>
            </div>

          </section>

          <button class="submit" id="submitButton" type="submit">
            제출하기
          </button>

        </form>

      </section>

      <!-- 회원탈퇴 -->
      <section class="panel business-info-card">

        <section class="field-group">

          <h2 class="group-title">회원탈퇴</h2>

          <p class="agreement-all-desc">
            탈퇴하면 로그인이 즉시 차단되고, 등록하신 모든 상품이 고객 화면에서 사라집니다. 이 작업은 되돌릴 수 없습니다.
          </p>

          <%-- 탈퇴 처리: VendorController.withdraw() (비밀번호 재확인 → 상태 탈퇴 + 상품 전부 숨김) --%>
          <form method="post" id="withdrawForm"
            action="${pageContext.request.contextPath}/vendor/withdraw.htm"
            onsubmit="return confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.');">

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="field">
              <label class="label" for="withdrawPassword">비밀번호 확인</label>
              <input class="input" id="withdrawPassword" name="password" type="password" placeholder="비밀번호를 입력해주세요">
            </div>

            <button class="submit" type="submit" style="background:#c0392b;">
              탈퇴하기
            </button>

          </form>

        </section>

      </section>

    </main>

  </div>


  <script src="${pageContext.request.contextPath}/resources/js/vendor-common.js"></script>
  <script src="//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
  <script>

    document.getElementById("zipcodeButton").addEventListener("click", function () {

      new kakao.Postcode({

        oncomplete: function (data) {

          const addr = (data.userSelectedType === "R") ? data.roadAddress : data.jibunAddress;

          document.getElementById("zipcode").value = data.zonecode;
          document.getElementById("businessAddress").value = addr;
          document.getElementById("businessDetailAddress").focus();
        }

      }).open();
    });

    (function loadCategories() {

      fetch("${pageContext.request.contextPath}/category/getinfo?ctype=main")
        .then(function (res) { return res.json(); })
        .then(function (grouped) {

          const select = document.getElementById("categoryNo");
          const mainCategories = grouped["1"] || [];
          const selectedCategoryNo = "${seller.categoryNo}";

          mainCategories.forEach(function (category) {

            const option = document.createElement("option");
            option.value = category.categoryNo;
            option.textContent = category.categoryName;

            if (String(category.categoryNo) === selectedCategoryNo) {
              option.selected = true;
            }

            select.appendChild(option);
          });
        })
        .catch(function (err) {
          console.error("카테고리 목록을 불러오지 못했습니다.", err);
        });

    })();

  </script>
