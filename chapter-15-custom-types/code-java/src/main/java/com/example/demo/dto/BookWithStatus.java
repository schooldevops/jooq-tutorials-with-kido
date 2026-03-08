package com.example.demo.dto;

import com.example.demo.type.BookStatus;

/**
 * status 컬럼(Enum 변환)을 포함한 책 정보 DTO
 */
public record BookWithStatus(
        Integer id,
        String title,
        Integer publishedYear,
        BookStatus status
) {}
