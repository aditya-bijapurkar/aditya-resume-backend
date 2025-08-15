package com.example.aditya_resume_backend.dto.chat.embeddings;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class DocumentEmbeddings {
    String text;
    List<Double> vectorEmbeddings;
}
