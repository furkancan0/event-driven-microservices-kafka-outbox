package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


public class ProductMapper {

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    public static List<ProductResponse> toProductsResponse(List<Product> products) {
        return products.stream()
                .map(p -> ProductResponse.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .price(p.getPrice())
                    .description(p.getDescription())
                    .build()).collect(Collectors.toList());
    }
}
