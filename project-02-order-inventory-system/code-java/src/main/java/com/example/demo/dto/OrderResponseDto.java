package com.example.demo.dto;

import java.math.BigDecimal;

public record OrderResponseDto(
    Long orderId,
    String productName,
    int quantity,
    BigDecimal totalPrice,
    boolean success,
    String message
) {}
