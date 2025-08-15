package com.example.aditya_resume_backend.dto.chat.openapi;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenApiResponseChoice {
    private Integer index;
    private String finishReason;
    private OpenApiMessage message;
    private OpenApiMessage delta;
}
