package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.AdminProductApprovalDTO;
import org.springframework.stereotype.Repository;

/*
 * 관리자 상품 승인 관리 - 기존 GoodPang AdminProductDAO.
 */
@Repository
public interface AdminProductMapper {

	// 전체 상품 - '승인 대기'가 맨 위, 그 안에서 최신 등록순
	public List<AdminProductApprovalDTO> findAll();

	// 승인('판매 중') / 반려('판매 중지'). '승인 대기' 상태인 상품만 바뀌고, 아니면 0
	public int updateApprovalStatus(@Param("productNo") int productNo, @Param("saleStatus") String saleStatus);

}
