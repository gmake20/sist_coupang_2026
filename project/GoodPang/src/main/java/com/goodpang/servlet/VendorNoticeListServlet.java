package com.goodpang.servlet;

import java.io.IOException;
import java.util.List;

import com.goodpang.dao.NoticeDAO;
import com.goodpang.dto.NoticeDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 판매자센터 공지사항 목록(vendor-notice-list.jsp) — 조회 전용, 등록/수정/삭제는 관리자만 가능.
 * 로그인 여부는 VendorAuthFilter가 이 URL을 가드해서 처리한다.
 */
@WebServlet("/vendor/notice")
public class VendorNoticeListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		NoticeDAO dao = new NoticeDAO();
		List<NoticeDTO> noticeList = dao.findAll();

		request.setAttribute("noticeList", noticeList);

		request.getRequestDispatcher("/WEB-INF/views/vendor-notice-list.jsp")
			   .forward(request, response);
	}

}
