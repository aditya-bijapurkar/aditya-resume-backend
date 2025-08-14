package com.example.aditya_resume_backend.dto.chat.perplexity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerplexityMessage {
    private String role;
    private String content;
}
