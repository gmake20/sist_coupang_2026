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
 * 공지사항 목록 조회 (관리자용).
 */
@WebServlet("/admin/notices")
public class AdminNoticeListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		NoticeDAO dao = new NoticeDAO();
		List<NoticeDTO> noticeList = dao.findAll();

		request.setAttribute("noticeList", noticeList);

		request.getRequestDispatcher("/WEB-INF/views/admin-notice-list.jsp")
			   .forward(request, response);
	}

}
