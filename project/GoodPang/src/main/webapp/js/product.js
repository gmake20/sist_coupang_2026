/* ============================================================
   product.js — 상품 상세 페이지 전용 JS

   지금 들어있는 것 2개뿐:
     1. 수량 + / - 버튼
     2. 썸네일을 누르면 큰 이미지가 바뀌는 것

     header.js 는 헤더/맨위로 버튼용이라 이 페이지에도 그대로 필요함.
   ============================================================ */


/* ── 1. 수량 + / - (+ 가격 연동) ──────────────────
   최소 1개, 최대 10개로 제한.
   1일 때는 - 버튼을 disabled 로 막음 (CSS 가 회색으로 흐리게 보여줌)

   ★ 2026-08-21 추가 — 수량을 바꾸면 위 가격(.total-price)도 같이 바뀌게 함.
     원래 이름이 "total-price" 인데 실제로는 "단가"만 찍혀있어서 이름값을 못 했음.
     원본 쿠팡도 수량을 바꾸면 이 숫자가 "단가 × 수량" 으로 바뀜.

     단가는 .total-price 의 data-unit-price 에서 읽음 (예: "19900").
     화면 글자(예: "19,900원")를 다시 파싱하면 두 번째 클릭부터
     "39,800원" 을 단가로 잘못 읽는 사고가 나서, 원래 값을 data 속성에 따로 저장해둠.
     ▶JSP 로 가면: <strong class="total-price" data-unit-price="${p.price}">${p.priceText}</strong> */
function setupQuantity() {
  const box = document.querySelector('.product-quantity');
  if (!box) return;                     // 이 페이지에 수량박스가 없으면 아무것도 안 함

  const input = box.querySelector('.qty-input');
  const minus = box.querySelector('.qty-minus');
  const plus  = box.querySelector('.qty-plus');
  const priceEl = document.querySelector('.total-price');
  const unitPrice = priceEl ? Number(priceEl.dataset.unitPrice) || 0 : 0;

  //여기 재고수량에 맞게 수정해야함
  const MIN = 1;
  const MAX = 20;

  /* 화면에 숫자를 다시 그리고, 버튼을 켤지 끌지 정하는 함수.
     "값을 바꾸는 곳"과 "화면을 고치는 곳"을 한 군데로 모아두면
     나중에 고칠 때 여기만 보면 됨 */
  function render(n) {
    input.value = n;
    minus.disabled = (n <= MIN);
    plus.disabled  = (n >= MAX);

    /* 품절이면 가격을 안 건드림 — CSS 가 이미 회색으로 처리하고 있고,
       0원으로 다시 그리면 "품절인데 원가는 남아있는" 원본 화면과 달라짐 */
    if (priceEl && !document.body.classList.contains('is-soldout')) {
      priceEl.textContent = (unitPrice * n).toLocaleString('ko-KR') + '원';
    }
  }

  minus.addEventListener('click', function () {
    const n = Number(input.value) - 1;
    if (n >= MIN) render(n);
  });

  plus.addEventListener('click', function () {
    const n = Number(input.value) + 1;
    if (n <= MAX) render(n);
  });

  render(Number(input.value) || MIN);    // 페이지가 열릴 때 버튼 상태를 한 번 맞춰둠
}


/* ── 2. 썸네일 → 큰 이미지 ───────────────────────
   썸네일을 누르면 ① 파란 테두리가 그쪽으로 옮겨가고 ② 큰 이미지가 그 사진으로 바뀜.
   사진은 images/product-detail/photo-1~3.jpg (2026-08-20 부터 실제 사진)
   ※ images/products/ 는 메인페이지 상품카드 전용이라 여기서 쓰지 않음 */
function setupThumbs() {
  const list = document.querySelector('.product-image__thumbs');
  if (!list) return;

  const items = list.querySelectorAll('li');

  items.forEach(function (li) {
    li.addEventListener('click', function (e) {
      e.preventDefault();               // <a href="#"> 때문에 맨 위로 튀는 것 방지

      /* 파란 테두리(.is-on)를 전부 떼고, 누른 것에만 다시 붙임.
         "하나만 켜기"는 전부 끄고 → 하나 켜기 순서가 제일 간단함 */
      items.forEach(function (other) {
        other.classList.remove('is-on');
      });
      li.classList.add('is-on');

      /* 누른 썸네일의 사진을 큰 이미지 자리에 그대로 넣음.
         썸네일(1000x1000)과 큰 이미지가 같은 파일이라 새로 받아올 게 없어 즉시 바뀜 */
      const main = document.querySelector('.product-image__main img');
      const picked = li.querySelector('img');
      if (main && picked) main.src = picked.src;
    });
  });
}


