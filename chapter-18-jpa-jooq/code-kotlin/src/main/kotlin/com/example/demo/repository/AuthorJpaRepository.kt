package com.example.demo.repository

import com.example.demo.entity.AuthorEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 【Chapter 18】 CQRS Command(CUD) 영역: Kotlin Spring Data JPA
 */
interface AuthorJpaRepository : JpaRepository<AuthorEntity, Int>
