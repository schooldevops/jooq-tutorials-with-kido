package com.example.demo.service

import com.example.demo.dto.DashboardResponseDto
import com.example.demo.repository.DashboardRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DashboardService(
    private val dashboardRepository: DashboardRepository
) {
    fun getDailyDashboard(targetDate: LocalDate): DashboardResponseDto {
        val ranks = dashboardRepository.getDashboardData(targetDate)
        return DashboardResponseDto(targetDate, ranks)
    }
}
