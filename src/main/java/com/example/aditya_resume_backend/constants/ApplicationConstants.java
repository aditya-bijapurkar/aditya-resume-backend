package com.example.aditya_resume_backend.constants;

import java.util.Map;

public class ApplicationConstants {

    private ApplicationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String IST = "Asia/Kolkata";
    public static final Integer WORK_START_TIME = 9;
    public static final Integer WORK_END_TIME = 18;

    public static final String CALENDAR_ID = "primary";
    public static final String CALENDAR_ACCESS_TYPE = "offline";
    public static final String CALENDAR_APPROVAL_PROMPT = "force";
    public static final String MEET_TYPE = "hangoutsMeet";
    public static final String VIDEO = "video";
    public static final String USER = "user";
    public static final String APPLICATION_NAME = "Aditya Meeting Schedule";
    public static final String DEFAULT_MEET_SUMMARY = "Scheduled meet with Aditya Bijapurkar";

}
