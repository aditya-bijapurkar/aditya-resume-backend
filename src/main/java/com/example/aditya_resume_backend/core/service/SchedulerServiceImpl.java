package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.core.entity.schedule.MeetSchedule;
import com.example.aditya_resume_backend.core.entity.schedule.MeetUserMap;
import com.example.aditya_resume_backend.core.entity.schedule.Status;
import com.example.aditya_resume_backend.core.entity.user.UserProfile;
import com.example.aditya_resume_backend.core.enums.MeetPlatformEnum;
import com.example.aditya_resume_backend.core.enums.StatusEnum;
import com.example.aditya_resume_backend.core.port.dto.MeetingEmailsDTO;
import com.example.aditya_resume_backend.core.port.dto.NameEmailDTO;
import com.example.aditya_resume_backend.core.port.dto.UserDTO;
import com.example.aditya_resume_backend.core.port.repository.schedule.MeetScheduleRepository;
import com.example.aditya_resume_backend.core.port.repository.schedule.MeetUserMapRepository;
import com.example.aditya_resume_backend.core.port.repository.schedule.StatusRepository;
import com.example.aditya_resume_backend.core.port.service.IEmailService;
import com.example.aditya_resume_backend.core.port.service.IMeetLinkService;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulerServiceImpl implements ISchedulerService {

    private final IUserManagementService userManagementService;
    private final IEmailService emailService;
    private final IMeetLinkService meetLinkService;

    private final MeetScheduleRepository meetScheduleRepository;
    private final MeetUserMapRepository meetUserMapRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public SchedulerServiceImpl(IEmailService emailService, IUserManagementService userManagementService,
                                MeetScheduleRepository meetScheduleRepository, MeetUserMapRepository meetUserMapRepository,
                                StatusRepository statusRepository, IMeetLinkService meetLinkService) {
        this.userManagementService = userManagementService;
        this.emailService = emailService;
        this.meetLinkService = meetLinkService;

        this.meetScheduleRepository = meetScheduleRepository;
        this.meetUserMapRepository = meetUserMapRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public ScheduleAvailabilityResponse getAvailableSlots(LocalDate date) {
        LocalTime workStart = date.equals(ZonedDateTime.now(ZoneId.of(ApplicationConstants.IST)).toLocalDate())
                ? LocalTime.of(Math.max(ZonedDateTime.now(ZoneId.of(ApplicationConstants.IST)).plusHours(1).getHour(), ApplicationConstants.WORK_START_TIME), 0)
                : LocalTime.of(ApplicationConstants.WORK_START_TIME, 0);
        LocalTime workEnd = LocalTime.of(ApplicationConstants.WORK_END_TIME, 0);

        LocalDateTime dayStart = date.atTime(workStart);
        LocalDateTime dayEnd = date.atTime(workEnd);

        List<LocalDateTime> meetings = meetScheduleRepository.findByScheduledAtBetween(dayStart, dayEnd, StatusEnum.SCHEDULED.value);
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

    private LocalDateTime getMeetScheduledTime(UUID meetingId) {
        Optional<MeetSchedule> meetSchedule = meetScheduleRepository.findById(meetingId);
        if(meetSchedule.isPresent()) {
            MeetSchedule meeting = meetSchedule.get();
            return meeting.getScheduledAt();
        }

        throw new GenericRuntimeException(String.format("Meeting Id %s not found", meetingId));
    }

    private void updateMeetingStatus(List<UUID> meetingIds, String response, String meetingLink) {
        List<MeetSchedule> meetSchedules = meetScheduleRepository.findByIdIn(meetingIds);
        Status updatedStatus = statusRepository.findByTitle(StatusEnum.getEnumFromString(response).value);

        meetSchedules.forEach(schedule -> {
            if(schedule.getStatus().equals(updatedStatus)) {
                throw new GenericRuntimeException(String.format("Meeting already in status: %s", updatedStatus.getTitle()));
            }

            schedule.setStatus(updatedStatus);
            if(meetingLink != null && !meetingLink.isEmpty()) {
                schedule.setMeetLink(meetingLink);
            }
        });

        meetScheduleRepository.saveAll(meetSchedules);
    }

    private void rejectOtherSimilarMeets(UUID meetingId, LocalDateTime scheduledTime) throws Exception {
        List<MeetingEmailsDTO> otherSimilarMeets = meetScheduleRepository.getOtherSimilarMeets(scheduledTime, meetingId);

        updateMeetingStatus(
                otherSimilarMeets.stream().map(MeetingEmailsDTO::getMeetingId).toList(),
                StatusEnum.DECLINED.value,
                null
        );

        for(MeetingEmailsDTO meetingEmails : otherSimilarMeets) {
            List<String> emailIds = Arrays.stream(meetingEmails.getEmailIdsString().split(",")).toList();
            emailService.sendRejectionEmail(emailIds, scheduledTime);
        }
    }

    @Override
    public void respondToSchedule(UUID meetingId, String response) throws Exception {
        LocalDateTime scheduleTime = getMeetScheduledTime(meetingId);
        List<NameEmailDTO> meetingUsersEmail = meetUserMapRepository.getRequiredUsersEmail(meetingId);

        if(response.equals(StatusEnum.SCHEDULED.value)) {
            String meetingLink = meetLinkService.generateGoogleMeetingLink(scheduleTime);
            updateMeetingStatus(List.of(meetingId), response, meetingLink);
            emailService.sendConfirmationEmail(meetingUsersEmail, meetingLink, scheduleTime);

            rejectOtherSimilarMeets(meetingId, scheduleTime);
        }
        else {
            updateMeetingStatus(List.of(meetingId), response, null);
            emailService.sendRejectionEmail(
                    meetingUsersEmail.stream().map(NameEmailDTO::getEmailId).toList(),
                    scheduleTime
            );
        }
    }

}
