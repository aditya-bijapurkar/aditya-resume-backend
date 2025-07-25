package com.example.aditya_resume_backend.core.port.service;

import java.time.LocalDateTime;

public interface IMeetLinkService {

    String generateGoogleMeetingLink(LocalDateTime dateTime) throws Exception;

}
