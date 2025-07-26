package com.example.aditya_resume_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Base64;

public class AwsSecretsManagerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(AwsSecretsManagerInitializer.class);
    private static final String TOKENS_DIRECTORY = "tokens";
    private static final String STORED_CREDENTIAL_FILE = "StoredCredential";
    private static final String STORED_CREDENTIAL_BASE64_KEY = "STORED_CREDENTIAL_BASE64";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println(System.getenv("DB_PASSWORD"));
        try {
            String storedCredentialBase64 = System.getenv(STORED_CREDENTIAL_BASE64_KEY);
            logger.info("base64: {}", storedCredentialBase64);
            java.io.File tokensDir = new java.io.File(TOKENS_DIRECTORY);
            if (!tokensDir.exists()) {
                tokensDir.mkdirs();
                logger.info("Created tokens directory");
            }
            
            storedCredentialBase64 = storedCredentialBase64.trim().replaceAll("\\s+", "");
            
            try {
                byte[] storedCredentialBytes = Base64.getDecoder().decode(storedCredentialBase64);
                
                java.io.File storedCredentialFile = new java.io.File(tokensDir, STORED_CREDENTIAL_FILE);
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(storedCredentialFile)) {
                    fos.write(storedCredentialBytes);
                    logger.info("saving this to token {}", Arrays.toString(storedCredentialBytes));
                }
                
                logger.info("Created StoredCredential file from base64-encoded serialized HashMap ({} bytes)", storedCredentialBytes.length);
            } catch (IllegalArgumentException e) {
                logger.error("Failed to decode base64 StoredCredential content: {}", e.getMessage());
                throw new RuntimeException("Invalid base64 encoding for StoredCredential", e);
            }
            
        } catch (Exception e) {
            logger.error("Failed to create tokens folder", e);
        }
    }

} 