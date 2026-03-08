package com.example.demo.dto

/**
 * 【Chapter 12】 JOIN 결과를 담는 Kotlin data class DTO
 * fetchInto() 자동 매핑을 위해 null-safe 기본값 포함
 */
data class BookWithAuthor(
    val id: Int? = null,
    val title: String? = null,
    val publishedYear: Int? = null,
    val firstName: String? = null,
    val lastName: String? = null
)
