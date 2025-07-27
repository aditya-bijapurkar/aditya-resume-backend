package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.constants.EmailConstants;
import com.example.aditya_resume_backend.core.port.service.IMeetLinkService;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MeetLinkServiceImpl implements IMeetLinkService {

    @Value("${google.access_key_base64}")
    private String GOOGLE_ACCESS_KEY_BASE64;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIR = "/tokens";
    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/calendar.events");

    private Calendar getCalendarService() throws Exception {
        byte[] decoded = Base64.getDecoder().decode(GOOGLE_ACCESS_KEY_BASE64);
        InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(decoded), StandardCharsets.UTF_8);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, in);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES
        ).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIR)))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new AuthorizationCodeInstalledApp(flow,
                        new LocalServerReceiver.Builder()
                                .setPort(ApplicationConstants.CALLBACK_PORT)
                                .setHost(ApplicationConstants.EC2_PUBLIC_DNS)
                                .build())
                        .authorize(ApplicationConstants.USER)
        ).setApplicationName(ApplicationConstants.APPLICATION_NAME).build();
    }

    @Override
    public String generateGoogleMeetingLink(LocalDateTime dateTime) throws Exception {
        Calendar service = getCalendarService();
        ZoneId ist = ZoneId.of(EmailConstants.IST);

        Event event = new Event().setSummary(ApplicationConstants.DEFAULT_MEET_SUMMARY);

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(dateTime.atZone(ist).toInstant())))
                .setTimeZone(EmailConstants.IST);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(dateTime.plusHours(1).atZone(ist).toInstant())))
                .setTimeZone(EmailConstants.IST);

        event.setStart(start);
        event.setEnd(end);

        CreateConferenceRequest createRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(
                        new ConferenceSolutionKey().setType(ApplicationConstants.MEET_TYPE)
                );

        event.setConferenceData(new ConferenceData().setCreateRequest(createRequest));

        Event createdEvent = service.events().insert(ApplicationConstants.CALENDAR_ID, event)
                .setConferenceDataVersion(1)
                .execute();

        String meetLink = createdEvent.getHangoutLink();

        if (meetLink == null && createdEvent.getConferenceData() != null) {
            meetLink = createdEvent.getConferenceData().getEntryPoints().stream()
                    .filter(e -> ApplicationConstants.VIDEO.equals(e.getEntryPointType()))
                    .map(EntryPoint::getUri)
                    .findFirst()
                    .orElse(null);
        }

        return meetLink;
    }
}
