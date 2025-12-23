package com.example.aditya_resume_backend.core.port.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScheduledMeetingDetailsDTO {

    @JsonProperty("start_url")
    private String startUrl;

    @JsonProperty("join_url")
    private String joinUrl;

    @JsonProperty("password")
    private String password;

}
