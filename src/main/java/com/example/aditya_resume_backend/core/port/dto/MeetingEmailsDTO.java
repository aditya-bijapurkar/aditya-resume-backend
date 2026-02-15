package com.example.aditya_resume_backend.core.port.dto;

import java.util.UUID;

public interface MeetingEmailsDTO {
    UUID getMeetingId();
    String[] getAttendeeEmails();
}
