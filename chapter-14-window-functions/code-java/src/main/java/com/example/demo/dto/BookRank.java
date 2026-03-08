package com.example.demo.dto;

/**
 * RANK() OVER 결과를 담는 DTO
 */
public record BookRank(
        Integer id,
        String title,
        Integer authorId,
        Integer publishedYear,
        Integer rankInAuthor
) {}
