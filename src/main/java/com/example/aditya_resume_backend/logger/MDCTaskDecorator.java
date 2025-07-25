package com.example.aditya_resume_backend.logger;

import com.example.aditya_resume_backend.constants.LoggerConstants;
import com.example.aditya_resume_backend.utils.LoggerUtils;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

public class MDCTaskDecorator implements TaskDecorator {

    // Async functions should cascade the traceId from its parent but have different spanId
    @NonNull
    @Override
    public Runnable decorate(@NonNull Runnable runnable) {

        String parentThreadTraceId = MDC.getCopyOfContextMap().get(LoggerConstants.LOGGER_TRACE_ID);

        return () -> {
            LoggerUtils.setTraceContext(parentThreadTraceId);

            try {
                runnable.run();
            }
            finally {
                LoggerUtils.clearTraceContext();
            }
        };
    }

}
