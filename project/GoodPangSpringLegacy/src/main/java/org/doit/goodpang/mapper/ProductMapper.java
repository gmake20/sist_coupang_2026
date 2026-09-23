package org.doit.goodpang.mapper;

import org.doit.goodpang.domain.ProductDTO;

public interface ProductMapper {
	
	/** 상품 1건 (PRODUCT + SELLER + 카테고리 3단 JOIN). 없으면 null */
	public ProductDTO selectProduct(int productNo);
	
	/** 이 상품의 첫 옵션 ID (OPTION_ID 순). 없으면 null */
	public Integer getDefaultOptionId(int productNo);
	
}
