package com.emma_ea.product.repository;

import com.emma_ea.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> { }
