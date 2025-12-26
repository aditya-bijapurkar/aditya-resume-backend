package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.core.port.service.IResumeService;
import com.example.aditya_resume_backend.utils.AWSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class ResumeServiceImpl implements IResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeServiceImpl.class);

    @Value("${aws.s3.bucket_name}")
    private String s3BucketName;
    @Value("${aws.s3.resume_key}")
    private String resumeKey;

    @Override
    public InputStreamResource downloadResume() {
        logger.info("Downloading Resume from S3 bucket...");
        ResponseInputStream<GetObjectResponse> s3Object = AWSUtils.downloadFromS3BucketAsInputStream(s3BucketName, resumeKey);
        logger.info("Successfully downloaded resume from S3 bucket!");
        return new InputStreamResource(s3Object);
    }

}
