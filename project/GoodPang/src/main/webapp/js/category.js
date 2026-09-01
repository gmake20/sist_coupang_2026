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
