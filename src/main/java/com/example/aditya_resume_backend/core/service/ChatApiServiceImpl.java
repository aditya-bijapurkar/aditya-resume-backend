package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.constants.ControllerConstants;
import com.example.aditya_resume_backend.core.port.service.IChatApiService;
import com.example.aditya_resume_backend.dto.chat.ChatPromptRequest;
import com.example.aditya_resume_backend.dto.chat.ChatPromptResponse;
import com.example.aditya_resume_backend.dto.chat.perplexity.PerplexityResponseDTO;
import com.example.aditya_resume_backend.exceptions.AiModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ChatApiServiceImpl implements IChatApiService {

    private static final Logger logger = LoggerFactory.getLogger(ChatApiServiceImpl.class);

    private final WebClient webClient;

    private static final String ROLE = "role";
    private static final String USER = "user";
    private static final String CONTENT = "content";

    @Autowired
    public ChatApiServiceImpl(
            @Value("${perplexity_ai.model_url}") String perplexityAiModelUrl,
            @Value("${perplexity_ai.access_token}") String perplexityAiAccessToken
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(perplexityAiModelUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, String.format(ControllerConstants.BEARER_VALUE, perplexityAiAccessToken))
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

            Map<String, Object> perplexityRequestBody = new java.util.HashMap<>(ApplicationConstants.DEFAULT_PERPLEXITY_REQUEST_BODY);
            perplexityRequestBody.put(ApplicationConstants.MESSAGES, setModelRequestContext(chatPromptRequest.getPrompt()));

            PerplexityResponseDTO modelResponse = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(perplexityRequestBody)
                    .retrieve()
                    .bodyToMono(PerplexityResponseDTO.class)
                    .block();

            assert modelResponse != null;
            return ChatPromptResponse.builder()
                    .response(modelResponse.getChoices().get(0).getMessage().getContent())
                    .build();
        }
        catch (Exception e) {
            logger.error("Some error occurred in getting AI model's response: {}", e.getMessage());
            throw new AiModelException(e.getMessage());
        }
    }

}
