package com.example.demo.service;

import com.example.demo.config.TenantContext;
import com.example.demo.repository.TenantRepository;
import com.example.jooq.tables.pojos.Author;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Author> getAuthorsForTenant(String tenantId) {
        // 인터셉터, 필터 수준에서 셋업하는 것이 정석이나, 본 실습에서는 서비스 진입점에서 값 셋업 및 초기화
        try {
            TenantContext.setCurrentTenant(tenantId);
            return tenantRepository.findAllAuthors();
        } finally {
            // 반드시 자원 반납 (Thread 꼬임 방지)
            TenantContext.clear();
        }
    }
}
