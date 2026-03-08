package com.example.demo.dto;

/**
 * SUM() OVER 누적합 결과를 담는 DTO
 */
public record YearlyRunningTotal(
        Integer publishedYear,
        Integer bookCount,
        Long runningTotal
) {}
