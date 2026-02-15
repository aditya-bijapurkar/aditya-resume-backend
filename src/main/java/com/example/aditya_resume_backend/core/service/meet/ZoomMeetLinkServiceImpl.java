package com.example.aditya_resume_backend.core.service.meet;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.core.port.dto.MeetingDetailsDTO;
import com.example.aditya_resume_backend.core.port.dto.ScheduledMeetingDetailsDTO;
import com.example.aditya_resume_backend.core.port.dto.ZoomMeetingRequestBodyDTO;
import com.example.aditya_resume_backend.core.port.service.IMeetLinkService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class ZoomMeetLinkServiceImpl implements IMeetLinkService {

    @Value("${zoom.auth_token_url}")
    private String zoomAuthTokenUrl;
    @Value("${zoom.meet_link_url}")
    private String zoomMeetLinkUrl;
    @Value("${zoom.account_id}")
    private String zoomAccountId;
    @Value("${zoom.client_id}")
    private String zoomClientId;
    @Value("${zoom.client_secret}")
    private String zoomClientSecret;

    private final ObjectMapper objectMapper;

    @Autowired
    public ZoomMeetLinkServiceImpl() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private String getZoomAccessToken() throws IOException, InterruptedException {
        String credentials = String.format("%s:%s", zoomClientId, zoomClientSecret);
        String encodedAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        String accessTokenUrl = String.format(zoomAuthTokenUrl, zoomAccountId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(accessTokenUrl))
                .header(ApplicationConstants.AUTHORIZATION, ApplicationConstants.BASIC + encodedAuth)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Map<String, Object> responseMap = objectMapper.readValue(response.body(), new TypeReference<>() {});
        return (String) responseMap.get(ApplicationConstants.ACCESS_TOKEN);
    }

    @Override
    public ScheduledMeetingDetailsDTO generateMeetingLink(MeetingDetailsDTO meetingDetails) throws Exception {
        String accessToken = getZoomAccessToken();

        ZoomMeetingRequestBodyDTO requestBodyDTO = new ZoomMeetingRequestBodyDTO(
                    meetingDetails.getDescription().isBlank() ? ApplicationConstants.DEFAULT_MEET_SUMMARY : meetingDetails.getDescription(),
                    meetingDetails.getMeetingTime()
                );
        String requestBody = objectMapper.writeValueAsString(requestBodyDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(zoomMeetLinkUrl))
                .header(ApplicationConstants.AUTHORIZATION, String.format(ApplicationConstants.BEARER_PARAM, accessToken))
                .header(ApplicationConstants.CONTENT_TYPE, ApplicationConstants.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), ScheduledMeetingDetailsDTO.class);
    }

}
