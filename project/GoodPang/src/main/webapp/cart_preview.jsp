
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"
    uri="jakarta.tags.core"%>

<i class="arrow"></i>

<c:choose>

    <c:when test="${not empty sessionScope.cartPreviewItems}">

        <ul class="cart-preview-list">

            <c:forEach
                var="item"
                items="${sessionScope.cartPreviewItems}">

                <li class="cart-preview-item">

                    <a href="${pageContext.request.contextPath}/product?productNo=${item.productNo}">

                        <div class="cart-preview-image">

                            <c:choose>

                                <c:when test="${not empty item.imageUrl}">

                                    <img
                                        src="${pageContext.request.contextPath}/${item.imageUrl}"
                                        alt="${item.productName}">

                                </c:when>

                                <c:otherwise>

                                    <span>이미지</span>

                                </c:otherwise>

                            </c:choose>

                        </div>

                        <div class="cart-preview-info">

                            <p class="cart-preview-name">
                                ${item.productName}
                            </p>

                            <p class="cart-preview-quantity">
                                수량 ${item.quantity}개
                            </p>

                        </div>

                    </a>

                </li>

            </c:forEach>

        </ul>

    </c:when>

    <c:otherwise>

        <div class="cart-preview-empty">
            장바구니에 담은 상품이 없습니다.
        </div>

    </c:otherwise>

</c:choose>

<a
    href="${pageContext.request.contextPath}/cart"
    class="cart-btn">

    <span>장바구니 전체보기</span>

</a>