/* ── 3. 광고 캐러셀 "다음" 버튼 ──────────────────
   "함께 비교하면 좋을 상품"은 카드가 가로로 쭉 이어져 있고, 보이는 상자(.sdp-ads__body)가
   넘치는 부분을 잘라내고 있음. 버튼을 누르면 안쪽 목록(ul)을 왼쪽으로 밀어서 다음 카드를 보여줌.

   ★ scrollLeft 가 아니라 transform:translateX 를 쓴 이유
     스크롤막대를 만들지 않고 움직이려는 것. CSS 에 transition 을 걸어놔서 부드럽게 밀림. */
function setupAdSlider() {
  const body = document.querySelector('.sdp-ads__body');
  if (!body) return;

  const list = body.querySelector('.sdp-ads__list');
  const next = body.querySelector('.sdp-ads__next');
  const item = list.querySelector('.ad-item');
  if (!list || !next || !item) return;

  /* 카드 하나가 차지하는 가로 길이 = 카드 폭 + 오른쪽 여백.
     CSS 에 140/16 이라고 적혀 있지만 여기서 다시 계산함 —
     나중에 CSS 값을 바꿔도 JS 를 안 고쳐도 되게 하려고 */
  const style = getComputedStyle(item);
  const step = item.getBoundingClientRect().width + parseFloat(style.marginRight);

  let moved = 0;                       // 지금까지 밀어낸 거리(px)

  function render() {
    list.style.transform = 'translateX(' + (-moved) + 'px)';
    /* 더 밀 데가 없으면 버튼을 끔 */
    const max = list.scrollWidth - body.clientWidth;
    next.disabled = moved >= max - 1;
  }

  next.addEventListener('click', function () {
    const max = list.scrollWidth - body.clientWidth;
    /* 한 번에 "보이는 칸 수"만큼 밀되, 끝을 넘지 않게 */
    const page = Math.floor(body.clientWidth / step) * step;
    moved = Math.min(moved + page, max);
    render();
  });

  render();                            // 처음에 버튼 상태를 맞춰둠
}


/* ── 4. 색상 고르기 ─────────────────────────────
   색상 칩을 누르면 ① 파란 테두리가 그쪽으로 옮겨가고
   ② 위 라벨("색상: 화이트")이 바뀌고
   ③ (2026-08-24 추가) 큰 사진도 그 색상으로 바뀜.

   ★ 왜 "화이트만" 제대로 된 사진 세트인가:
     처음에 받아둔 사진이 photo-1~3(1000x1000, 화이트 한 벌을 여러 각도로 찍은 것) 뿐이고
     네이비·블랙은 옵션칩 사진(option-1/2, 200x200, 색상당 1장)밖에 없음.
     그래서 네이비/블랙을 고르면:
       - 큰 사진과 첫 썸네일을 그 칩 사진(option-N.jpg)으로 바꾸고
       - 나머지 썸네일 2장은 숨김 (화이트 각도별 사진이라 색이 안 맞음)
     화이트로 돌아오면 원래 photo-1~3 세 장으로 복귀.

   ★ 썸네일 "줄" 자체는 절대 숨기지 말 것 —
     처음엔 통째로 display:none 으로 했는데, 그 70px 자리가 사라지면서
     큰 사진이 600 → 680 으로 커져 화면이 덜컹 움직였음(실측). 칸은 남기고 안쪽만 비우는 게 맞음.

   ▶JSP: 옵션(itemId)마다 사진 배열이 DB에 따로 있으면 이 분기 자체가 필요없어짐 —
     ${item.images} 를 그대로 큰사진+썸네일에 채우면 됨. */
