package org.doit.goodpang.mapper;

import java.util.List;

import org.doit.goodpang.domain.CategoryProductDTO;
import org.doit.goodpang.domain.SearchDTO;
import org.springframework.stereotype.Repository;

/*
 * 검색 결과 페이지(/search) 조회 — 구버전 SearchDAO.
 * 결과 카드 모양이 카테고리 목록과 같아서 DTO 도 CategoryProductDTO 를 그대로 씀.
 */
@Repository
public interface SearchMapper {

	// 상품명에 검색어가 들어간 상품 목록 — 정렬/페이지 적용
	public List<CategoryProductDTO> findByKeyword(SearchDTO search);

	// 페이지 계산용 전체 개수 — 조건은 findByKeyword 와 같음
	public int countByKeyword(SearchDTO search);

}
