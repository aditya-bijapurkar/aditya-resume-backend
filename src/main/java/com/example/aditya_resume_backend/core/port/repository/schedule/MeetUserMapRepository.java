package com.example.aditya_resume_backend.core.port.repository.schedule;

import com.example.aditya_resume_backend.core.entity.schedule.MeetUserMap;
import com.example.aditya_resume_backend.core.port.dto.NameEmailDTO;
import com.example.aditya_resume_backend.dto.initiate_meet.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeetUserMapRepository extends JpaRepository<MeetUserMap, UUID> {

    @Query("""
            SELECT
                m.userProfile.firstName,
                m.userProfile.emailId
            FROM
                MeetUserMap m
            WHERE
                m.meetSchedule.id = :meeting_id
            """)
    List<NameEmailDTO> getRequiredUsersEmail(@Param("meeting_id") UUID meetingId);

    @Query("""
            SELECT
                m.meetSchedule.scheduledAt,
                m.meetSchedule.description,
                m.meetSchedule.meetPlatform,
                m.meetSchedule.meetLink,
                m.meetSchedule.meetPassword,
                m.meetSchedule.status.title
            FROM
                MeetUserMap m
            WHERE
                m.userProfile.emailId = :email_id
            ORDER BY
                m.meetSchedule.scheduledAt
            """)
    List<Schedule> fetchScheduleListForUser(@Param("email_id") String emailId);

}
