package com.example.aditya_resume_backend.dto.chat.perplexity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerplexityResponseChoice {
    private Integer index;
    private String finishReason;
    private PerplexityMessage message;
    private PerplexityMessage delta;
}
