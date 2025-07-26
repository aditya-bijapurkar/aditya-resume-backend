package com.example.aditya_resume_backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AwsSecretsManagerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(AwsSecretsManagerInitializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String LOCAL_PROFILE = "local";
    private static final String AWS_SECRET_NAME_PROPERTY = "aws.secret-name";
    private static final String AWS_REGION_PROPERTY = "aws.region";
    private static final String DEFAULT_SECRET_NAME = "aditya-resume-backend-secrets";
    private static final String DEFAULT_REGION = "ap-south-1";
    private static final String AWS_SECRETS_PROPERTY_SOURCE = "aws-secrets";
    private static final String TOKENS_DIRECTORY = "tokens";
    private static final String STORED_CREDENTIAL_FILE = "StoredCredential";
    private static final String STORED_CREDENTIAL_BASE64_KEY = "STORED_CREDENTIAL_BASE64";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isLocal = false;
        for (String profile : activeProfiles) {
            if (LOCAL_PROFILE.equals(profile)) {
                isLocal = true;
                break;
            }
        }
        
        if (isLocal) {
            logger.info("Skipping AWS Secrets Manager initialization for local profile");
            return;
        }

        try {
            String secretName = environment.getProperty(AWS_SECRET_NAME_PROPERTY, DEFAULT_SECRET_NAME);
            String region = environment.getProperty(AWS_REGION_PROPERTY, DEFAULT_REGION);
            
            logger.info("Loading secrets from AWS Secrets Manager: {} in region: {}", secretName, region);
            
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            String secret = getSecretValueResponse.secretString();
            
            if (secret == null) {
                secret = new String(Base64.getDecoder().decode(getSecretValueResponse.secretBinary().asByteArray()));
            }

            JsonNode secretJson = objectMapper.readTree(secret);
            Map<String, Object> properties = new HashMap<>();
            
            secretJson.fieldNames().forEachRemaining(key -> {
                String value = secretJson.get(key).asText();
                properties.put(key, value);
                // Also set as system environment variable
                System.setProperty(key, value);
                logger.info("Set property: {}", key);
                logger.info("Set value: {}", value);
                logger.info("Set system property: {} = {}", key, key.contains("PASSWORD") ? "***MASKED***" : value);
            });
            
            MapPropertySource propertySource = new MapPropertySource(AWS_SECRETS_PROPERTY_SOURCE, properties);
            environment.getPropertySources().addFirst(propertySource);
            
            // Debug: Show property source order
            logger.info("Property source order after adding AWS secrets:");
            int i = 0;
            for (org.springframework.core.env.PropertySource<?> source : environment.getPropertySources()) {
                logger.info("{}: {}", i++, source.getName());
            }
            
            // Debug: Check if properties are accessible
            logger.info("Testing property access after adding to environment:");
            for (String key : properties.keySet()) {
                String envValue = environment.getProperty(key);
                String sysValue = System.getProperty(key);
                logger.info("Property '{}' - Environment: {}, System: {}", 
                    key, 
                    envValue != null ? "YES" : "NO",
                    sysValue != null ? "YES" : "NO");
                if (envValue != null) {
                    logger.info("Property '{}' env value: {}", key, key.contains("PASSWORD") ? "***MASKED***" : envValue);
                }
                if (sysValue != null) {
                    logger.info("Property '{}' sys value: {}", key, key.contains("PASSWORD") ? "***MASKED***" : sysValue);
                }
            }
            
            createTokensFolder(secretJson);
            
            logger.info("Added {} properties from AWS Secrets Manager", properties.size());
            client.close();

        } catch (Exception e) {
            logger.error("Failed to load secrets from AWS Secrets Manager", e);
        }
    }
    
    private void createTokensFolder(JsonNode secretJson) {
        try {
            java.io.File tokensDir = new java.io.File(TOKENS_DIRECTORY);
            if (!tokensDir.exists()) {
                tokensDir.mkdirs();
                logger.info("Created tokens directory");
            }

            if (secretJson.has(STORED_CREDENTIAL_BASE64_KEY)) {
                String storedCredentialBase64 = secretJson.get(STORED_CREDENTIAL_BASE64_KEY).asText();
                
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
            }
            
        } catch (Exception e) {
            logger.error("Failed to create tokens folder", e);
        }
    }
} 