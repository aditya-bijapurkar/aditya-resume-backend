package com.example.aditya_resume_backend.dto.chat.perplexity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PerplexityResponseDTO {
    private UUID id;
    private String model;
    private List<PerplexityResponseChoice> choices;
}
