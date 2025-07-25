package com.example.aditya_resume_backend.core.port.repository.schedule;

import com.example.aditya_resume_backend.core.entity.schedule.MeetSchedule;
import com.example.aditya_resume_backend.core.port.dto.MeetingEmailsDTO;
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

    @Query(value = """
                SELECT
                    m.id AS meetingId,
                    string_agg(up.email_id, ',') AS emailIds
                FROM
                    meet_schedule m
                JOIN
                    schedule_user_map sum ON sum.meet_id = m.id
                JOIN
                    user_profile up ON up.id = sum.user_id
                WHERE
                    m.scheduled_at = :scheduled_at
                    AND m.id <> :current_meeting_id
                GROUP BY
                    m.id
    """, nativeQuery = true)
    List<MeetingEmailsDTO> getOtherSimilarMeets(@Param("scheduled_at") LocalDateTime scheduledAt,
                                                @Param("current_meeting_id") UUID currentMeetingId);

}