function setupColorChips() {
  const list = document.querySelector('.option-chips');
  if (!list) return;

  const chips    = list.querySelectorAll('li');
  const label    = document.querySelector('.option-value');
  const mainImg  = document.querySelector('.product-image__main img');
  const thumbBox = document.querySelector('.product-image__thumbs');
  const thumbs   = thumbBox ? Array.from(thumbBox.querySelectorAll('li')) : [];

  /* 원래 썸네일 3장의 주소를 미리 적어둠 — 화이트로 돌아올 때 되돌리려면 필요함.
     (한 번 src 를 바꿔버리면 원래 값이 사라지므로 시작할 때 챙겨두는 것) */
  const whiteSrcs = thumbs.map(function (li) {
    const img = li.querySelector('img');
    return img ? img.src : '';
  });

  chips.forEach(function (li) {
    li.addEventListener('click', function (e) {
      e.preventDefault();

      chips.forEach(function (other) { other.classList.remove('is-on'); });
      li.classList.add('is-on');

      const color   = li.dataset.color;
      const chipImg = li.querySelector('img');
      if (label && color) label.textContent = color;

      /* 장바구니/바로구매 폼의 hidden input 에도 같이 반영 (2026-08-24).
         아래 mainImg/chipImg 체크보다 위에 두는 이유: 그 체크에 걸려 함수가
         일찍 끝나도 색상 값은 항상 갱신되게 하려는 것 */
      const colorInput = document.getElementById('selectedColor');
      if (colorInput && color) colorInput.value = color;

      if (!mainImg || !chipImg || !thumbs.length) return;

      const isWhite = (color === '화이트');

      thumbs.forEach(function (t, i) {
        const img = t.querySelector('img');
        if (i === 0) {
          if (img) img.src = isWhite ? whiteSrcs[0] : chipImg.src;
        } else {
          /* 사진이 1장뿐인 색상에서는 2·3번째 썸네일을 감춤 */
          t.classList.toggle('is-hidden', !isWhite);
          if (img && isWhite) img.src = whiteSrcs[i];
        }
        t.classList.toggle('is-on', i === 0);
      });

      mainImg.src = thumbs[0].querySelector('img').src;
    });
  });
}


/* ── 5. 필수 표기 정보 더보기 ─────────────────────
   표에 .is-open 을 붙였다 뗐다 하면 숨겨둔 줄(.is-folded)이 나타나고 사라짐.
   aria-expanded 는 화면낭독기에게 "지금 펼쳐져 있다/접혀 있다"를 알려주는 표시 */
function setupItemBriefMore() {
  const btn   = document.querySelector('.item-brief__more');
  const table = document.querySelector('.item-brief__table');
  if (!btn || !table) return;

  btn.addEventListener('click', function () {
    const open = table.classList.toggle('is-open');
    btn.classList.toggle('is-open', open);
    btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    btn.firstChild.nodeValue = open ? '필수 표기 정보 접기' : '필수 표기 정보 더보기';
  });
}


/* ── 5.5 리뷰 설문 "자세히 보기" ────────────────
   2026-08-24 추가. 원본을 Playwright 로 직접 열어서 확인한 동작:
     모달이 아니라 **왼쪽 요약 칸 안에서 아래로 펼쳐지고**, 버튼 글자가 "접기" 로 바뀜.
   위 setupItemBriefMore 와 완전히 같은 방식이라 구조를 일부러 똑같이 맞춰둠
   (나중에 하나를 고치면 다른 것도 같이 보게 하려고). */
function setupSurveyMore() {
  const btn    = document.querySelector('.survey-more');
  const detail = document.querySelector('.survey-detail');
  if (!btn || !detail) return;

  btn.addEventListener('click', function () {
    const open = detail.classList.toggle('is-open');
    btn.classList.toggle('is-open', open);
    btn.setAttribute('aria-expanded', open ? 'true' : 'false');
    btn.textContent = open ? '접기' : '자세히 보기';
  });
}


/* ── 6. 탭 — 지금 보고 있는 구간을 자동으로 표시 ──────
   원본은 스크롤을 내리면 탭이 화면 위에 붙어 있고(CSS sticky),
   지금 보는 구간에 맞춰 활성 탭이 저절로 바뀜. 그걸 흉내낸 것.

   ★ 원리: 각 탭이 가리키는 섹션(#detail, #reviews …)의 위치를 재서,
     화면 위쪽 기준선을 이미 지나간 섹션 중 **제일 마지막 것**을 지금 구간으로 봄. */
