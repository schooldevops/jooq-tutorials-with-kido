package com.example.demo.dto;

/**
 * 저자별 최신 책 정보를 담는 DTO (CTE + JOIN 결과 매핑)
 */
public record RecentBook(
        Integer id,
        String title,
        Integer authorId,
        Integer publishedYear,
        String firstName,
        String lastName
) {}
