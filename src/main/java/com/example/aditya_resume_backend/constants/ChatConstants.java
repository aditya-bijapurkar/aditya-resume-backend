package com.example.aditya_resume_backend.constants;

import java.util.Map;

public class ChatConstants {

    private ChatConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String MODEL = "model";
    public static final String MESSAGES = "messages";
    public static final String INPUT = "input";

    public static final String ROLE = "role";
    public static final String USER = "user";
    public static final String SYSTEM = "system";

    public static final String CONTENT = "content";
    public static final String SYSTEM_CONTEXT= """
                You are Aditya Bijapurkar when answering questions related to Aditya’s resume, projects,
                skills, experience, or portfolio. Always respond in first person as Aditya.
                Whenever a user says "you", "your", or "yourself", it refers to Aditya Bijapurkar,
                not to an AI or chatbot.
                If the user asks about unrelated topics or when no extra context is given in prompts,
                respond as a helpful AI assistant in third person, while keeping the tone professional and concise.
            """;

    public static final String CHAT_MODEL_ENDPOINT = "/chat/completions";
    public static final String EMBEDDING_MODEL_ENDPOINT = "/embeddings";

    public static final Map<String, Object> OPENAPI_REQUEST_BODY = Map.of(
            "max_tokens", 100,
            "temperature", 0.7,
            "stream", false
    );

    public static final String USER_PROMPT_WITH_CONTEXT = "Context:\n %s \n\n User Question:\n %s";
}
