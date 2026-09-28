package org.doit.goodpang.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/*
 * 관리자 액션 로그(ADMIN_ACTION_LOG) - 기존 GoodPang AdminActionLogDAO.
 * 관리자가 한 주요 작업(공지 등록/수정/삭제, 상품 승인, 판매자 승인/반려 등)을 기록한다.
 */
@Repository
public interface AdminActionLogMapper {

	// actionType 예: "공지 등록" / targetType 예: "NOTICE" / targetNo = 대상 PK / reason = 사유(없으면 null)
	public int insertLog(@Param("adminNo") int adminNo, @Param("actionType") String actionType,
			@Param("targetType") String targetType, @Param("targetNo") int targetNo,
			@Param("reason") String reason);

}
