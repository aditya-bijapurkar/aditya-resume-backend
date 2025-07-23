package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.core.entity.schedule.MeetSchedule;
import com.example.aditya_resume_backend.core.entity.schedule.MeetUserMap;
import com.example.aditya_resume_backend.core.entity.user.UserProfile;
import com.example.aditya_resume_backend.core.enums.MeetPlatformEnum;
import com.example.aditya_resume_backend.core.enums.StatusEnum;
import com.example.aditya_resume_backend.core.port.dto.UserDTO;
import com.example.aditya_resume_backend.core.port.repository.schedule.MeetScheduleRepository;
import com.example.aditya_resume_backend.core.port.repository.schedule.MeetUserMapRepository;
import com.example.aditya_resume_backend.core.port.repository.schedule.StatusRepository;
import com.example.aditya_resume_backend.core.port.service.IEmailService;
import com.example.aditya_resume_backend.core.port.service.ISchedulerService;
import com.example.aditya_resume_backend.core.port.service.IUserManagementService;
import com.example.aditya_resume_backend.dto.get_availability.ScheduleAvailabilityResponse;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import com.example.aditya_resume_backend.exceptions.GenericRuntimeException;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulerServiceImpl implements ISchedulerService {

    private final IUserManagementService userManagementService;
    private final IEmailService emailService;
    private final MeetScheduleRepository meetScheduleRepository;
    private final MeetUserMapRepository meetUserMapRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public SchedulerServiceImpl(IEmailService emailService, IUserManagementService userManagementService,
                                MeetScheduleRepository meetScheduleRepository, MeetUserMapRepository meetUserMapRepository,
                                StatusRepository statusRepository) {
        this.userManagementService = userManagementService;
        this.emailService = emailService;
        this.meetScheduleRepository = meetScheduleRepository;
        this.meetUserMapRepository = meetUserMapRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public ScheduleAvailabilityResponse getAvailableSlots(LocalDate date) {
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(18, 0);

        LocalDateTime dayStart = date.atTime(workStart);
        LocalDateTime dayEnd = date.atTime(workEnd);

        List<LocalDateTime> meetings = meetScheduleRepository.findByScheduledAtBetween(dayStart, dayEnd);
        Set<LocalTime> bookedTimes = meetings.stream()
                .map(LocalDateTime::toLocalTime)
                .collect(Collectors.toSet());

        List<LocalDateTime> availableSlots = new ArrayList<>();
        for (LocalTime slot = workStart; !slot.plusHours(1).isAfter(workEnd); slot = slot.plusHours(1)) {
            if (!bookedTimes.contains(slot)) {
                availableSlots.add(date.atTime(slot));
            }
        }

        return ScheduleAvailabilityResponse.builder()
                .availableSlots(availableSlots)
                .build();
    }

    private void saveNewMeetingRequest(UUID meetingId, ScheduleMeetRequest scheduleMeetRequest) {
        List<UserProfile> requiredUsers = userManagementService.getUsersFromEmail(
                scheduleMeetRequest.getRequiredUsers().stream().map(UserDTO::getEmailId).toList()
        );

        MeetSchedule meetSchedule = MeetSchedule.builder()
                .id(meetingId)
                .description(scheduleMeetRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .scheduledAt(scheduleMeetRequest.getScheduleTime())
                .meetPlatform(MeetPlatformEnum.GOOGLE_MEET.getValue())
                .status(statusRepository.findByTitle(StatusEnum.PENDING_APPROVAL.getValue()))
                .build();

        List<MeetUserMap> meetUserMaps = new ArrayList<>();
        for(UserProfile user : requiredUsers) {
            meetUserMaps.add(
                    MeetUserMap.builder()
                            .id(UUID.randomUUID())
                            .meetSchedule(meetSchedule)
                            .userProfile(user)
                            .build()
            );
        }

        meetScheduleRepository.save(meetSchedule);
        meetUserMapRepository.saveAll(meetUserMaps);
    }

    @Override
    public void initiateMeetingRequest(ScheduleMeetRequest scheduleMeetRequest) throws TemplateException, MessagingException, IOException {
        userManagementService.createNewUsers(scheduleMeetRequest.getRequiredUsers());

        UUID meetingId = UUID.randomUUID();
        saveNewMeetingRequest(meetingId, scheduleMeetRequest);

        emailService.sendMeetRequestEmail(meetingId, scheduleMeetRequest);
        emailService.sendMeetScheduleEmail(
                scheduleMeetRequest.getRequiredUsers().stream().map(UserDTO::getEmailId).toList(),
                scheduleMeetRequest.getScheduleTime()
        );
    }

    private LocalDateTime updateMeetingStatus(UUID meetingId, String response, String meetingLink) {
        Optional<MeetSchedule> meetSchedule = meetScheduleRepository.findById(meetingId);

        if(meetSchedule.isPresent()) {
            MeetSchedule meeting = meetSchedule.get();

            meeting.setStatus(statusRepository.findByTitle(StatusEnum.getEnumFromString(response).value));
            if(meetingLink != null && !meetingLink.isEmpty()) {
                meeting.setMeetLink(meetingLink);
            }

            meetScheduleRepository.save(meeting);

            return meeting.getScheduledAt();
        }
        else {
            throw new GenericRuntimeException(String.format("Meeting Id %s not found", meetingId));
        }
    }

    @Override
    public void acceptMeetingRequest(UUID meetingId, String response) throws TemplateException, MessagingException, IOException {

        List<String> meetingUsersEmail = meetUserMapRepository.getRequiredUsersEmail(meetingId);

        if(response.equals(StatusEnum.SCHEDULED.value)) {
            String meetingLink = "https://google.com";
            LocalDateTime scheduledTime = updateMeetingStatus(meetingId, response, meetingLink);
            emailService.sendConfirmationEmail(meetingUsersEmail, meetingLink, scheduledTime);
        }
        else {
            LocalDateTime scheduleTime = updateMeetingStatus(meetingId, response, null);
            emailService.sendRejectionEmail(meetingUsersEmail, scheduleTime);
        }
    }

}
