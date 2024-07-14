package com.shop.product.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paging response for ProductDTO")
public class ProductPagingResponse extends PagingResponse<ProductDTO> {
    public ProductPagingResponse(long total_results, int page, int size, long pageOffset, int totalPages, List<ProductDTO> results) {
        super(total_results, page, size, pageOffset, totalPages, results);
    }
    // No additional fields required, just extends PagingResponse<ProductDTO>
}