function setupTabSpy() {
  const tabs = [].slice.call(document.querySelectorAll('.detail-tabs a'));
  if (!tabs.length) return;

  /* 각 탭과 그 탭이 가리키는 섹션을 짝지어 둠 */
  const pairs = tabs.map(function (a) {
    return { tab: a, target: document.querySelector(a.getAttribute('href')) };
  }).filter(function (p) { return p.target; });

  /* 탭을 누르면 그 섹션으로 부드럽게 이동.
     탭이 화면 위에 붙어 있으므로 탭 높이(49)만큼 위로 더 올려야 제목이 안 가림 */
  pairs.forEach(function (p) {
    p.tab.addEventListener('click', function (e) {
      e.preventDefault();
      const y = window.scrollY + p.target.getBoundingClientRect().top - 49;
      window.scrollTo({ top: y, behavior: 'smooth' });
    });
  });

  function update() {
    const line = 60;              // 화면 위에서 60px 지점을 기준선으로 봄
    let current = pairs[0];
    pairs.forEach(function (p) {
      if (p.target.getBoundingClientRect().top <= line) current = p;
    });
    pairs.forEach(function (p) {
      p.tab.classList.toggle('is-on', p === current);
    });
  }

  /* 스크롤할 때마다 계산하면 너무 자주 도니까
     "다음 화면 그리기 직전에 한 번만" 하도록 묶음 (requestAnimationFrame) */
  let waiting = false;
  window.addEventListener('scroll', function () {
    if (waiting) return;
    waiting = true;
    requestAnimationFrame(function () { update(); waiting = false; });
  });

  update();
}


/* ── 6.3 리뷰 정렬 / 검색 / 별점 필터 ─────────────
   2026-08-21 추가. 원본을 실제로 열어서 확인함(vp_04_review.jpeg):
     정렬은 "베스트순·최신순" 둘뿐 — 별점순은 없음.
     옆의 "모든 별점" select 는 정렬이 아니라 "필터"임 (고른 별점만 남기고 나머진 숨김).

   ▶JSP 로 가면: 정렬·필터·검색 전부 서버에 물어보는 걸로 바뀜
     (예: ?sort=best&rating=5&q=포장 → SQL 의 ORDER BY / WHERE).
     지금은 서버가 없어서 이미 화면에 있는 카드 3개를 JS 로 줄 세우고 숨기는 걸로 흉내냄. */
