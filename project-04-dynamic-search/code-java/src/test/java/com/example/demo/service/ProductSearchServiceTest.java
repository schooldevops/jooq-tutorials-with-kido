package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductSearchRequestDto;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProductSearchServiceTest {

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Init sample data
        jdbcTemplate.execute("TRUNCATE TABLE product RESTART IDENTITY CASCADE");
        jdbcTemplate.execute(
            "INSERT INTO product (name, category, price, stock_status, status, manufacturer, created_at) VALUES " +
            "('MacBook Pro M3', 'Electronics', 2500.00, 'IN_STOCK', 'ACTIVE', 'Apple', NOW()), " +
            "('iPhone 15 Pro', 'Electronics', 1200.00, 'IN_STOCK', 'ACTIVE', 'Apple', NOW()), " +
            "('Galaxy S24 Ultra', 'Electronics', 1300.00, 'OUT_OF_STOCK', 'ACTIVE', 'Samsung', NOW()), " +
            "('Sony WH-1000XM5', 'Accessories', 350.00, 'IN_STOCK', 'ACTIVE', 'Sony', NOW()), " +
            "('AirPods Pro 2', 'Accessories', 250.00, 'IN_STOCK', 'DISCONTINUED', 'Apple', NOW())"
        );
    }

    @Test
    @DisplayName("Empty request should return all products")
    void testEmptyRequest() {
        ProductSearchRequestDto request = ProductSearchRequestDto.builder().build();
        List<ProductDto> results = productSearchService.searchProducts(request);
        assertThat(results).hasSize(5);
    }

    @Test
    @DisplayName("Filter by keyword (name contains)")
    void testKeywordFilter() {
        ProductSearchRequestDto request = ProductSearchRequestDto.builder()
                .keyword("pro")
                .build();
        List<ProductDto> results = productSearchService.searchProducts(request);
        
        assertThat(results).hasSize(3); // MacBook Pro, iPhone Pro, AirPods Pro
        assertThat(results).extracting("name").contains("MacBook Pro M3", "iPhone 15 Pro", "AirPods Pro 2");
    }

    @Test
    @DisplayName("Filter by category and price range")
    void testCategoryAndPrice() {
        ProductSearchRequestDto request = ProductSearchRequestDto.builder()
                .category("Electronics")
                .minPrice(new BigDecimal("1250.00"))
                .build();
        List<ProductDto> results = productSearchService.searchProducts(request);

        assertThat(results).hasSize(2);
        assertThat(results).extracting("name").contains("MacBook Pro M3", "Galaxy S24 Ultra");
    }

    @Test
    @DisplayName("Filter by manufacturer and stock status")
    void testManufacturerAndStock() {
        ProductSearchRequestDto request = ProductSearchRequestDto.builder()
                .manufacturer("Apple")
                .stockStatus("IN_STOCK")
                .status("ACTIVE")
                .build();
        List<ProductDto> results = productSearchService.searchProducts(request);

        assertThat(results).hasSize(2);
        assertThat(results).extracting("name").contains("MacBook Pro M3", "iPhone 15 Pro");
    }
}
