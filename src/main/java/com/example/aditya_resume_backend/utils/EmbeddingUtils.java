package com.example.aditya_resume_backend.utils;

import com.example.aditya_resume_backend.dto.chat.embeddings.DocumentEmbeddings;
import com.example.aditya_resume_backend.exceptions.GenericRuntimeException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class EmbeddingUtils {

    private EmbeddingUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static double cosineSimilarity(List<Double> a, List<Double> b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for(int i = 0; i < a.size(); i++) {
            dotProduct += a.get(i) * b.get(i);
            normA += Math.pow(a.get(i), 2);
            normB += Math.pow(b.get(i), 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static List<DocumentEmbeddings> parseStringToDocuments(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<DocumentEmbeddings>>() {});
        }
        catch (Exception e) {
            throw new GenericRuntimeException("Failed to parse JSON into DocumentEmbeddings list");
        }
    }

}
