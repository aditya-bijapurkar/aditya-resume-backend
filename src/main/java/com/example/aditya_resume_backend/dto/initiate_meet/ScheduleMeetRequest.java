package com.example.aditya_resume_backend.dto.initiate_meet;

import com.example.aditya_resume_backend.core.port.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ScheduleMeetRequest {
    private String description;
    private LocalDateTime scheduleTime;
    private List<UserDTO> requiredUsers;
}
