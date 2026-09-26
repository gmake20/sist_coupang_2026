package org.doit.goodpang.mapper;

import java.util.List;

import org.doit.goodpang.domain.CategoryDTO;
import org.doit.goodpang.domain.CategoryProductDTO;
import org.doit.goodpang.domain.CategorySearchDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryProductMapper {
	
	// 카테고리 1개 (없으면 null)
	public CategoryDTO findCategory(long categoryNo);
	
	// 자식 카테고리 목록 + 중분류 페이지 원형 아이콘
	public List<CategoryDTO> findChildCategories(long categoryNo);
	
	// [대분류 → … → 현재] 순서. 구버전은 CategoryDTO[] 였지만 JSP 는 c:forEach 라 List 로 충분
	public List<CategoryDTO> findBreadcrumb(long categoryNo);
	
	// 같은 부모를 가진 형제 카테고리 — 소분류 페이지 사이드바
	public List<CategoryDTO> findSiblingCategories(long categoryNo);
	
	// 색상 필터에 띄울 색상 목록
	public List<String> findColorOptionsByCategory(long categoryNo);
	
	// 필터까지 적용한 전체 상품 수 (페이지 계산용)
	public int countByCategory(CategorySearchDTO search);
	
	// 상품 목록 — 정렬/필터/페이지 적용 (search.getOffset() 으로 건너뛸 개수 계산)	
	public List<CategoryProductDTO> findByCategory(CategorySearchDTO search);
	
}
