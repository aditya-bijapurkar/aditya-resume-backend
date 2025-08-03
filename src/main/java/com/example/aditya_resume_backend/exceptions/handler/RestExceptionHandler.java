package com.example.aditya_resume_backend.exceptions.handler;

import com.example.aditya_resume_backend.dto.ApiResponse;
import com.example.aditya_resume_backend.exceptions.RecaptchaFailedException;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RecaptchaFailedException.class)
    public ResponseEntity<ApiResponse<Boolean>> handleRecaptchaError(RecaptchaFailedException ex) {
        return ResponseUtils.createApiResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

}
