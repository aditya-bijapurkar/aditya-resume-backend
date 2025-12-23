package com.example.aditya_resume_backend.core.port.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduledMeetingDetailsDTO {

    @JsonProperty("start_url")
    private String startUrl;

    @JsonProperty("join_url")
    private String joinUrl;

    @JsonProperty("password")
    private String password;

}
