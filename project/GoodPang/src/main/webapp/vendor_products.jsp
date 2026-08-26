<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<html lang="ko">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="./css/vendor_products.css">
  <title>굿팡 판매자 상품 관리</title>

</head>

<body>

  <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

  <% String sellerGrade = null; %>
  <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

  <% String menu = "products"; %>
  <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


    <!-- 메인 -->
    <main class="main">

      <div class="page-head">

        <div>
          <h1 class="page-title">상품 관리</h1>
          <p class="page-desc">판매 중인 상품을 등록, 수정하고 관리할 수 있습니다.</p>
        </div>

        <div class="page-actions">
          <button class="btn btn-outline" type="button">엑셀 다운로드</button>
          <button class="btn btn-outline" type="button">엑셀 업로드</button>
          <a class="btn btn-primary" href="/vendor/product/write">상품 등록</a>
        </div>

      </div>


      <!-- 통계 카드 -->
      <section class="stat-row">

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">전체 상품</span>
            <span class="stat-icon stat-icon-blue"><svg class="icon"><use href="#ic-box" /></svg></span>
          </div>
          <div class="stat-value">1,248 <small>개</small></div>
          <a href="#" class="stat-link">전체보기 <svg class="icon"><use href="#ic-chevron-down" /></svg></a>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">판매 중</span>
            <span class="stat-icon stat-icon-green"><svg class="icon"><use href="#ic-bag" /></svg></span>
          </div>
          <div class="stat-value">942 <small>개</small></div>
          <span class="stat-rate rate-green">75.6%</span>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">품절</span>
            <span class="stat-icon stat-icon-orange"><svg class="icon"><use href="#ic-ban" /></svg></span>
          </div>
          <div class="stat-value">82 <small>개</small></div>
          <span class="stat-rate rate-orange">6.6%</span>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">판매 중지</span>
            <span class="stat-icon stat-icon-red"><svg class="icon"><use href="#ic-pause-circle" /></svg></span>
          </div>
          <div class="stat-value">68 <small>개</small></div>
          <span class="stat-rate rate-red">5.5%</span>
        </article>

        <article class="stat-card">
          <div class="stat-top">
            <span class="stat-label">승인 대기</span>
            <span class="stat-icon stat-icon-blue"><svg class="icon"><use href="#ic-clock" /></svg></span>
          </div>
          <div class="stat-value">156 <small>개</small></div>
          <span class="stat-rate rate-blue">12.3%</span>
        </article>

      </section>


      <!-- 검색 필터 -->
      <section class="panel filter-panel">

        <div class="filter-row">

          <div class="filter-field">
            <label>상품명</label>
            <input class="input" type="text" placeholder="상품명을 입력해주세요.">
          </div>

          <div class="filter-field">
            <label>카테고리</label>
            <select class="input select">
              <option>대분류 선택</option>
              <option>가전디지털</option>
              <option>주방용품</option>
              <option>식품/음료</option>
              <option>가구/인테리어</option>
            </select>
          </div>

          <div class="filter-field">
            <label>판매 상태</label>
            <select class="input select">
              <option>전체</option>
              <option>판매 중</option>
              <option>품절</option>
              <option>판매 중지</option>
              <option>승인 대기</option>
            </select>
          </div>

          <div class="filter-field">
            <label>상품 유형</label>
            <select class="input select">
              <option>전체</option>
              <option>일반상품</option>
              <option>로켓배송</option>
              <option>정기배송</option>
            </select>
          </div>

          <div class="filter-field">
            <label>브랜드</label>
            <select class="input select">
              <option>전체</option>
              <option>코멧</option>
              <option>로켓프레시</option>
              <option>탐사</option>
              <option>홈플래닛</option>
            </select>
          </div>

        </div>

        <div class="filter-row filter-row-date">

          <div class="filter-field date-field">

            <label>등록일</label>

            <div class="date-controls">

              <div class="quick-range">
                <button class="quick-btn" type="button">오늘</button>
                <button class="quick-btn" type="button">7일</button>
                <button class="quick-btn" type="button">1개월</button>
                <button class="quick-btn" type="button">3개월</button>
              </div>

              <div class="date-range">
                <div class="date-input">
                  <input class="input" type="text" value="2025-05-12">
                  <svg class="icon"><use href="#ic-calendar" /></svg>
                </div>
                <span class="date-tilde">~</span>
                <div class="date-input">
                  <input class="input" type="text" value="2025-05-19">
                  <svg class="icon"><use href="#ic-calendar" /></svg>
                </div>
              </div>

            </div>

          </div>

          <div class="filter-buttons">
            <button class="btn btn-outline" type="button" id="resetButton">초기화</button>
            <button class="btn btn-primary" type="button">검색</button>
          </div>

        </div>

      </section>


      <!-- 결과 툴바 -->
      <div class="result-toolbar">
        <p class="result-count">검색 결과 <strong>1,248</strong>개</p>

        <div class="result-controls">
          <select class="input select select-sm">
            <option>등록일 최신순</option>
            <option>등록일 오래된순</option>
            <option>판매가 높은순</option>
            <option>판매가 낮은순</option>
          </select>
          <select class="input select select-sm">
            <option>20개씩 보기</option>
            <option>50개씩 보기</option>
            <option>100개씩 보기</option>
          </select>
        </div>
      </div>


      <!-- 상품 목록 테이블 -->
      <section class="panel table-panel">

        <table class="product-table">
          <thead>
            <tr>
              <th class="col-check"><input type="checkbox" id="checkAll"></th>
              <th class="col-info">상품 정보</th>
              <th class="col-price">판매가</th>
              <th class="col-stock">재고 수량</th>
              <th class="col-status">판매 상태</th>
              <th class="col-date">등록일</th>
              <th class="col-date">수정일</th>
              <th class="col-manage">관리</th>
            </tr>
          </thead>
          <tbody>

            <tr>
              <td class="col-check"><input type="checkbox"></td>
              <td class="col-info">
                <div class="product-cell">
                  <span class="thumb"></span>
                  <div class="product-text">
                    <p class="product-name">쿠팡프레시 공기청정기 A3</p>
                    <p class="product-sku">SKU 8801234567890</p>
                    <p class="product-cat">가전디지털 &gt; 계절가전 &gt; 공기청정기</p>
                  </div>
                </div>
              </td>
              <td class="col-price">₩239,000</td>
              <td class="col-stock">128</td>
              <td class="col-status"><span class="status-badge status-active">판매 중</span></td>
              <td class="col-date">2025-05-18<br><span class="time">10:30</span></td>
              <td class="col-date">2025-05-18<br><span class="time">14:20</span></td>
              <td class="col-manage">
                <button class="btn btn-outline btn-sm" type="button">수정</button>
                <button class="btn btn-outline btn-sm btn-more" type="button">
                  더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg>
                </button>
              </td>
            </tr>

            <tr>
              <td class="col-check"><input type="checkbox"></td>
              <td class="col-info">
                <div class="product-cell">
                  <span class="thumb"></span>
                  <div class="product-text">
                    <p class="product-name">코팅이강한 IH 냄비 24cm</p>
                    <p class="product-sku">SKU 8801234567891</p>
                    <p class="product-cat">주방용품 &gt; 냄비/프라이팬 &gt; 냄비</p>
                  </div>
                </div>
              </td>
              <td class="col-price">₩29,900</td>
              <td class="col-stock">0</td>
              <td class="col-status"><span class="status-badge status-soldout">품절</span></td>
              <td class="col-date">2025-05-17<br><span class="time">09:15</span></td>
              <td class="col-date">2025-05-17<br><span class="time">11:05</span></td>
              <td class="col-manage">
                <button class="btn btn-outline btn-sm" type="button">수정</button>
                <button class="btn btn-outline btn-sm btn-more" type="button">
                  더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg>
                </button>
              </td>
            </tr>

            <tr>
              <td class="col-check"><input type="checkbox"></td>
              <td class="col-info">
                <div class="product-cell">
                  <span class="thumb"></span>
                  <div class="product-text">
                    <p class="product-name">탐사수 2L x 6개입</p>
                    <p class="product-sku">SKU 8801234567892</p>
                    <p class="product-cat">식품 &gt; 생수/음료 &gt; 생수</p>
                  </div>
                </div>
              </td>
              <td class="col-price">₩4,800</td>
              <td class="col-stock">356</td>
              <td class="col-status"><span class="status-badge status-active">판매 중</span></td>
              <td class="col-date">2025-05-16<br><span class="time">16:40</span></td>
              <td class="col-date">2025-05-18<br><span class="time">09:10</span></td>
              <td class="col-manage">
                <button class="btn btn-outline btn-sm" type="button">수정</button>
                <button class="btn btn-outline btn-sm btn-more" type="button">
                  더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg>
                </button>
              </td>
            </tr>

            <tr>
              <td class="col-check"><input type="checkbox"></td>
              <td class="col-info">
                <div class="product-cell">
                  <span class="thumb"></span>
                  <div class="product-text">
                    <p class="product-name">컴포트 메쉬 사무용 의자</p>
                    <p class="product-sku">SKU 8801234567893</p>
                    <p class="product-cat">가구/인테리어 &gt; 의자 &gt; 사무용 의자</p>
                  </div>
                </div>
              </td>
              <td class="col-price">₩89,000</td>
              <td class="col-stock">27</td>
              <td class="col-status"><span class="status-badge status-active">판매 중</span></td>
              <td class="col-date">2025-05-15<br><span class="time">13:20</span></td>
              <td class="col-date">2025-05-18<br><span class="time">10:50</span></td>
              <td class="col-manage">
                <button class="btn btn-outline btn-sm" type="button">수정</button>
                <button class="btn btn-outline btn-sm btn-more" type="button">
                  더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg>
                </button>
              </td>
            </tr>

            <tr>
              <td class="col-check"><input type="checkbox"></td>
              <td class="col-info">
                <div class="product-cell">
                  <span class="thumb"></span>
                  <div class="product-text">
                    <p class="product-name">로지택 무선 마우스 M331</p>
                    <p class="product-sku">SKU 8801234567894</p>
                    <p class="product-cat">가전디지털 &gt; PC주변기기 &gt; 마우스</p>
                  </div>
                </div>
              </td>
              <td class="col-price">₩19,900</td>
              <td class="col-stock">63</td>
              <td class="col-status"><span class="status-badge status-stopped">판매 중지</span></td>
              <td class="col-date">2025-05-14<br><span class="time">11:10</span></td>
              <td class="col-date">2025-05-16<br><span class="time">17:30</span></td>
              <td class="col-manage">
                <button class="btn btn-outline btn-sm" type="button">수정</button>
                <button class="btn btn-outline btn-sm btn-more" type="button">
                  더보기 <svg class="icon"><use href="#ic-chevron-down" /></svg>
                </button>
              </td>
            </tr>

          </tbody>
        </table>

        <!-- 페이지네이션 -->
        <nav class="pagination" aria-label="페이지 이동">
          <button class="page-arrow" type="button" aria-label="이전 페이지">
            <svg class="icon"><use href="#ic-chevron-left" /></svg>
          </button>

          <button class="page-num active" type="button">1</button>
          <button class="page-num" type="button">2</button>
          <button class="page-num" type="button">3</button>
          <button class="page-num" type="button">4</button>
          <button class="page-num" type="button">5</button>
          <span class="page-ellipsis">…</span>
          <button class="page-num" type="button">63</button>

          <button class="page-arrow" type="button" aria-label="다음 페이지">
            <svg class="icon rotate-180"><use href="#ic-chevron-left" /></svg>
          </button>
        </nav>

      </section>

    </main>

  </div>


  <script src="js/vendor-common.js"></script>
  <script>

    /* =========================================================
       등록일 빠른선택 버튼
       (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리)
    ========================================================= */

    document.querySelectorAll(".quick-btn").forEach(function (button) {
      button.addEventListener("click", function () {
        document.querySelectorAll(".quick-btn").forEach(function (btn) {
          btn.classList.remove("active");
        });
        button.classList.add("active");
      });
    });


    /* =========================================================
       전체 선택 체크박스
    ========================================================= */

    document.getElementById("checkAll").addEventListener("change", function () {
      document.querySelectorAll(".product-table tbody .col-check input").forEach(
        (checkbox) => (checkbox.checked = this.checked)
      );
    });


    /* =========================================================
       초기화
    ========================================================= */

    document.getElementById("resetButton").addEventListener("click", function () {
      document.querySelectorAll(".filter-panel .input").forEach(function (field) {
        if (field.tagName === "SELECT") {
          field.selectedIndex = 0;
        } else if (field.type === "text" && !field.closest(".date-input")) {
          field.value = "";
        }
      });
      document.querySelectorAll(".quick-btn").forEach((btn) => btn.classList.remove("active"));
    });

  </script>

</body>

</html>
