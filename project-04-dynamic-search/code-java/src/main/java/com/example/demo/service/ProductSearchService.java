package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductSearchRequestDto;
import com.example.demo.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    public List<ProductDto> searchProducts(ProductSearchRequestDto request) {
        return productSearchRepository.searchProducts(request);
    }
}
