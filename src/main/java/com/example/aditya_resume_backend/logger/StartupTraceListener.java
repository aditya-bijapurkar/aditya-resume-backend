package com.example.aditya_resume_backend.logger;

import com.example.aditya_resume_backend.utils.LoggerUtils;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StartupTraceListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(StartupTraceListener.class);

    // No need to clear the MDC as the main thread will run throughout the lifecycle of the application
    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        String traceId = LoggerUtils.generateRandomTraceId();
        LoggerUtils.setTraceContext(traceId);

        logger.info("Application starting with traceId: {}", traceId);
    }

}
