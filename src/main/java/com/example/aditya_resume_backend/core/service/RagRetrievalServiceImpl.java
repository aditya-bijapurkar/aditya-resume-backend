package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.core.port.service.IRagRetrievalService;
import com.example.aditya_resume_backend.dto.chat.embeddings.DocumentEmbeddings;
import com.example.aditya_resume_backend.utils.AWSUtils;
import com.example.aditya_resume_backend.utils.EmbeddingUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Service
public class RagRetrievalServiceImpl implements IRagRetrievalService  {

    private static final Logger logger = LoggerFactory.getLogger(RagRetrievalServiceImpl.class);

    @Value("${aws.s3.bucket_name}")
    private String s3BucketName;
    @Value("${aws.s3.vector_embeddings_key}")
    private String vectorEmbeddingsKey;
    @Value("${openai.retrieval.topK}")
    private Integer topK;
    @Value("${openai.retrieval.threshold}")
    private Double threshold;

    private static List<DocumentEmbeddings> documentEmbeddings;

    @PostConstruct
    public void loadEmbeddingsFromS3() {
        logger.info("Downloading context file from S3 bucket...");
        String jsonString = AWSUtils.downloadFromS3Bucket(s3BucketName, vectorEmbeddingsKey);

        logger.info("Storing context file as local knowledge vector embeddings...");
        documentEmbeddings = EmbeddingUtils.parseStringToDocuments(jsonString);

        logger.info("RAG service post construct process completed successfully!");
    }

    @Override
    public List<DocumentEmbeddings> getRelevantDocuments(List<Double> promptEmbedding) {
        return documentEmbeddings.stream()
                .map(doc -> new AbstractMap.SimpleEntry<>(
                        doc,
                        EmbeddingUtils.cosineSimilarity(promptEmbedding, doc.getVectorEmbeddings())
                ))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .filter(e -> e.getValue() >= threshold)
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }
}
