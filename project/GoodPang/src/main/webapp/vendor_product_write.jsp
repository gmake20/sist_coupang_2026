<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!DOCTYPE html>

  <html lang="ko">

  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="./css/vendor_product_write.css">
    <title>굿팡 판매자 상품 등록</title>

  </head>

  <body>

    <%@ include file="/WEB-INF/jspf/vendor/icon-sprite.jspf" %>

    <% String sellerGrade = null; %>
    <%@ include file="/WEB-INF/jspf/vendor/topbar.jspf" %>

    <% String menu = "productWrite"; %>
    <%@ include file="/WEB-INF/jspf/vendor/sidebar.jspf" %>


      <!-- 메인 -->
      <main class="main">

        <form id="productForm" novalidate>

          <div class="write-head">
            <h1 class="page-title">상품 등록 <span class="required-dot">•</span><span class="required-label">필수항목</span>
            </h1>
            <div class="write-head-actions">
              <button class="btn btn-outline" type="button">동영상 가이드</button>
              <button class="btn btn-primary" type="button" id="topSubmitButton">상품등록</button>
            </div>
          </div>


          <!-- 카탈로그 매칭하기 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>카탈로그 매칭하기 <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">
              <div class="info-box">
                쿠팡에서 판매중인 상품 정보를 불러와 손쉽게 상품을 등록할 수 있습니다.<br>
                카탈로그 매칭은 내 상품의 검색 노출수와, 랭킹을 높이는데 보다 더 유리합니다. 상품을 매칭하고 아이템 위너로 만들어 보세요.
              </div>
              <div class="inline-search">
                <input class="input" type="text" placeholder="상품명, 상품 ID, URL, 브랜드명, 쿠팡 상품 번호">
                <button class="btn btn-primary" type="button">
                  <svg class="icon">
                    <use href="#ic-search" />
                  </svg>
                  검색
                </button>
              </div>
            </div>
          </section>


          <!-- 노출상품명 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>노출상품명 <span class="required-dot">•</span></h2>
            </div>
            <div class="block-body">

              <div class="field-row">
                <label class="field-label">브랜드 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <input class="input" id="brandInput" type="text" placeholder="상품에 표시된 브랜드 이름 (예: 나이키, 휠라)">
                  <label class="checkbox-item sub-checkbox">
                    <input type="checkbox" id="noBrandCheck">
                    브랜드 없음(또는 자체제작)
                  </label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">노출상품명 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <div class="input-counter">
                    <input class="input" id="displayNameInput" type="text" maxlength="100"
                      placeholder="상품 모델(해당 시) + 상품 유형 + 핵심 특징">
                    <span class="counter"><span id="displayNameCount">0</span> / 100</span>
                  </div>
                </div>
              </div>

              <button class="collapse-line" type="button" data-target="managedNameBody">
                등록상품명(판매자관리용) <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
              <div class="field-row collapsible" id="managedNameBody" hidden>
                <label class="field-label"></label>
                <div class="field-control">
                  <input class="input" type="text" placeholder="판매자 내부 관리용 상품명 (고객에게 노출되지 않음)">
                </div>
              </div>

            </div>
          </section>


          <!-- 카테고리 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>카테고리 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">

              <div class="tab-group" id="categoryTabs">
                <button class="tab-btn active" type="button" data-tab="search">카테고리 검색</button>
                <button class="tab-btn" type="button" data-tab="select">카테고리 선택</button>
              </div>

              <div class="tab-panel" id="categoryTabSearch">
                <div class="autocomplete">
                  <div class="input-icon-wrap">
                    <svg class="icon">
                      <use href="#ic-search" />
                    </svg>
                    <input class="input" id="categorySearchInput" type="text" placeholder="예) 귤, 백팩, 공기청정기">
                  </div>
                  <ul class="autocomplete-list" id="categorySearchList" hidden></ul>
                </div>
              </div>

              <div class="tab-panel" id="categoryTabSelect" hidden>
                <div class="cascade-select">
                  <select class="input select" id="mainCategorySelect">
                    <option value="">대분류 선택</option>
                  </select>
                  <select class="input select" id="midCategorySelect" disabled>
                    <option value="">중분류 선택</option>
                  </select>
                  <select class="input select" id="subCategorySelect" disabled>
                    <option value="">소분류 선택</option>
                  </select>
                </div>
              </div>

              <p class="selected-category" id="selectedCategory"></p>

            </div>
          </section>


          <!-- 옵션 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>옵션 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">

              <div class="tab-group" id="optionModeTabs">
                <button class="tab-btn active" type="button" data-mode="on">설정함</button>
                <button class="tab-btn" type="button" data-mode="off">설정 안 함</button>
              </div>

              <div id="optionModeOn">

                <div class="field-row">
                  <label class="field-label">사이즈 <span class="help-q">?</span> <span
                      class="required-dot">•</span></label>
                  <div class="field-control">
                    <div class="option-add-row">
                      <input class="input" id="sizeValueInput" type="text" placeholder="옵션값 입력">
                      <button class="btn btn-primary" type="button" id="sizeAddButton">추가</button>
                      <div class="chip-list" id="sizeChipList"></div>
                    </div>
                    <p class="hint">S, Medium, Free, 대, one size 등</p>
                  </div>
                </div>

                <div class="field-row">
                  <label class="field-label">색상 <span class="help-q">?</span> <span
                      class="required-dot">•</span></label>
                  <div class="field-control">
                    <div class="option-add-row">
                      <input class="input" id="colorValueInput" type="text" placeholder="옵션값 입력">
                      <button class="btn btn-primary" type="button" id="colorAddButton">추가</button>
                      <div class="chip-list" id="colorChipList"></div>
                    </div>
                    <p class="hint">화이트, 도트블루, Red, BR01, 그레이, 블랙, 블루, 민트, 레드, 퍼플, 옐로우 등</p>
                  </div>
                </div>

                <a href="#" class="text-link">옵션 구성을 제안하고 싶어요</a>

                <p class="option-count">옵션 목록 (총 <span id="optionCount">0</span>개) <span class="required-dot">•</span>
                </p>

                <div class="table-toolbar">
                  <button class="btn btn-outline btn-sm" type="button" id="optionDeleteButton">삭제</button>
                  <button class="btn btn-outline btn-sm" type="button" id="bulkPriceButton">판매가 일괄입력</button>
                  <button class="btn btn-outline btn-sm" type="button" id="bulkStockButton">재고수량 일괄입력</button>
                </div>

                <div class="table-scroll">
                  <table class="data-table" id="optionTable">
                    <thead>
                      <tr>
                        <th class="col-check"><input type="checkbox" id="optionCheckAll"></th>
                        <th>옵션명</th>
                        <th>정상가(원)</th>
                        <th>판매가(원) <span class="required-dot">•</span></th>
                        <th>판매자 자동가격조정</th>
                        <th>재고수량 <span class="required-dot">•</span></th>
                        <th>판매자상품코드</th>
                        <th>모델번호</th>
                        <th>상품바코드</th>
                      </tr>
                    </thead>
                    <tbody id="optionTableBody">
                      <tr id="optionEmptyRow">
                        <td colspan="9" class="empty-row">데이터가 존재하지 않습니다.</td>
                      </tr>
                    </tbody>
                  </table>
                </div>

              </div>

            </div>
          </section>


          <!-- 상품이미지 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>상품이미지 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="imageBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="imageBlockBody">

              <div class="tab-group" id="imageModeTabs">
                <button class="tab-btn" type="button" data-mode="basic">기본 등록</button>
                <button class="tab-btn active" type="button" data-mode="option">옵션별 등록</button>
              </div>

              <p class="hint">이미지 권장 크기 : 1,000px x 1,000px (최소 500px 이상) / 10MB 이하의 JPG, PNG 파일</p>

              <div class="table-toolbar">
                <button class="btn btn-outline btn-sm" type="button">대표이미지 일괄등록</button>
                <button class="btn btn-outline btn-sm" type="button">추가이미지 일괄등록</button>
                <button class="btn btn-outline btn-sm" type="button">이미지 URL주소로 일괄등록</button>
                <button class="btn btn-outline btn-sm" type="button">추가이미지 일괄삭제</button>
              </div>

              <div class="table-scroll">
                <table class="data-table" id="imageTable">
                  <thead>
                    <tr>
                      <th class="col-check"><input type="checkbox"></th>
                      <th>옵션명</th>
                      <th>대표이미지 <span class="required-dot">•</span></th>
                      <th>추가이미지 (최대 9장)</th>
                      <th>이미지 URL주소로 등록</th>
                    </tr>
                  </thead>
                  <tbody id="imageTableBody"></tbody>
                </table>
              </div>

            </div>
          </section>


          <!-- 상세설명 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>상세설명 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="descBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="descBlockBody">

              <div class="tab-group" id="descModeTabs">
                <button class="tab-btn active" type="button" data-mode="basic">기본 등록</button>
                <button class="tab-btn" type="button" data-mode="option">옵션별 등록</button>
              </div>

              <div class="tab-group" id="descTypeTabs">
                <button class="tab-btn active" type="button" data-type="image">이미지 업로드</button>
                <button class="tab-btn" type="button" data-type="editor">에디터 작성</button>
                <button class="tab-btn" type="button" data-type="html">HTML 작성</button>
              </div>

              <div class="desc-empty" id="descEmpty">
                <svg class="icon empty-icon">
                  <use href="#ic-image" />
                </svg>
                <p>등록된 이미지가 없습니다</p>
                <button class="btn btn-primary" type="button" id="descRegisterButton">이미지 등록</button>
              </div>

              <p class="hint">이미지 권장 크기 : 780px x 5,000px / 10MB 이하의 JPG, PNG 파일</p>

            </div>
          </section>


          <!-- 상품 주요 정보 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>상품 주요 정보 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">

              <div class="field-row">
                <label class="field-label">제조사</label>
                <div class="field-control">
                  <input class="input" type="text" placeholder="제조사를 알 수 없는 경우 브랜드명을 입력해주세요.">
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">상품 구성 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="productComposition" checked>동일한 상품으로 구성됨 <span
                      class="help-q">?</span></label>
                  <label class="radio-item"><input type="radio" name="productComposition">다양한 상품이 혼합되어 구성됨 <span
                      class="help-q">?</span></label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">인증정보 <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="certification">인증·신고 대상</label>
                  <label class="radio-item"><input type="radio" name="certification">상세페이지 별도표기</label>
                  <label class="radio-item"><input type="radio" name="certification" checked>인증·신고 대상 아님</label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">병행수입 <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="parallelImport">병행수입</label>
                  <label class="radio-item"><input type="radio" name="parallelImport" checked>병행수입 아님</label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">미성년자 구매 <span class="required-dot">•</span> <span
                    class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="minorPurchase" checked>가능</label>
                  <label class="radio-item"><input type="radio" name="minorPurchase">불가능</label>
                  <p class="warning-text">
                    ⚠ 상품 등록 후에는 미성년자 구매 '가능'으로 변경할 수 없습니다. 실수로 '불가능'을 선택하신 경우, 상품을 새로 등록해 주세요.
                  </p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">인당 최대구매수량 <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="maxPurchase">설정함</label>
                  <label class="radio-item"><input type="radio" name="maxPurchase" checked>설정안함</label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">판매기간 <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="salePeriod">설정함</label>
                  <label class="radio-item"><input type="radio" name="salePeriod" checked>설정안함</label>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">부가세 <span class="required-dot">•</span> <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="vat" checked>과세</label>
                  <label class="radio-item"><input type="radio" name="vat">면세</label>
                </div>
              </div>

            </div>
          </section>


          <!-- 검색어 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>검색어 <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="tagBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="tagBlockBody">

              <div class="field-row">
                <label class="field-label">태그</label>
                <div class="field-control">
                  <div class="option-add-row">
                    <input class="input" id="tagInput" type="text" placeholder="쉼표(,)로 구분하여 최대 20개까지 입력 가능">
                    <button class="btn btn-primary" type="button" id="tagAddButton">추가</button>
                  </div>
                  <div class="chip-list" id="tagChipList"></div>
                  <p class="hint">검색어는 고객이 내 상품을 빠르게 찾을 수 있게 합니다. 상품과 관계없는 검색어는 삭제/변경 될 수 있습니다.</p>
                </div>
              </div>

            </div>
          </section>



          <!-- 상품정보제공고시 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>상품정보제공고시 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="noticeBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="noticeBlockBody">

              <div class="notice-select-row">
                <select class="input select" id="noticeTypeSelect">
                  <option value="">선택하세요</option>
                  <option value="fashion">의류</option>
                  <option value="kitchen">주방용품</option>
                  <option value="food">가공식품</option>
                  <option value="cosmetic">화장품</option>
                </select>
                <label class="checkbox-item">
                  <input type="checkbox" id="noticeReferAllCheck">
                  전체 상품 상세페이지 참조
                </label>
              </div>

              <div class="info-box">
                각 카테고리에 해당하는 상품 고시정보를 선택하여 입력해주세요.<br>
                ※ 판매 상품에 여러 구성품이 포함되어 있는 경우 모든 구성품에 대해 '상품정보제공고시'를 상품 상세페이지에 제공해주세요.
                <a href="#">자세히보기</a>
              </div>

              <table class="notice-table">
                <thead>
                  <tr>
                    <th>고시정보명</th>
                    <th>내용</th>
                    <th class="col-refer">상품 상세페이지 참조</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>제품 소재</td>
                    <td><textarea class="input textarea"
                        placeholder="섬유의 조성 또는 혼용율을 백분율로 표시, 충전재를 사용한 제품은 충전재를 함께 표기&#10;예) 면 50%, 폴리에스터 50% (혼용율 100%가 되도록 기재)"></textarea>
                    </td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>색상</td>
                    <td><textarea class="input textarea" placeholder="직접 입력해주세요"></textarea></td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>치수</td>
                    <td><textarea class="input textarea"
                        placeholder="예) 이불 : 400x2000mm / 패드 : 400x2200mm (mm, cm 단위로 기재해주세요)"></textarea></td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>제품구성</td>
                    <td><textarea class="input textarea" placeholder="예) 이불 + 패드"></textarea></td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>제조자(수입자)</td>
                    <td><textarea class="input textarea" placeholder="예) CPLB/쿠팡 (수입상품의 경우 수입자도 함께 기재해주세요)"></textarea>
                    </td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>제조국</td>
                    <td><textarea class="input textarea" placeholder="한글로 기재해주세요 (2개 이상의 국가일 경우 모두 기재해주세요)"></textarea>
                    </td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>세탁방법 및 취급시 주의사항</td>
                    <td><textarea class="input textarea" placeholder="예) 손세탁, 드라이클리닝 등"></textarea></td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>품질보증기준</td>
                    <td><textarea class="input textarea" placeholder="예) 관련 법령 및 소비자분쟁해결기준에 따름"></textarea></td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                  <tr>
                    <td>A/S 책임자와 전화번호</td>
                    <td><textarea class="input textarea" placeholder="예) 판매자 고객센터 1600-0000"></textarea></td>
                    <td class="col-refer"><input type="checkbox" class="notice-refer"></td>
                  </tr>
                </tbody>
              </table>

            </div>
          </section>


          <!-- 구비서류 -->
          <section class="panel form-block collapsed" id="docsBlock">
            <div class="block-head">
              <h2>구비서류 <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="docsBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="docsBlockBody" hidden>
              <p class="hint">선택하신 카테고리에 별도로 필요한 구비서류가 없습니다.</p>
            </div>
          </section>


          <!-- 배송 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>배송 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
              <button class="collapse-toggle" type="button" data-target="shippingBlockBody">
                <svg class="icon chevron">
                  <use href="#ic-chevron-down" />
                </svg>
              </button>
            </div>
            <div class="block-body" id="shippingBlockBody">

              <div class="field-row">
                <label class="field-label">출고지 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <button class="address-box" type="button">주소록에서 출고지를 선택해주세요 &gt;</button>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">제주/도서산간 배송여부 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="jejuShipping" checked>가능</label>
                  <label class="radio-item"><input type="radio" name="jejuShipping">불가능</label>
                  <p class="side-note">주소록/배송관리 메뉴에서 택배사와 도서산간 추가배송비를 설정할 수 있습니다.</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">택배사 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <select class="input select" id="courierSelect">
                    <option value="">선택하세요</option>
                    <option>롯데택배 (쿠팡 제휴 택배사)</option>
                    <option>한진택배 (쿠팡 제휴 택배사)</option>
                    <option selected>로젠택배</option>
                    <option>우체국</option>
                    <option>CJ대한통운</option>
                    <option>경동택배</option>
                    <option>일양택배</option>
                    <option>천일택배</option>
                    <option>대신택배</option>
                  </select>
                  <p class="side-note">제주/도서산간지역 배송 시 출고지에 등록된 택배사만 선택할 수 있습니다.</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">배송방법 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <select class="input select">
                    <option value="">선택하세요</option>
                    <option selected>일반배송</option>
                    <option>신선냉동</option>
                    <option>주문제작</option>
                    <option>구매대행</option>
                    <option>설치배송 또는 판매자 직접전달</option>
                  </select>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">묶음배송 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="bundleShipping" checked>가능</label>
                  <label class="radio-item"><input type="radio" name="bundleShipping">불가능</label>
                  <p class="side-note">출고 정보가 같은 상품만 묶음배송할 수 있습니다. (착불배송 선택 불가)</p>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">배송비 종류 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <select class="input select">
                    <option selected>무료배송</option>
                    <option>유료배송</option>
                    <option>조건부무료배송</option>
                    <option>9,800원 이상 무료배송</option>
                    <option>19,800원 이상 무료배송</option>
                    <option>30,000원 이상 무료배송</option>
                    <option>착불배송</option>
                  </select>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">출고 소요일 <span class="help-q">?</span></label>
                <div class="field-control">
                  <label class="radio-item"><input type="radio" name="leadTime" checked>기본 입력</label>
                  <label class="radio-item"><input type="radio" name="leadTime">구매 옵션별로 입력</label>
                </div>
              </div>

              <div class="field-row" id="dayLeadTimeRow">
                <label class="field-label"></label>
                <div class="field-control lead-time-control">
                  <input class="input lead-time-input" type="number" value="1" min="0">
                  <span>일</span>
                  <label class="checkbox-item">
                    <input type="checkbox" id="sameDayShipCheck" checked>
                    당일출고
                  </label>
                </div>
              </div>

              <div class="field-row" id="sameDayCutoffRow">
                <label class="field-label">당일출고 마감</label>
                <div class="field-control">
                  <select class="input select cutoff-select">
                    <option>10:00</option>
                    <option>11:00</option>
                    <option selected>12:00</option>
                    <option>13:00</option>
                    <option>14:00</option>
                    <option>15:00</option>
                    <option>16:00</option>
                    <option>17:00</option>
                    <option>18:00</option>
                  </select>
                  <p class="side-note">당일출고가 지켜지지 않는 경우, 판매자 점수 하락 등 페널티가 발생할 수 있습니다.</p>
                </div>
              </div>

            </div>
          </section>


          <!-- 반품/교환 -->
          <section class="panel form-block">
            <div class="block-head">
              <h2>반품/교환 <span class="required-dot">•</span> <a href="#" class="help-link">도움말</a></h2>
            </div>
            <div class="block-body">

              <div class="field-row">
                <label class="field-label">반품/교환지 <span class="required-dot">•</span></label>
                <div class="field-control">
                  <button class="address-box" type="button">주소록에서 반품/교환지를 선택해주세요 &gt;</button>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">초도배송비(편도) <span class="required-dot">•</span></label>
                <div class="field-control">
                  <div class="unit-input">
                    <input class="input" id="initialShippingFee" type="number" value="3000">
                    <span>원</span>
                  </div>
                </div>
              </div>

              <div class="field-row">
                <label class="field-label">반품배송비(편도) <span class="required-dot">•</span></label>
                <div class="field-control">
                  <div class="unit-input">
                    <input class="input" id="returnShippingFee" type="number" value="3000">
                    <span>원</span>
                  </div>
                  <p class="side-note">
                    고객사유로 인한 반품 시, 왕복 반품/배송비는 초도배송비 + 반품배송비의 합계인
                    <strong id="totalReturnFee">6,000</strong>원이 청구됩니다.
                  </p>
                </div>
              </div>

            </div>
          </section>


          <!-- 하단 버튼 -->
          <div class="write-foot">
            <button class="btn btn-outline" type="button">취소</button>
            <button class="btn btn-outline" type="button">미리보기</button>
            <button class="btn btn-outline" type="button">중간저장</button>
            <button class="btn btn-outline" type="button">임시저장</button>
            <button class="btn btn-primary btn-lg" type="submit">판매요청</button>
          </div>

        </form>

      </main>

    </div>


    <!-- 상세설명 이미지 등록 모달 -->
    <div class="modal-backdrop" id="descModalBackdrop">
      <div class="modal modal-lg">

        <div class="modal-head">
          <h2>이미지 등록</h2>
          <button class="icon-button" type="button" id="descModalClose">
            <svg class="icon">
              <use href="#ic-close" />
            </svg>
          </button>
        </div>

        <div class="modal-body">

          <div class="tab-group" id="descModalTabs">
            <button class="tab-btn active" type="button" data-type="image">이미지 추가</button>
            <button class="tab-btn" type="button" data-type="html">텍스트(HTML) 추가</button>
          </div>

          <p class="hint">컨텐츠의 순서를 바꾸시려면 왼쪽의 ⁝⁝ 를 원하는 위치로 끌어다 놓으세요.(드래그 앤 드롭)</p>

          <div class="modal-toolbar">
            <button class="btn btn-outline btn-sm" type="button">전체 다운로드</button>
            <button class="btn btn-outline btn-sm" type="button" id="descClearAllButton">전체 삭제</button>
            <button class="btn btn-outline btn-sm" type="button">실제크기</button>
            <button class="btn btn-outline btn-sm active" type="button">축소크기</button>
          </div>

          <div class="desc-image-list" id="descImageList"></div>

          <button class="add-image-slot" type="button" id="descAddImageButton">
            <svg class="icon">
              <use href="#ic-plus" />
            </svg>
            이미지 추가
          </button>

        </div>

        <div class="modal-foot">
          <button class="btn btn-outline" type="button" id="descModalCancel">취소</button>
          <button class="btn btn-primary" type="button" id="descModalSave">저장</button>
        </div>

      </div>
    </div>


    <script src="js/vendor-common.js"></script>
    <script>

      /* =========================================================
         섹션 접기/펼치기 (우측 상단 ^ 버튼)
         (사이드바 아코디언·사이드바 접기·사용자 메뉴는 vendor-common.js가 처리)
      ========================================================= */

      document.querySelectorAll(".collapse-toggle").forEach(function (button) {
        button.addEventListener("click", function () {
          const body = document.getElementById(button.dataset.target);
          const block = button.closest(".form-block");
          const willHide = !body.hidden;

          body.hidden = willHide;
          block.classList.toggle("collapsed", willHide);
        });
      });

      document.querySelectorAll(".collapse-line").forEach(function (button) {
        button.addEventListener("click", function () {
          const body = document.getElementById(button.dataset.target);
          body.hidden = !body.hidden;
          button.classList.toggle("open", !body.hidden);
        });
      });


      /* =========================================================
         탭 공통 (data-tab / data-mode / data-type 버튼 그룹)
      ========================================================= */

      function setupTabGroup(groupId, onChange) {
        const group = document.getElementById(groupId);
        if (!group) return;

        group.addEventListener("click", function (event) {
          const button = event.target.closest(".tab-btn");
          if (!button) return;

          group.querySelectorAll(".tab-btn").forEach((btn) => btn.classList.remove("active"));
          button.classList.add("active");

          if (onChange) onChange(button);
        });
      }

      setupTabGroup("categoryTabs", function (button) {
        const isSearch = button.dataset.tab === "search";
        document.getElementById("categoryTabSearch").hidden = !isSearch;
        document.getElementById("categoryTabSelect").hidden = isSearch;
      });

      setupTabGroup("optionModeTabs", function (button) {
        document.getElementById("optionModeOn").hidden = button.dataset.mode !== "on";
      });

      setupTabGroup("imageModeTabs");
      setupTabGroup("descModeTabs");
      setupTabGroup("descTypeTabs");
      setupTabGroup("descModalTabs");


      /* =========================================================
         노출상품명 글자수
      ========================================================= */

      const displayNameInput = document.getElementById("displayNameInput");
      const displayNameCount = document.getElementById("displayNameCount");

      displayNameInput.addEventListener("input", function () {
        displayNameCount.textContent = displayNameInput.value.length;
      });


      /* =========================================================
         브랜드 없음 체크 시 브랜드 입력 비활성화
      ========================================================= */

      const brandInput = document.getElementById("brandInput");
      const noBrandCheck = document.getElementById("noBrandCheck");

      noBrandCheck.addEventListener("change", function () {
        brandInput.disabled = noBrandCheck.checked;
        if (noBrandCheck.checked) brandInput.value = "";
      });


      /* =========================================================
         카테고리 — 검색 / 선택 (이전에 만든 CATEGORY 재귀 테이블 데모 데이터)
      ========================================================= */

      const categoryTree = {
        "식품": {
          "신선식품": {
            "과일류": ["과일"]
          }
        },
        "가전디지털": {
          "계절가전": ["공기청정기"]
        },
        "패션의류/잡화": {
          "남성패션": {
            "가방/잡화": ["가방"]
          }
        }
      };

      const categoryPaths = [
        "식품>신선식품>과일류>과일>귤",
        "식품>신선식품>과일류>과일>사과",
        "식품>생수/음료>음료>과일/야채음료>감귤/한라봉주스",
        "가전디지털>계절가전>공기청정기",
        "패션의류/잡화>남성패션>가방/잡화>가방>백팩",
        "패션의류/잡화>여성패션>가방/잡화>가방>토트백",
        "뷰티>향수>액체향수>여성향수>오 드 뚜왈렛",
        "뷰티>향수>액체향수>남성향수>오 드 뚜왈렛",
        "주방용품>취사도구>부자재/패킹>냄비뚜껑/멀티커버"
      ];

      const categorySearchInput = document.getElementById("categorySearchInput");
      const categorySearchList = document.getElementById("categorySearchList");
      const selectedCategory = document.getElementById("selectedCategory");

      function selectCategory(path) {
        selectedCategory.textContent = "선택된 카테고리 : " + path;
        categorySearchList.hidden = true;
        categorySearchInput.value = path.split(">").pop();
      }

      categorySearchInput.addEventListener("input", function () {
        const keyword = categorySearchInput.value.trim();

        if (!keyword) {
          categorySearchList.hidden = true;
          categorySearchList.innerHTML = "";
          return;
        }

        const matches = categoryPaths.filter((path) => path.includes(keyword));

        if (matches.length === 0) {
          categorySearchList.hidden = true;
          categorySearchList.innerHTML = "";
          return;
        }

        categorySearchList.innerHTML = matches
          .map((path) => "<li>" + path.replace(/>/g, "&gt;") + "</li>")
          .join("");
        categorySearchList.hidden = false;
      });

      categorySearchList.addEventListener("click", function (event) {
        const item = event.target.closest("li");
        if (!item) return;
        selectCategory(item.textContent.replace(/&gt;/g, ">"));
      });


      const mainCategorySelect = document.getElementById("mainCategorySelect");
      const midCategorySelect = document.getElementById("midCategorySelect");
      const subCategorySelect = document.getElementById("subCategorySelect");

      Object.keys(categoryTree).forEach(function (mainName) {
        const option = document.createElement("option");
        option.value = mainName;
        option.textContent = mainName;
        mainCategorySelect.appendChild(option);
      });

      function resetSelect(select, placeholder) {
        select.innerHTML = "<option value=''>" + placeholder + "</option>";
        select.disabled = true;
      }

      mainCategorySelect.addEventListener("change", function () {
        resetSelect(midCategorySelect, "중분류 선택");
        resetSelect(subCategorySelect, "소분류 선택");

        const midTree = categoryTree[mainCategorySelect.value];
        if (!midTree) return;

        Object.keys(midTree).forEach(function (midName) {
          const option = document.createElement("option");
          option.value = midName;
          option.textContent = midName;
          midCategorySelect.appendChild(option);
        });
        midCategorySelect.disabled = false;
      });

      midCategorySelect.addEventListener("change", function () {
        resetSelect(subCategorySelect, "소분류 선택");

        const midTree = categoryTree[mainCategorySelect.value];
        const subList = midTree && midTree[midCategorySelect.value];
        if (!subList) return;

        subList.forEach(function (subName) {
          const option = document.createElement("option");
          option.value = subName;
          option.textContent = subName;
          subCategorySelect.appendChild(option);
        });
        subCategorySelect.disabled = false;
      });

      subCategorySelect.addEventListener("change", function () {
        if (!subCategorySelect.value) return;
        selectCategory(
          [mainCategorySelect.value, midCategorySelect.value, subCategorySelect.value].join(">")
        );
      });


      /* =========================================================
         옵션 — 사이즈/색상 값 추가 → 조합으로 옵션 목록 테이블 생성
      ========================================================= */

      const sizeValues = [];
      const colorValues = [];

      function renderChips(listElement, values, onRemove) {
        listElement.innerHTML = values
          .map(
            (value, index) =>
              '<span class="chip" data-index="' + index + '">' + value +
              '<button type="button" class="chip-remove">&times;</button></span>'
          )
          .join("");

        listElement.querySelectorAll(".chip-remove").forEach(function (button) {
          button.addEventListener("click", function () {
            const index = Number(button.parentElement.dataset.index);
            onRemove(index);
          });
        });
      }

      function renderOptionTable() {
        const tbody = document.getElementById("optionTableBody");
        const emptyRow = document.getElementById("optionEmptyRow");

        const combinations = [];
        if (sizeValues.length && colorValues.length) {
          colorValues.forEach((color) => {
            sizeValues.forEach((size) => {
              combinations.push(color + ", " + size);
            });
          });
        } else if (sizeValues.length) {
          sizeValues.forEach((size) => combinations.push(size));
        } else if (colorValues.length) {
          colorValues.forEach((color) => combinations.push(color));
        }

        document.getElementById("optionCount").textContent = combinations.length;
        document.getElementById("filterOptionCount").textContent = combinations.length;

        if (combinations.length === 0) {
          tbody.innerHTML = "";
          tbody.appendChild(emptyRow);
          renderFilterTable(combinations);
          renderImageTable(combinations);
          return;
        }

        tbody.innerHTML = combinations
          .map(
            (label) =>
              "<tr>" +
              '<td class="col-check"><input type="checkbox"></td>' +
              "<td>" + label + "</td>" +
              '<td><input class="input input-sm" type="number" value="0"></td>' +
              '<td><input class="input input-sm" type="number" value="0"></td>' +
              '<td class="center"><label class="switch"><input type="checkbox"><span class="slider"></span></label></td>' +
              '<td><input class="input input-sm" type="number" value="0"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              "</tr>"
          )
          .join("");

        renderFilterTable(combinations);
        renderImageTable(combinations);
      }

      function renderFilterTable(combinations) {
        const tbody = document.getElementById("filterTableBody");
        tbody.innerHTML = combinations
          .map(
            (label) =>
              "<tr><td>" + label + "</td>" +
              '<td><input class="input input-sm" type="text"></td>' +
              '<td><input class="input input-sm" type="text"></td>' +
              '<td><input class="input input-sm" type="text"></td></tr>'
          )
          .join("");
      }

      function renderImageTable(combinations) {
        const tbody = document.getElementById("imageTableBody");
        tbody.innerHTML = combinations
          .map(
            (label) =>
              "<tr>" +
              '<td class="col-check"><input type="checkbox"></td>' +
              "<td>" + label + "</td>" +
              '<td><button type="button" class="image-slot main-slot"><svg class="icon"><use href="#ic-plus"/></svg></button></td>' +
              '<td><button type="button" class="image-slot"><svg class="icon"><use href="#ic-plus"/></svg></button></td>' +
              '<td><button type="button" class="btn btn-outline btn-xs">이미지 URL 입력</button></td>' +
              "</tr>"
          )
          .join("");

        tbody.querySelectorAll(".image-slot").forEach(function (slot) {
          slot.addEventListener("click", function () {
            slot.classList.toggle("filled");
          });
        });
      }

      function handleSizeRemove(index) {
        sizeValues.splice(index, 1);
        renderChips(document.getElementById("sizeChipList"), sizeValues, handleSizeRemove);
        renderOptionTable();
      }

      function handleColorRemove(index) {
        colorValues.splice(index, 1);
        renderChips(document.getElementById("colorChipList"), colorValues, handleColorRemove);
        renderOptionTable();
      }

      document.getElementById("sizeAddButton").addEventListener("click", function () {
        const input = document.getElementById("sizeValueInput");
        const value = input.value.trim();
        if (!value || sizeValues.includes(value)) return;
        sizeValues.push(value);
        input.value = "";
        renderChips(document.getElementById("sizeChipList"), sizeValues, handleSizeRemove);
        renderOptionTable();
      });

      document.getElementById("colorAddButton").addEventListener("click", function () {
        const input = document.getElementById("colorValueInput");
        const value = input.value.trim();
        if (!value || colorValues.includes(value)) return;
        colorValues.push(value);
        input.value = "";
        renderChips(document.getElementById("colorChipList"), colorValues, handleColorRemove);
        renderOptionTable();
      });

      document.getElementById("optionDeleteButton").addEventListener("click", function () {
        document.querySelectorAll("#optionTableBody tr").forEach(function (row) {
          const checkbox = row.querySelector(".col-check input");
          if (checkbox && checkbox.checked) row.remove();
        });
      });

      document.getElementById("bulkPriceButton").addEventListener("click", function () {
        const value = prompt("일괄 적용할 판매가를 입력하세요.", "10000");
        if (value === null) return;
        document.querySelectorAll("#optionTableBody td:nth-child(4) input").forEach(function (input) {
          input.value = value;
        });
      });

      document.getElementById("bulkStockButton").addEventListener("click", function () {
        const value = prompt("일괄 적용할 재고수량을 입력하세요.", "100");
        if (value === null) return;
        document.querySelectorAll("#optionTableBody td:nth-child(6) input").forEach(function (input) {
          input.value = value;
        });
      });

      document.getElementById("optionCheckAll").addEventListener("change", function () {
        document.querySelectorAll("#optionTableBody .col-check input").forEach(
          (checkbox) => (checkbox.checked = this.checked)
        );
      });


      /* =========================================================
         검색어 태그
      ========================================================= */

      const tagValues = [];

      function handleTagRemove(index) {
        tagValues.splice(index, 1);
        renderChips(document.getElementById("tagChipList"), tagValues, handleTagRemove);
      }

      document.getElementById("tagAddButton").addEventListener("click", function () {
        const input = document.getElementById("tagInput");
        const rawValues = input.value.split(",").map((value) => value.trim()).filter(Boolean);

        rawValues.forEach(function (value) {
          if (tagValues.length < 20 && !tagValues.includes(value)) tagValues.push(value);
        });

        input.value = "";
        renderChips(document.getElementById("tagChipList"), tagValues, handleTagRemove);
      });


      /* =========================================================
         상품정보제공고시 — 참조 체크 시 textarea 비활성화
      ========================================================= */

      document.querySelectorAll(".notice-refer").forEach(function (checkbox) {
        checkbox.addEventListener("change", function () {
          const textarea = checkbox.closest("tr").querySelector("textarea");
          textarea.disabled = checkbox.checked;
        });
      });

      document.getElementById("noticeReferAllCheck").addEventListener("change", function () {
        const checked = this.checked;
        document.querySelectorAll(".notice-refer").forEach(function (checkbox) {
          checkbox.checked = checked;
          checkbox.dispatchEvent(new Event("change"));
        });
      });


      /* =========================================================
         배송 — 출고 소요일 / 당일출고
      ========================================================= */

      document.getElementById("sameDayShipCheck").addEventListener("change", function () {
        document.getElementById("sameDayCutoffRow").hidden = !this.checked;
      });


      /* =========================================================
         반품/교환 — 왕복 배송비 자동 계산
      ========================================================= */

      function updateTotalReturnFee() {
        const initial = Number(document.getElementById("initialShippingFee").value) || 0;
        const returnFee = Number(document.getElementById("returnShippingFee").value) || 0;
        document.getElementById("totalReturnFee").textContent = (initial + returnFee).toLocaleString();
      }

      document.getElementById("initialShippingFee").addEventListener("input", updateTotalReturnFee);
      document.getElementById("returnShippingFee").addEventListener("input", updateTotalReturnFee);


      /* =========================================================
         상세설명 이미지 등록 모달
      ========================================================= */

      const descImages = [];
      const descModalBackdrop = document.getElementById("descModalBackdrop");

      function renderDescImageList() {
        const list = document.getElementById("descImageList");

        if (descImages.length === 0) {
          list.innerHTML = "";
          return;
        }

        list.innerHTML = descImages
          .map(
            (_, index) =>
              '<div class="desc-image-row" data-index="' + index + '">' +
              '<span class="drag-handle">⁝⁝</span>' +
              '<div class="desc-image-thumb"><svg class="icon"><use href="#ic-image"/></svg></div>' +
              '<div class="desc-image-actions">' +
              '<button type="button" class="btn btn-outline btn-xs desc-edit">수정</button>' +
              '<button type="button" class="btn btn-outline btn-xs desc-remove">삭제</button>' +
              "</div></div>"
          )
          .join("");

        list.querySelectorAll(".desc-remove").forEach(function (button) {
          button.addEventListener("click", function () {
            const index = Number(button.closest(".desc-image-row").dataset.index);
            descImages.splice(index, 1);
            renderDescImageList();
          });
        });
      }

      document.getElementById("descRegisterButton").addEventListener("click", function () {
        descModalBackdrop.classList.add("show");
      });

      document.getElementById("descAddImageButton").addEventListener("click", function () {
        descImages.push({});
        renderDescImageList();
      });

      document.getElementById("descClearAllButton").addEventListener("click", function () {
        descImages.length = 0;
        renderDescImageList();
      });

      function closeDescModal() {
        descModalBackdrop.classList.remove("show");
      }

      document.getElementById("descModalClose").addEventListener("click", closeDescModal);
      document.getElementById("descModalCancel").addEventListener("click", closeDescModal);

      document.getElementById("descModalSave").addEventListener("click", function () {
        document.getElementById("descEmpty").hidden = descImages.length > 0;
        closeDescModal();
      });

      descModalBackdrop.addEventListener("click", function (event) {
        if (event.target === descModalBackdrop) closeDescModal();
      });


      /* =========================================================
         폼 제출 — 데모용. 실제 서버 전송은 하지 않음
      ========================================================= */

      function submitProductForm(event) {
        if (event) event.preventDefault();
        alert("데모 화면입니다. 실제 상품 등록 요청은 전송되지 않습니다.");
      }

      document.getElementById("productForm").addEventListener("submit", submitProductForm);
      document.getElementById("topSubmitButton").addEventListener("click", submitProductForm);

    </script>

  </body>

  </html>