package com.example.demo.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ProductSalesRankDto(
    val saleDate: LocalDate,
    val productId: Long,
    val category: String,
    val revenue: BigDecimal,
    val previousRevenue: BigDecimal?,
    val rank: Int
)
