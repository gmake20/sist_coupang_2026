package org.doit.goodpang.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.doit.goodpang.domain.CategoryDTO;
import org.doit.goodpang.mapper.CategoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/*
 * 카테고리 조회 ajax — 구버전 CategoryInfo 서블릿(/category/getinfo).
 *   /category/getinfo?ctype=main   → header.js 카테고리 메뉴 (단계별로 묶은 맵 {"1":[...],"2":[...],"3":[...]})
 *   /category/getinfo              → 전체 목록
 *   /category/getinfo?keyword=의류 → 상품 등록(product_write.jsp) 카테고리 검색 자동완성
 * 반환 객체는 Jackson(pom.xml의 jackson-databind)이 JSON으로 변환한다.
 */
@Controller
@Log4j
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryMapper categoryMapper;

	@GetMapping(value = "/getinfo", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<Object> getInfo(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "ctype", required = false) String ctype) {

		try {
			if (keyword != null && !keyword.isBlank()) {
				return ResponseEntity.ok(categoryMapper.searchLeafByKeyword(keyword.trim()));
			}

			List<CategoryDTO> categoryList = categoryMapper.selectCategoryTree();

			if ("main".equals(ctype)) {
				return ResponseEntity.ok(groupByLevel(categoryList));
			}
			return ResponseEntity.ok(categoryList);

		} catch (Exception e) {
			log.error("카테고리 조회 실패 keyword=" + keyword, e);

			String message = (keyword != null && !keyword.isBlank())
					? "검색어 카테고리 조회 중 오류가 발생했습니다."
					: "카테고리 조회 중 오류가 발생했습니다.";

			Map<String, String> error = new LinkedHashMap<>();
			error.put("error", message);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	/** categoryLevel(1=대분류, 2=중분류, 3=소분류)별로 묶어서 "1", "2", "3" 키를 가진 맵으로 정리 */
	private Map<Integer, List<CategoryDTO>> groupByLevel(List<CategoryDTO> categoryList) {
		Map<Integer, List<CategoryDTO>> groupedByLevel = new LinkedHashMap<>();

		for (CategoryDTO category : categoryList) {
			groupedByLevel
					.computeIfAbsent(category.getCategoryLevel(), level -> new ArrayList<>())
					.add(category);
		}

		return groupedByLevel;
	}
}
