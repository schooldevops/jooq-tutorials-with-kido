package com.example.demo.dto

import java.time.LocalDate

data class DashboardResponseDto(
    val targetDate: LocalDate,
    val salesRanks: List<ProductSalesRankDto>
)
