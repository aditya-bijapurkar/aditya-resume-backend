package com.example.aditya_resume_backend.controller;

import com.example.aditya_resume_backend.annotations.Time;
import com.example.aditya_resume_backend.constants.EmailConstants;
import com.example.aditya_resume_backend.core.port.service.ISchedulerService;
import com.example.aditya_resume_backend.dto.ApiResponse;
import com.example.aditya_resume_backend.dto.get_availability.ScheduleAvailabilityResponse;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleList;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import com.example.aditya_resume_backend.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.UUID;

import static com.example.aditya_resume_backend.constants.ControllerConstants.*;

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

    @Time(metricName = "schedule_availability", apiName = "availability")
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

    @Time(metricName = "schedule_initiate", apiName = "initiate")
    @PostMapping("${Routes.schedule.initiate}")
    public ResponseEntity<ApiResponse<Boolean>> initiateMeetingSchedule(
        @RequestBody ScheduleMeetRequest scheduleMeetRequest
    ) {
        try {
            schedulerService.initiateMeetingRequest(scheduleMeetRequest);
            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, null);
        }
        catch (Exception e) {
            logger.error("Error in scheduling meet {}", e.getMessage());
            String responseMessage = e.getMessage().equals(TIMESLOT_ERROR)
                    ? TIMESLOT_ERROR_MESSAGE
                    : FAILED;

            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, responseMessage, null);
        }
    }

    @Time(metricName = "schedule_respond", apiName = "respond")
    @GetMapping("${Routes.schedule.respond}")
    public ResponseEntity<Void> respondToSchedule(
        @RequestParam(name = "meetingId") UUID meetingId,
        @RequestParam(name = "response") String response
    ) {
        try {
            schedulerService.respondToSchedule(meetingId, response);
            return ResponseUtils.createRedirectResponse();
        }
        catch (Exception e) {
            logger.error("Exception occurred in accepting meet {}", e.getMessage());
            return null;
        }
    }

    @Time(metricName = "fetch_list", apiName = "fetch")
    @GetMapping("${Routes.schedule.fetch}")
    public ResponseEntity<ApiResponse<ScheduleList>> fetchScheduleList(
        HttpServletRequest request
    ) {
        try {
            String email = (String) request.getAttribute(EmailConstants.EMAIL);
            if(email == null || email.isEmpty()) {
                logger.info("Cannot fetch calls, could not get email_id from auth token");
                return ResponseUtils.createApiResponse(HttpStatus.FORBIDDEN, FAILED, null);
            }

            ScheduleList scheduleList = schedulerService.getScheduledList(email);

            return ResponseUtils.createApiResponse(HttpStatus.OK, SUCCESS, scheduleList);
        }
        catch (Exception e) {
            logger.error("Error in fetching scheduled called list {}", e.getMessage());
            return ResponseUtils.createApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, FAILED, null);
        }
    }

}
