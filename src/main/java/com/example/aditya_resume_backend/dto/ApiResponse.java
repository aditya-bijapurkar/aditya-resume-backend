package com.example.aditya_resume_backend.dto;

public record ApiResponse<T>(int status, String message, T data) {

}