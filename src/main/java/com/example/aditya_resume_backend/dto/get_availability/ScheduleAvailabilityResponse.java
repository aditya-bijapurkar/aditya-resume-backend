package com.example.aditya_resume_backend.dto.get_availability;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ScheduleAvailabilityResponse {
    private List<LocalDateTime> availableSlots;
}
