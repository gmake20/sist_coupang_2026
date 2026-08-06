/* =========================================================
   main.js — 메인페이지 본문 동작

   ★ 제일 중요한 개념:
   쿠팡은 React로 상품 카드를 찍어내는데,
   우리는 "데이터 배열 + 반복문 + innerHTML" 로 똑같이 할 수 있음.
   React가 하는 일이 사실 이거거든.
   ========================================================= */


/* ---------------------------------------------------------
   1. 히어로 배너 자동 슬라이드  ✅ 완성 (2026-08-05)

   ★ 핵심: "움직이는" 게 아니라 6장을 같은 자리에 겹쳐두고
     그중 1장만 보이게 하는 것. JS는 클래스만 갈아끼움.

   ⚠ 동시에 3가지를 맞춰야 함. 하나라도 빠지면 화면이 어긋남:
       ① .main-today__bg          → .is-on          (배경 그림)
       ② .today-image             → .is-on          (클릭용 투명 링크)
       ③ .todayshot-right-thumbnail → .today-selected (썸네일 파란 테두리)

   ※ fetch 를 안 쓰므로 index.html 을 더블클릭해도 잘 돌아감
   --------------------------------------------------------- */

const heroBgs = document.querySelectorAll('.main-today__bg');
const heroLinks = document.querySelectorAll('.today-image');
const heroThumbs = document.querySelectorAll('.todayshot-right-thumbnail');
const heroSection = document.querySelector('.main-today');

if (heroBgs.length > 0) {

  let heroIndex = 0;        // 지금 몇 번째를 보여주는 중인지 (0부터 시작)
  let heroTimer = null;     // 자동 넘김 타이머를 담아둘 곳
  const HERO_DELAY = 4000;  // 4초마다 넘김

  /* i 번째 배너를 보여줌 */
  function heroShow(i) {
    /* % 는 나머지 연산. 6장일 때 6 % 6 = 0 이라 마지막 다음은 처음으로 돌아감.
       앞에 heroBgs.length 를 더해두면 -1 이 들어와도 5로 바뀜(거꾸로 갈 때 대비) */
    heroIndex = (i + heroBgs.length) % heroBgs.length;

    /* toggle(클래스, 조건) = 조건이 true면 붙이고 false면 뗌.
       add/remove 를 따로 쓰면 "이전 걸 떼는" 코드를 빠뜨리기 쉬운데
       이렇게 하면 전부 훑으면서 한 번에 정리돼서 안 어긋남 */
    heroBgs.forEach(function (el, n) { el.classList.toggle('is-on', n === heroIndex); });
    heroLinks.forEach(function (el, n) { el.classList.toggle('is-on', n === heroIndex); });
    heroThumbs.forEach(function (el, n) { el.classList.toggle('today-selected', n === heroIndex); });
  }

  function heroStart() {
    heroStop();   // 타이머가 겹쳐서 두 개가 도는 걸 막음
    heroTimer = setInterval(function () { heroShow(heroIndex + 1); }, HERO_DELAY);
  }

  function heroStop() {
    clearInterval(heroTimer);
  }

  /* 썸네일에 마우스만 올려도 바뀜 (원본과 같은 방식). 클릭도 됨 */
  heroThumbs.forEach(function (thumb, n) {
    thumb.addEventListener('mouseenter', function () { heroShow(n); });

    const link = thumb.querySelector('a');
    if (link) {
      link.addEventListener('click', function (e) {
        // href="#" 라서 그냥 두면 페이지 맨 위로 튐. 그걸 막음
        e.preventDefault();
        heroShow(n);
      });
    }
  });

  /* 배너 위에 마우스를 올려둔 동안은 자동 넘김을 멈춤.
     보는 중에 휙 바뀌면 불편하니까 */
  if (heroSection) {
    heroSection.addEventListener('mouseenter', heroStop);
    heroSection.addEventListener('mouseleave', heroStart);
  }

  heroShow(0);   // 시작 상태를 HTML이 아니라 JS가 정하도록 한 번 맞춰줌
  heroStart();
}


/* ---------------------------------------------------------
   2. 상품 카드 찍어내기  ★ 핵심
   data/products.json 의 데이터를 읽어서 HTML로 만들어 넣음
   --------------------------------------------------------- */
// TODO
//
// fetch('data/products.json')
//   .then(res => res.json())
//   .then(products => {
//     const list = document.querySelector('.prod-list');
//
//     // 상품 하나당 <li> 하나씩 만들기
//     const html = products.map(function (item) {
//       return `
//         <li>
//           <a href="#" class="product-unit">
//             <img src="${item.image}" alt="${item.name}">
//             <span class="name">${item.name}</span>
//             <div class="price">
//               <strong>${item.price.toLocaleString()}원</strong>
//             </div>
//           </a>
//         </li>
//       `;
//     }).join('');
//
//     list.innerHTML = html;
//   });
//
// 💡 item.price.toLocaleString() → 8200 을 "8,200" 으로 바꿔줌 (쉼표 자동)
// 💡 `${...}` 는 백틱(``) 안에서만 되는 문법. 따옴표 아님! 키보드 1 왼쪽 키


/* ---------------------------------------------------------
   3. HOT! TREND 카테고리 탭 전환
   쿠팡 원본: li 를 전부 display:none 해두고 선택된 것만 보여줌
   --------------------------------------------------------- */
// TODO
