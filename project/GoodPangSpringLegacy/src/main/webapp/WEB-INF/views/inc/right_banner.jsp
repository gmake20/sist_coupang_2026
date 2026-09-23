<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>


<style>
.order-side-banner {
    width: 102px;
    min-width: 102px;
    flex-shrink: 0;
    margin: 0;
}

.order-promotion-banner {
    width: 102px;
    margin: 0;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 8px;
    list-style: none;
}

.order-promotion-banner li {
    width: 102px;
    height: 150px;
    margin: 0;
    padding: 0;
    line-height: 0;
}

.order-ad-link {
    display: block;
    width: 102px;
    height: 150px;
    overflow: hidden;
    border-radius: 7px;
    text-decoration: none;
    background: transparent;
    transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.order-ad-link img {
    display: block;
    width: 102px;
    height: 150px;
    object-fit: contain;
}

.order-ad-link:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.side-mini-menu {
    margin-top: 10px;
    border: 1px solid #ddd;
}

.side-mini-menu div {
    height: 30px;
    padding: 0 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #34405c;
    color: #fff;
    font-size: 10px;
}

.side-mini-menu div + div {
    border-top: 1px solid #566078;
}

.side-mini-menu strong {
    color: #39a5ff;
    font-size: 11px;
}

.recent-product {
    height: 78px;
    margin-top: 0;
    border: 1px solid #ddd;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 42px;
}
/* 
@media (max-width: 1180px) {
    .order-side-banner {
        display: none !important;
    }
} */
</style>

<aside class="order-side-banner">
    <ul class="order-promotion-banner">
        <li>
            <a href="${pageContext.request.contextPath}/" class="order-ad-link">
                <img src="${pageContext.request.contextPath}/images/ads/beauty.png" alt="오늘의 뷰티 특가">
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/" class="order-ad-link">
                <img src="${pageContext.request.contextPath}/images/ads/fresh.png" alt="신선식품 로켓프레시">
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/" class="order-ad-link">
                <img src="${pageContext.request.contextPath}/images/ads/only.png" alt="GoodPang 단독 상품">
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/" class="order-ad-link">
                <img src="${pageContext.request.contextPath}/images/ads/tech.png" alt="디지털 인기상품">
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/wow/join" class="order-ad-link">
                <img src="${pageContext.request.contextPath}/images/ads/wow.png" alt="와우회원 전용 혜택">
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/" class="order-ad-link">
                <img src="${pageContext.request.contextPath}/images/ads/seller.png" alt="GoodPang 판매자 모집">
            </a>
        </li>
    </ul>

    <div class="side-mini-menu">
        <div>장바구니 <strong>2</strong></div>
        <div>최근본상품 <strong>15</strong></div>
    </div>

    <div class="recent-product">👟</div>
</aside>