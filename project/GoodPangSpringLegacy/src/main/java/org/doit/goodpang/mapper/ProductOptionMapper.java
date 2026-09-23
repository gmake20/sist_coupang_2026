package org.doit.goodpang.mapper;

import java.util.List;

import org.doit.goodpang.domain.ProductOptionDTO;

public interface ProductOptionMapper {

	/** 이 상품의 옵션 전체 (OPTION_ID 순) */
	List<ProductOptionDTO> selectOptionByProductNo(int productNo);

	/** 옵션 1건. 없으면 null */
	ProductOptionDTO selectOptionById(int optionId);
	
}


