package com.example.aditya_resume_backend.core.entity.user;

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
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @Column(name = "id")
    UUID id;

    @Column(name = "firstname")
    String firstName;

    @Column(name = "lastname")
    String lastName;

    @Column(name = "email_id")
    String emailId;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}