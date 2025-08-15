package com.example.aditya_resume_backend.dto.chat.embeddings;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VectorEmbeddingResponse {
    private String model;
    private List<EmbeddingData> data;
}
