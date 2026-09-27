package org.doit.goodpang.controller;

import java.util.List;

import org.doit.goodpang.domain.CategoryDTO;
import org.doit.goodpang.domain.CategoryProductDTO;
import org.doit.goodpang.domain.CategorySearchDTO;
import org.doit.goodpang.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;


/*
 * 카테고리 목록 페이지(PLP) — 구버전 command/CategoryHandler 를 옮김.
 *
 * 구버전은 request.getParameter + parseIntOrDefault 로 값을 하나씩 꺼냈지만,
 * 여기선 스프링이 주소 파라미터를 CategorySearchDTO 필드에 이름대로 채워줌(@ModelAttribute).
 */

@Controller
@Log4j
@RequiredArgsConstructor
public class CategoryListController {
	
	private final CategoryService categoryService;
	
	@GetMapping("/category/{categoryNo:\\d+}") // 경로에 숫자만 받기
	public String listByPath(
							@PathVariable("categoryNo") int categoryNo
							,@ModelAttribute("search") CategorySearchDTO search
							, BindingResult bindingResult
							, Model model) {
		search.setCategoryNo(categoryNo);
		return list(search, bindingResult, model);
	}//listByPath
	
	/**
     * ② 옛 주소 — /category?categoryNo=10301
     * ★ 아직 필요함: header.js 5곳(andy) / product.jsp 3곳 / category_list.jsp 6곳이 이 주소를 씀.
     *   전부 경로형으로 바뀌면 이 메서드만 지우면 됨.
     */
    @GetMapping(value = "/category", params = "categoryNo")
    public String listByParam(
                    @ModelAttribute("search") CategorySearchDTO search,
                    BindingResult bindingResult,
                    Model model) {

            return list(search, bindingResult, model);
    } //listByParam
    
    /** ①②가 공통으로 쓰는 본체 */
    private String list(CategorySearchDTO search, BindingResult bindingResult, Model model) {

            // 숫자 자리에 글자나 빈칸(maxPrice= 등)이 오면 그 값만 버리고 기본값 유지 — 구버전 parseIntOrDefault 와 같은
            if (bindingResult.hasErrors()) {
                    log.debug("카테고리 파라미터 일부 무시: " + bindingResult.getFieldErrors());
            }
            search.normalize();

            int categoryNo = search.getCategoryNo();
            CategoryDTO current = categoryService.findCategory(categoryNo);

            // 없는 카테고리 번호 → 메인으로 (구버전은 빈 목록을 그렸음. 2026-09-26 변경)
            if (current == null) {
                    return "redirect:/";
            }

            boolean isMidCategory = categoryService.isMidCategory(current);
            boolean isTopCategory = categoryService.isTopCategory(current);
            // 배너 이미지가 패션 전용 캡처라 "레벨"과 "배너 자료 유무"를 따로 판정 (구버전 2026-09-06)
            boolean hasTopBanners = categoryService.hasTopBanners(current);
            boolean hasMidBanners = categoryService.hasMidBanners(current);
            List<CategoryDTO> childCategories = categoryService.findChildCategories(categoryNo);
            // 형제 목록은 아래 sidebarCategories 에서도 쓰므로 한 번만 조회
            List<CategoryDTO> siblingCategories = categoryService.findSiblingCategories(categoryNo);

            // 같은 봉투를 목록·개수에 그대로 → 필터 조건이 절대 안 어긋남
            List<CategoryProductDTO> products = categoryService.findProducts(search);
            int totalCount = categoryService.countProducts(search);

            // ── 상품 목록 + 페이지 ──
            model.addAttribute("products", products);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("totalPages", categoryService.calcTotalPages(totalCount, search.getListSize()));
            model.addAttribute("page", search.getPage());
            model.addAttribute("listSize", search.getListSize());

            // ── 지금 걸린 정렬·필터 — JSP 링크들이 이 값을 이어 붙임 (구버전과 이름 그대로) ──
            model.addAttribute("sort", search.getSort());
            model.addAttribute("minPrice", search.getMinPrice());
            model.addAttribute("maxPrice", search.getMaxPrice() == Integer.MAX_VALUE ? "" : search.getMaxPrice());
            model.addAttribute("rating", search.getRating());
            model.addAttribute("selectedColors", search.getColor());
            model.addAttribute("selectedColorMap", categoryService.buildSelectedColorMap(search.getColor()));

            // ── 카테고리 정보 ──
            model.addAttribute("categoryNo", categoryNo);
            model.addAttribute("categoryName", current.getCategoryName());
            model.addAttribute("breadcrumb", categoryService.findBreadcrumb(categoryNo));   // [대분류 → … → 현재]
            model.addAttribute("siblingCategories", siblingCategories);
            model.addAttribute("colorOptions", categoryService.findColorOptions(categoryNo));
            model.addAttribute("isMidCategory", isMidCategory);
            model.addAttribute("isTopCategory", isTopCategory);
            model.addAttribute("hasTopBanners", hasTopBanners);
            model.addAttribute("hasMidBanners", hasMidBanners);

            // 왼쪽 사이드바 "카테고리" — 대·중분류는 자식 목록, 소분류는 형제 목록 (구버전 그대로)
            model.addAttribute("sidebarCategories",
                            (isMidCategory || isTopCategory) ? childCategories : siblingCategories);

            // 중분류 원형 타일 — 이미지 파일이 있는 카테고리만
            model.addAttribute("categoryTiles", isMidCategory ? categoryService.tilesWithImage(childCategories) : null);

            // 대분류 타일 10칸 — 배너를 그리는 대분류에서만
            model.addAttribute("tileSlots", hasTopBanners ? categoryService.buildTileSlots(childCategories) : null);

            // 동작 안 하는 장식용 필터 그룹들
            model.addAttribute("beforeColorGroups", categoryService.buildBeforeColorGroups());
            model.addAttribute("afterColorGroups", categoryService.buildAfterColorGroups());

            model.addAttribute("deliveryDate", categoryService.calcDeliveryDate());

            // ── 레이아웃(layout_shop.jsp)용 — 상품 상세 때와 같은 방식 ──
            model.addAttribute("pageTitle", current.getCategoryName() + " | 굿팡");   // 구버전 category_list.jsp 18줄
            model.addAttribute("bodyClass", "page-category");                          // 구버전 category_list.jsp 29줄

            return "category.list";
	}//list
    
} //class
