package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.DashboardResponseDto;
import com.example.demo.dto.ProductSalesRankDto;
import com.example.demo.repository.DashboardRepository;

@Service
public class DashboardService {
    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardResponseDto getDailyDashboard(LocalDate targetDate) {
        List<ProductSalesRankDto> ranks = dashboardRepository.getDashboardData(targetDate);
        return new DashboardResponseDto(targetDate, ranks);
    }
}
