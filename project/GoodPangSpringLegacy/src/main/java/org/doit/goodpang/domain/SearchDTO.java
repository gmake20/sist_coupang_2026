package org.doit.goodpang.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*
 * 검색 결과 페이지(/search) 조건 — CategorySearchDTO 와 같은 역할.
 *
 * 필드명 = 주소 파라미터명 → Controller 에서 스프링이 자동으로 채워줌
 *   /search?keyword=티셔츠&sort=PRICE_ASC&page=2
 *
 * 구버전 SearchServlet 에서 getParameter + parseIntOrDefault / parseSortOrDefault 로 하나씩 꺼내던 것들을 모음.
 * 같은 객체를 countByKeyword / findByKeyword 에 그대로 넘김 (mapper.xml 에서 #{필드명} 으로 꺼냄)
 */
@Getter
@Setter
public class SearchDTO {

	public static final int LIST_SIZE = 60;	// 구버전 PAGE_SIZE — 검색은 60개 고정 (카테고리처럼 120개 보기 없음)

	private String keyword;
	private String sort = "LATEST";		// 정렬 4종 : 아래 normalize()
	private int page = 1;				// 현재 페이지(1부터)

	public void normalize() {
		keyword = (keyword == null) ? null : keyword.trim();
		sort = (sort == null) ? "LATEST" : sort.trim().toUpperCase();	// 구버전도 대소문자 무시
		// 정렬 화이트리스트 : 4개 외에는 최신순. 검색은 '쿠팡랭킹순'(RANKING)을 지원하지 않아서 그것도 최신순 (구버전 그대로)
		if (!List.of("LATEST", "PRICE_ASC", "PRICE_DESC", "SALE_COUNT").contains(sort)) {
			sort = "LATEST";
		}
		page = Math.max(1, page);
	}

	/** 검색어가 있는지 — 없으면 DB 조회 없이 빈 결과 */
	public boolean hasKeyword() {
		return keyword != null && !keyword.isEmpty();
	}

	/** mapper.xml 에서 #{listSize} 로 씀 */
	public int getListSize() {
		return LIST_SIZE;
	}

	/** 몇 개 건너뛸지 — mapper.xml 에서 #{offset} 으로 씀 */
	public int getOffset() {
		return (page - 1) * LIST_SIZE;
	}
}
