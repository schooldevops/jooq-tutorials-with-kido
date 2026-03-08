package com.example.demo.dto

data class RoleDto(
    val id: Long? = null,
    val name: String? = null,
    val permissions: List<PermissionDto> = emptyList()
)
