package org.doit.goodpang.service;

import java.util.List;

import org.doit.goodpang.domain.CategoryProductDTO;
import org.doit.goodpang.domain.SearchDTO;
import org.doit.goodpang.mapper.SearchMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/*
 * 검색 결과 페이지(/search) 업무 로직 — 구버전 SearchServlet 이 SearchDAO 를 new 해서 부르던 부분.
 * 검색어가 없으면 DB 를 조회하지 않고 빈 결과 (구버전 그대로).
 */
@Service
@RequiredArgsConstructor
public class SearchService {

	private final SearchMapper searchMapper;

	/** 상품명에 검색어가 들어간 상품 목록 — 정렬/페이지 적용 */
	public List<CategoryProductDTO> findProducts(SearchDTO search) {
		if (!search.hasKeyword()) {
			return List.of();
		}
		return searchMapper.findByKeyword(search);
	}

	/** 페이지 계산용 전체 개수 */
	public int countProducts(SearchDTO search) {
		if (!search.hasKeyword()) {
			return 0;
		}
		return searchMapper.countByKeyword(search);
	}
}
