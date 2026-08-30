<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>와우 멤버십 관리</title>
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/order_list.css">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/wow_membership.css">
</head>
<body>
    <jsp:include page="/inc/header.jsp" />

    <div class="wow-page">
        <aside class="wow-sidebar">
            <div class="wow-sidebar-top">MY쿠팡</div>

            <div class="wow-menu-group">
                <h3>MY 쇼핑</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/order/order_list">주문목록/배송조회</a></li>
                    <li><a href="#">취소/반품/교환/환불내역</a></li>
                    <li><a href="${pageContext.request.contextPath}/wow/membership" class="active">와우 멤버십</a></li>
                    <li><a href="#">구독 서비스</a></li>
                    <li><a href="#">로켓프레시 프레시백</a></li>
                    <li><a href="#">영수증 조회/출력</a></li>
                </ul>
            </div>

            <div class="wow-menu-group">
                <h3>MY 혜택</h3>
                <ul>
                    <li><a href="#">쿠폰 · 이용권</a></li>
                    <li><a href="#">쿠팡캐시/기프트카드</a></li>
                </ul>
            </div>

            <div class="wow-menu-group">
                <h3>MY 활동</h3>
                <ul>
                    <li><a href="#">문의하기</a></li>
                    <li><a href="#">문의내역 확인</a></li>
                    <li><a href="#">리뷰관리</a></li>
                    <li><a href="#">찜 리스트</a></li>
                </ul>
            </div>

            <div class="wow-menu-group">
                <h3>MY 정보</h3>
                <ul>
                    <li><a href="#">개인정보확인/수정</a></li>
                    <li><a href="#">결제수단·쿠페이 관리</a></li>
                    <li><a href="#">배송지 관리</a></li>
                    <li><a href="#">회원 탈퇴</a></li>
                </ul>
            </div>
        </aside>

        <main class="wow-content">
            <div class="wow-summary-bar">
                <div class="wow-summary-item"><strong>쿠페이 머니</strong><span>0원</span></div>
                <div class="wow-summary-item"><strong>쿠팡캐시</strong><span>0원</span></div>
            </div>

            <div class="wow-title-row">
                <h2>와우 멤버십 관리</h2>

                <div class="wow-title-actions">
                    <button type="button" class="wow-outline-btn" onclick="location.href='${pageContext.request.contextPath}/payment-method/list'">
                        결제수단 관리
                    </button>

                    <button type="button" id="wowCancelOpenBtn" class="wow-outline-btn">
                        해지하기
                    </button>
                </div>
            </div>

            <section class="wow-card">
                <div class="wow-card-header">
                    <div class="wow-logo">WOW!</div>
                    <div class="wow-name-row">
                        <strong>와우 멤버십</strong>
                        <span class="wow-badge">회원</span>
                    </div>
                    <p class="wow-next-payment">다음 결제 예정일은 <strong>${nextPaymentDate}</strong> 입니다.</p>
                </div>

                <div class="wow-divider"></div>

                <div class="wow-saving-title">와우 멤버십으로 절약한 금액</div>
                <div class="wow-saving-total">총 <strong><fmt:formatNumber value="${savingAmount}" pattern="#,##0" /></strong>원</div>

                <div class="wow-saving-box">
                    <div class="wow-saving-row"><span>무료배송 (건당 3,000원)</span><strong><fmt:formatNumber value="${deliverySaving}" pattern="#,##0" />원</strong></div>
                    <div class="wow-saving-sub">ㄴ 로켓배송 무조건 무료배송</div>
                    <div class="wow-saving-sub">ㄴ 로켓와우 새벽배송/당일배송</div>
                    <div class="wow-saving-sub">ㄴ 로켓프레시 무료배송</div>

                    <div class="wow-saving-row"><span>쿠팡 할인</span><strong><fmt:formatNumber value="${discountSaving}" pattern="#,##0" />원</strong></div>
                    <div class="wow-saving-row"><span>쿠팡이츠 혜택</span><strong><fmt:formatNumber value="${eatsSaving}" pattern="#,##0" />원</strong></div>
                    <div class="wow-saving-row"><span>쿠팡캐시 적립</span><strong><fmt:formatNumber value="${cashSaving}" pattern="#,##0" />원</strong></div>
                    <div class="wow-saving-row"><span>30일 무료반품 (건당 5,000원)</span><strong><fmt:formatNumber value="${returnSaving}" pattern="#,##0" />원</strong></div>
                    <div class="wow-saving-row"><span>로켓직구 무조건 무료배송 (건당 2,500원)</span><strong><fmt:formatNumber value="${globalSaving}" pattern="#,##0" />원</strong></div>
                </div>

                <p class="wow-footnote">
                    가입일(${joinDate})부터 누적 금액 기준, 금액으로 환산 가능한 혜택만 포함
                </p>
            </section>
        </main>
    </div>

    <div id="wowCancelModal" class="wow-cancel-overlay">
        <div class="wow-cancel-modal">
            <button type="button" id="wowCancelCloseBtn" class="wow-cancel-close">&times;</button>
            <div class="wow-cancel-logo">WOW</div>
            <h3>와우 멤버십을 해지하시겠어요?</h3>
            <p>해지하면 무료배송, 무료반품 등 와우 전용 혜택을 더 이상 이용할 수 없습니다.</p>

            <form action="${pageContext.request.contextPath}/wow/cancel" method="post">
                <div class="wow-cancel-buttons">
                    <button type="button" id="wowCancelKeepBtn" class="wow-cancel-keep">멤버십 유지하기</button>
                    <button type="submit" class="wow-cancel-submit">해지하기</button>
                </div>
            </form>
        </div>
    </div>
    
    <c:if test="${param.canceled eq 'Y'}">
    <script>
        alert("와우 멤버십이 해지되었습니다.");
    </script>
</c:if>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const wowCancelOpenBtn = document.getElementById("wowCancelOpenBtn");
            const wowCancelModal = document.getElementById("wowCancelModal");
            const wowCancelCloseBtn = document.getElementById("wowCancelCloseBtn");
            const wowCancelKeepBtn = document.getElementById("wowCancelKeepBtn");

            function closeWowCancelModal() {
                wowCancelModal.style.display = "none";
            }

            if (wowCancelOpenBtn) {
                wowCancelOpenBtn.addEventListener("click", function() {
                    wowCancelModal.style.display = "flex";
                });
            }

            if (wowCancelCloseBtn) {
                wowCancelCloseBtn.addEventListener("click", closeWowCancelModal);
            }

            if (wowCancelKeepBtn) {
                wowCancelKeepBtn.addEventListener("click", closeWowCancelModal);
            }

            if (wowCancelModal) {
                wowCancelModal.addEventListener("click", function(event) {
                    if (event.target === wowCancelModal) {
                        closeWowCancelModal();
                    }
                });
            }
        });
    </script>
</body>
</html>