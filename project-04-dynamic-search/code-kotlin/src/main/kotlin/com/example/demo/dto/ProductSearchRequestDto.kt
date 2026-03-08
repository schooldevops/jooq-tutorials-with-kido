package com.example.demo.dto

import java.math.BigDecimal

data class ProductSearchRequestDto(
    val keyword: String? = null,
    val category: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val stockStatus: String? = null,
    val status: String? = null,
    val manufacturer: String? = null
)
