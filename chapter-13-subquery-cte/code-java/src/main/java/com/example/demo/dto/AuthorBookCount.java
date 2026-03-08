package com.example.demo.dto;

/**
 * 저자별 책 수를 담는 DTO (CTE 결과 매핑)
 */
public record AuthorBookCount(
        Integer id,
        String firstName,
        String lastName,
        Integer cnt
) {}
