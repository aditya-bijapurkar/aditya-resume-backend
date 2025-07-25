package com.example.aditya_resume_backend.core.port.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MeetingEmailsDTO {
    private UUID meetingId;
    private String emailIdsString;
}
