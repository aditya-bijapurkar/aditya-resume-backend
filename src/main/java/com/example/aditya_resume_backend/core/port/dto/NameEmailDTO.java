package com.example.aditya_resume_backend.core.port.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NameEmailDTO {
    private String firstName;
    private String emailId;
}
