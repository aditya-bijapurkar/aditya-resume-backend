package com.example.aditya_resume_backend.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatPromptRequest {
    private String prompt;
}
