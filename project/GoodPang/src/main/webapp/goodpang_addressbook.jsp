<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>GoodPang | 배송지 관리</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/goodpang_addressbook.css">     
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body>

<jsp:include page="/inc/header.jsp" />

<main class="addressbook-page">

    <aside class="left-menu">
        <div class="mypang-title">MY굿팡</div>

        <div class="menu-section">
            <h3>MY 쇼핑</h3>
            <a href="#">주문목록/배송조회</a>
            <a href="#">취소/반품/교환/환불내역</a>
            <a href="#">와우 멤버십</a>
            <a href="#">구독 서비스 <em>N</em></a>
            <a href="#">로켓프레시 프레시백 <em>N</em></a>
            <a href="#">영수증 조회/출력</a>
        </div>

        <div class="menu-section">
            <h3>MY 혜택</h3>
            <a href="#">쿠폰 · 이용권</a>
            <a href="#">굿팡캐시/기프트카드</a>
        </div>

        <div class="menu-section">
            <h3>MY 활동</h3>
            <a href="#">문의하기</a>
            <a href="#">문의내역 확인</a>
            <a href="#">리뷰관리</a>
            <a href="#">찜 리스트</a>
        </div>

        <div class="menu-section">
            <h3>MY 정보</h3>
            <a href="#">개인정보확인/수정</a>
            <a href="#">결제수단·굿페이 관리</a>

            <a class="active"
               href="${pageContext.request.contextPath}/address/list">
                배송지 관리
            </a>

            <a href="#">회원 탈퇴</a>
        </div>
    </aside>

    <section class="content-area">

        <div class="benefit-bar">
            <div class="benefit-item">
                <strong>굿페이 머니</strong>
                <span>0 <small>원</small></span>
            </div>

            <div class="benefit-item">
                <strong>굿팡캐시</strong>
                <span>0 <small>원</small></span>
            </div>
        </div>

        <div class="address-panel">

            <div class="panel-header">
                <h2>주소록·배송지 관리</h2>

                <button type="button" class="guide-btn">
                    가려진 정보 보기
                </button>
            </div>

            <c:choose>

                <c:when test="${not empty addressList}">

                    <c:forEach var="address" items="${addressList}">

                        <article class="address-card">

                            <div class="name-line">

                                <strong>${address.receiverName}</strong>

                                <c:if test="${address.addressDefault eq 'Y'}">
                                    <span class="badge default">
                                        기본배송지
                                    </span>
                                </c:if>

                                <span class="badge green">
                                    로켓프레시 가능
                                </span>

                                <span class="badge blue">
                                    로켓와우 가능
                                </span>

                            </div>

                            <p class="road">
                                ${address.address}

                                <c:if test="${not empty address.detailAddress}">
                                    ${address.detailAddress}
                                </c:if>
                            </p>

                            <p>${address.tel}</p>

                            <p>
                                <c:choose>
                                    <c:when test="${not empty address.requestMsg}">
                                        ${address.requestMsg}
                                    </c:when>
                                    <c:otherwise>
                                        문 앞
                                    </c:otherwise>
                                </c:choose>
                            </p>

                            <form method="get"
                                  action="${pageContext.request.contextPath}/address/edit">

                                <input type="hidden"
                                       name="addressNo"
                                       value="${address.addressNo}">

                                <button class="edit-btn" type="submit">
                                    수정
                                </button>
                            </form>

                        </article>

                    </c:forEach>

                </c:when>

                <c:otherwise>
                    <div class="empty-address">
                        등록된 배송지가 없습니다.
                    </div>
                </c:otherwise>

            </c:choose>

        </div>

        <a href="${pageContext.request.contextPath}/address/add"
           class="add-address">
            <span class="plus">＋</span>
            배송지 추가
        </a>

    </section>

    <aside class="right-banner">
        <div class="only-card">
            <strong>GoodPang only</strong>
            <div class="ad-box">오늘의 특가</div>
            <div class="ad-box sky">무료배송</div>
            <div class="ad-box yellow">알림 혜택</div>
            <div class="ad-box orange">판매자 가입</div>
        </div>
    </aside>

</main>

<jsp:include page="/inc/footer.jsp" />

</body>
</html>