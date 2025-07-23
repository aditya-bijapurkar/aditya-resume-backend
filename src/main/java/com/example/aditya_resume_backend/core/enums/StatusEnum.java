package com.example.aditya_resume_backend.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    PENDING_APPROVAL("pending_approval"),
    SCHEDULED("scheduled"),
    DECLINED("declined");

    public final String value;

    public static StatusEnum getEnumFromString(String value) {
        for(StatusEnum statusEnum : StatusEnum.values()) {
            if(statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }

        throw new IllegalArgumentException("Unsupported status type: " + value);
    }

}
