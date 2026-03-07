package com.example.demo.dto;

/**
 * AUTHOR와 BOOK을 JOIN한 결과를 담는 커스텀 DTO.
 * Java 16+ record 문법으로 간결하게 정의합니다.
 */
public record BookSummaryDto(Integer id, String title, String authorLastName) {
}
