package com.example.aditya_resume_backend.constants;

public class LoggerConstants {

    private LoggerConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SERVICE_NAME = "aditya-resume-backend";

    public static final String TRACE_ID_HEADER = "trace_id";

    public static final String LOGGER_TRACE_ID = "trace_id";
    public static final String LOGGER_SPAN_ID = "span_id";

}
