package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.core.port.service.IHuggingFaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class HuggingFaceService implements IHuggingFaceService {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceService.class);

    @Autowired
    public HuggingFaceService() {

    }

    @Override
    public String generateModelResponse(String userPrompt) {
        return "";
    }

}
