/* =========================================================
   header.js — 헤더에서 일어나는 동작들

   이 파일은 index.html 맨 아래 </body> 바로 앞에서 불러옴.
   그래서 HTML이 전부 만들어진 뒤에 실행돼서
   document.querySelector 가 요소를 제대로 찾을 수 있음.
   ========================================================= */


/* ---------------------------------------------------------
   1. 카테고리 메뉴 — 마우스 올리면 펼쳐지기
   쿠팡 원본: #wa-pc-category (평소엔 display:none)
   --------------------------------------------------------- */
// TODO
// const categoryBtn = document.querySelector('.category-btn');
// const categoryPanel = document.querySelector('.category-panel');
// categoryBtn.addEventListener('mouseenter', function () {
//   categoryPanel.style.display = 'block';
// });


/* ---------------------------------------------------------
   2. GNB 좌우 화살표 스크롤  ✅ 완성 (2026-08-05)

   메뉴가 16개인데 창에는 9개쯤만 보임. 화살표로 밀어서 나머지를 봄.

   움직이는 원리 (CSS와 나눠 가짐):
     .gnb-menu-container  overflow:hidden  ← 창 밖으로 나간 건 잘라냄
     .gnb-menu-scroll     left 값을 바꿈   ← JS가 하는 일은 이것 하나뿐
                          transition 도 CSS에 이미 있어서 저절로 부드럽게 밀림

   화살표 모양도 CSS가 담당함. JS는 .gnb-menu-btn-active 클래스만 붙였다 뗌
     붙어있음 = 진회색 (더 갈 수 있음)  /  없음 = 연회색 (끝)
   --------------------------------------------------------- */

const gnbContainer = document.querySelector('.gnb-menu-container');
const gnbScroll = document.querySelector('.gnb-menu-scroll');
const gnbLeftBtn = document.querySelector('.gnb-menu-btn-left');
const gnbRightBtn = document.querySelector('.gnb-menu-btn-right');

if (gnbContainer && gnbScroll && gnbLeftBtn && gnbRightBtn) {

  let gnbOffset = 0;   // 지금 왼쪽으로 몇 px 밀려 있는지 (0 = 맨 처음)
  let gnbMax = 0;      // 최대로 밀 수 있는 거리
  let gnbStep = 0;     // 한 번 누를 때 밀 거리

  /* 창 너비와 메뉴 전체 길이를 재서 gnbMax / gnbStep 을 다시 계산.
     창 크기가 바뀌면 이 값들도 달라지므로 함수로 빼둠 */
  function gnbMeasure() {
    // 보이는 창의 안쪽 너비 = 패딩 박스 - 좌우 패딩(화살표 자리 32px씩)
    const cs = getComputedStyle(gnbContainer);
    const visible = gnbContainer.clientWidth
      - parseFloat(cs.paddingLeft) - parseFloat(cs.paddingRight);

    /* 메뉴 전체 길이.
       .gnb-menu-scroll 은 width가 9999px로 박혀 있어서 그 값은 쓸 수 없음.
       대신 "마지막 메뉴의 왼쪽 끝 + 그 메뉴의 너비" 로 실제 길이를 구함.
       (offsetLeft 는 position:relative 인 .gnb-menu-scroll 기준이라 안정적) */
    const items = gnbScroll.children;
    const last = items[items.length - 1];
    const total = last ? last.offsetLeft + last.offsetWidth : 0;

    // 넘치는 만큼만 밀 수 있음. 안 넘치면 0 (화살표 둘 다 비활성)
    gnbMax = Math.max(total - visible, 0);
    gnbStep = visible;   // 한 번에 창 하나만큼(= 한 페이지) 이동

    if (gnbOffset > gnbMax) gnbOffset = gnbMax;   // 창이 커져서 덜 밀어도 될 때
    gnbApply();
  }

  /* 계산된 위치를 실제 화면에 반영 */
  function gnbApply() {
    gnbScroll.style.left = -gnbOffset + 'px';

    const canLeft = gnbOffset > 0;
    const canRight = gnbOffset < gnbMax;

    // 화살표 색 (CSS가 이 클래스를 보고 진회색/연회색을 정함)
    gnbLeftBtn.classList.toggle('gnb-menu-btn-active', canLeft);
    gnbRightBtn.classList.toggle('gnb-menu-btn-active', canRight);

    // 못 가는 방향은 아예 눌리지 않게 막음 (색만 바꾸면 눌리긴 해서 헷갈림)
    gnbLeftBtn.disabled = !canLeft;
    gnbRightBtn.disabled = !canRight;
  }

  gnbRightBtn.addEventListener('click', function () {
    gnbOffset = Math.min(gnbOffset + gnbStep, gnbMax);   // 끝을 넘지 않게
    gnbApply();
  });

  gnbLeftBtn.addEventListener('click', function () {
    gnbOffset = Math.max(gnbOffset - gnbStep, 0);        // 0보다 작아지지 않게
    gnbApply();
  });

  // 창 크기가 바뀌면 다시 계산
  window.addEventListener('resize', gnbMeasure);

  gnbMeasure();
  // 아이콘 이미지가 늦게 떠서 길이가 달라지는 경우를 대비해 한 번 더
  window.addEventListener('load', gnbMeasure);
}


/* ---------------------------------------------------------
   3. 검색창 자동완성 / 인기검색어
   쿠팡 원본: .headerPopupWords
   --------------------------------------------------------- */
// TODO


/* ---------------------------------------------------------
   4. 맨 위로 버튼  ✅ 완성 (2026-08-05)

   스크롤을 300px 넘게 내리면 나타나고, 누르면 맨 위로 올라감.

   ※ "JS는 나중에 몰아서" 하기로 했지만 이건 예외로 지금 만들었음.
     버튼이 기본적으로 숨겨져 있어서(opacity:0) JS가 없으면
     영영 안 보이는 = 아무 쓸모 없는 버튼이 되기 때문.

   ※ 이 코드는 fetch() 를 안 써서 index.html 을 더블클릭해도 잘 돌아감.
     (더블클릭이 막히는 건 나중에 상품목록에서 fetch 를 쓸 때부터)
   --------------------------------------------------------- */

const gotoTop = document.querySelector('#goto-top');

// 요소가 없을 때 아래 코드가 에러를 내면서 멈추면
// 이 파일의 다른 기능까지 같이 죽음. 그래서 있는지 먼저 확인함
if (gotoTop) {

  // scroll 이벤트는 스크롤하는 내내 아주 자주 불림.
  // 여기서는 클래스만 붙였다 떼는 가벼운 일이라 그냥 둬도 괜찮음
  window.addEventListener('scroll', function () {
    // window.scrollY = 지금 위에서 몇 px 내려왔는지
    if (window.scrollY > 300) {
      gotoTop.classList.add('is-on');
    } else {
      gotoTop.classList.remove('is-on');
    }
  });

  gotoTop.addEventListener('click', function () {
    // behavior:'smooth' = 뚝 끊기지 않고 스르륵 올라감
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });
}
