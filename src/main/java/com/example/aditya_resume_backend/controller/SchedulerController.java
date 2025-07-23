package com.example.aditya_resume_backend.controller;

import com.example.aditya_resume_backend.core.port.service.ISchedulerService;
import com.example.aditya_resume_backend.dto.ApiResponse;
import com.example.aditya_resume_backend.dto.get_availability.ScheduleAvailabilityResponse;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetResponse;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.UUID;

import static com.example.aditya_resume_backend.constants.ControllerConstants.FAILED;
import static com.example.aditya_resume_backend.constants.ControllerConstants.SUCCESS;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("${RequestMapping.schedule}")
public class SchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    private final ISchedulerService schedulerService;

    @Autowired
    public SchedulerController(ISchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @GetMapping("${Routes.schedule.availability}")
    public ResponseEntity<ApiResponse<ScheduleAvailabilityResponse>> getScheduleAvailability(
            @RequestParam(name = "date") LocalDate date
    ) {
        try {
            ScheduleAvailabilityResponse availableSlots = schedulerService.getAvailableSlots(date);
            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, availableSlots);
        }
        catch (Exception e) {
            logger.error("Error in fetching availability {}", e.getMessage());
            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, FAILED, null);
        }
    }

    @PostMapping("${Routes.schedule.initiate}")
    public ResponseEntity<ApiResponse<ScheduleMeetResponse>> initiateMeetingSchedule(
        @RequestBody ScheduleMeetRequest scheduleMeetRequest
    ) {
        try {
            schedulerService.initiateMeetingRequest(scheduleMeetRequest);
            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, null);
        }
        catch (Exception e) {
            logger.error("Error in scheduling meet {}", e.getMessage());
            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, FAILED, null);
        }
    }

    @GetMapping("${Routes.schedule.respond}")
    public ResponseEntity initiateMeetingSchedule(
        @RequestParam(name = "meetingId") UUID meetingId,
        @RequestParam(name = "response") String response
    ) {
        try {
            schedulerService.acceptMeetingRequest(meetingId, response);
            return ResponseUtils.createRedirectResponse();
        }
        catch (Exception e) {
            logger.error("Exception occurred in accepting meet {}", e.getMessage());
            return null;
        }
    }

}
