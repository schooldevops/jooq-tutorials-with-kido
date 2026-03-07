package com.example.demo.dto

/**
 * AUTHOR와 BOOK을 JOIN한 결과를 담는 커스텀 DTO.
 * Kotlin data class로 equals/hashCode/toString 자동 생성됩니다.
 */
data class BookSummaryDto(
    val id: Int?,
    val title: String?,
    val authorLastName: String?
)
