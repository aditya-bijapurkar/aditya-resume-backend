package com.example.aditya_resume_backend.utils;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.charset.StandardCharsets;

public class AWSUtils {

    private AWSUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static S3Client getS3Client() {
        String region = System.getenv("AWS_REGION");

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    public static String downloadFromS3BucketAsString(String bucketName, String keyName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = getS3Client().getObjectAsBytes(objectRequest);

        return new String(objectBytes.asByteArray(), StandardCharsets.UTF_8);
    }

    public static ResponseInputStream<GetObjectResponse> downloadFromS3BucketAsInputStream(String bucketName, String keyName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        return getS3Client().getObject(objectRequest);
    }

}
