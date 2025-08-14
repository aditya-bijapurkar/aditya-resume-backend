package com.example.aditya_resume_backend.constants;

public class ControllerConstants {

    private ControllerConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";

    public static final String X_RECAPTCHA_V3_TOKEN = "x-recaptcha-v3-token";
    public static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    public static final Double RECAPTCHA_VERIFY_SCORE = 0.5;

    public static final String BEARER_VALUE = "Bearer %s";

}