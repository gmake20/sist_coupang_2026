package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;
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

}
