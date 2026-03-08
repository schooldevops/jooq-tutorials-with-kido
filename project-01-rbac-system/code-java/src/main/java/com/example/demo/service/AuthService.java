package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.UserProfileDto;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.UserQueryRepository;

@Service
public class AuthService {

    private final UserQueryRepository userQueryRepository;
    private final PermissionRepository permissionRepository;

    public AuthService(UserQueryRepository userQueryRepository, PermissionRepository permissionRepository) {
        this.userQueryRepository = userQueryRepository;
        this.permissionRepository = permissionRepository;
    }

    public UserProfileDto getUserProfile(String username) {
        return userQueryRepository.getUserProfileWithPermissions(username);
    }

    public boolean checkPermission(Long userId, String resource, String action) {
        return permissionRepository.hasPermission(userId, resource, action);
    }
}
