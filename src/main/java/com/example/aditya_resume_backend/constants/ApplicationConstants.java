package com.example.aditya_resume_backend.constants;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
public class ApplicationConstants {

    private ApplicationConstants() {
        throw new IllegalStateException("Utility class");
    }

    @Value("${web.redirection_location}")
    private final String defaultRedirectionLocation;
    @Value("${aws.ec2.public_dns}")
    private final String ec2PublicDns;
    @Value("${aws.ec2.callback_port}")
    private final Integer ec2CallbackPort;

    public static String DEFAULT_REDIRECTION_LOCATION;
    public static String EC2_PUBLIC_DNS;
    public static Integer EC2_CALLBACK_PORT;

    @PostConstruct
    public void init() {
        DEFAULT_REDIRECTION_LOCATION = defaultRedirectionLocation;
        EC2_PUBLIC_DNS = ec2PublicDns;
        EC2_CALLBACK_PORT = ec2CallbackPort;
    }


    public static final String IST = "Asia/Kolkata";
    public static final Integer WORK_START_TIME = 9;
    public static final Integer WORK_END_TIME = 18;

    public static final String CALENDAR_ID = "primary";
    public static final String MEET_TYPE = "hangoutsMeet";
    public static final String VIDEO = "video";
    public static final String USER = "user";
    public static final String APPLICATION_NAME = "Aditya Meeting Schedule";
    public static final String DEFAULT_MEET_SUMMARY = "Scheduled meet with Aditya Bijapurkar";

}
