package com.example.aditya_resume_backend.core.port.service;

import org.springframework.core.io.InputStreamResource;

public interface IResumeService {

    InputStreamResource downloadResume();

}
