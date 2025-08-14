package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.EmailConstants;
import com.example.aditya_resume_backend.core.port.service.IContactService;
import com.example.aditya_resume_backend.core.port.service.IEmailService;
import com.example.aditya_resume_backend.dto.contact.ContactMailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactServiceImpl implements IContactService {

    private final IEmailService emailService;

    @Autowired
    public ContactServiceImpl(IEmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendContactMail(ContactMailRequest contactMailRequest) {
        String adminSubject = String.format(EmailConstants.NEW_CONTACT_MAIL, contactMailRequest.getName(), contactMailRequest.getSubject());
        String adminText = String.format(EmailConstants.NEW_CONTACT_MAIL, contactMailRequest.getEmailId(), contactMailRequest.getText());
        emailService.sendSimpleMail(null, adminSubject, adminText);

        String copySubject = String.format(EmailConstants.COPY_OF, contactMailRequest.getSubject());
        String copyText = String.format(EmailConstants.COPY_OF, contactMailRequest.getText());
        emailService.sendSimpleMail(contactMailRequest.getEmailId(), copySubject, copyText);
    }

}
