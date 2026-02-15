package com.example.aditya_resume_backend.core.port.repository.schedule;

import com.example.aditya_resume_backend.core.entity.schedule.MeetSchedule;
import com.example.aditya_resume_backend.core.port.dto.MeetingEmailsDTO;
import com.example.aditya_resume_backend.dto.initiate_meet.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeetScheduleRepository extends JpaRepository<MeetSchedule, UUID> {

    List<MeetSchedule> findByIdIn(List<UUID> meetingIds);

    @Query("""
                SELECT
                    m.scheduledAt
                FROM
                    MeetSchedule m
                WHERE
                    m.scheduledAt BETWEEN :start AND :end
                    AND m.status.title = :schedule_title
            """)
    List<LocalDateTime> findByScheduledAtBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("schedule_title") String scheduleTitle);

    @Query("""
                SELECT
                    CASE WHEN COUNT(*) = 0 THEN TRUE ELSE FALSE END
                FROM
                    MeetSchedule m
                WHERE
                    m.scheduledAt = :scheduled_time
                    AND m.status.title = 'scheduled'
            """)
    Boolean checkIfTimeslotIsAvailable(@Param("scheduled_time") LocalDateTime scheduledTime);

    @Query(value = """
                SELECT
                    m.id AS meetingId,
                    m.attendee_emails AS attendeeEmails
                FROM
                    meet_schedule m
                WHERE
                    m.scheduled_at = :scheduled_at
                    AND m.id <> :current_meeting_id
    """, nativeQuery = true)
    List<MeetingEmailsDTO> getOtherSimilarMeets(@Param("scheduled_at") LocalDateTime scheduledAt,
                                                @Param("current_meeting_id") UUID currentMeetingId);

    @Query(value = """
                SELECT
                    m.scheduled_at AS scheduledAt,
                    m.description AS description,
                    m.meet_platform AS meetPlatform,
                    m.meet_link AS meetLink,
                    m.meet_password AS meetPassword,
                    s.title AS status
                FROM
                    meet_schedule m
                JOIN
                    status s ON s.id = m.meet_status_id
                WHERE
                    :email_id = ANY(m.attendee_emails)
    """, nativeQuery = true)
    List<Schedule> fetchScheduleListForUser(@Param("email_id") String emailId);

}
