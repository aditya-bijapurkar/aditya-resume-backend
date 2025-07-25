package com.example.aditya_resume_backend;

import com.example.aditya_resume_backend.logger.StartupTraceListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdityaResumeBackendApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AdityaResumeBackendApplication.class);
		app.addInitializers(new StartupTraceListener());
		app.run(args);
	}

}
