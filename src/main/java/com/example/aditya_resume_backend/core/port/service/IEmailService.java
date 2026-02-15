package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.core.port.dto.ScheduledMeetingDetailsDTO;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IEmailService {

    void sendMeetRequestEmail(UUID meetingId, ScheduleMeetRequest scheduleMeetRequest) throws IOException, TemplateException, MessagingException;

    void sendMeetScheduleEmail(List<String> recipients, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException;

    void sendConfirmationEmail(String[] attendeeEmails, ScheduledMeetingDetailsDTO scheduledMeeting, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException;

    void sendConfirmationEmailToAdmin(String[] attendeeEmails, ScheduledMeetingDetailsDTO scheduledMeeting, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException;

    void sendRejectionEmail(String[] attendeeEmails, LocalDateTime scheduleTime) throws IOException, TemplateException, MessagingException;

    void sendSimpleMail(String toEmail, String subject, String text);

}