function setupReviewTools() {
  const tools = document.querySelector('.review-tools');
  if (!tools) return;

  const list      = document.querySelector('.review-list');
  const sortLinks = tools.querySelectorAll('.review-sort a');
  const searchBox = tools.querySelector('.review-search');
  const ratingSel = tools.querySelector('.review-filter');
  const emptyMsg  = document.querySelector('.review-empty');
  const pager     = document.querySelector('.review-pagination');
  const pageNums  = pager ? pager.querySelector('.page-numbers') : null;
  const prevBtn   = pager ? pager.querySelector('.page-prev') : null;
  const nextBtn   = pager ? pager.querySelector('.page-next') : null;

  function items() {
    return Array.from(list.querySelectorAll('.review-item'));
  }

  /* 별점 — 2026-08-27: 원본과 별 아이콘을 맞추면서 별을 글자(★★★★☆)가 아니라
     스프라이트 이미지(.star-rating em 의 width%)로 그리게 바꿈 — 그러면 화면엔 셀 수 있는
     글자가 없어져서, article 태그에 심어둔 data-rating(product.jsp 에서 ${r.rating} 그대로 찍음)
     을 읽음. "두 군데 값이 어긋날 수 있다"던 예전 우려는 이제 해당 없음 — 화면(width%)과
     data-rating 이 둘 다 서버에서 같은 ${r.rating} 값 하나로 같이 찍히기 때문 */
  function ratingOf(item) {
    return Number(item.dataset.rating) || 0;
  }


  /* 정렬 — 카드 순서를 바꿔서 다시 꽂아넣음.
     "베스트순" 기준은 원본이 비공개라 지금은 data-helpful(더미 "도움돼요" 수)로 대신함 */
  function sortBy(key) {
    const sorted = items().sort(function (a, b) {
      if (key === 'latest') {
        return b.querySelector('.date').textContent.localeCompare(a.querySelector('.date').textContent);
      }
      return (Number(b.dataset.helpful) || 0) - (Number(a.dataset.helpful) || 0);
    });
    sorted.forEach(function (el) { list.insertBefore(el, emptyMsg); });
  }

  /* ★ 정렬·검색·별점필터·페이지 이동이 전부 한 함수(render)를 거치게 만든 이유:
       따로 만들면 "2페이지를 보다가 검색어를 치면 2페이지가 그대로 남아 아무것도 안 보이는"
       식으로 서로 어긋남. 조건이 하나라도 바뀌면 무조건 처음부터 다시 계산하는 게 안전함.
     순서: ① 필터로 남길 카드를 고른다 → ② 그중 지금 페이지 몫만 보여준다 */
  const PAGE_SIZE = 3;    // 한 페이지에 리뷰 3개 (더미가 5개라 2페이지가 됨)
  let page = 1;

  /* ★ 2026-08-26 수정: DB 붙이면서 .review-headline(제목)이 마크업에서 아예 빠짐
     (product.jsp 리뷰 카드엔 .review-text 만 있음) — 원래 코드가 그걸 그대로 찾다가
     null.textContent 로 터졌음. 검색은 이제 review-text 하나만 봄.
     혹시 나중에 헤드라인이 다시 생기면 optional chaining(?.)으로 안전하게 더할 것 */
  function passesFilter(item) {
    const q = searchBox.value.trim().toLowerCase();
    const rating = ratingSel.value;   // '' = 모든 별점
	const textEl = item.querySelector('.review-text');
	const text = (textEl ? textEl.textContent : '').toLowerCase();
	    return (!q || text.includes(q)) && (!rating || ratingOf(item) === Number(rating));
  }

  function render() {
    const kept = items().filter(passesFilter);
    const totalPages = Math.max(1, Math.ceil(kept.length / PAGE_SIZE));
    if (page > totalPages) page = totalPages;      // 필터로 개수가 줄면 페이지도 당겨줌

    const start = (page - 1) * PAGE_SIZE;
    items().forEach(function (item) {
      const idx = kept.indexOf(item);
      const onThisPage = idx >= start && idx < start + PAGE_SIZE;
      item.classList.toggle('is-hidden', !onThisPage);
    });

	/* product.jsp 는 리뷰가 있으면 <c:otherwise> 쪽(.review-empty)을 아예 안 찍음 —
	       그래서 리뷰가 있을 땐 emptyMsg 가 null 일 수 있음. null-safe 하게 처리 */
	if (emptyMsg) emptyMsg.style.display = kept.length === 0 ? 'block' : 'none';
    renderPager(totalPages);
  }

  /* 페이지 번호 버튼을 매번 새로 그림.
     미리 HTML 에 박아두지 않는 이유: 필터를 걸면 총 페이지 수가 달라지기 때문 */
  function renderPager(totalPages) {
    if (!pager) return;
    pager.classList.toggle('is-hidden', totalPages <= 1);

    pageNums.innerHTML = '';
    for (let i = 1; i <= totalPages; i++) {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.textContent = i;
      if (i === page) btn.classList.add('is-on');
      btn.addEventListener('click', function () { page = i; render(); scrollToTop(); });
      pageNums.appendChild(btn);
    }

    prevBtn.disabled = (page === 1);
    nextBtn.disabled = (page === totalPages);
  }

  /* 페이지를 넘기면 리뷰 목록 맨 위로 올려줌 —
     안 그러면 3페이지째 아래쪽에 있다가 넘겼을 때 화면 중간에 뚝 떨어짐 */
  function scrollToTop() {
    const section = document.querySelector('.product-review');
    if (section) window.scrollTo({ top: section.offsetTop - 60, behavior: 'smooth' });
  }

  sortLinks.forEach(function (a) {
    a.addEventListener('click', function (e) {
      e.preventDefault();
      sortLinks.forEach(function (other) { other.classList.remove('is-on'); });
      a.classList.add('is-on');
      sortBy(a.dataset.sort);
      page = 1;          // 정렬이 바뀌면 1페이지부터 다시
      render();
    });
  });

  /* 검색·필터를 건드리면 보고 있던 페이지 번호는 의미가 없어지므로 1페이지로 */
  searchBox.addEventListener('input',  function () { page = 1; render(); });
  ratingSel.addEventListener('change', function () { page = 1; render(); });

  if (prevBtn) prevBtn.addEventListener('click', function () {
    if (page > 1) { page--; render(); scrollToTop(); }
  });
  if (nextBtn) nextBtn.addEventListener('click', function () {
    page++; render(); scrollToTop();
  });

  sortBy('best');   // 페이지가 열릴 때 기본 탭(베스트순) 기준으로 한 번 정렬해둠
  render();
}


