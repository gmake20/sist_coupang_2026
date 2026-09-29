package org.doit.goodpang.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.doit.goodpang.domain.ProductImageDTO;
import org.doit.goodpang.domain.ProductOptionWriteDTO;
import org.doit.goodpang.domain.ProductWriteDTO;
import org.doit.goodpang.domain.VendorProductDetailDTO;
import org.doit.goodpang.domain.VendorProductOptionDetailDTO;
import org.doit.goodpang.mapper.VendorProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/*
 * 판매자센터 상품 - 여러 쿼리 결과를 조립하거나 트랜잭션이 필요한 작업, 그리고 판매자 액션 로그를 남기는 처리.
 * (단순 조회는 Controller에서 VendorProductMapper를 바로 호출)
 *
 * 등록/노출변경/판매상태변경/옵션수정의 판매자 액션 로그는 ActionLogAspect가 반환값을 보고 남긴다 (@AfterReturning).
 */
@Service
@RequiredArgsConstructor
public class VendorProductService {

	private final VendorProductMapper vendorProductMapper;

	/*
	 * 상품 상세 - PRODUCT + 카테고리, PRODUCT_OPTION 목록, PRODUCT_IMAGE 목록을 각각 조회해 하나로 조립한다
	 * (기존 VendorProductDetailDAO.findByProductNo). 이 판매자 상품이 아니면 null.
	 */
	public VendorProductDetailDTO getProductDetail(int productNo, int sellerNo) {

		VendorProductDetailDTO product = vendorProductMapper.selectProductDetail(productNo, sellerNo);

		if (product == null) {
			return null;
		}

		// 이미지를 옵션에 붙이기 위해 OPTION_ID로 찾을 수 있게 (순서는 OPTION_ID순 유지)
		Map<Integer, VendorProductOptionDetailDTO> optionMap = new LinkedHashMap<>();
		for (VendorProductOptionDetailDTO option : vendorProductMapper.selectOptionDetails(productNo)) {
			optionMap.put(option.getOptionId(), option);
		}

		applyImages(product, optionMap, vendorProductMapper.selectProductImages(productNo));

		product.getOptions().addAll(optionMap.values());

		return product;
	}

	// OPTION_ID 없는 이미지 = 상세설명 이미지, 있으면 그 옵션의 '대표' 또는 '추가' 이미지
	private void applyImages(VendorProductDetailDTO product, Map<Integer, VendorProductOptionDetailDTO> optionMap,
			List<ProductImageDTO> images) {

		for (ProductImageDTO image : images) {

			if (image.getOptionId() == null) {
				product.getDetailImageUrls().add(image.getImageUrl());
				continue;
			}

			VendorProductOptionDetailDTO option = optionMap.get(image.getOptionId());
			if (option == null) {
				continue;
			}

			if ("대표".equals(image.getImagePurpose())) {
				option.setMainImageUrl(image.getImageUrl());
			} else if ("추가".equals(image.getImagePurpose())) {
				option.getExtraImageUrls().add(image.getImageUrl());
			}
		}
	}

	/*
	 * 상품 등록 - PRODUCT + PRODUCT_OPTION + PRODUCT_IMAGE 세 테이블에 한 트랜잭션으로 저장하고 새 상품번호를 돌려준다.
	 * (기존 ProductWriteDAO.insertProduct의 setAutoCommit(false)/commit/rollback을 @Transactional이 대신함 -
	 *  중간에 INSERT가 하나라도 실패하면 전부 rollback)
	 * 이미지 파일 저장(디스크 I/O)은 Controller 책임이고, 여기서는 이미 저장된 이미지 URL 문자열만 받아서 INSERT한다.
	 */
	@Transactional
	public int registerProduct(ProductWriteDTO dto) {

		int productNo = vendorProductMapper.nextProductNo();
		vendorProductMapper.insertProduct(productNo, dto);

		for (ProductOptionWriteDTO option : dto.getOptions()) {
			int optionId = vendorProductMapper.nextOptionId();
			vendorProductMapper.insertOption(optionId, productNo, option);

			if (option.getMainImageUrl() != null) {
				vendorProductMapper.insertImage(productNo, optionId, "대표", 1, option.getMainImageUrl());
			}

			int order = 1;
			for (String extraImageUrl : option.getExtraImageUrls()) {
				vendorProductMapper.insertImage(productNo, optionId, "추가", order++, extraImageUrl);
			}
		}

		int detailOrder = 1;
		for (String detailImageUrl : dto.getDetailImageUrls()) {
			vendorProductMapper.insertImage(productNo, null, "상세설명", detailOrder++, detailImageUrl);
		}

		return productNo;
	}

	// 노출여부 변경 ("Y" 노출 / "N" 숨김) - 소프트 삭제/복원. 이 판매자 상품이 아니면 false
	public boolean changeDisplayYn(int productNo, int sellerNo, String displayYn) {
		return vendorProductMapper.updateDisplayYn(productNo, sellerNo, displayYn) == 1;
	}

	// 판매중지/판매재개 ("판매 중" <-> "판매 중지"). 승인 대기/품절 상품이거나 이 판매자 상품이 아니면 false
	public boolean changeSaleStatus(int productNo, int sellerNo, String saleStatus) {
		return vendorProductMapper.updateSaleStatus(productNo, sellerNo, saleStatus) == 1;
	}

	// 옵션 하나의 판매가/정상가/재고/상태(Y 정상, N 품절) 수정. 이 판매자 상품의 옵션이 아니면 false
	public boolean updateOption(int optionId, int sellerNo, int price, Integer normalPrice, int quantity, String status) {
		return vendorProductMapper.updateOption(optionId, sellerNo, price, normalPrice, quantity, status) == 1;
	}
}
