package com.goodpang.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.goodpang.command.CommandHandler;
import com.goodpang.command.NullHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 커맨드 패턴의 진입점(컨트롤러) 서블릿.
 * web.xml 에서 /product, /category, /option 3개 URL 을 이 서블릿 하나로 매핑함.
 *
 * 흐름: URL -> commandHandler.properties 에서 Handler 클래스 찾기 -> 리플렉션으로 생성(1회, init 시점) ->
 *       CommandHandler.process() 실행 -> 리턴받은 View(jsp 경로)로 forward
 */
public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Map<String, CommandHandler> commandHandlerMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();

        // 1. web.xml 의 <init-param> 에서 mappingPath 경로 추출 (학원 방식)
        String urlMappingPath = this.getInitParameter("mappingPath");

        Properties p = new Properties();

        // 2. commandHandler.properties 는 webapp/WEB-INF 밑에 둠 -> 클래스로더가 아니라
        //    ServletContext 로 읽어야 함 (getRealPath() 는 WAR 배포 시 null 나올 수 있어서 안 씀)
        try (InputStream is = this.getServletContext().getResourceAsStream(urlMappingPath)) {
            if (is == null) {
                throw new ServletException("경로에서 프로퍼티 파일을 찾을 수 없습니다: " + urlMappingPath);
            }
            p.load(is);
        } catch (IOException e) {
            throw new ServletException("설정 파일 로딩 실패", e);
        }

        // 3. 인스턴스 1회 생성 후 Map 에 보관 (학원 방식 - 요청마다 리플렉션 안 함)
        Set<Map.Entry<Object, Object>> set = p.entrySet();
        Iterator<Map.Entry<Object, Object>> ir = set.iterator();

        while (ir.hasNext()) {
            Map.Entry<Object, Object> entry = ir.next();
            String url = (String) entry.getKey();
            String fullName = ((String) entry.getValue()).trim();

            try {
                Class<?> commandHandlerClass = Class.forName(fullName);
                CommandHandler handler = (CommandHandler) commandHandlerClass
                        .getDeclaredConstructor()
                        .newInstance();

                commandHandlerMap.put(url, handler);
            } catch (Exception e) {
                throw new ServletException("핸들러 등록 실패 (" + fullName + ")", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 학원 방식 URL 분석 (컨텍스트 패스 절삭) - 예: "/product", "/category", "/option"
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // 2. Map 에서 핸들러 탐색 (없으면 NullHandler 대체 - 학원 방식)
        CommandHandler handler = this.commandHandlerMap.get(path);
        if (handler == null) {
            handler = new NullHandler();
        }

        // 3. 핸들러 실행
        String viewName = null;
        try {
            viewName = handler.process(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        if (viewName == null) {
            // Handler 안에서 이미 응답 처리를 끝낸 경우 (redirect, sendError 등)
            return;
        }

        // 4. redirect: 접두사 판별 (학원 방식)
        if (viewName.startsWith("redirect:")) {
            String location = viewName.substring("redirect:".length());
            response.sendRedirect(contextPath + location);
        } else {
            // 5. 포워딩
            RequestDispatcher dispatcher = request.getRequestDispatcher(viewName);
            dispatcher.forward(request, response);
        }
    }
}
