package com.example.aditya_resume_backend.core.port.dto;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ZoomMeetingRequestBodyDTO {

    private final String topic;

    private final int type = 2;

    @JsonProperty("start_time")
    private final LocalDateTime startTime;

    private final int duration = 60;

    private final String timezone = ApplicationConstants.IST;

    private final Settings settings = new Settings();

    public ZoomMeetingRequestBodyDTO(
            String topic,
            LocalDateTime startTime
    ) {
        this.topic = topic;
        this.startTime = startTime;
    }

    @Getter
    public static class Settings {

        @JsonProperty("join_before_host")
        private final boolean joinBeforeHost = true;

        @JsonProperty("waiting_room")
        private final boolean waitingRoom = false;
    }

}
