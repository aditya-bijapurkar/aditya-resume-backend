package com.example.aditya_resume_backend.exceptions;

public class RecaptchaFailedException extends Exception {
    public RecaptchaFailedException(String message) {
        super(message);
    }
}
