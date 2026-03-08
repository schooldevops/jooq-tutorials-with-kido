package com.example.demo.service

import com.example.demo.dto.ProductDto
import com.example.demo.dto.ProductSearchRequestDto
import com.example.demo.repository.ProductSearchRepository
import org.springframework.stereotype.Service

@Service
class ProductSearchService(
    private val productSearchRepository: ProductSearchRepository
) {

    fun searchProducts(request: ProductSearchRequestDto): List<ProductDto> {
        return productSearchRepository.searchProducts(request)
    }
}
