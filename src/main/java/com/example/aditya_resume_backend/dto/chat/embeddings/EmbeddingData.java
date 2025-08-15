package com.example.aditya_resume_backend.dto.chat.embeddings;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmbeddingData {
    private String object;
    private Integer index;
    private List<Double> embedding;
}
