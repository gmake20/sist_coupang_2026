<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--=========================================================topbar.jsp — 판매자센터 공통 상단바 (Tiles "topbar" 속성) 등급 배지는
        request/session 속성 "sellerGrade" 가 있을 때만 표시 (예) model.addAttribute("sellerGrade", "파워셀러" ); // 배지 표시 값을 안 넣으면 배지
        없음 (대부분의 페이지) Tiles의 insertAttribute는 동적 include라 layout.jsp의 스크립틀릿 지역변수가 이 파일에서 안 보임 → 반드시 request 속성 +
        EL(${sellerGrade})로 받아야 함=========================================================--%>
<%-- 판매자 등급: 아직 DB(SELLER)에 등급 컬럼이 없어서 임시로 하드코딩.
     스크립틀릿 변수(<% String sellerGrade = ... %>)는 EL에서 안 보이므로 c:set으로 page 범위 변수를 만든다.
     Controller/세션이 sellerGrade를 넘겨주면 그 값이 우선이고, 없을 때만 "파워셀러"로 채운다. --%>
<c:if test="${empty sellerGrade}">
	<c:set var="sellerGrade" value="파워셀러" />
</c:if>

<!-- 상단바 -->
<header class="topbar">

	<div class="topbar-left">

		<a href="${pageContext.request.contextPath}/" class="brand-seller">
			<span class="brand-coupang">coupang</span> <span
			class="brand-seller-text">seller</span>
		</a>

		<button class="icon-button menu-toggle" id="sidebarToggle"
			type="button" aria-label="메뉴 열기/닫기">
			<svg class="icon">
                  <use href="#ic-menu" />
                </svg>
		</button>

	</div>

	<div class="topbar-right">

		<div class="user-menu" id="userMenu">
			<c:set var="topbarDisplayName"
				value="${empty sessionScope.storeName ? '판매자' : sessionScope.storeName}님" />
			<button class="user-menu-trigger" id="userMenuTrigger" type="button">
				<span class="avatar"> <svg class="icon">
                      <use href="#ic-user" />
                    </svg>
				</span>
				<c:choose>
					<c:when test="${not empty sellerGrade}">
						<span class="user-name-group"> <span class="user-name">
								<c:out value="${topbarDisplayName}" />
						</span> <span class="user-grade"> <c:out value="${sellerGrade}" />
						</span>
						</span>
					</c:when>
					<c:otherwise>
						<span class="user-name"> <c:out
								value="${topbarDisplayName}" />
						</span>
					</c:otherwise>
				</c:choose>
				<svg class="icon chevron">
                    <use href="#ic-chevron-down" />
                  </svg>
			</button>

			<div class="user-menu-panel" id="userMenuPanel">
				<a
					href="${pageContext.request.contextPath}/vendor/business_info.htm">판매자
					정보 관리</a> <a
					href="${pageContext.request.contextPath}/vendor/logout.htm">로그아웃</a>
			</div>

		</div>

	</div>

</header>