package com.example.demo.service

import com.example.demo.dto.UserProfileDto
import com.example.demo.repository.PermissionRepository
import com.example.demo.repository.UserQueryRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userQueryRepository: UserQueryRepository,
    private val permissionRepository: PermissionRepository
) {

    fun getUserProfile(username: String): UserProfileDto? {
        return userQueryRepository.getUserProfileWithPermissions(username)
    }

    fun checkPermission(userId: Long, resource: String, action: String): Boolean {
        return permissionRepository.hasPermission(userId, resource, action)
    }
}
