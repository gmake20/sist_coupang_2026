package org.doit.goodpang.service;

import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.mapper.AdminProductMapper;
import org.doit.goodpang.mapper.NoticeMapper;
import org.doit.goodpang.mapper.VendorMapper;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * 관리자 처리(공지 등록/수정/삭제, 상품 승인/반려, 판매자 상태 변경).
 *
 * 각 메서드는 "실제로 처리됐는지"를 반환값으로 돌려준다.
 * 관리자 액션 로그는 여기서 남기지 않고 ActionLogAspect가 이 반환값을 보고 남긴다 (@AfterReturning).
 * 그래서 반환값의 의미(성공 여부, 새 번호 등)를 바꿀 때는 ActionLogAspect도 같이 봐야 한다.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

	private final NoticeMapper noticeMapper;
	private final AdminProductMapper adminProductMapper;
	private final VendorMapper vendorMapper;

	// ===== 공지사항 =====

	// 공지 등록 - 성공하면 새 공지 번호, 실패하면 0
	public int registerNotice(NoticeDTO notice) {
		return noticeMapper.insertNotice(notice) == 1 ? notice.getNoticeNo() : 0;
	}

	// 공지 수정 (notice.noticeNo 대상). 없는 번호면 false
	public boolean updateNotice(NoticeDTO notice) {
		return noticeMapper.updateNotice(notice) == 1;
	}

	// 공지 삭제. 없는 번호면 false
	public boolean deleteNotice(int noticeNo) {
		return noticeMapper.deleteNotice(noticeNo) == 1;
	}

	// ===== 상품 승인 =====

	// 승인(true) → '판매 중', 반려(false) → '판매 중지'. '승인 대기' 상품이 아니면(이미 처리됨 등) false
	public boolean decideProductApproval(int productNo, boolean approve) {
		return adminProductMapper.updateApprovalStatus(productNo, approve ? "판매 중" : "판매 중지") == 1;
	}

	// ===== 판매자 상태 =====

	// 판매자 상태 변경. reason은 반려/정지일 때만 저장(그 외에는 null로 지움). 없는 판매자면 false
	public boolean changeSellerStatus(int sellerNo, SellerStatusAction action, String reason) {
		String savedReason = action.isReasonKept() ? reason : null;
		return vendorMapper.updateApprovalStatus(sellerNo, action.getApprovalStatus(), savedReason) == 1;
	}

	/*
	 * 관리자가 판매자에게 할 수 있는 처리. 같은 '승인'이라도 입점 승인과 정지 해제는 로그를 구분해서 남긴다.
	 * (REJECT_REASON 컬럼을 정지 사유에도 재사용 - 기존 GoodPang과 동일)
	 */
	@Getter
	public enum SellerStatusAction {
		APPROVE("승인", "판매자 승인", false),
		REJECT("반려", "판매자 반려", true),
		SUSPEND("정지", "판매자 정지", true),
		REACTIVATE("승인", "판매자 정지해제", false);

		private final String approvalStatus; // SELLER.APPROVAL_STATUS에 저장할 값
		private final String logActionType;  // ADMIN_ACTION_LOG.ACTION_TYPE
		private final boolean reasonKept;    // 사유를 저장하는 처리인지

		SellerStatusAction(String approvalStatus, String logActionType, boolean reasonKept) {
			this.approvalStatus = approvalStatus;
			this.logActionType = logActionType;
			this.reasonKept = reasonKept;
		}
	}
}
