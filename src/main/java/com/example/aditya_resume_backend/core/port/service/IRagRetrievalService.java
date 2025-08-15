package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.dto.chat.embeddings.DocumentEmbeddings;

import java.util.List;

public interface IRagRetrievalService {

    List<DocumentEmbeddings> getRelevantDocuments(List<Double> promptEmbedding);

}
