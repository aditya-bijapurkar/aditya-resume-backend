package com.example.aditya_resume_backend.dto.initiate_meet;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Schedule {
    private LocalDateTime scheduledAt;
    private String description;
    private String meetPlatform;
    private String meetLink;
    private String meetPassword;
    private String status;
}
