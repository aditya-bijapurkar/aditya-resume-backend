package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.core.port.dto.MeetingDetailsDTO;
import com.example.aditya_resume_backend.core.port.dto.ScheduledMeetingDetailsDTO;

public interface IMeetLinkService {

    ScheduledMeetingDetailsDTO generateMeetingLink(MeetingDetailsDTO meetingDetails) throws Exception;

}
