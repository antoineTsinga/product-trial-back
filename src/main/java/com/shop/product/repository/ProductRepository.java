package com.shop.product.repository;

import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.entities.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Specification<ProductDTO> spec, Pageable pageable);

    Optional<Product> findByCode(String code);
}
