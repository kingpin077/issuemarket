/** 접근 거부 핸들러(사용자가 권한이 없는 리소스에 접근을 시도했을 때 **/

package com.example.demo.Handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);


        String htmlResponse = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; font-size: 30px; text-align: center; margin-top: 50px; }" +
                ".error-message { color: red; font-weight: bold; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='error-message'>접근할 수 있는 권한이 없습니다.</div>" +
                "<p>" + accessDeniedException.getMessage() + "</p>" +
                "</body>" +
                "</html>";

        response.getWriter().write(htmlResponse);
    }
}