/* =========================================================
   search.js — 검색결과 페이지(index_search.html) 전용 동작

   1. 정렬 탭      랭킹순 / 낮은가격순 / 높은가격순 / 리뷰많은순
   2. 빠른 필터 칩  로켓배송 / 쿠폰적용
   둘 다 서버 없이 이미 그려진 카드들을 JS로 재배치·숨김만 함
   ========================================================= */

(function () {

  const list = document.querySelector('.search-product-list');
  if (!list) return;

  const cards = Array.from(list.children);
  const originalOrder = cards.slice();   // "랭킹순" = 처음 그려진 순서

  const resultCount = document.querySelector('.search-result-count');
  const emptyMsg = document.querySelector('.search-empty');

  const activeFilters = new Set();   // 'rocket', 'coupon' 등 켜진 필터 이름


  /* ---------------------------------------------------------
     정렬
     --------------------------------------------------------- */

  function sortCards(key) {
    let sorted;

    if (key === 'rank') {
      sorted = originalOrder.slice();
    } else if (key === 'price-asc') {
      sorted = cards.slice().sort((a, b) => Number(a.dataset.price) - Number(b.dataset.price));
    } else if (key === 'price-desc') {
      sorted = cards.slice().sort((a, b) => Number(b.dataset.price) - Number(a.dataset.price));
    } else if (key === 'review-desc') {
      sorted = cards.slice().sort((a, b) => Number(b.dataset.review) - Number(a.dataset.review));
    } else {
      sorted = originalOrder.slice();
    }

    sorted.forEach(function (card) { list.appendChild(card); });
  }

  document.querySelectorAll('.sort-tab').forEach(function (tab) {
    tab.addEventListener('click', function () {
      document.querySelectorAll('.sort-tab').forEach(t => t.classList.remove('is-active'));
      tab.classList.add('is-active');
      sortCards(tab.dataset.sort);
    });
  });


  /* ---------------------------------------------------------
     빠른 필터 (여러 개 동시에 켤 수 있음 — AND 조건)
     --------------------------------------------------------- */

  function applyFilters() {
    let visible = 0;

    cards.forEach(function (card) {
      const passRocket = !activeFilters.has('rocket') || card.dataset.rocket === 'true';
      const passCoupon = !activeFilters.has('coupon') || card.dataset.coupon === 'true';
      const show = passRocket && passCoupon;

      card.classList.toggle('is-hidden', !show);
      if (show) visible += 1;
    });

    if (resultCount) {
      resultCount.textContent = '상품 ' + visible.toLocaleString() + '개';
    }

    if (emptyMsg) {
      emptyMsg.classList.toggle('show', visible === 0);
    }
  }

  document.querySelectorAll('.quick-filter[data-filter]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      const key = btn.dataset.filter;

      if (activeFilters.has(key)) {
        activeFilters.delete(key);
        btn.classList.remove('is-active');
      } else {
        activeFilters.add(key);
        btn.classList.add('is-active');
      }

      applyFilters();
    });
  });


  /* ---------------------------------------------------------
     체크박스 필터 — 지금은 진짜 카테고리/브랜드 데이터가 없어서
     선택 상태만 시각적으로 반영. 나중에 데이터가 생기면
     applyFilters() 안에 조건을 추가하면 됨
     --------------------------------------------------------- */

  document.querySelectorAll('.filter-check input').forEach(function (checkbox) {
    checkbox.addEventListener('change', function () {
      checkbox.closest('.filter-check').classList.toggle('is-checked', checkbox.checked);
    });
  });


  /* ---------------------------------------------------------
     필터 초기화
     --------------------------------------------------------- */

  const resetBtn = document.querySelector('.filter-reset');
  if (resetBtn) {
    resetBtn.addEventListener('click', function () {
      document.querySelectorAll('.filter-check input').forEach(function (checkbox) {
        checkbox.checked = false;
        checkbox.closest('.filter-check').classList.remove('is-checked');
      });

      document.querySelectorAll('.quick-filter[data-filter]').forEach(function (btn) {
        btn.classList.remove('is-active');
      });

      activeFilters.clear();
      applyFilters();
    });
  }

  applyFilters();

}());
