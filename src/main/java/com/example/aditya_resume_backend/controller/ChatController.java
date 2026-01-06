package com.example.aditya_resume_backend.controller;

import com.example.aditya_resume_backend.core.port.service.IChatApiService;
import com.example.aditya_resume_backend.dto.ApiResponse;
import com.example.aditya_resume_backend.dto.chat.ChatPromptRequest;
import com.example.aditya_resume_backend.dto.chat.ChatPromptResponse;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.aditya_resume_backend.constants.ControllerConstants.FAILED;
import static com.example.aditya_resume_backend.constants.ControllerConstants.SUCCESS;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "${RequestMapping.chat}")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final IChatApiService chatApiService;

    @Autowired
    public ChatController(IChatApiService chatApiService) {
        this.chatApiService = chatApiService;
    }

    @PostMapping("${Routes.chat.response}")
    ResponseEntity<ApiResponse<ChatPromptResponse>> getChatbotResponse(
            @RequestBody ChatPromptRequest chatPromptRequest
    ) {
        try {
            ChatPromptResponse chatPromptResponse = chatApiService.generateModelResponse(chatPromptRequest);
            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, chatPromptResponse);
        }
        catch (Exception e) {
            logger.error("Error in fetching chat response from RAG model {}", e.getMessage());
            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, FAILED, null);
        }
    }

}
