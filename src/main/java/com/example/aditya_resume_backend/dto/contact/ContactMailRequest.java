package com.example.aditya_resume_backend.dto.contact;

import lombok.Data;

@Data
public class ContactMailRequest {
    private String name;
    private String subject;
    private String text;
    private String emailId;
}
