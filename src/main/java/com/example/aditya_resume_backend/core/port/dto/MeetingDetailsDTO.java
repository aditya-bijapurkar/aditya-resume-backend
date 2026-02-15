package com.example.aditya_resume_backend.core.port.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MeetingDetailsDTO {
    LocalDateTime meetingTime;
    String description;
    String[] attendeeEmails;
}
