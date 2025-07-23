package com.example.aditya_resume_backend.core.port.repository.user;

import com.example.aditya_resume_backend.core.entity.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    @Query("""
            SELECT
                u.emailId
            FROM
                UserProfile u
            WHERE
                u.emailId IN :emailIds
        """)
    List<String> findExistingEmailIds(@Param("emailIds") List<String> emailIds);

    List<UserProfile> findByEmailIdIn(List<String> emailIds);

}
