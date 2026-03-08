package com.example.demo.dto;

/**
 * 【Chapter 12】 JOIN 결과를 담는 DTO
 * book 테이블과 author 테이블의 컬럼을 하나로 결합합니다.
 */
public record BookWithAuthor(
        Integer id,
        String title,
        Integer publishedYear,
        String firstName,
        String lastName
) {}
