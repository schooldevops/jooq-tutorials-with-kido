package com.example.demo.dto

data class OrderRequestDto(
    val userId: Long,
    val productId: Long,
    val quantity: Int
)
