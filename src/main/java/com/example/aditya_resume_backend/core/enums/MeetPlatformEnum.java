package com.example.aditya_resume_backend.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetPlatformEnum {
    GOOGLE_MEET("google_meet"),
    ZOOM_MEET("zoom_meet");

    public final String value;

    public static MeetPlatformEnum getEnumFromString(String value) {
        for(MeetPlatformEnum meetPlatformEnum : MeetPlatformEnum.values()) {
            if(meetPlatformEnum.getValue().equals(value)) {
                return meetPlatformEnum;
            }
        }

        throw new IllegalArgumentException("Unsupported meeting platform: " + value);
    }

}