package com.example.aditya_resume_backend.dto.initiate_meet;

import java.time.LocalDateTime;

public interface Schedule {
    LocalDateTime getScheduledAt();
    String getDescription();
    String getMeetPlatform();
    String getMeetLink();
    String getMeetPassword();
    String getStatus();
}