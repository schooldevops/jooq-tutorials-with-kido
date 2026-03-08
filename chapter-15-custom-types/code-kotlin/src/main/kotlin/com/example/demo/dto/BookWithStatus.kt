package com.example.demo.dto

import com.example.demo.type.BookStatus

data class BookWithStatus(
    val id: Int? = null,
    val title: String? = null,
    val publishedYear: Int? = null,
    val status: BookStatus? = null
)
