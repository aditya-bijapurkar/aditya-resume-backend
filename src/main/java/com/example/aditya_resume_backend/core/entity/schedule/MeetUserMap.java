package com.example.aditya_resume_backend.core.entity.schedule;

import com.example.aditya_resume_backend.core.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule_user_map")
public class MeetUserMap {
    @Id
    @Column(name = "id")
    UUID id;

    @ManyToOne(targetEntity = MeetSchedule.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    MeetSchedule meetSchedule;

    @ManyToOne(targetEntity = UserProfile.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserProfile userProfile;
}
