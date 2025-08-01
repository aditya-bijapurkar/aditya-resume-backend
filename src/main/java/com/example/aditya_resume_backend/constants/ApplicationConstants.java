package com.example.aditya_resume_backend.constants;

public class ApplicationConstants {

    private ApplicationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String IST = "Asia/Kolkata";
    public static final Integer WORK_START_TIME = 9;
    public static final Integer WORK_END_TIME = 18;

    public static final String DEFAULT_REDIRECTION_LOCATION = "https://adityabijapurkar.in";

    public static final String EC2_PUBLIC_DNS = "ec2-13-233-192-71.ap-south-1.compute.amazonaws.com";
    public static final Integer CALLBACK_PORT = 8888;

    public static final String CALENDAR_ID = "primary";
    public static final String MEET_TYPE = "hangoutsMeet";
    public static final String VIDEO = "video";
    public static final String USER = "user";
    public static final String APPLICATION_NAME = "Aditya Meeting Schedule";
    public static final String DEFAULT_MEET_SUMMARY = "Scheduled meet with Aditya Bijapurkar";

}
