package com.example.aditya_resume_backend.core.port.repository.schedule;

import com.example.aditya_resume_backend.core.entity.schedule.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatusRepository extends JpaRepository<Status, UUID> {

    Status findByTitle(String title);

}
