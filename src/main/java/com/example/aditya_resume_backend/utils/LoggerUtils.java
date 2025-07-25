package com.example.aditya_resume_backend.utils;

import com.example.aditya_resume_backend.constants.LoggerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoggerUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);

    private LoggerUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateRandomSpanId() {
        return String.format("%016x", UUID.randomUUID().getMostSignificantBits());
    }

    public static String generateRandomTraceId() {
        return String.format("%s:%s"
                ,LoggerConstants.SERVICE_NAME
                ,UUID.randomUUID());
    }

    public static void setTraceContext(String traceId) {
        if(traceId == null || traceId.isEmpty()) {
            traceId = generateRandomTraceId();
            logger.info("Generating a new trace_id {} due to lack of request headers", traceId);
        }

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put(LoggerConstants.LOGGER_TRACE_ID, traceId);
        contextMap.put(LoggerConstants.LOGGER_SPAN_ID, generateRandomSpanId());

        MDC.setContextMap(contextMap);
    }

    public static void clearTraceContext() {
        MDC.clear();
    }

    public static String getCurrentThreadTraceId() {
        return MDC.get(LoggerConstants.LOGGER_TRACE_ID);
    }

}

