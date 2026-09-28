package org.doit.goodpang.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.doit.goodpang.domain.NoticeDTO;
import org.doit.goodpang.mapper.AdminActionLogMapper;
import org.doit.goodpang.mapper.NoticeMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

/*
 * 관리자 화면.
 *
 * 인증/권한은 Spring Security가 처리한다 (security-context.xml의 관리자 전용 <http>, SL14_SECURITY2_JDBC 방식).
 *  - 로그인 처리: 폼이 /admin/loginProcess 로 POST → jdbc-user-service가 ADMIN 테이블로 확인, ROLE_ADMIN 부여
 *                 → AdminLoginSuccessHandler가 세션에 loginAdmin/adminNo/adminName을 넣고 이동
 *  - 로그아웃:    /admin/logout.htm POST → Security LogoutFilter가 세션 무효화 후 /admin/login.htm?logout
 *  - 권한 확인:   /admin/** 는 hasRole('ADMIN') - 로그인 안 했으면 자동으로 로그인 화면으로 보내고,
 *                 로그인 후 원래 가려던 화면으로 돌려보낸다.
 * 그래서 기존 AdminLoginServlet/AdminLogoutServlet/AdminAuthFilter가 하던 일은 이 Controller에 없다.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final NoticeMapper noticeMapper;
	private final AdminActionLogMapper adminActionLogMapper;

	private static final int PAGE_SIZE = 20; // 목록 한 페이지 행 수

	/*
	 * 관리자 로그인 화면 - 판매자 로그인처럼 레이아웃 없는 단독 화면 (tiles.xml admin.login).
	 * Security가 상황에 따라 파라미터를 붙여서 이 화면으로 보낸다:
	 *   ?error  로그인 실패 / ?logout  로그아웃 완료 / ?denied  관리자 권한이 없는 계정으로 접근
	 */
	@GetMapping(value = "/login.htm")
	public ModelAndView login(
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "denied", required = false) String denied) {

		ModelAndView mav = new ModelAndView("admin.login");

		if (error != null) {
			mav.addObject("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
		} else if (denied != null) {
			mav.addObject("error", "관리자 권한이 없는 계정입니다. 관리자 계정으로 로그인해주세요.");
		} else if (logout != null) {
			mav.addObject("message", "로그아웃되었습니다.");
		}

		return mav;
	}

	// 관리자 대시보드 - 각 관리 메뉴로 가는 카드 목록 (기존 AdminDashboardServlet). 권한 확인은 Security가 함
	@GetMapping(value = "/dashboard.htm")
	public ModelAndView dashboard() {
		return new ModelAndView("admin.dashboard");
	}

	// ===================================================================
	// 공지사항 관리 (기존 AdminNoticeList/Write/Edit/DeleteServlet)
	// 판매자센터 공지 화면(VendorController.notice)과 같은 NOTICE 테이블/NoticeMapper를 쓴다.
	// ===================================================================

	// 공지 목록 - '공지' 타입 먼저, 그 안에서 최신순
	@GetMapping(value = "/notices.htm")
	public ModelAndView notices(@RequestParam(value = "page", required = false) String pageParam) {

		int page = parsePage(pageParam);

		List<NoticeDTO> noticeList = noticeMapper.findAll((page - 1) * PAGE_SIZE, PAGE_SIZE);
		int totalCount = noticeMapper.countAll();
		int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

		ModelAndView mav = new ModelAndView("admin.notices");
		mav.addObject("noticeList", noticeList);
		mav.addObject("page", page);
		mav.addObject("totalPages", totalPages);
		mav.addObject("totalCount", totalCount);

		return mav;
	}

	// 공지 등록 화면
	@GetMapping(value = "/notice_write.htm")
	public ModelAndView noticeWrite() {
		return new ModelAndView("admin.notice_write");
	}

	/*
	 * 공지 등록 처리. 제목/내용/구분('공지' 또는 '안내')이 하나라도 없으면 같은 화면에 error
	 * (입력했던 값은 JSP가 param으로 다시 채움). 성공하면 관리자 액션 로그를 남기고 목록으로.
	 * 작성자(adminNo)는 로그인 시 AdminLoginSuccessHandler가 세션에 넣어둔 값.
	 */
	@PostMapping(value = "/notice_write.htm")
	public ModelAndView noticeWritePost(
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "noticeType", required = false) String noticeType,
			HttpSession session) {

		Integer adminNo = (Integer) session.getAttribute("adminNo");

		if (!isValidNotice(title, content, noticeType) || adminNo == null) {
			ModelAndView mav = new ModelAndView("admin.notice_write");
			mav.addObject("error", "제목, 내용, 구분을 모두 입력해주세요.");
			return mav;
		}

		NoticeDTO notice = new NoticeDTO();
		notice.setTitle(title.trim());
		notice.setContent(content);
		notice.setNoticeType(noticeType);
		notice.setAdminNo(adminNo);

		if (noticeMapper.insertNotice(notice) == 1) {
			writeAdminLog(adminNo, "공지 등록", notice.getNoticeNo());
		}

		return new ModelAndView("redirect:/admin/notices.htm");
	}

	// 공지 수정 화면 - 번호가 잘못됐거나 없는 공지면 목록으로
	@GetMapping(value = "/notice_edit.htm")
	public ModelAndView noticeEdit(@RequestParam(value = "noticeNo", required = false) String noticeNoParam) {

		Integer noticeNo = parseNoticeNo(noticeNoParam);
		NoticeDTO notice = (noticeNo == null) ? null : noticeMapper.findByNoticeNo(noticeNo);

		if (notice == null) {
			return new ModelAndView("redirect:/admin/notices.htm");
		}

		ModelAndView mav = new ModelAndView("admin.notice_edit");
		mav.addObject("notice", notice);
		return mav;
	}

	/*
	 * 공지 수정 처리. 값이 빠졌으면 같은 화면에 error - 기존 서블릿은 DB의 원래 내용으로 되돌려 보여줬는데,
	 * 여기서는 방금 입력했던 값을 그대로 다시 보여준다(고치던 내용이 사라지지 않게).
	 */
	@PostMapping(value = "/notice_edit.htm")
	public ModelAndView noticeEditPost(
			@RequestParam(value = "noticeNo", required = false) String noticeNoParam,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "noticeType", required = false) String noticeType,
			HttpSession session) {

		Integer noticeNo = parseNoticeNo(noticeNoParam);

		if (noticeNo == null) {
			return new ModelAndView("redirect:/admin/notices.htm");
		}

		NoticeDTO notice = new NoticeDTO();
		notice.setNoticeNo(noticeNo);
		notice.setTitle(title);
		notice.setContent(content);
		notice.setNoticeType(noticeType);

		if (!isValidNotice(title, content, noticeType)) {
			ModelAndView mav = new ModelAndView("admin.notice_edit");
			mav.addObject("error", "제목, 내용, 구분을 모두 입력해주세요.");
			mav.addObject("notice", notice);
			return mav;
		}

		notice.setTitle(title.trim());

		if (noticeMapper.updateNotice(notice) == 1) {
			writeAdminLog((Integer) session.getAttribute("adminNo"), "공지 수정", noticeNo);
		}

		return new ModelAndView("redirect:/admin/notices.htm");
	}

	// 공지 삭제 (목록의 삭제 버튼 - POST + CSRF 토큰). 번호가 잘못됐으면 아무것도 지우지 않고 목록으로
	@PostMapping(value = "/notice_delete.htm")
	public ModelAndView noticeDelete(
			@RequestParam(value = "noticeNo", required = false) String noticeNoParam,
			HttpSession session) {

		Integer noticeNo = parseNoticeNo(noticeNoParam);

		if (noticeNo != null && noticeMapper.deleteNotice(noticeNo) == 1) {
			writeAdminLog((Integer) session.getAttribute("adminNo"), "공지 삭제", noticeNo);
		}

		return new ModelAndView("redirect:/admin/notices.htm");
	}

	// 제목/내용이 비어 있지 않고, 구분이 '공지' 또는 '안내'인지
	private boolean isValidNotice(String title, String content, String noticeType) {
		boolean validType = "공지".equals(noticeType) || "안내".equals(noticeType);
		return title != null && !title.isBlank() && content != null && !content.isBlank() && validType;
	}

	// 공지 관련 관리자 액션 로그. 로그 기록 실패가 공지 처리 자체를 되돌리면 안 되므로 예외는 삼킨다 (기존과 동일)
	private void writeAdminLog(Integer adminNo, String actionType, int noticeNo) {

		if (adminNo == null) {
			return;
		}

		try {
			adminActionLogMapper.insertLog(adminNo, actionType, "NOTICE", noticeNo, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// noticeNo 파싱. 없거나 숫자가 아니면 null
	private Integer parseNoticeNo(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// ?page= 파싱. 없거나 숫자가 아니면 1페이지
	private int parsePage(String pageParam) {
		try {
			return Math.max(1, Integer.parseInt(pageParam));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
