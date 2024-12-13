//필터를 통해 모든 요청을 추적하여 IP를 기록
package com.example.demo;

import com.example.demo.Service.ActiveVisitorService;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class VisitorTrackingFilter implements Filter {

    private final ActiveVisitorService activeVisitorService;

    public VisitorTrackingFilter(ActiveVisitorService activeVisitorService) {
        this.activeVisitorService = activeVisitorService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            activeVisitorService.trackVisitor((HttpServletRequest) request);
        }
        chain.doFilter(request, response);
    }
}
