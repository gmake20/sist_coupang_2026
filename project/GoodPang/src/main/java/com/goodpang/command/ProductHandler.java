package com.goodpang.command;

import com.goodpang.servlet.CommandHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /product 요청 처리.
 * TODO: 다음 단계에서 ProductServlet.doGet() 내용을 여기로 그대로 옮기고,
 *       가격/할인율/평균평점 등 계산 로직은 service 패키지로 분리할 예정.
 *       (아직 미완성 — web.xml 매핑도 안 걸려있어서 지금은 호출 안 됨)
 */
public class ProductHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO: ProductServlet 로직 이관
        return "/product.jsp";
    }
}
