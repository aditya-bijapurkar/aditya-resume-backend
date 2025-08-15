package com.example.aditya_resume_backend.dto.chat.openapi;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpenApiResponseDTO {
    private String model;
    private List<OpenApiResponseChoice> choices;
}
