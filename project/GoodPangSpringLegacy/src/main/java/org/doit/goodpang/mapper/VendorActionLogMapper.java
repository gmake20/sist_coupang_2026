package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.VendorActionLogSearchDTO;
import org.doit.goodpang.domain.VendorActionLogDTO;
import org.springframework.stereotype.Repository;

/*
 * 판매자 액션 로그(VENDOR_ACTION_LOG) - 기존 GoodPang VendorActionLogDAO.
 * 판매자가 한 주요 작업(배송 처리, 상품 숨김 등)을 관리자 화면에서 볼 수 있도록 기록한다.
 */
@Repository
public interface VendorActionLogMapper {

	// actionType 예: "배송 처리" / targetType 예: "ORDERS" / targetNo = 대상 PK / detail = 부가 설명
	public int insertLog(@Param("sellerNo") int sellerNo, @Param("actionType") String actionType,
			@Param("targetType") String targetType, @Param("targetNo") int targetNo,
			@Param("detail") String detail);

	// ===== 관리자 판매자 액션 로그 조회 (판매자센터에는 노출 안 함) =====

	// 검색조건(search)에 맞는 로그 - 최신순. 조건 필드가 비어 있으면 그 조건은 안 붙음. offset = (page - 1) * pageSize
	public List<VendorActionLogDTO> findAll(@Param("search") VendorActionLogSearchDTO search,
			@Param("offset") int offset, @Param("pageSize") int pageSize);

	// 페이지네이션용 총 개수 (findAll과 같은 검색조건)
	public int countAll(@Param("search") VendorActionLogSearchDTO search);

}
