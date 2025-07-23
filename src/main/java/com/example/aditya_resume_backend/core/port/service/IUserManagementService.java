package com.example.aditya_resume_backend.core.port.service;

import com.example.aditya_resume_backend.core.entity.user.UserProfile;
import com.example.aditya_resume_backend.core.port.dto.UserDTO;

import java.util.List;

public interface IUserManagementService {

    void createNewUsers(List<UserDTO> userDetails);

    List<UserProfile> getUsersFromEmail(List<String> emailIds);

}
