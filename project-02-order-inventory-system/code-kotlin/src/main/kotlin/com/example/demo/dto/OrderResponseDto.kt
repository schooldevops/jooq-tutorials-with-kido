package com.example.demo.dto

import java.math.BigDecimal

data class OrderResponseDto(
    val orderId: Long?,
    val productName: String?,
    val quantity: Int,
    val totalPrice: BigDecimal?,
    val success: Boolean,
    val message: String?
)
