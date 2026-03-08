package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductSalesRankDto(
    LocalDate saleDate,
    Long productId,
    String category,
    BigDecimal revenue,
    BigDecimal previousRevenue,
    Integer rank
) {}
