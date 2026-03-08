package com.example.demo.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDto(
    val id: Long,
    val name: String,
    val category: String,
    val price: BigDecimal,
    val stockStatus: String,
    val status: String,
    val manufacturer: String?,
    val createdAt: LocalDateTime?
)
