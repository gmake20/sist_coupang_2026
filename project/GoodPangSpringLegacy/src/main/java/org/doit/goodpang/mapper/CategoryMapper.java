package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.CategoryDTO;
import org.springframework.stereotype.Repository;

/*
 * 카테고리(CATEGORY) 조회 - 기존 GoodPang CategoryInfo 서블릿의 쿼리.
 */
@Repository
public interface CategoryMapper {

	// 대/중/소분류 전체 트리 (계층 순서). 대분류(1단계)만 imgUrl 채움
	public List<CategoryDTO> selectCategoryTree();

	// 이름에 keyword가 포함된 "최종 항목"(자식 없는 리프)만 + categoryPath("대분류 > 중분류 > 소분류")
	public List<CategoryDTO> searchLeafByKeyword(@Param("keyword") String keyword);


}
