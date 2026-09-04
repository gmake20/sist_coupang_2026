package com.goodpang.command;

import com.goodpang.servlet.CommandHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /category 요청 처리.
 * TODO: 다음 단계에서 CategoryServlet.doGet() 내용을 여기로 그대로 옮길 예정.
 *       (아직 미완성 — web.xml 매핑도 안 걸려있어서 지금은 호출 안 됨)
 */
public class CategoryHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO: CategoryServlet 로직 이관
        return "/WEB-INF/views/category_list.jsp";
    }
}
