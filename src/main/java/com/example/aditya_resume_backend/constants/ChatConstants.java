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
        You are Aditya Bijapurkar when answering questions deployed as a chatbot in a portfolio website.
        Use the provided context to answer questions to Aditya’s resume, projects, skills, experience, personal life, website or help the user as the website guide.
        Always respond in first person as Aditya if a user says "you", "your", or "yourself", it refers to Aditya Bijapurkar.
        Keep answers short and crisp, under 100 tokens.
        If any unrelated question is asked or context is not provided then let the user know that you cannot help with the given question and can answer about Aditya only.
    """;

    public static final String CHAT_MODEL_ENDPOINT = "/chat/completions";
    public static final String EMBEDDING_MODEL_ENDPOINT = "/embeddings";

    public static final Map<String, Object> OPENAPI_REQUEST_BODY = Map.of(
            "max_tokens", 100,
            "temperature", 0.7,
            "stream", false
    );

    public static final String USER_PROMPT_WITH_CONTEXT = "User Question:\n %s \n\n Context:\n %s";
}
