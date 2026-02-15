package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.core.entity.schedule.MeetSchedule;
import com.example.aditya_resume_backend.core.entity.schedule.Status;
import com.example.aditya_resume_backend.core.enums.MeetPlatformEnum;
import com.example.aditya_resume_backend.core.enums.StatusEnum;
import com.example.aditya_resume_backend.core.port.dto.*;
import com.example.aditya_resume_backend.core.port.repository.schedule.MeetScheduleRepository;
import com.example.aditya_resume_backend.core.port.repository.schedule.StatusRepository;
import com.example.aditya_resume_backend.core.port.service.IEmailService;
import com.example.aditya_resume_backend.core.port.service.IMeetLinkService;
import com.example.aditya_resume_backend.core.port.service.ISchedulerService;
import com.example.aditya_resume_backend.core.service.meet.ZoomMeetLinkServiceImpl;
import com.example.aditya_resume_backend.dto.get_availability.ScheduleAvailabilityResponse;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleList;
import com.example.aditya_resume_backend.dto.initiate_meet.ScheduleMeetRequest;
import com.example.aditya_resume_backend.exceptions.GenericRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.aditya_resume_backend.constants.ControllerConstants.TIMESLOT_ERROR;

@Service
public class SchedulerServiceImpl implements ISchedulerService {

    @Value("${spring.mail.admin-email}")
    private String adminEmail;

    private final IEmailService emailService;
    private final IMeetLinkService meetLinkService;

    private final MeetScheduleRepository meetScheduleRepository;
    private final StatusRepository statusRepository;

    @Autowired
    public SchedulerServiceImpl(IEmailService emailService, MeetScheduleRepository meetScheduleRepository,
                                StatusRepository statusRepository, ZoomMeetLinkServiceImpl zoomMeetLinkService) {
        this.emailService = emailService;
        this.meetLinkService = zoomMeetLinkService;

        this.meetScheduleRepository = meetScheduleRepository;
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
        meetScheduleRepository.save(
            MeetSchedule.builder()
                .id(meetingId)
                .description(scheduleMeetRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .scheduledAt(scheduleMeetRequest.getScheduleTime())
                .meetPlatform(MeetPlatformEnum.ZOOM_MEET.getValue())
                .status(statusRepository.findByTitle(StatusEnum.PENDING_APPROVAL.getValue()))
                .attendeeEmails(
                    scheduleMeetRequest.getRequiredUsers().stream().map(UserDTO::getEmailId).toArray(String[]::new)
                )
                .build()
        );
    }

    private Boolean checkIfTimeslotIsAvailable(LocalDateTime timeslot) {
        return meetScheduleRepository.checkIfTimeslotIsAvailable(timeslot);
    }

    @Override
    public void initiateMeetingRequest(ScheduleMeetRequest scheduleMeetRequest) throws Exception {
//        userManagementService.createNewUsers(scheduleMeetRequest.getRequiredUsers());

        Boolean timeslotIsAvailable = checkIfTimeslotIsAvailable(scheduleMeetRequest.getScheduleTime());
        if(!timeslotIsAvailable) {
            throw new Exception(TIMESLOT_ERROR);
        }

        UUID meetingId = UUID.randomUUID();
        saveNewMeetingRequest(meetingId, scheduleMeetRequest);

        emailService.sendMeetRequestEmail(meetingId, scheduleMeetRequest);
        emailService.sendMeetScheduleEmail(
                scheduleMeetRequest.getRequiredUsers().stream().map(UserDTO::getEmailId).toList(),
                scheduleMeetRequest.getScheduleTime()
        );
    }

    private MeetingDetailsDTO getMeetingDetails(UUID meetingId) {
        Optional<MeetSchedule> meetSchedule = meetScheduleRepository.findById(meetingId);
        if(meetSchedule.isPresent()) {
            MeetSchedule meeting = meetSchedule.get();
            return MeetingDetailsDTO.builder()
                    .meetingTime(meeting.getScheduledAt())
                    .description(meeting.getDescription())
                    .attendeeEmails(meeting.getAttendeeEmails())
                    .build();
        }

        throw new GenericRuntimeException(String.format("Meeting Id %s not found", meetingId));
    }

    private void updateMeetingStatus(List<UUID> meetingIds, String response, String meetingLink, String meetPassword) {
        List<MeetSchedule> meetSchedules = meetScheduleRepository.findByIdIn(meetingIds);
        Status updatedStatus = statusRepository.findByTitle(StatusEnum.getEnumFromString(response).value);

        meetSchedules.forEach(schedule -> {
            if(schedule.getStatus().equals(updatedStatus)) {
                throw new GenericRuntimeException(String.format("Meeting already in status: %s", updatedStatus.getTitle()));
            }

            schedule.setStatus(updatedStatus);
            schedule.setMeetPassword(meetPassword);
            if(meetingLink != null && !meetingLink.isEmpty()) {
                schedule.setMeetLink(meetingLink);
            }
        });

        meetScheduleRepository.saveAll(meetSchedules);
    }

    private void sendConfirmationMails(String[] attendeeEmails, ScheduledMeetingDetailsDTO scheduledMeeting, MeetingDetailsDTO meetingDetails) throws Exception {
        emailService.sendConfirmationEmail(attendeeEmails, scheduledMeeting, meetingDetails.getMeetingTime());
        emailService.sendConfirmationEmailToAdmin(attendeeEmails, scheduledMeeting, meetingDetails.getMeetingTime());
    }

    private void rejectOtherSimilarMeets(UUID meetingId, LocalDateTime scheduledTime) throws Exception {
        List<MeetingEmailsDTO> otherSimilarMeets = meetScheduleRepository.getOtherSimilarMeets(scheduledTime, meetingId);

        updateMeetingStatus(
                otherSimilarMeets.stream().map(MeetingEmailsDTO::getMeetingId).toList(),
                StatusEnum.DECLINED.value,
                null,
                null
        );

        for(MeetingEmailsDTO meetingEmails : otherSimilarMeets) {
            emailService.sendRejectionEmail(meetingEmails.getAttendeeEmails(), scheduledTime);
        }
    }

    @Override
    public void respondToSchedule(UUID meetingId, String response) throws Exception {
        MeetingDetailsDTO meetingDetails = getMeetingDetails(meetingId);
        Boolean timeslotIsAvailable = checkIfTimeslotIsAvailable(meetingDetails.getMeetingTime());

        if(timeslotIsAvailable && response.equals(StatusEnum.SCHEDULED.value)) {
            ScheduledMeetingDetailsDTO scheduledMeeting = meetLinkService.generateMeetingLink(meetingDetails);
            updateMeetingStatus(List.of(meetingId), StatusEnum.SCHEDULED.value, scheduledMeeting.getJoinUrl(), scheduledMeeting.getPassword());

            sendConfirmationMails(meetingDetails.getAttendeeEmails(), scheduledMeeting, meetingDetails);
            rejectOtherSimilarMeets(meetingId, meetingDetails.getMeetingTime());
        }
        else {
            updateMeetingStatus(List.of(meetingId), StatusEnum.DECLINED.value, null, null);
            emailService.sendRejectionEmail(meetingDetails.getAttendeeEmails(), meetingDetails.getMeetingTime());
        }
    }

    @Override
    public ScheduleList getScheduledList(String emailId) {
        return ScheduleList.builder()
                .scheduleList(meetScheduleRepository.fetchScheduleListForUser(emailId))
                .build();
    }

}
