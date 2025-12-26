package com.example.aditya_resume_backend.utils;

import com.example.aditya_resume_backend.dto.ApiResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ResponseUtils {

    @Value("${web.redirection_location}")
    private String defaultRedirectionLocation;

    private static String redirectionLocation ;

    @PostConstruct
    public void init() {
        redirectionLocation = defaultRedirectionLocation;
    }

    public static <T> ResponseEntity<ApiResponse<T>> createApiResponse(HttpStatus httpStatus, String message, T data) {
        ApiResponse<T> response;
        if (data == null) {
            response = new ApiResponse<>(httpStatus.value(), message, null);
        } else {
            response = new ApiResponse<>(httpStatus.value(), message, data);
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    public static <T> ResponseEntity<T> createApiResponseWithHeaders(HttpStatus httpStatus, T data, HttpHeaders headers) {
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    public static ResponseEntity<Void> createRedirectResponse() {
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(redirectionLocation))
                .build();
    }

}

