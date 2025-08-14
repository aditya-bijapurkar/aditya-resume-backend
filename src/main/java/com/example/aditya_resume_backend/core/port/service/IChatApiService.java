package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.dto.chat.ChatPromptRequest;
import com.example.aditya_resume_backend.dto.chat.ChatPromptResponse;
import com.example.aditya_resume_backend.exceptions.AiModelException;

public interface IChatApiService {

    ChatPromptResponse generateModelResponse(ChatPromptRequest chatPromptRequest) throws AiModelException;

}
