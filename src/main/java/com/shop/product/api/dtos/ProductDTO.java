package com.shop.product.api.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDTO {
    private Long id;
    @NotEmpty
    @NotNull
    private String code;
    @NotEmpty
    @NotNull
    private String name;
    private String description;
    private String image;
    private Float price;
    private String category;
    private Integer quantity;
    private String inventoryStatus;
    private Float rating;
}

