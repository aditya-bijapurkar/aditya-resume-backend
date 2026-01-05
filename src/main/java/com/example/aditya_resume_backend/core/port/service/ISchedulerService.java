package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.dto.get_availability.ScheduleAvailabilityResponse;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleList;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;

import java.time.LocalDate;
import java.util.UUID;

public interface ISchedulerService {

    ScheduleAvailabilityResponse getAvailableSlots(LocalDate date);

    void initiateMeetingRequest(ScheduleMeetRequest scheduleMeetRequest) throws Exception;

    void respondToSchedule(UUID meetingId, String response) throws Exception;

    ScheduleList getScheduledList(String emailId);
}
