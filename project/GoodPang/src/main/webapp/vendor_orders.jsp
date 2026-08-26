<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!DOCTYPE html>

  <html lang="ko">

  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="./css/vendor_orders.css">
    <title>굿팡 판매자 주문/배송 관리</title>

  </head>

  <body>

    <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

      <% String sellerGrade="파워셀러" ; %>
        <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

          <% String menu="orders" ; %>
            <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


              <!-- 메인 -->
              <main class="main">

                <div class="page-head">

                  <div>
                    <h1 class="page-title">주문/배송 관리</h1>
                    <p class="page-desc">주문 처리 및 배송 현황을 확인하고 관리할 수 있습니다.</p>
                  </div>

                  <div class="page-actions">

                    <div class="search-box">
                      <svg class="icon">
                        <use href="#ic-search" />
                      </svg>
                      <input type="text" placeholder="주문번호, 상품명, 구매자명 검색">
                    </div>

                    <button class="btn btn-outline" type="button">엑셀 다운로드</button>

                  </div>

                </div>


                <!-- 통계 카드 -->
                <section class="stat-row">

                  <article class="stat-card">
                    <div class="stat-top">
                      <span class="stat-label">신규 주문</span>
                      <span class="stat-icon stat-icon-purple"><svg class="icon">
                          <use href="#ic-bag" />
                        </svg></span>
                    </div>
                    <div class="stat-value">35 <small>건</small></div>
                    <p class="stat-compare">어제 대비 <strong class="up">▲ 16 (84%)</strong></p>
                  </article>

                  <article class="stat-card">
                    <div class="stat-top">
                      <span class="stat-label">결제 대기</span>
                      <span class="stat-icon stat-icon-orange"><svg class="icon">
                          <use href="#ic-card" />
                        </svg></span>
                    </div>
                    <div class="stat-value">12 <small>건</small></div>
                    <p class="stat-compare">어제 대비 <strong class="up">▲ 5 (71%)</strong></p>
                  </article>

                  <article class="stat-card">
                    <div class="stat-top">
                      <span class="stat-label">상품 준비중</span>
                      <span class="stat-icon stat-icon-violet"><svg class="icon">
                          <use href="#ic-box" />
                        </svg></span>
                    </div>
                    <div class="stat-value">74 <small>건</small></div>
                    <p class="stat-compare">어제 대비 <strong class="up">▲ 12 (19%)</strong></p>
                  </article>

                  <article class="stat-card">
                    <div class="stat-top">
                      <span class="stat-label">배송 중</span>
                      <span class="stat-icon stat-icon-green"><svg class="icon">
                          <use href="#ic-truck" />
                        </svg></span>
                    </div>
                    <div class="stat-value">58 <small>건</small></div>
                    <p class="stat-compare">어제 대비 <strong class="up">▲ 9 (18%)</strong></p>
                  </article>

                  <article class="stat-card">
                    <div class="stat-top">
                      <span class="stat-label">배송 완료 (오늘)</span>
                      <span class="stat-icon stat-icon-blue"><svg class="icon">
                          <use href="#ic-check-circle" />
                        </svg></span>
                    </div>
                    <div class="stat-value">320 <small>건</small></div>
                    <p class="stat-compare">어제 대비 <strong class="up">▲ 28 (10%)</strong></p>
                  </article>

                  <article class="stat-card">
                    <div class="stat-top">
                      <span class="stat-label">취소/반품/교환</span>
                      <span class="stat-icon stat-icon-red"><svg class="icon">
                          <use href="#ic-return" />
                        </svg></span>
                    </div>
                    <div class="stat-value">8 <small>건</small></div>
                    <p class="stat-compare">어제 대비 <strong class="down">▼ 3 (27%)</strong></p>
                  </article>

                </section>


                <!-- 검색 필터 -->
                <section class="panel filter-panel">

                  <div class="filter-row filter-row-main">

                    <div class="filter-field date-field">

                      <label>주문일</label>

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
                            <svg class="icon">
                              <use href="#ic-calendar" />
                            </svg>
                          </div>
                          <span class="date-tilde">~</span>
                          <div class="date-input">
                            <input class="input" type="text" value="2025-05-19">
                            <svg class="icon">
                              <use href="#ic-calendar" />
                            </svg>
                          </div>
                        </div>

                      </div>

                    </div>

                    <div class="filter-field select-field">
                      <label>주문상태</label>
                      <select class="input select">
                        <option>전체</option>
                        <option>결제 대기</option>
                        <option>상품 준비중</option>
                        <option>배송 중</option>
                        <option>배송 완료</option>
                      </select>
                    </div>

                    <div class="filter-field select-field">
                      <label>배송상태</label>
                      <select class="input select">
                        <option>전체</option>
                        <option>출고 대기</option>
                        <option>배송 중</option>
                        <option>배송 완료</option>
                      </select>
                    </div>

                    <div class="filter-field select-field">
                      <label>결제상태</label>
                      <select class="input select">
                        <option>전체</option>
                        <option>결제 완료</option>
                        <option>결제 대기</option>
                        <option>결제 취소</option>
                      </select>
                    </div>

                    <button class="btn btn-outline btn-detail" type="button">
                      <svg class="icon">
                        <use href="#ic-filter" />
                      </svg>
                      상세검색
                    </button>

                  </div>

                </section>


                <!-- 본문: 주문 목록 + 사이드 -->
                <div class="content-grid">

                  <!-- 주문 목록 -->
                  <section class="panel table-panel">

                    <div class="result-toolbar">
                      <p class="result-count">주문 목록 <strong>1,248</strong>건</p>

                      <select class="input select select-sm">
                        <option>주문일 최신순</option>
                        <option>주문일 오래된순</option>
                        <option>주문금액 높은순</option>
                        <option>주문금액 낮은순</option>
                      </select>
                    </div>

                    <div class="table-scroll">
                      <table class="order-table">
                        <thead>
                          <tr>
                            <th class="col-check"><input type="checkbox" id="checkAll"></th>
                            <th class="col-order">주문 정보</th>
                            <th class="col-info">상품 정보</th>
                            <th class="col-price">주문금액</th>
                            <th class="col-status">주문상태</th>
                            <th class="col-status">배송상태</th>
                            <th class="col-date">주문일</th>
                            <th class="col-manage">관리</th>
                          </tr>
                        </thead>
                        <tbody>

                          <tr>
                            <td class="col-check"><input type="checkbox"></td>
                            <td class="col-order">
                              <p class="order-no">주문번호<br><strong>2025051900012345</strong></p>
                              <p class="order-buyer">홍길동 (010-1234-5678)</p>
                            </td>
                            <td class="col-info">
                              <div class="product-cell">
                                <span class="thumb"></span>
                                <div class="product-text">
                                  <p class="product-name">쿠팡프레시 공기청정기 A3</p>
                                  <p class="product-qty">수량 1개</p>
                                </div>
                              </div>
                            </td>
                            <td class="col-price">
                              <p class="price">239,000원</p>
                              <p class="delivery-fee">(배송비 0원)</p>
                            </td>
                            <td class="col-status"><span class="status-badge status-preparing">상품 준비중</span></td>
                            <td class="col-status"><span class="status-badge status-waiting">출고 대기</span></td>
                            <td class="col-date">2025-05-19<br><span class="time">14:30:25</span></td>
                            <td class="col-manage">
                              <button class="btn btn-outline btn-sm" type="button">상세보기</button>
                              <button class="btn btn-outline btn-sm btn-more" type="button">
                                작업 <svg class="icon">
                                  <use href="#ic-chevron-down" />
                                </svg>
                              </button>
                            </td>
                          </tr>

                          <tr>
                            <td class="col-check"><input type="checkbox"></td>
                            <td class="col-order">
                              <p class="order-no">주문번호<br><strong>2025051900012344</strong></p>
                              <p class="order-buyer">김철수 (010-2222-3333)</p>
                            </td>
                            <td class="col-info">
                              <div class="product-cell">
                                <span class="thumb"></span>
                                <div class="product-text">
                                  <p class="product-name">코팅이강한 IH 냄비 24cm</p>
                                  <p class="product-qty">수량 1개</p>
                                </div>
                              </div>
                            </td>
                            <td class="col-price">
                              <p class="price">29,900원</p>
                              <p class="delivery-fee">(배송비 0원)</p>
                            </td>
                            <td class="col-status"><span class="status-badge status-pending">결제 대기</span></td>
                            <td class="col-status"><span class="status-dash">-</span></td>
                            <td class="col-date">2025-05-19<br><span class="time">13:20:11</span></td>
                            <td class="col-manage">
                              <button class="btn btn-outline btn-sm" type="button">상세보기</button>
                              <button class="btn btn-outline btn-sm btn-more" type="button">
                                작업 <svg class="icon">
                                  <use href="#ic-chevron-down" />
                                </svg>
                              </button>
                            </td>
                          </tr>

                          <tr>
                            <td class="col-check"><input type="checkbox"></td>
                            <td class="col-order">
                              <p class="order-no">주문번호<br><strong>2025051900012343</strong></p>
                              <p class="order-buyer">이영희 (010-4444-5555)</p>
                            </td>
                            <td class="col-info">
                              <div class="product-cell">
                                <span class="thumb"></span>
                                <div class="product-text">
                                  <p class="product-name">탐사수 2L x 6개입</p>
                                  <p class="product-qty">수량 1개</p>
                                </div>
                              </div>
                            </td>
                            <td class="col-price">
                              <p class="price">4,800원</p>
                              <p class="delivery-fee">(배송비 0원)</p>
                            </td>
                            <td class="col-status"><span class="status-badge status-preparing">상품 준비중</span></td>
                            <td class="col-status"><span class="status-badge status-waiting">출고 대기</span></td>
                            <td class="col-date">2025-05-19<br><span class="time">11:05:48</span></td>
                            <td class="col-manage">
                              <button class="btn btn-outline btn-sm" type="button">상세보기</button>
                              <button class="btn btn-outline btn-sm btn-more" type="button">
                                작업 <svg class="icon">
                                  <use href="#ic-chevron-down" />
                                </svg>
                              </button>
                            </td>
                          </tr>

                          <tr>
                            <td class="col-check"><input type="checkbox"></td>
                            <td class="col-order">
                              <p class="order-no">주문번호<br><strong>2025051900012342</strong></p>
                              <p class="order-buyer">박민수 (010-6666-7777)</p>
                            </td>
                            <td class="col-info">
                              <div class="product-cell">
                                <span class="thumb"></span>
                                <div class="product-text">
                                  <p class="product-name">컴포트 메쉬 사무용 의자</p>
                                  <p class="product-qty">수량 1개</p>
                                </div>
                              </div>
                            </td>
                            <td class="col-price">
                              <p class="price">89,000원</p>
                              <p class="delivery-fee">(배송비 0원)</p>
                            </td>
                            <td class="col-status"><span class="status-badge status-shipping">배송 중</span></td>
                            <td class="col-status"><span class="status-badge status-shipping">배송 중</span></td>
                            <td class="col-date">2025-05-19<br><span class="time">09:40:33</span></td>
                            <td class="col-manage">
                              <button class="btn btn-outline btn-sm" type="button">상세보기</button>
                              <button class="btn btn-outline btn-sm btn-more" type="button">
                                작업 <svg class="icon">
                                  <use href="#ic-chevron-down" />
                                </svg>
                              </button>
                            </td>
                          </tr>

                          <tr>
                            <td class="col-check"><input type="checkbox"></td>
                            <td class="col-order">
                              <p class="order-no">주문번호<br><strong>2025051900012341</strong></p>
                              <p class="order-buyer">정수진 (010-8888-9999)</p>
                            </td>
                            <td class="col-info">
                              <div class="product-cell">
                                <span class="thumb"></span>
                                <div class="product-text">
                                  <p class="product-name">로지택 무선 마우스 M331</p>
                                  <p class="product-qty">수량 1개</p>
                                </div>
                              </div>
                            </td>
                            <td class="col-price">
                              <p class="price">19,900원</p>
                              <p class="delivery-fee">(배송비 0원)</p>
                            </td>
                            <td class="col-status"><span class="status-badge status-done">배송 완료</span></td>
                            <td class="col-status"><span class="status-badge status-done">배송 완료</span></td>
                            <td class="col-date">2025-05-18<br><span class="time">22:10:17</span></td>
                            <td class="col-manage">
                              <button class="btn btn-outline btn-sm" type="button">상세보기</button>
                              <button class="btn btn-outline btn-sm btn-more" type="button">
                                작업 <svg class="icon">
                                  <use href="#ic-chevron-down" />
                                </svg>
                              </button>
                            </td>
                          </tr>

                        </tbody>
                      </table>
                    </div>

                    <!-- 페이지네이션 -->
                    <nav class="pagination" aria-label="페이지 이동">
                      <button class="page-arrow" type="button" aria-label="이전 페이지">
                        <svg class="icon">
                          <use href="#ic-chevron-left" />
                        </svg>
                      </button>

                      <button class="page-num active" type="button">1</button>
                      <button class="page-num" type="button">2</button>
                      <button class="page-num" type="button">3</button>
                      <button class="page-num" type="button">4</button>
                      <button class="page-num" type="button">5</button>
                      <span class="page-ellipsis">…</span>
                      <button class="page-num" type="button">125</button>

                      <button class="page-arrow" type="button" aria-label="다음 페이지">
                        <svg class="icon rotate-180">
                          <use href="#ic-chevron-left" />
                        </svg>
                      </button>
                    </nav>

                  </section>


                  <!-- 사이드 -->
                  <aside class="side-col">

                    <section class="panel">

                      <div class="panel-head">
                        <h2>배송 현황 <span class="head-sub">(오늘 기준)</span></h2>
                        <a href="#" class="more-link">더보기 <svg class="icon">
                            <use href="#ic-chevron-down" />
                          </svg></a>
                      </div>

                      <div class="donut-wrap">

                        <div class="donut" id="statusDonut"></div>

                        <ul class="donut-legend">
                          <li><i class="dot dot-blue"></i>배송 완료 <b>320건 (68%)</b></li>
                          <li><i class="dot dot-green"></i>배송 중 <b>58건 (12%)</b></li>
                          <li><i class="dot dot-orange"></i>출고 대기 <b>74건 (16%)</b></li>
                          <li><i class="dot dot-gray"></i>기타 <b>12건 (4%)</b></li>
                        </ul>

                      </div>

                    </section>

                    <section class="panel">

                      <div class="panel-head">
                        <h2>공지사항</h2>
                        <a href="#" class="more-link">더보기 <svg class="icon">
                            <use href="#ic-chevron-down" />
                          </svg></a>
                      </div>

                      <ul class="notice-list">
                        <li>
                          <span class="notice-tag tag-notice">공지</span>
                          <span class="notice-title">시스템 점검 안내 (5/25 새벽)</span>
                          <span class="notice-date">05.17</span>
                        </li>
                        <li>
                          <span class="notice-tag tag-notice">공지</span>
                          <span class="notice-title">배송 지역 보상 정책 변경 안내</span>
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
                          <span class="notice-title">정산 주기 변경 안내 (6월~)</span>
                          <span class="notice-date">05.07</span>
                        </li>
                      </ul>

                    </section>

                    <section class="panel">

                      <div class="panel-head">
                        <h2>자주 찾는 메뉴</h2>
                      </div>

                      <div class="quick-menu">
                        <a href="/vendor/product/write" class="quick-menu-item">
                          <span class="quick-icon"><svg class="icon">
                              <use href="#ic-doc-plus" />
                            </svg></span>
                          상품 등록
                        </a>
                        <a href="#" class="quick-menu-item">
                          <span class="quick-icon"><svg class="icon">
                              <use href="#ic-list" />
                            </svg></span>
                          반품 신청 목록
                        </a>
                        <a href="#" class="quick-menu-item">
                          <span class="quick-icon"><svg class="icon">
                              <use href="#ic-calculator" />
                            </svg></span>
                          정산 내역
                        </a>
                        <a href="#" class="quick-menu-item">
                          <span class="quick-icon"><svg class="icon">
                              <use href="#ic-megaphone" />
                            </svg></span>
                          프로모션 관리
                        </a>
                      </div>

                    </section>

                  </aside>

                </div>

              </main>

              </div>


              <script src="js/vendor-common.js"></script>
              <script>

                /* =========================================================
                   주문일 빠른선택 버튼
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
                  document.querySelectorAll(".order-table tbody .col-check input").forEach(
                    (checkbox) => (checkbox.checked = this.checked)
                  );
                });


                /* =========================================================
                   배송 현황 도넛 차트
                ========================================================= */

                (function renderDonut() {
                  const stops = [
                    "#4285f4 0% 68%",
                    "#17c964 68% 80%",
                    "#ff9f1c 80% 96%",
                    "#c9ccd3 96% 100%"
                  ];

                  document.getElementById("statusDonut").style.background =
                    "conic-gradient(" + stops.join(", ") + ")";
                })();

              </script>

  </body>

  </html>