package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
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

    @Value("${google.calendar_event_scope}")
    private String googleCalendarEventScope;
    @Value("${google.access_key_base64}")
    private String googleAccessKeyBase64;
    @Value("${google.tokens_dir}")
    private String tokensDir;
    @Value("${aws.ec2.callback_port}")
    private Integer ec2CallbackPort;
    @Value("${aws.ec2.public_dns}")
    private String ec2PublicDns;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Calendar getCalendarService() throws Exception {
        byte[] decoded = Base64.getDecoder().decode(googleAccessKeyBase64);
        InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(decoded), StandardCharsets.UTF_8);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, in);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                List.of(googleCalendarEventScope)
        ).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDir)))
                .setAccessType(ApplicationConstants.CALENDAR_ACCESS_TYPE)
                .setApprovalPrompt(ApplicationConstants.CALENDAR_APPROVAL_PROMPT)
                .build();

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new AuthorizationCodeInstalledApp(flow,
                        new LocalServerReceiver.Builder()
                                .setPort(ec2CallbackPort)
                                .setHost(ec2PublicDns)
                                .build())
                        .authorize(ApplicationConstants.USER)
        ).setApplicationName(ApplicationConstants.APPLICATION_NAME).build();
    }

    @Override
    public String generateGoogleMeetingLink(LocalDateTime dateTime) throws Exception {
        Calendar service = getCalendarService();
        ZoneId ist = ZoneId.of(ApplicationConstants.IST);

        Event event = new Event().setSummary(ApplicationConstants.DEFAULT_MEET_SUMMARY);

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(dateTime.atZone(ist).toInstant())))
                .setTimeZone(ApplicationConstants.IST);

        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(dateTime.plusHours(1).atZone(ist).toInstant())))
                .setTimeZone(ApplicationConstants.IST);

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
