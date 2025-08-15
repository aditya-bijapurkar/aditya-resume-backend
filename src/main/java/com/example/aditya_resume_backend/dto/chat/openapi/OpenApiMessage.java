package com.example.aditya_resume_backend.dto.chat.openapi;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenApiMessage {
    private String role;
    private String content;
}
