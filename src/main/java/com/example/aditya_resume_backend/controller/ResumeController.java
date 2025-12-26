package com.example.aditya_resume_backend.controller;

import com.example.aditya_resume_backend.annotations.Time;
import com.example.aditya_resume_backend.core.port.service.IResumeService;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.aditya_resume_backend.constants.ControllerConstants.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("${RequestMapping.resume}")
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    private final IResumeService resumeService;

    @Autowired
    public ResumeController(IResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @Time(metricName = "resume_download", apiName = "download")
    @GetMapping("${Routes.resume.download}")
    public ResponseEntity<InputStreamResource> downloadResume() {
        try {
            InputStreamResource s3Resource = resumeService.downloadResume();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(ATTACHMENT, FILE_NAME);

            return ResponseUtils.createApiResponseWithHeaders(HttpStatus.OK, s3Resource, headers);
        }
        catch (Exception e) {
            logger.error("Error in downloading resume from S3 bucket: {}", e.getMessage());
            return ResponseUtils.createApiResponseWithHeaders(HttpStatus.INTERNAL_SERVER_ERROR, null, null);
        }
    }

}
