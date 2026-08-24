<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="./css/vendor_dashboard.css">
  <title>굿팡 판매자 대시보드</title>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "dashboard"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">대시보드</h1>
          <p class="page-desc">오늘의 판매 현황과 주요 지표를 한눈에 확인하세요.</p>
        </div>

        <div class="date-picker" id="datePicker">

          <button class="date-picker-trigger" id="datePickerTrigger" type="button">
            <svg class="icon"><use href="#ic-calendar" /></svg>
            <span>2025.05.19 (월)</span>
            <svg class="icon chevron"><use href="#ic-chevron-down" /></svg>
          </button>

          <div class="date-picker-panel" id="datePickerPanel">
            <a href="#">2025.05.19 (월)</a>
            <a href="#">2025.05.18 (일)</a>
            <a href="#">2025.05.17 (토)</a>
          </div>

        </div>

      </div>


      <!-- KPI 카드 -->
      <section class="kpi-row">

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">오늘 주문수</span>
            <span class="kpi-icon kpi-icon-blue"><svg class="icon"><use href="#ic-cart" /></svg></span>
          </div>
          <div class="kpi-value">128 <small>건</small></div>
          <div class="kpi-compare">어제 대비 <strong class="up">▲ 18.7%</strong></div>
          <svg class="kpi-spark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="0,30 25,26 50,28 75,20 100,22 125,10 160,6" fill="none" stroke="#4285f4"
              stroke-width="2" />
          </svg>
        </article>

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">오늘 매출</span>
            <span class="kpi-icon kpi-icon-green"><svg class="icon"><use href="#ic-receipt" /></svg></span>
          </div>
          <div class="kpi-value">₩ 4,235,000</div>
          <div class="kpi-compare">어제 대비 <strong class="up">▲ 21.4%</strong></div>
          <svg class="kpi-spark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="0,32 25,24 50,26 75,18 100,20 125,12 160,8" fill="none" stroke="#17c964"
              stroke-width="2" />
          </svg>
        </article>

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">오늘 방문자수</span>
            <span class="kpi-icon kpi-icon-orange"><svg class="icon"><use href="#ic-users" /></svg></span>
          </div>
          <div class="kpi-value">2,453 <small>명</small></div>
          <div class="kpi-compare">어제 대비 <strong class="up">▲ 8.1%</strong></div>
          <svg class="kpi-spark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="0,20 25,28 50,18 75,24 100,14 125,20 160,10" fill="none" stroke="#ff9f1c"
              stroke-width="2" />
          </svg>
        </article>

        <article class="kpi-card">
          <div class="kpi-top">
            <span class="kpi-label">오늘 상품 노출수</span>
            <span class="kpi-icon kpi-icon-purple"><svg class="icon"><use href="#ic-eye" /></svg></span>
          </div>
          <div class="kpi-value">12,845 <small>회</small></div>
          <div class="kpi-compare">어제 대비 <strong class="up">▲ 11.3%</strong></div>
          <svg class="kpi-spark" viewBox="0 0 160 40" preserveAspectRatio="none">
            <polyline points="0,28 25,22 50,24 75,14 100,18 125,8 160,12" fill="none" stroke="#9b5de5"
              stroke-width="2" />
          </svg>
        </article>

      </section>


      <!-- 중단 -->
      <section class="mid-row">

        <article class="panel sales-panel">

          <div class="panel-head">
            <h2>매출 현황 <span class="info-dot" title="최근 7일 매출액과 주문수">?</span></h2>
            <div class="tab-group" id="salesTabs">
              <button class="tab-btn active" type="button" data-range="daily">일간</button>
              <button class="tab-btn" type="button" data-range="weekly">주간</button>
              <button class="tab-btn" type="button" data-range="monthly">월간</button>
            </div>
          </div>

          <div class="chart-legend">
            <span class="legend-item"><i class="dot dot-blue"></i>매출액(원)</span>
            <span class="legend-item"><i class="dot dot-green"></i>주문수(건)</span>
          </div>

          <svg id="salesChart" class="sales-chart" viewBox="0 0 680 300"></svg>

        </article>

        <article class="panel">

          <div class="panel-head">
            <h2>주문/배송 현황</h2>
            <a href="#" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <ul class="status-list">
            <li>
              <span class="status-icon status-icon-purple"><svg class="icon"><use href="#ic-box" /></svg></span>
              <span class="status-label">신규 주문</span>
              <span class="status-value">35 <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-orange"><svg class="icon"><use href="#ic-clock" /></svg></span>
              <span class="status-label">결제 대기</span>
              <span class="status-value">12 <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-blue"><svg class="icon"><use href="#ic-clipboard-check" /></svg></span>
              <span class="status-label">상품 준비중</span>
              <span class="status-value">74 <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-teal"><svg class="icon"><use href="#ic-truck" /></svg></span>
              <span class="status-label">배송 중</span>
              <span class="status-value">58 <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-green"><svg class="icon"><use href="#ic-truck" /></svg></span>
              <span class="status-label">배송 완료</span>
              <span class="status-value">320 <small>건</small></span>
            </li>
            <li>
              <span class="status-icon status-icon-red"><svg class="icon"><use href="#ic-return" /></svg></span>
              <span class="status-label">취소/반품/교환</span>
              <span class="status-value">8 <small>건</small></span>
            </li>
          </ul>

        </article>

        <article class="panel">

          <div class="panel-head">
            <h2>공지사항</h2>
            <a href="#" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <ul class="notice-list">
            <li>
              <span class="notice-tag tag-notice">공지</span>
              <span class="notice-title">2025년 5월 정산 일정 안내</span>
              <span class="notice-date">05.16</span>
            </li>
            <li>
              <span class="notice-tag tag-notice">공지</span>
              <span class="notice-title">배송 지연 보상 정책 변경 안내</span>
              <span class="notice-date">05.14</span>
            </li>
            <li>
              <span class="notice-tag tag-info">안내</span>
              <span class="notice-title">판매자 이용약관 개정 안내</span>
              <span class="notice-date">05.10</span>
            </li>
            <li>
              <span class="notice-tag tag-info">안내</span>
              <span class="notice-title">여름맞이 프로모션 참여 안내</span>
              <span class="notice-date">05.08</span>
            </li>
            <li>
              <span class="notice-tag tag-info">안내</span>
              <span class="notice-title">시스템 점검 안내 (5/25 새벽)</span>
              <span class="notice-date">05.07</span>
            </li>
          </ul>

        </article>

      </section>


      <!-- 하단 -->
      <section class="bottom-row">

        <article class="panel">

          <div class="panel-head">
            <h2>인기 상품 TOP 5</h2>
            <a href="#" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <table class="top-table">
            <thead>
              <tr>
                <th>순위</th>
                <th>상품명</th>
                <th>판매수</th>
                <th>매출액</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="rank">1</td>
                <td class="product-name"><span class="thumb"></span>코멧 무선 청소기 V3</td>
                <td>245</td>
                <td>₩1,225,000</td>
              </tr>
              <tr>
                <td class="rank">2</td>
                <td class="product-name"><span class="thumb"></span>로켓프레시 베이직 생수 2L x 6</td>
                <td>198</td>
                <td>₩396,000</td>
              </tr>
              <tr>
                <td class="rank">3</td>
                <td class="product-name"><span class="thumb"></span>탐사 강아지 사료 3kg</td>
                <td>156</td>
                <td>₩468,000</td>
              </tr>
              <tr>
                <td class="rank">4</td>
                <td class="product-name"><span class="thumb"></span>홈플래닛 LED 스탠드</td>
                <td>143</td>
                <td>₩429,000</td>
              </tr>
              <tr>
                <td class="rank">5</td>
                <td class="product-name"><span class="thumb"></span>도브 바디워시 1L</td>
                <td>127</td>
                <td>₩317,500</td>
              </tr>
            </tbody>
          </table>

        </article>

        <article class="panel">

          <div class="panel-head">
            <h2>매출 채널 비중</h2>
            <a href="#" class="more-link">더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <div class="donut-wrap">

            <div class="donut" id="channelDonut">
              <div class="donut-center">
                <strong>₩ 42,350,000</strong>
                <span>(이번 달)</span>
              </div>
            </div>

            <ul class="donut-legend">
              <li><i class="dot dot-blue"></i>쿠팡 검색 <b>45%</b></li>
              <li><i class="dot dot-teal"></i>추천/기획전 <b>25%</b></li>
              <li><i class="dot dot-orange"></i>광고 <b>15%</b></li>
              <li><i class="dot dot-mint"></i>즐겨찾기/찜 <b>10%</b></li>
              <li><i class="dot dot-red"></i>기타 <b>5%</b></li>
            </ul>

          </div>

        </article>

        <article class="panel">

          <div class="panel-head">
            <h2>판매자 점수</h2>
            <a href="#" class="more-link">자세히 보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
          </div>

          <div class="score-top">
            <span class="score-face"><svg class="icon"><use href="#ic-smile" /></svg></span>
            <div>
              <strong class="score-grade">우수</strong>
              <p>상위 20% 판매자입니다.<br>(최근 30일 기준)</p>
            </div>
          </div>

          <div class="score-grid">
            <div class="score-item">
              <span class="score-num">4.8<small>/5</small></span>
              <span class="score-name">배송 만족도</span>
            </div>
            <div class="score-item">
              <span class="score-num">4.6<small>/5</small></span>
              <span class="score-name">상품 만족도</span>
            </div>
            <div class="score-item">
              <span class="score-num">4.7<small>/5</small></span>
              <span class="score-name">고객 응대</span>
            </div>
            <div class="score-item">
              <span class="score-num">5.0<small>/5</small></span>
              <span class="score-name">정책 준수</span>
            </div>
          </div>

        </article>

      </section>

    </main>

  </div>


  <script src="js/vendor-common.js"></script>
  <script>

    /* =========================================================
       날짜 선택 드롭다운
       (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리.
        setupDropdown()도 그 파일에 정의돼있는 함수를 그대로 가져다 씀)
    ========================================================= */

    setupDropdown("datePickerTrigger", "datePickerPanel");


    /* =========================================================
       매출 현황 차트 — 막대(매출액) + 선(주문수)
    ========================================================= */

    const salesData = {
      daily: {
        labels: ["5/13", "5/14", "5/15", "5/16", "5/17", "5/18", "5/19"],
        sales: [2300000, 3700000, 2000000, 2300000, 2600000, 3600000, 3000000],
        orders: [90, 150, 140, 145, 150, 200, 175]
      },
      weekly: {
        labels: ["1주", "2주", "3주", "4주", "5주"],
        sales: [15200000, 18400000, 16800000, 21500000, 19600000],
        orders: [620, 710, 680, 830, 760]
      },
      monthly: {
        labels: ["1월", "2월", "3월", "4월", "5월"],
        sales: [58000000, 61200000, 67400000, 72300000, 69800000],
        orders: [2450, 2600, 2820, 3050, 2900]
      }
    };

    const salesMax = { daily: 5000000, weekly: 25000000, monthly: 80000000 };
    const orderMax = { daily: 250, weekly: 1000, monthly: 3500 };

    function formatAxis(value) {
      if (value >= 100000000) return (value / 100000000) + "억";
      if (value >= 10000) return (value / 10000) + "만";
      return value.toLocaleString();
    }

    function renderSalesChart(range) {
      const data = salesData[range];
      const svg = document.getElementById("salesChart");

      const width = 680;
      const height = 300;
      const padLeft = 60;
      const padRight = 40;
      const padTop = 20;
      const padBottom = 34;

      const plotW = width - padLeft - padRight;
      const plotH = height - padTop - padBottom;

      const sMax = salesMax[range];
      const oMax = orderMax[range];
      const count = data.labels.length;
      const slot = plotW / count;
      const barW = Math.min(40, slot * 0.5);

      let svgParts = [];

      /* 격자 + y축 라벨 (매출액, 5단계) */
      for (let i = 0; i <= 5; i++) {
        const y = padTop + plotH - (plotH * i / 5);
        const value = sMax * i / 5;
        svgParts.push(
          '<line x1="' + padLeft + '" y1="' + y + '" x2="' + (width - padRight) + '" y2="' + y +
          '" stroke="#eee" stroke-width="1" />'
        );
        svgParts.push(
          '<text x="' + (padLeft - 10) + '" y="' + (y + 4) + '" text-anchor="end" class="axis-label">' +
          formatAxis(value) + '</text>'
        );
        svgParts.push(
          '<text x="' + (width - padRight + 10) + '" y="' + (y + 4) + '" text-anchor="start" class="axis-label">' +
          Math.round(oMax * i / 5) + '</text>'
        );
      }

      /* 막대 (매출액) */
      const points = [];
      data.sales.forEach(function (value, index) {
        const x = padLeft + slot * index + (slot - barW) / 2;
        const barH = plotH * (value / sMax);
        const y = padTop + plotH - barH;

        svgParts.push(
          '<rect x="' + x + '" y="' + y + '" width="' + barW + '" height="' + barH +
          '" rx="3" fill="#5b8bff" />'
        );

        svgParts.push(
          '<text x="' + (x + barW / 2) + '" y="' + (height - padBottom + 20) + '" text-anchor="middle" class="axis-label">' +
          data.labels[index] + '</text>'
        );

        const cx = padLeft + slot * index + slot / 2;
        const cy = padTop + plotH - (plotH * (data.orders[index] / oMax));
        points.push(cx + "," + cy);
      });

      /* 선 (주문수) */
      svgParts.push('<polyline points="' + points.join(" ") + '" fill="none" stroke="#17c964" stroke-width="2.5" />');
      points.forEach(function (point) {
        const parts = point.split(",");
        svgParts.push('<circle cx="' + parts[0] + '" cy="' + parts[1] + '" r="3.5" fill="#17c964" />');
      });

      svg.innerHTML = svgParts.join("");
    }

    document.getElementById("salesTabs").addEventListener("click", function (event) {
      const button = event.target.closest(".tab-btn");
      if (!button) return;

      document.querySelectorAll("#salesTabs .tab-btn").forEach(function (btn) {
        btn.classList.remove("active");
      });
      button.classList.add("active");

      renderSalesChart(button.dataset.range);
    });

    renderSalesChart("daily");


    /* =========================================================
       매출 채널 비중 — 도넛 차트 (conic-gradient)
    ========================================================= */

    const channelData = [
      { pct: 45, color: "#4285f4" },
      { pct: 25, color: "#17c9b0" },
      { pct: 15, color: "#ff9f1c" },
      { pct: 10, color: "#7bdcb5" },
      { pct: 5, color: "#f4514a" }
    ];

    (function renderDonut() {
      let stops = [];
      let acc = 0;

      channelData.forEach(function (item) {
        const start = acc;
        acc += item.pct;
        stops.push(item.color + " " + start + "% " + acc + "%");
      });

      document.getElementById("channelDonut").style.background =
        "conic-gradient(" + stops.join(", ") + ")";
    })();

  </script>

</body>

</html>
