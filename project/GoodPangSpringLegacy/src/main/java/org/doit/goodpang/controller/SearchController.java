package org.doit.goodpang.controller;

import org.doit.goodpang.domain.SearchDTO;
import org.doit.goodpang.service.CategoryService;
import org.doit.goodpang.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.HtmlUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/*
 * 검색 결과 페이지 — 구버전 SearchServlet(/search?keyword=...) 을 옮김.
 * 헤더 검색창(header.jsp)이 이미 /search?keyword=... 로 보내고 있어서 주소는 그대로.
 *
 * 구버전은 request.getParameter + parseIntOrDefault 로 값을 하나씩 꺼냈지만,
 * 여기선 스프링이 주소 파라미터를 SearchDTO 필드에 이름대로 채워줌 (CategoryListController 와 같은 방식).
 */
@Controller
@Log4j
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;
	private final CategoryService categoryService;	// 페이지 수 / 배송문구 계산 재사용 (카테고리 목록과 같은 카드)

	@GetMapping("/search")
	public String search(@ModelAttribute("search") SearchDTO search, BindingResult bindingResult, Model model) {

		// page 자리에 글자가 오면 그 값만 버리고 기본값(1) 유지 — 구버전 parseIntOrDefault 와 같음
		if (bindingResult.hasErrors()) {
			log.debug("검색 파라미터 일부 무시: " + bindingResult.getFieldErrors());
		}
		search.normalize();

		int totalCount = searchService.countProducts(search);

		model.addAttribute("keyword", search.getKeyword());
		model.addAttribute("products", searchService.findProducts(search));
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPages", categoryService.calcTotalPages(totalCount, SearchDTO.LIST_SIZE));
		model.addAttribute("page", search.getPage());
		model.addAttribute("sort", search.getSort());
		model.addAttribute("deliveryDate", categoryService.calcDeliveryDate());

		// ── 레이아웃(layout_shop.jsp)용 ──
		// layout_shop.jsp 는 pageTitle 을 그대로 출력하므로 사용자가 입력한 검색어는 여기서 HTML 이스케이프
		String keyword = search.hasKeyword() ? search.getKeyword() : "";
		model.addAttribute("pageTitle", "'" + HtmlUtils.htmlEscape(keyword, "UTF-8") +"' 검색결과 | 굿팡");	// 구버전 search_list.jsp 15줄
		model.addAttribute("bodyClass", "page-category");											// 구버전 search_list.jsp 27줄

		return "search.list";
	}
}
