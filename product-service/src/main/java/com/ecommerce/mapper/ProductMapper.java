package com.ecommerce.mapper;

import com.ecommerce.entity.Product;
import com.ecommerce.dto.ProductResponse;

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
