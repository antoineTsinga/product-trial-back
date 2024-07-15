package com.shop.product.repository;

import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.entities.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Specification<ProductDTO> spec, Pageable pageable);

    Optional<Product> findByCode(String code);

    boolean existsByCode(String code);

    @Modifying
    @Query(value = "DELETE FROM product", nativeQuery = true)
    void deleteAllProducts();

    @Modifying
    @Query(value = "TRUNCATE TABLE product", nativeQuery = true)
    void truncateTable();


    @Modifying
    @Query(value = "ALTER TABLE product ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetIdSequenceH2();

    @Modifying
    @Query(value = "ALTER SEQUENCE product_id_seq RESTART WITH 1", nativeQuery = true)
    void resetIdSequencePostgres();
}
