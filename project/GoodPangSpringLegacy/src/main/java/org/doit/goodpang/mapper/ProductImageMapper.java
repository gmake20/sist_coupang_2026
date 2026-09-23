package org.doit.goodpang.mapper;

import java.util.List;

import org.doit.goodpang.domain.ProductImageDTO;

public interface ProductImageMapper {
	
	 /** 이 상품의 사진 전체 (옵션별 → 대표 먼저 → 순서) */
	List<ProductImageDTO> selectImagesByProductNo(int productNo);
	
	 /** 이 옵션 전용 사진들 (대표 먼저 → 순서) */
	List<ProductImageDTO> selectImagesByOptionId(int optionId);
	
}