/* ── 6.5 배송 방법 선택 ─────────────────────────
   2026-08-21 추가. 원본 상세페이지를 Playwright 로 직접 열어서 확인함:
     ① 로켓배송 상품 19,800원 이상 무료배송   ← 기본 선택
     ② 무료배송 + 무료반품 | 로켓와우 신청시
   ②를 고르면 [장바구니 담기][바로구매] 두 칸이 [로켓와우로 무료배송 >] 한 칸으로 바뀜.
   (처음엔 그냥 클릭해봐도 안 바뀌길래 "버그인가" 했는데, div 전체가 아니라
    18px짜리 동그라미(span.radio)를 정확히 눌러야 반응하는 걸 원본에서도 확인함 —
    그래서 우리도 li 전체에 클릭 이벤트를 걺. 이러면 그런 정밀 클릭이 필요 없어져서 더 편함)

   ▶JSP: 지금은 "① 기본 선택"을 화면에서만 정하는데, 나중엔 로그인 여부·와우 가입 여부에
     따라 서버가 처음부터 다른 쪽을 선택해서 내려줄 자리 (${member.isWow ? 'is-on' : ''}) */
function setupDeliveryOption() {
  const list = document.querySelector('.radio-group');
  if (!list) return;

  const items = list.querySelectorAll('.radio-item');
  const WOW_INDEX = 1;   // 두 번째 항목 = 로켓와우

  items.forEach(function (li, i) {
    li.addEventListener('click', function () {
      items.forEach(function (other) { other.classList.remove('is-on'); });
      li.classList.add('is-on');

      /* body 에 클래스를 붙이면 css/product.css 가 버튼을 알아서 바꿔치기함
         (품절 처리와 똑같은 방식 — CLAUDE.md 참고) */
      document.body.classList.toggle('is-wow-delivery', i === WOW_INDEX);
    });
  });
}


/* ── 7. 품절 상태 ───────────────────────────────
   <body class="page-product is-soldout"> 가 붙으면 화면이 품절 모습으로 바뀜.
   (실제로 바꾸는 건 css/product.css 9장 — 여기서는 클래스만 붙이고 뗌)

   지금은 확인용으로 주소 뒤에 ?soldout=1 을 붙이면 켜지게 해둠.
   ▶JSP 로 가면 이 함수는 필요 없음. <body> 태그에서 바로 정하면 됨:
       <body class="page-product ${p.stock == 0 ? 'is-soldout' : ''}">
     서버가 재고를 알고 있으니 화면을 켜고 끄는 데 JS 가 낄 이유가 없음 */
function setupSoldout() {
  const soldout = new URLSearchParams(location.search).get('soldout') === '1';
  document.body.classList.toggle('is-soldout', soldout);

  if (soldout) {
    /* 수량을 0 으로 고정 — 원본도 품절이면 0 */
    const input = document.querySelector('.qty-input');
    if (input) input.value = 0;
  }
}


setupQuantity();
setupThumbs();
setupAdSlider();
setupColorChips();
setupReviewTools();
setupDeliveryOption();
/* ── 9. 리뷰 사진 갤러리 (2단 모달) ────────────────
   2026-08-24 추가. 원본을 Playwright 로 직접 열어서 확인한 흐름 그대로:
     ① 리뷰 사진 썸네일 클릭       → "갤러리" 모달 (사진 전체를 격자로)
     ② 갤러리 안 썸네일 또 클릭    → "사진 뷰어" (큰 사진 + 좌우 화살표 + 그 리뷰 글)
   뷰어의 화살표는 **갤러리 사진 전체**를 넘김 (그 리뷰 것만이 아님 — 원본이 그렇게 동작함).
   사진이 바뀌면 위 작성자 정보와 아래 리뷰 글도 그 사진의 주인 리뷰로 같이 바뀜.

   ★★ 사진 목록의 출처는 "리뷰 카드 안의 .review-photos" 하나뿐임 (2026-08-24 수정).
     전에는 위쪽 갤러리 줄에도 사진을 손으로 적고 data-review 로 주인을 표시했는데,
     같은 정보(어느 사진이 누구 것인지)가 두 군데에 있어서 실제로 어긋났음 —
     갤러리는 review-4 를 최*영 것이라 했지만 최*영 카드에는 사진이 없어서
     "전체보기로 가보면 사진이 없는" 상태가 됐음(사용자가 발견).
     지금은 카드에서 긁어모으므로 어긋날 수가 없고, 위쪽 갤러리 줄도 JS 가 그려줌.
     (수량↔가격, 리뷰 별점 때도 같은 원칙을 썼음: 값은 한 곳에만 둔다)

   ★ 리뷰 id 로 주인을 기억하는 이유: 정렬(베스트순/최신순)을 누르면 카드의 DOM 순서가
     바뀌므로 "몇 번째 리뷰" 로 기억하면 어긋남.

   ▶JSP: 카드 안 사진만 <c:forEach> 로 찍어내면 이 JS 는 그대로 동작함. */
