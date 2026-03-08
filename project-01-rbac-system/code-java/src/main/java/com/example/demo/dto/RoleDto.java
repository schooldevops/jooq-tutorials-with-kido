package com.example.demo.dto;

import java.util.List;

public record RoleDto(
    Long id,
    String name,
    List<PermissionDto> permissions
) {}
