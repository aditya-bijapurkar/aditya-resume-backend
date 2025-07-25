package com.example.aditya_resume_backend.logger;

import com.example.aditya_resume_backend.constants.LoggerConstants;
import com.example.aditya_resume_backend.utils.LoggerUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class RESTTraceIdConfiguration implements Filter {

    private String generateRequestTraceID() {
        return String.format("%s:%s"
                ,LoggerConstants.SERVICE_NAME
                ,UUID.randomUUID());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String requestTraceId = httpRequest.getHeader(LoggerConstants.TRACE_ID_HEADER);
            if(requestTraceId == null || requestTraceId.isEmpty()) {
                requestTraceId = generateRequestTraceID();
            }

            LoggerUtils.setTraceContext(requestTraceId);

            filterChain.doFilter(request, response);
        }
        finally {
            LoggerUtils.clearTraceContext();
        }
    }

}
