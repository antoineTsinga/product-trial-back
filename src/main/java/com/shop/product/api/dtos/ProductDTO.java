package com.shop.product.api.dtos;

import lombok.*;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDTO {
    private Long id;
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

