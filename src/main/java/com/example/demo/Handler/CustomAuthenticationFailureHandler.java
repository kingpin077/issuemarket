/** 로그인 실패 핸들러 **/

package com.example.demo.Handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(value = "authenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.error("Authentication failed: " + exception.getMessage());
        response.getWriter().write("Authentication failed: " + exception.getMessage());
        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
        } else {
            errorMessage = exception.getMessage();
        }
        request.getSession().setAttribute("errorMessage", errorMessage);
        if (!response.isCommitted()) {
            super.setDefaultFailureUrl("/login?error=true");
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}