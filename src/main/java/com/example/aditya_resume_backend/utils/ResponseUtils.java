package com.example.aditya_resume_backend.utils;

import com.example.aditya_resume_backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ResponseUtils {

    // utils are not instantiatable
    private ResponseUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String DEFAULT_REDIRECT_LOCATION = "https://google.com";

    public static <T> ResponseEntity<ApiResponse<T>> createApiResponse(HttpStatus httpStatus, String message, T data) {
        ApiResponse<T> response;
        if (data == null) {
            response = new ApiResponse<>(httpStatus.value(), message, null);
        } else {
            response = new ApiResponse<>(httpStatus.value(), message, data);
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity createRedirectResponse() {
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(DEFAULT_REDIRECT_LOCATION))
                .build();
    }

}

