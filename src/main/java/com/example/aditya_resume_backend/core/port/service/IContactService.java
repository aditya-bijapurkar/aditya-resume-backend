package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.dto.contact.ContactMailRequest;

public interface IContactService {

    void sendContactMail(ContactMailRequest contactMailRequest);

}
