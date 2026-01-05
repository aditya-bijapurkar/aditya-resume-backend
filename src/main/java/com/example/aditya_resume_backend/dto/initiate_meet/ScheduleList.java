package com.example.aditya_resume_backend.dto.initiate_meet;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScheduleList {
    private List<Schedule> scheduleList;
}
