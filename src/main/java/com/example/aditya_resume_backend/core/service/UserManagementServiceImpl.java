package com.example.aditya_resume_backend.core.service;

import com.example.aditya_resume_backend.constants.ApplicationConstants;
import com.example.aditya_resume_backend.core.entity.user.UserProfile;
import com.example.aditya_resume_backend.core.port.dto.UserDTO;
import com.example.aditya_resume_backend.core.port.repository.user.UserProfileRepository;
import com.example.aditya_resume_backend.core.port.service.IUserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UserManagementServiceImpl implements IUserManagementService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserManagementServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void createNewUsers(List<UserDTO> userDetails) {
        Set<String> currentUsers = new HashSet<>(
                userProfileRepository.findExistingEmailIds(
                        userDetails.stream().map(UserDTO::getEmailId).toList()
                )
        );

        List<UserProfile> usersToCreate = new ArrayList<>();
        for(UserDTO userDetail : userDetails) {
            if(!currentUsers.contains(userDetail.getEmailId())) {
                usersToCreate.add(
                        UserProfile.builder()
                                .id(UUID.randomUUID())
                                .firstName(userDetail.getFirstName())
                                .lastName(userDetail.getLastName())
                                .emailId(userDetail.getEmailId())
                                .createdAt(ZonedDateTime.now(ZoneId.of(ApplicationConstants.IST)).toLocalDateTime())
                                .build()
                );
            }
        }

        userProfileRepository.saveAll(usersToCreate);
    }

    @Override
    public List<UserProfile> getUsersFromEmail(List<String> emailIds) {
        return userProfileRepository.findByEmailIdIn(emailIds);
    }

}
