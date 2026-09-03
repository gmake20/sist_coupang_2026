package com.goodpang.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.goodpang.servlet.CommandHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 커맨드 패턴의 진입점(컨트롤러) 서블릿.
 * web.xml 에서 /product, /category, /option 3개 URL 을 이 서블릿 하나로 매핑함.
 *
 * 흐름: URL -> commandHandler.properties 에서 Handler 클래스 찾기 -> 리플렉션으로 생성 ->
 *       CommandHandler.process() 실행 -> 리턴받은 View(jsp 경로)로 forward
 */
public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Properties commandProps = new Properties();

    @Override
    public void init() throws ServletException {
        // commandHandler.properties 는 webapp/WEB-INF 밑에 둠 -> 클래스로더가 아니라
        // ServletContext 로 읽어야 함
        try (InputStream is = getServletContext().getResourceAsStream("/WEB-INF/commandHandler.properties")) {
            if (is == null) {
                throw new ServletException("commandHandler.properties 를 찾을 수 없음 (WEB-INF 밑 확인 필요)");
            }
            commandProps.load(is);
        } catch (IOException e) {
            throw new ServletException("commandHandler.properties 로드 실패", e);
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

        // 예: "/product", "/category", "/option"
        String url = request.getServletPath();
        String className = commandProps.getProperty(url);

        if (className == null) {
            throw new ServletException("등록되지 않은 command URL: " + url);
        }

        try {
            Class<?> clazz = Class.forName(className);
            CommandHandler handler = (CommandHandler) clazz.getDeclaredConstructor().newInstance();

            String viewPath = handler.process(request, response);

            // viewPath 가 null 이면 Handler 안에서 response.sendRedirect() 등으로
            // 이미 응답 처리를 끝낸 것으로 보고 forward 생략
            if (viewPath != null) {
                RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
                dispatcher.forward(request, response);
            }
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException("command 실행 중 오류 (" + className + ")", e);
        }
    }
}
