package com.example.aditya_resume_backend.controller;

import com.example.aditya_resume_backend.dto.ApiResponse;
import com.example.aditya_resume_backend.dto.chat.ChatPromptRequest;
import com.example.aditya_resume_backend.dto.chat.ChatPromptResponse;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("${Routes.chat.response}")
    ResponseEntity<ApiResponse<ChatPromptResponse>> getChatbotResponse(
            @RequestBody ChatPromptRequest chatPromptRequest
    ) {
        try {
            ChatPromptResponse chatPromptResponse = ChatPromptResponse.builder()
                    .response("This feature is under construction, thanks for your patience!")
                    .build();
            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, chatPromptResponse);
        }
        catch (Exception e) {
            logger.error("Error in fetching chat response from RAG model {}", e.getMessage());
            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, FAILED, null);
        }
    }


}
