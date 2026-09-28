package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.ProductImageDTO;
import org.doit.goodpang.domain.ProductOptionWriteDTO;
import org.doit.goodpang.domain.ProductWriteDTO;
import org.doit.goodpang.domain.VendorProductDetailDTO;
import org.doit.goodpang.domain.VendorProductListDTO;
import org.doit.goodpang.domain.VendorProductOptionDTO;
import org.doit.goodpang.domain.VendorProductOptionDetailDTO;
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

	// ===== 상품 상세 - VendorProductService.getProductDetail에서 조립 =====

	// 상품 기본정보 + 카테고리. 이 판매자 상품이 아니면 null
	public VendorProductDetailDTO selectProductDetail(@Param("productNo") int productNo, @Param("sellerNo") int sellerNo);

	// 옵션 목록 (OPTION_ID순)
	public List<VendorProductOptionDetailDTO> selectOptionDetails(@Param("productNo") int productNo);

	// 상품의 전체 이미지 (옵션 대표/추가 + 상세설명)
	public List<ProductImageDTO> selectProductImages(@Param("productNo") int productNo);

	// 노출여부 변경 ("Y" 노출 / "N" 숨김). 이 판매자 상품이 아니면 0
	public int updateDisplayYn(@Param("productNo") int productNo, @Param("sellerNo") int sellerNo,
			@Param("displayYn") String displayYn);

	// 판매중지/판매재개 ("판매 중" <-> "판매 중지"). 승인 대기/품절 상품이거나 이 판매자 상품이 아니면 0
	public int updateSaleStatus(@Param("productNo") int productNo, @Param("sellerNo") int sellerNo,
			@Param("saleStatus") String saleStatus);

	// 옵션 수정 (판매가/정상가/재고/상태 Y=정상,N=품절). normalPrice는 null 가능. 이 판매자 상품의 옵션이 아니면 0
	public int updateOption(@Param("optionId") int optionId, @Param("sellerNo") int sellerNo,
			@Param("price") int price, @Param("normalPrice") Integer normalPrice,
			@Param("quantity") int quantity, @Param("status") String status);

	// ===== 상품 등록 - VendorProductService.registerProduct에서 한 트랜잭션으로 호출 =====

	// SEQ_PRODUCT.NEXTVAL
	public int nextProductNo();

	// SEQ_OPTION.NEXTVAL
	public int nextOptionId();

	// PRODUCT 한 행 (판매상태는 dto.saleStatus = "승인 대기")
	public int insertProduct(@Param("productNo") int productNo, @Param("dto") ProductWriteDTO dto);

	// PRODUCT_OPTION 한 행 (재고 0이면 STATUS = N)
	public int insertOption(@Param("optionId") int optionId, @Param("productNo") int productNo,
			@Param("option") ProductOptionWriteDTO option);

	// PRODUCT_IMAGE 한 행. purpose = "대표"/"추가"/"상세설명", optionId는 상세설명이면 null
	public int insertImage(@Param("productNo") int productNo, @Param("optionId") Integer optionId,
			@Param("purpose") String purpose, @Param("imageOrder") int imageOrder, @Param("imageUrl") String imageUrl);

	// 판매자 탈퇴 시 노출 중인 상품 전부 숨김. 바뀐 상품 수를 돌려줌
	public int hideAllBySeller(@Param("sellerNo") int sellerNo);

}
