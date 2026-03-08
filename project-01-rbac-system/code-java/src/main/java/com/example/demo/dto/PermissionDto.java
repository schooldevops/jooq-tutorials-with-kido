package com.example.demo.dto;

public record PermissionDto(
    Long id,
    String resource,
    String action
) {}
