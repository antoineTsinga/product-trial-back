package com.shop.product.api.service;

import com.shop.product.api.dtos.PagingResponse;
import com.shop.product.api.dtos.ProductDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public interface IProductService {

    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO, Long id);
    ProductDTO partialUpdateProduct(ProductDTO productDTO, Long id);
    void deleteProduct(Long id);
    void deleteProducts(List<Long> ids);
    ProductDTO getProductById(Long id);
    PagingResponse<ProductDTO> getProducts(Specification<ProductDTO> spec,int page, int size, Sort sort);
}
