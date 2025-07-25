package com.example.aditya_resume_backend.controller;

import com.example.aditya_resume_backend.annotations.Time;
import com.example.aditya_resume_backend.core.port.service.IContactService;
import com.example.aditya_resume_backend.dto.ApiResponse;
import com.example.aditya_resume_backend.dto.contact.ContactMailRequest;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.aditya_resume_backend.constants.ControllerConstants.FAILED;
import static com.example.aditya_resume_backend.constants.ControllerConstants.SUCCESS;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("${RequestMapping.contact}")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final IContactService contactService;

    @Autowired
    public ContactController(IContactService contactService) {
        this.contactService = contactService;
    }

    @Time(metricName = "contact_email", apiName = "send")
    @PostMapping("${Routes.contact.send}")
    ResponseEntity<ApiResponse<Boolean>> sendContactEmail(
            @RequestBody ContactMailRequest contactMailRequest
    ) {
        try {
            contactService.sendContactMail(contactMailRequest);
            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, Boolean.TRUE);
        }
        catch (Exception e) {
            logger.error("Error in fetching availability {}", e.getMessage());
            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, FAILED, null);
        }
    }

}
