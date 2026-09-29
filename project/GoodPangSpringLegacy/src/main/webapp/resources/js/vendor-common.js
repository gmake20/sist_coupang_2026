/* =========================================================
   vendor-common.js — 판매자센터 공통 동작

   vendor_dashboard.jsp / vendor_orders.jsp / vendor_products.jsp
   vendor_product_write.jsp 4개 파일에 똑같이 복사돼있던 스크립트를
   여기 하나로 모음.

   ⚠ 로드 순서 주의: 각 페이지 자신의 <script> 보다 "먼저" 이 파일을
   불러와야 함. 페이지별 스크립트가 여기서 정의한 setupDropdown() 같은
   함수를 그대로 가져다 쓰기 때문 (예: vendor_dashboard.jsp의 날짜선택 드롭다운)
   ========================================================= */


/* ---------------------------------------------------------
   1. 사이드바 서브메뉴 아코디언
   --------------------------------------------------------- */

document.querySelectorAll(".side-group-toggle").forEach(function (button) {
  button.addEventListener("click", function () {
    const submenu = button.nextElementSibling;
    const isOpen = button.classList.contains("open");

    document.querySelectorAll(".side-group-toggle.open").forEach(function (other) {
      if (other !== button) {
        other.classList.remove("open");
        other.nextElementSibling.classList.remove("open");
      }
    });

    button.classList.toggle("open", !isOpen);
    submenu.classList.toggle("open", !isOpen);
  });
});


/* ---------------------------------------------------------
   2. 사이드바 접기/펼치기 (상단바 햄버거 버튼 + 사이드바 하단 버튼)

   collapseBtn은 페이지에 없을 수도 있어서(→ 이제는 sidebar.jspf 덕분에
   모든 페이지에 다 있지만) 안전하게 존재 여부를 확인하고 붙임
   --------------------------------------------------------- */

const sidebarEl = document.getElementById("sidebar");

const sidebarToggleBtn = document.getElementById("sidebarToggle");
if (sidebarToggleBtn && sidebarEl) {
  sidebarToggleBtn.addEventListener("click", function () {
    sidebarEl.classList.toggle("collapsed");
  });
}

const collapseBtn = document.getElementById("collapseBtn");
if (collapseBtn && sidebarEl) {
  collapseBtn.addEventListener("click", function () {
    sidebarEl.classList.toggle("collapsed");
  });
}


/* ---------------------------------------------------------
   3. 드롭다운 공통 (사용자 메뉴 / 날짜선택 등)

   setupDropdown()은 페이지별 스크립트에서도 그대로 재사용함
   (예: vendor_dashboard.jsp → setupDropdown("datePickerTrigger", "datePickerPanel"))

   열려있는 드롭다운 목록을 배열에 등록해두고, 화면 아무데나 클릭하면
   전부 닫음 — 페이지마다 "바깥 클릭하면 닫기" 코드를 따로 안 써도 됨
   --------------------------------------------------------- */

const openDropdownPanels = [];

function setupDropdown(triggerId, panelId) {
  const trigger = document.getElementById(triggerId);
  const panel = document.getElementById(panelId);
  if (!trigger || !panel) return;

  openDropdownPanels.push(panel);

  trigger.addEventListener("click", function (event) {
    event.stopPropagation();
    panel.classList.toggle("open");
  });
}

setupDropdown("userMenuTrigger", "userMenuPanel");

document.addEventListener("click", function () {
  openDropdownPanels.forEach(function (panel) {
    panel.classList.remove("open");
  });
});

/*
 * 숫자 입력칸(type="number")이 마우스 휠로 바뀌지 않게 막는다.
 * 브라우저 기본 동작으로, 숫자칸에 커서가 있는 채로 페이지를 스크롤하려고 휠을 굴리면
 * 값이 1씩 바뀐다 (예: 상품가격 50000 입력 후 휠 3칸 → 49997로 저장됨).
 * 휠을 굴리는 순간 포커스를 빼서, 값은 그대로 두고 페이지만 스크롤되게 한다.
 * (키보드 ↑/↓로 값을 바꾸는 건 의도한 조작이라 그대로 둔다)
 */
document.addEventListener("wheel", function (event) {
  var target = event.target;
  if (target && target.tagName === "INPUT" && target.type === "number" && document.activeElement === target) {
    target.blur();
  }
}, { passive: true });
