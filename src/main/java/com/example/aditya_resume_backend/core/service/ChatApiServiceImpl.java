package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.core.port.service.IChatApiService;
import com.example.aditya_resume_backend.dto.chat.ChatPromptRequest;
import com.example.aditya_resume_backend.dto.chat.ChatPromptResponse;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static com.example.aditya_resume_backend.constants.ChatConstants.*;

@Service
public class ChatApiServiceImpl implements IChatApiService {

    private static final Logger logger = LoggerFactory.getLogger(ChatApiServiceImpl.class);

    private final WebClient webClient;

    @Value("${openai.retrieval.topK}")
    private Integer topK;
    @Value("${openai.api.chat_model}")
    private String openaiChatModel;

    @Autowired
    public ChatApiServiceImpl(
            @Value("${openai.api.access_token}") String openaiAccessToken,
            @Value("${openai.api.base_url}") String openaiBaseUrl
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(openaiAccessToken);

        this.webClient = WebClient.builder()
                .baseUrl(openaiBaseUrl)
                .defaultHeaders(headers -> headers.addAll(httpHeaders))
                .build();
    }

    private List<Map<String, String>> setModelRequestContext(String userPrompt) {
        return List.of(
            Map.of(
                ROLE, USER,
                CONTENT, userPrompt
            )
        );
    }

    @Override
    public ChatPromptResponse generateModelResponse(ChatPromptRequest chatPromptRequest) throws AiModelException {
        try {
            Map<String, Object> requestBody = new java.util.HashMap<>(OPENAPI_REQUEST_BODY);
            requestBody.put(MODEL, openaiChatModel);
            requestBody.put(MESSAGES, setModelRequestContext(chatPromptRequest.getPrompt()));

            logger.info("Sending new prompt to OpenAi chat bot: {}", chatPromptRequest.getPrompt());

            OpenApiResponseDTO modelResponse = webClient.post()
                    .uri(CHAT_MODEL_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class).map(body -> new RuntimeException("OpenAI API error: " + body))
                    )
                    .bodyToMono(OpenApiResponseDTO.class)
                    .block();

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

}
