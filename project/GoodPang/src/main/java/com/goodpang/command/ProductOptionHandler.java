package com.goodpang.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /option 요청 처리 (옵션 변경 시 ajax 재조회).
 * TODO: 다음 단계에서 ProductOptionServlet.doGet() 내용을 여기로 그대로 옮길 예정.
 *       (아직 미완성 — web.xml 매핑도 안 걸려있어서 지금은 호출 안 됨)
 */
public class ProductOptionHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO: ProductOptionServlet 로직 이관
        return null;
    }
}
