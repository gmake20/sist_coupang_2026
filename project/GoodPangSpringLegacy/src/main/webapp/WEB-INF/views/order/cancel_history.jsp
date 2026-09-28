<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>


<!-- =========================
     본문
     ========================= -->

<div class="page-wrap">


    <!-- =========================
         본문
         ========================= -->

    <main class="content">

        <h1>취소/반품/교환/환불 내역</h1>


        <!-- 탭 -->

        <div class="tab-menu">

           <a href="${pageContext.request.contextPath}/order/order_list" >주문목록/배송조회</a>

            <a href="${pageContext.request.contextPath}/order/cancel_history"
               class="active">
                취소/반품/교환
            </a>

        </div>


        <!-- 안내 -->

        <div class="info-area">

            <div>
                - 취소/반품/교환 신청한 내역을 확인할 수 있습니다.
            </div>

            <div>
                - 상품에 대한 자세한 문의는
                <span>고객센터</span>를 이용해주세요.
            </div>

        </div>


        <c:choose>
            <c:when test="${not empty cancelList}">
                <c:forEach var="checkList" items="${cancelList}">
                    <div class="order-box">

                        <div class="order-header">

                            <span>취소접수일 : <fmt:formatDate value="${checkList.requestDate}" pattern="yyyy/MM/dd" /></span>

                            <span class="bar">|</span>

                            <span>주문일 : <fmt:formatDate value="${checkList.orderDate}" pattern="yyyy/MM/dd" /></span>

                            <span class="bar">|</span>

                            <span>주문번호 : ${checkList.orderNo}</span>

                        </div>


                        <div class="order-body">


                            <div class="product-info">

                                <div class="product-text">

                                    <p class="product-name">
                                        ${checkList.productName}
                                    </p>

                                    <!-- 옵션 1, 2 동적 출력 구문 (checkList 기준) -->
                                    <c:if test="${not empty checkList.option1Value or not empty checkList.option2Value}">
                                        <p class="product-option">
                                            <span>옵션: </span>
                                            <c:if test="${not empty checkList.option1Value}">
                                                <c:if test="${not empty checkList.option1Type}">${checkList.option1Type}: </c:if>${checkList.option1Value}
                                            </c:if>
                                            <c:if test="${not empty checkList.option1Value and not empty checkList.option2Value}"> / </c:if>
                                            <c:if test="${not empty checkList.option2Value}">
                                                <c:if test="${not empty checkList.option2Type}">${checkList.option2Type}: </c:if>${checkList.option2Value}
                                            </c:if>
                                        </p>
                                    </c:if>

                                </div>

                            </div>


                            <div class="product-price">

                                <span>${checkList.quantity}개</span>

                                <strong><fmt:formatNumber value="${checkList.totalPrice*checkList.quantity}" pattern="#,###" />원</strong>

                            </div>


                            <div class="cancel-status">

                                <strong>${checkList.orderStatus}</strong>

                                <p>
                                    <c:if test="${not empty checkList.expectedCancelDate}">
                                        <fmt:formatDate value="${checkList.expectedCancelDate}" pattern="M/dd(E)" /> 취소 완료 예정
                                    </c:if>
                                </p>

                                <!-- 이동 이벤트 핸들러 추가 -->
                            <button type="button" class="detail-btn" 
							    onclick="location.href='${pageContext.request.contextPath}/order/cancel_detail?orderNo=${checkList.orderNo}';">
							    취소상세
							</button>

                            </div>

                        </div>

                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-box">
                    <p>취소/반품/교환 내역이 존재하지 않습니다.</p>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- =========================
             페이지 (동적 페이징 수정 반영)
             ========================= -->

        <div class="pagination">
            <%-- 이전 페이지 버튼 --%>
            <c:choose>
                <c:when test="${curPage > 1}">
                    <a href="${pageContext.request.contextPath}/order/cancel_history?page=${curPage - 1}">&lt;</a>
                </c:when>
                <c:otherwise>
                    <a href="javascript:void(0);" onclick="alert('첫 번째 페이지입니다.');">&lt;</a>
                </c:otherwise>
            </c:choose>

            <%-- 페이지 번호 동적 출력 --%>
            <c:forEach var="p" begin="1" end="${totalPages}">
                <a href="${pageContext.request.contextPath}/order/cancel_history?page=${p}" 
                   class="${p eq curPage ? 'active' : ''}">${p}</a>
            </c:forEach>

            <%-- 다음 페이지 버튼 --%>
            <c:choose>
                <c:when test="${curPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/order/cancel_history?page=${curPage + 1}">&gt;</a>
                </c:when>
                <c:otherwise>
                    <a href="javascript:void(0);" onclick="alert('마지막 페이지입니다.');">&gt;</a>
                </c:otherwise>
            </c:choose>
        </div>

    </main>
    
