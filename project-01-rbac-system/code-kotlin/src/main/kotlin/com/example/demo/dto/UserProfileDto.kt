package com.example.demo.dto

data class UserProfileDto(
    val id: Long? = null,
    val username: String? = null,
    val status: String? = null,
    val roles: List<RoleDto> = emptyList()
)
