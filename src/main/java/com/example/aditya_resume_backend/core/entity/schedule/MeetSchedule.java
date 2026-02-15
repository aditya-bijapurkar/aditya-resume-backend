package com.example.aditya_resume_backend.core.entity.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "meet_schedule")
public class MeetSchedule {
    @Id
    @Column(name = "id")
    UUID id;

    @Column(name = "description")
    String description;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "scheduled_at")
    LocalDateTime scheduledAt;

    @Column(name = "meet_platform")
    String meetPlatform;

    @Column(name = "meet_link")
    String meetLink;

    @Column(name = "meet_password")
    String meetPassword;

    @ManyToOne(targetEntity = Status.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "meet_status_id", referencedColumnName = "id")
    Status status;

    @Column(name = "attendee_emails")
    String[] attendeeEmails;
}
