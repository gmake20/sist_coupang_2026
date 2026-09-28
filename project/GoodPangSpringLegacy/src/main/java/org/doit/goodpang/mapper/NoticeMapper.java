package org.doit.goodpang.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.doit.goodpang.domain.NoticeDTO;
import org.springframework.stereotype.Repository;

/*
 * 공지사항(NOTICE) - 기존 GoodPang NoticeDAO.
 * 판매자센터는 조회만, 등록/수정/삭제는 관리자 화면에서 한다 (관리자 쪽 옮길 때 여기에 추가).
 */
@Repository
public interface NoticeMapper {

	// 목록 - '공지' 타입 먼저, 그 안에서 최신순. offset = (page - 1) * pageSize
	public List<NoticeDTO> findAll(@Param("offset") int offset, @Param("pageSize") int pageSize);

	// 페이지네이션용 전체 건수
	public int countAll();

	// 상세 - 본문(CONTENT) 포함. 없는 번호면 null
	public NoticeDTO findByNoticeNo(@Param("noticeNo") int noticeNo);

	// ===== 관리자 공지 등록/수정/삭제 =====

	// 등록 - 성공하면 notice.noticeNo에 새 번호가 채워진다 (selectKey)
	public int insertNotice(NoticeDTO notice);

	// 수정 (제목/내용/구분). 없는 번호면 0
	public int updateNotice(NoticeDTO notice);

	// 삭제. 없는 번호면 0
	public int deleteNotice(@Param("noticeNo") int noticeNo);

}
