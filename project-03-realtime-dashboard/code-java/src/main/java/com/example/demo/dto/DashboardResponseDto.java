package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponseDto(
    LocalDate targetDate,
    List<ProductSalesRankDto> salesRanks
) {}
