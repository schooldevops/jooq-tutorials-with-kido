package com.example.demo.dto;

public record OrderRequestDto(
    Long userId,
    Long productId,
    int quantity
) {}
