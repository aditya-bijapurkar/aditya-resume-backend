package com.example.aditya_resume_backend.dto.initiate_meet;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ScheduleMeetResponse {
    private String platform;
    private LocalDateTime scheduleTime;
    private String meetLink;
}
