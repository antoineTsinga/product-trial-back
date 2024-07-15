package com.shop.product.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true)
    @NotNull()
    @NotBlank()
    private String code;
    private String name;
    private String description;
    private String image;
    private Float price;
    private String category;
    private Integer quantity;
    private String inventoryStatus;
    private Float rating;
}
