package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.VendorProductListDTO;
import org.doit.goodpang.domain.VendorProductOptionDTO;
import org.springframework.stereotype.Repository;

/*
 * 판매자센터 상품 목록(vendor/product.jsp) - 기존 GoodPang ProductListDAO의 판매자용 쿼리.
 */
@Repository
public interface VendorProductMapper {

	// displayYn "Y" = 노출 상품 탭, "N" = 숨김 상품 탭. offset = (page - 1) * pageSize
	public List<VendorProductListDTO> findBySellerNo(@Param("sellerNo") int sellerNo, @Param("displayYn") String displayYn,
			@Param("offset") int offset, @Param("pageSize") int pageSize);

	// saleStatus가 null이면 노출여부(displayYn)만으로 센다
	public int countBySellerNo(@Param("sellerNo") int sellerNo, @Param("displayYn") String displayYn,
			@Param("saleStatus") String saleStatus);

	// 상품 옵션 관리 - productNo가 null이면 전체 상품의 옵션
	public List<VendorProductOptionDTO> findOptionsBySellerNo(@Param("sellerNo") int sellerNo,
			@Param("productNo") Integer productNo);

	// 상품 옵션 관리 - 상품 필터 드롭다운 (옵션이 있는 상품만)
	public List<VendorProductOptionDTO> findDistinctOptionProductsBySellerNo(@Param("sellerNo") int sellerNo);

}
