package com.example.demo.service

import com.example.demo.config.TenantContext
import com.example.demo.repository.TenantRepository
import com.example.jooq.tables.pojos.Author
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TenantService(private val tenantRepository: TenantRepository) {

    @Transactional(readOnly = true)
    fun getAuthorsForTenant(tenantId: String): List<Author> {
        return try {
            TenantContext.setCurrentTenant(tenantId)
            tenantRepository.findAllAuthors()
        } finally {
            TenantContext.clear()
        }
    }
}