function setupReviewGallery() {
  const source = document.querySelector('.review-gallery');
  const modal  = document.querySelector('.review-gallery-modal');
  const viewer = document.querySelector('.photo-viewer');
  if (!source || !modal || !viewer) return;

  /* 리뷰 카드를 전부 훑어서 사진을 모음. [{ src, reviewId }, ...]
     ⚠ 페이지네이션으로 숨겨진(.is-hidden) 카드도 DOM 에는 있으므로 같이 걷힘 —
       원본도 갤러리에는 2페이지 사진까지 다 나오므로 이게 맞음 */
  const photos = [];
  document.querySelectorAll('.review-item').forEach(function (card) {
    const id = card.dataset.reviewId || '';
    card.querySelectorAll('.review-photos img').forEach(function (img) {
      photos.push({ src: img.src, reviewId: id });
    });
  });

  /* 위쪽 갤러리 줄을 모은 사진으로 그림 (HTML 에는 빈 <ul> 만 있음).
     클릭은 아래에서 ul 에 이벤트 위임으로 한 번만 걸어둠 */
  photos.forEach(function (p) {
    const li = document.createElement('li');
    const img = document.createElement('img');
    img.src = p.src;
    img.alt = '';
    li.appendChild(img);
    source.appendChild(li);
  });

  const grid      = modal.querySelector('.gallery-grid');
  const countEl   = modal.querySelector('.gallery-count strong');
  const stage     = viewer.querySelector('.viewer-img');
  const thumbList = viewer.querySelector('.viewer-thumbs');
  const prevBtn   = viewer.querySelector('.viewer-prev');
  const nextBtn   = viewer.querySelector('.viewer-next');
  let current = 0;

  /* 열려 있는 모달이 하나라도 있으면 뒤 페이지 스크롤을 막음 */
  function lockScroll() {
    const open = !modal.hidden || !viewer.hidden;
    document.body.classList.toggle('is-modal-open', open);
  }

  /* ── ① 갤러리 모달 ── */
  function openGallery() {
    countEl.textContent = photos.length;
    grid.innerHTML = '';
    photos.forEach(function (p, i) {
      const li = document.createElement('li');
      const img = document.createElement('img');
      img.src = p.src;
      img.alt = '';
      li.appendChild(img);
      /* 원본에 있는 오른쪽 아래 말풍선 배지 (CSS 로 그림) */
      const badge = document.createElement('span');
      badge.className = 'photo-badge';
      li.appendChild(badge);
      li.addEventListener('click', function () { openViewer(i); });
      grid.appendChild(li);
    });
    modal.hidden = false;
    lockScroll();
  }

  /* ── ② 사진 뷰어 ── */
  function openViewer(index) {
    current = index;
    thumbList.innerHTML = '';
    photos.forEach(function (p, i) {
      const li = document.createElement('li');
      const img = document.createElement('img');
      img.src = p.src;
      img.alt = '';
      li.appendChild(img);
      li.addEventListener('click', function () { showPhoto(i); });
      thumbList.appendChild(li);
    });
    viewer.hidden = false;
    lockScroll();
    showPhoto(index);
  }

  /* 사진 한 장을 화면에 올림 + 그 사진 주인 리뷰의 정보로 위아래를 채움 */
  function showPhoto(index) {
    current = (index + photos.length) % photos.length;   // 끝에서 넘기면 처음으로 돌아감
    const p = photos[current];
    stage.src = p.src;

    [].slice.call(thumbList.children).forEach(function (li, i) {
      li.classList.toggle('is-on', i === current);
    });

    /* 사진이 1장뿐이면 화살표를 감춤 */
    prevBtn.hidden = nextBtn.hidden = (photos.length <= 1);

    /* 이 사진이 달린 리뷰 카드를 id 로 찾아서 글자를 그대로 가져옴.
       리뷰 내용을 뷰어에 또 적어두지 않는 이유: 같은 글이 두 군데 있으면
       한쪽만 고쳤을 때 어긋남 (수량→가격 만들 때 겪은 것과 같은 종류의 실수) */
	   const card = document.querySelector('.review-item[data-review-id="' + p.reviewId + '"]');
	       const pick = function (sel) {
	         const el = card && card.querySelector(sel);
	         return el ? el.textContent.trim() : '';
	       };
	  viewer.querySelector('.viewer-writer .name').textContent  = pick('.name');
 	  viewer.querySelector('.viewer-writer .date').textContent  = pick('.date');

      /* 별점은 글자가 아니라 스프라이트(.star-rating > em 의 width%)라 textContent 로는
          못 옮김 — 원본 카드의 em width 를 그대로 복사함 (2026-08-27) */
      const srcStar = card && card.querySelector('.star-rating em');
      const dstStar = viewer.querySelector('.viewer-writer .star-rating em');
       if (dstStar) dstStar.style.width = srcStar ? srcStar.style.width : '0%';
      viewer.querySelector('.viewer-option').textContent        = pick('.review-option');
      viewer.querySelector('.viewer-text').textContent          = pick('.review-text');
      viewer.dataset.reviewId = p.reviewId;
	     }

  function closeViewer() { viewer.hidden = true; lockScroll(); }
  function closeAll()    { viewer.hidden = true; modal.hidden = true; lockScroll(); }

  /* 리뷰 사진 썸네일 → 갤러리 열기.
     각 li 가 아니라 부모(ul)에 이벤트를 거는 이유(이벤트 위임): 나중에 JSP 가
     사진을 몇 장 찍어낼지 모르므로, 목록이 바뀌어도 코드를 안 고치게 하려고 */
  source.addEventListener('click', function (e) {
    const li = e.target.closest('li');
    if (li) { e.preventDefault(); openGallery(); }
  });

  modal.querySelector('.gallery-close').addEventListener('click', closeAll);
  viewer.querySelector('.viewer-close').addEventListener('click', closeViewer);
  prevBtn.addEventListener('click', function () { showPhoto(current - 1); });
  nextBtn.addEventListener('click', function () { showPhoto(current + 1); });

  /* "전체보기" → 모달을 다 닫고 그 리뷰 카드로 이동 */
  viewer.querySelector('.viewer-all').addEventListener('click', function () {
    const id = viewer.dataset.reviewId;
    closeAll();
    const card = document.querySelector('.review-item[data-review-id="' + id + '"]');
    if (card) window.scrollTo({ top: card.offsetTop - 120, behavior: 'smooth' });
  });

  /* 어두운 배경(덮개)을 직접 눌렀을 때만 닫음.
     e.target === 덮개 를 확인하는 이유: 안쪽 패널을 눌렀을 때도 클릭이 부모로
     올라와서(버블링) 같이 닫혀버리기 때문 */
  modal.addEventListener('click', function (e) { if (e.target === modal) closeAll(); });
  viewer.addEventListener('click', function (e) { if (e.target === viewer) closeViewer(); });

  /* 키보드 — ESC 로 닫기, 좌우 화살표로 사진 넘기기 (뷰어가 열려 있을 때만) */
  document.addEventListener('keydown', function (e) {
    if (!viewer.hidden) {
      if (e.key === 'Escape')     closeViewer();
      if (e.key === 'ArrowLeft')  showPhoto(current - 1);
      if (e.key === 'ArrowRight') showPhoto(current + 1);
    } else if (!modal.hidden && e.key === 'Escape') {
      closeAll();
    }
  });
}


setupItemBriefMore();
setupSurveyMore();
setupTabSpy();
setupSoldout();
setupReviewGallery();
