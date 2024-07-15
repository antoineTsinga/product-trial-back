package com.shop.product.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code", nullable = false, unique = true)
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
