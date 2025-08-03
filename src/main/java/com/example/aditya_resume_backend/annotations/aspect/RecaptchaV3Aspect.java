package com.example.aditya_resume_backend.annotations.aspect;

import com.example.aditya_resume_backend.constants.ControllerConstants;
import com.example.aditya_resume_backend.exceptions.RecaptchaFailedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Aspect
@Component
public class RecaptchaV3Aspect {

    private static final Logger logger = LoggerFactory.getLogger(RecaptchaV3Aspect.class);

    private final HttpServletRequest httpServletRequest;
    private static final WebClient webClient = WebClient.create();

    @Value("${google.recaptcha_v3.secret_key}")
    private String secret;

    @Autowired
    public RecaptchaV3Aspect(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    private String getBodyRecaptchaBody(RecaptchaRequest request) {
        return String.format("secret=%s&response=%s",
                request.secret(),
                request.token()
        );
    }

    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void authorizeRecaptchaV3(JoinPoint joinPoint) throws RecaptchaFailedException {
        String methodName = joinPoint.getSignature().toShortString();
        String token = httpServletRequest.getHeader(ControllerConstants.X_RECAPTCHA_V3_TOKEN);

        if(token != null && !token.isEmpty()) {
            logger.info("Verifying human interaction using reCAPTCHA V3 before {}...", methodName);
            RecaptchaRequest request = new RecaptchaRequest(secret, token);

            RecaptchaResponse response = webClient.post()
                    .uri(ControllerConstants.RECAPTCHA_VERIFY_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(getBodyRecaptchaBody(request))
                    .retrieve()
                    .bodyToMono(RecaptchaResponse.class)
                    .block();

            if(
                response == null
                || !response.isSuccess()
                || response.getScore() < ControllerConstants.RECAPTCHA_VERIFY_SCORE
            ) {
                logger.warn("reCAPTCHA V3 failed to verify human interaction before {}...", methodName);
                throw new RecaptchaFailedException("reCAPTCHA V3 unable to verify that you are human, please try again...");
            }
            else {
                logger.info("Successfully verified human interaction, continuing with {}", methodName);
            }
        }
    }

    private record RecaptchaRequest(String secret, String token) {}

    @Data
    static class RecaptchaResponse {
        private boolean success;
        private float score;
        private String action;
        private String challenge_ts;
        private String hostname;
        private List<String> errorCodes;
    }

}
