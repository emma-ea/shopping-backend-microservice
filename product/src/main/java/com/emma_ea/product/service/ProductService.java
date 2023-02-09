package com.emma_ea.product.service;

import com.emma_ea.product.dto.ProductRequest;
import com.emma_ea.product.dto.ProductResponse;
import com.emma_ea.product.model.Product;
import com.emma_ea.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void create(ProductRequest pr) {
        Product product = Product.builder()
                .name(pr.getName())
                .description(pr.getDescription())
                .price(pr.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product {} created", product.getId());
    }

    public ProductResponse get(String id) {
        if (productRepository.findById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");

        Product product = productRepository.findById(id).get();
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public List<ProductResponse> products() {
        return productRepository.findAll().stream().map((product) -> ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build()).collect(Collectors.toList());
    }
}
