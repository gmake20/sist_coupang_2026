// 카테고리 목록 페이지(category_list.jsp) 전용 JS.
//
// 정렬 / 페이지네이션 / 카테고리·평점·가격·색상 필터는 전부 페이지 전체를 다시 요청하는 방식임
// (원본 coupang.com 도 실제로 이렇게 동작함 — SPA 부분갱신 아님, ref/category/STRUCTURE.md 10장 참고).
// 색상은 체크박스를 바꾸면 그 폼(#colorFilterForm)이 자동 제출되는 것까지 JSP 인라인 onchange 로 처리함.
//
// 2026-09-01: 브랜드/핏/소재 등 DB 연동이 없는 장식용 체크박스(name 없는 <input type="checkbox">)는
// 서버가 전혀 모르는 상태라, "선택한 필터"에 안 잡히고 "전체해제" 버튼도 안 뜨던 버그가 있었음
// (버그 리포트 1번). 여기서 그 상태를 직접 관리함 — 체크될 때마다 "선택한 필터" 줄에 칩을 추가하고,
// 전체해제 버튼도 보이게 함. 새로고침하면 그대로 리셋되는 건 의도한 동작(DB에 없는 필터라 정직하게 둠).
document.addEventListener('DOMContentLoaded', function () {
	var clearAllBtn = document.getElementById('filterClearAll');
	var selectedBar = document.getElementById('filterSelectedBar');
	var chipList = document.getElementById('filterSelectedChips');

	if (!selectedBar || !chipList) return;

	// beforeColorGroups/afterColorGroups(is-inert 섹션) + 맨 위 로켓/무료배송 줄, 전부 장식용
	var decoBoxes = document.querySelectorAll(
		'.category-filter .is-inert input[type="checkbox"], .filter-top-row input[type="checkbox"]'
	);

	decoBoxes.forEach(function (box) {
		box.addEventListener('change', function () {
			syncDecoChip(box);
			syncClearAllVisibility();
		});
	});

	function syncDecoChip(box) {
		var existing = chipList.querySelector('[data-deco-for="' + box.dataset.decoId + '"]');

		if (box.checked && !existing) {
			var li = document.createElement('li');
			li.setAttribute('data-deco-for', box.dataset.decoId);

			var a = document.createElement('a');
			a.href = '#';
			a.textContent = box.dataset.label;

			var remove = document.createElement('span');
			remove.className = 'remove';
			remove.textContent = '삭제';
			a.appendChild(remove);

			a.addEventListener('click', function (e) {
				e.preventDefault();
				box.checked = false;
				syncDecoChip(box);
				syncClearAllVisibility();
			});

			li.appendChild(a);
			chipList.appendChild(li);
		} else if (!box.checked && existing) {
			existing.remove();
		}
	}

	function syncClearAllVisibility() {
		var anyDecoChecked = Array.prototype.some.call(decoBoxes, function (b) { return b.checked; });
		var hasServerFilter = selectedBar.dataset.hasServerFilter === 'true';
		var show = anyDecoChecked || hasServerFilter;

		selectedBar.hidden = !show;
		if (clearAllBtn) clearAllBtn.hidden = !show;
	}
});

// 더보기/접기 — 2026-09-03 추가. 위 블록(선택한 필터 칩)과는 별개 기능이라 DOMContentLoaded 리스너를
// 따로 둠 — selectedBar/chipList 가 없어서 위 블록이 일찍 return 해도 이건 항상 실행되게.
//
// coupang.com 을 Playwright 로 직접 열어서 확인한 실제 동작(원본은 URL 안 바뀌는 순수 클라이언트 토글):
// 체크박스 필터 그룹(색상/소재/네크라인/패턴·프린트/제조년도 등)은 처음엔 5개까지만 보이고,
// "+ 더보기" 를 누르면 나머지가 펼쳐지며 버튼 글자가 "- 접기"로 바뀜.
// 카테고리/평점/가격처럼 <a> 링크만 있는 목록은 원본도 접지 않으므로, "체크박스가 들어있는 ul"만 골라서 처리함
// (li 개수가 아니라 checkbox 유무로 판별 — 카테고리 ul은 12개나 되지만 원본이 안 접기 때문).
document.addEventListener('DOMContentLoaded', function () {
	var FOLD_LIMIT = 5;
	var lists = document.querySelectorAll('.category-filter .filter-group ul');

	lists.forEach(function (ul) {
		if (!ul.querySelector('input[type="checkbox"]')) return;

		var items = Array.prototype.slice.call(ul.children);
		if (items.length <= FOLD_LIMIT) return;

		var hiddenItems = items.slice(FOLD_LIMIT);
		hiddenItems.forEach(function (li) { li.hidden = true; });

		var toggleLi = document.createElement('li');
		toggleLi.className = 'filter-fold-toggle';

		var toggleBtn = document.createElement('a');
		toggleBtn.href = '#';
		toggleBtn.textContent = '+ 더보기';
		toggleLi.appendChild(toggleBtn);
		ul.appendChild(toggleLi);

		var folded = true;
		toggleBtn.addEventListener('click', function (e) {
			e.preventDefault();
			folded = !folded;
			hiddenItems.forEach(function (li) { li.hidden = folded; });
			toggleBtn.textContent = folded ? '+ 더보기' : '- 접기';
		});
	});
});
