package com.example.aditya_resume_backend.core.port.repository.schedule;

import com.example.aditya_resume_backend.core.entity.schedule.MeetSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeetScheduleRepository extends JpaRepository<MeetSchedule, UUID> {

    @Query("""
            SELECT
                m.scheduledAt
            FROM
                MeetSchedule m
            WHERE
                m.scheduledAt BETWEEN :start AND :end
        """)
    List<LocalDateTime> findByScheduledAtBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

}
