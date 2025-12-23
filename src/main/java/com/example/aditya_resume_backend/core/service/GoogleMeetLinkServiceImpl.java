package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.core.port.dto.MeetingDetailsDTO;
import com.example.aditya_resume_backend.core.port.dto.ScheduledMeetingDetailsDTO;
import com.example.aditya_resume_backend.core.port.service.IMeetLinkService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class GoogleMeetLinkServiceImpl implements IMeetLinkService {

    @Value("${google.calendar_event_scope}")
    private String googleCalendarEventScope;
    @Value("${google.access_key_base64}")
    private String googleAccessKeyBase64;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Calendar getCalendarService() throws Exception {
        byte[] decoded = Base64.getDecoder().decode(googleAccessKeyBase64);

        ServiceAccountCredentials serviceAccount = ServiceAccountCredentials.fromStream(new ByteArrayInputStream(decoded));

        GoogleCredentials scopedCredentials = serviceAccount.createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        HttpCredentialsAdapter requestInitializer = new HttpCredentialsAdapter(scopedCredentials);

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                requestInitializer
            )
            .setApplicationName(ApplicationConstants.APPLICATION_NAME)
            .build();
    }

    @Override
    public ScheduledMeetingDetailsDTO generateMeetingLink(MeetingDetailsDTO meetingDetails) throws Exception {
        // NO LONGER USING THIS METHOD
        // to generate google meeting links via calendar events we have 2 options
        //     1. refresh token -> needs to be refreshed manually on server
        //     2. service account -> additional cost for having Google Workspace plan

        LocalDateTime dateTime = meetingDetails.getMeetingTime();
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

        return ScheduledMeetingDetailsDTO.builder()
                .startUrl(meetLink)
                .joinUrl(meetLink)
                .build();
    }

}
