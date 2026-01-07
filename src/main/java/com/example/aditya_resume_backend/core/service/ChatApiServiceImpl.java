package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.core.port.service.IChatApiService;
import com.example.aditya_resume_backend.core.port.service.IRagRetrievalService;
import com.example.aditya_resume_backend.dto.chat.ChatPromptRequest;
import com.example.aditya_resume_backend.dto.chat.ChatPromptResponse;
import com.example.aditya_resume_backend.dto.chat.embeddings.DocumentEmbeddings;
import com.example.aditya_resume_backend.dto.chat.embeddings.VectorEmbeddingResponse;
import com.example.aditya_resume_backend.dto.chat.openapi.OpenApiResponseDTO;
import com.example.aditya_resume_backend.exceptions.AiModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.aditya_resume_backend.constants.ChatConstants.*;

@Service
public class ChatApiServiceImpl implements IChatApiService {

    private static final Logger logger = LoggerFactory.getLogger(ChatApiServiceImpl.class);

    @Value("${openai.api.chat_model}")
    private String openaiChatModel;
    @Value("${openai.api.embedding_model}")
    private String openaiEmbeddingModel;

    private final RestClient restClient;
    private final IRagRetrievalService ragRetrievalService;

    @Autowired
    public ChatApiServiceImpl(
            @Value("${openai.api.access_token}") String openaiAccessToken,
            @Value("${openai.api.base_url}") String openaiBaseUrl,
            IRagRetrievalService ragRetrievalService
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(openaiAccessToken);
        this.restClient = RestClient.builder()
                .baseUrl(openaiBaseUrl)
                .defaultHeaders(headers -> headers.addAll(httpHeaders))
                .build();

        this.ragRetrievalService = ragRetrievalService;
    }

    private List<Map<String, String>> setModelRequestContext(String prompt) {
        return List.of(
            Map.of(
                ROLE, SYSTEM,
                CONTENT, SYSTEM_CONTEXT
            ),
            Map.of(
                ROLE, USER,
                CONTENT, prompt
            )
        );
    }

    private List<Double> getVectorEmbeddingForPrompt(String userPrompt) throws AiModelException {
        try {
            Map<String, Object> requestBody = new java.util.HashMap<>(OPENAPI_REQUEST_BODY);
            requestBody.put(MODEL, openaiEmbeddingModel);
            requestBody.put(INPUT, userPrompt);

            logger.info("Sending user prompt to OpenAi for embedding: {}...", userPrompt);

            VectorEmbeddingResponse vectorEmbedding = restClient.post()
                    .uri(EMBEDDING_MODEL_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String errorBody = response.getBody().readAllBytes() != null
                                ? new String(response.getBody().readAllBytes())
                                : "unknown error";
                        throw new RuntimeException("OpenAI embeddings API error: " + errorBody);
                    })
                    .body(VectorEmbeddingResponse.class);

            logger.info("Successfully fetched OpenAi embeddings from API response!");

            assert vectorEmbedding != null;
            return vectorEmbedding.getData().stream()
                    .findFirst().orElseThrow()
                    .getEmbedding();
        }
        catch (Exception e) {
            logger.error("Error in getting vector embeddings from OpenAi bot...");
            throw new AiModelException(e.getMessage());
        }
    }

    private String getFinalPrompt(String userPrompt, List<DocumentEmbeddings> topKDocuments) {
        if(topKDocuments == null || topKDocuments.isEmpty()) {
            return userPrompt;
        }

        String contextString = topKDocuments.stream()
                .map(DocumentEmbeddings::getText)
                .collect(Collectors.joining("\n\n"));

        return String.format(USER_PROMPT_WITH_CONTEXT, userPrompt, contextString);
    }

    private ChatPromptResponse generateChatModelResponse(String userPrompt, List<DocumentEmbeddings> topKDocuments) throws AiModelException {
        try {
            Map<String, Object> requestBody = new java.util.HashMap<>(OPENAPI_REQUEST_BODY);
            requestBody.put(MODEL, openaiChatModel);
            requestBody.put(MESSAGES, setModelRequestContext(getFinalPrompt(userPrompt, topKDocuments)));

            logger.info("Sending user prompt to OpenAi chat bot with {} supporting documents: {}...",
                    topKDocuments.size(),
                    userPrompt
            );

            OpenApiResponseDTO modelResponse = restClient.post()
                    .uri(CHAT_MODEL_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String errorBody = response.getBody().readAllBytes() != null
                                ? new String(response.getBody().readAllBytes())
                                : "unknown error";
                        throw new RuntimeException("OpenAI embeddings API error: " + errorBody);
                    })
                    .body(OpenApiResponseDTO.class);

            logger.info("Successfully fetched OpenAi chat bot's response!");

            assert modelResponse != null;
            return ChatPromptResponse.builder()
                    .response(modelResponse.getChoices().get(0).getMessage().getContent())
                    .build();
        }
        catch (Exception e) {
            logger.error("Error in getting response from OpenAi chat bot...");
            throw new AiModelException(e.getMessage());
        }
    }

    @Override
    public ChatPromptResponse generateModelResponse(ChatPromptRequest chatPromptRequest) throws AiModelException {
        List<Double> userPromptEmbedding = getVectorEmbeddingForPrompt(chatPromptRequest.getPrompt());

        List<DocumentEmbeddings> topKDocuments = ragRetrievalService.getRelevantDocuments(userPromptEmbedding);

        return generateChatModelResponse(chatPromptRequest.getPrompt(), topKDocuments);
    }

}
