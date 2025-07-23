package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.dto.get_availability.ScheduleAvailabilityResponse;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public interface ISchedulerService {

    ScheduleAvailabilityResponse getAvailableSlots(LocalDate date);

    void initiateMeetingRequest(ScheduleMeetRequest scheduleMeetRequest) throws TemplateException, MessagingException, IOException;

    void acceptMeetingRequest(UUID meetingId, String response) throws TemplateException, MessagingException, IOException;

}
