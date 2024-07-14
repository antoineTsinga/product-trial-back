package com.shop.product.service;


import com.shop.product.api.dtos.PagingResponse;
import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.api.service.IProductService;
import com.shop.product.entities.Product;
import com.shop.product.mapper.ProductMapper;
import com.shop.product.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    final ProductRepository productRepository;
    final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .code(productDTO.getCode())
                .image(productDTO.getImage())
                .price(productDTO.getPrice())
                .category(productDTO.getCategory())
                .description(productDTO.getDescription())
                .quantity(productDTO.getQuantity())
                .inventoryStatus(productDTO.getInventoryStatus())
                .rating(productDTO.getRating())
                .build();
        Product productResult = productRepository.save(product);
        return productMapper.toDto(productResult);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long id) {
        Optional<Product> customerOptional = productRepository.findById(id);

        if (customerOptional.isPresent()) {
            Product customer = productMapper.toEntity(productDTO);
            customer.setId(id);
            Product customerResult = productRepository.save(customer);
            return productMapper.toDto(customerResult);
        }else {
            throw new NoSuchElementException("Product with id " + id + " not found");
        }
    }

    @Override
    public ProductDTO partialUpdateProduct(ProductDTO productDTO, Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product customer = getProduct(productDTO, productOptional.get());
            Product customerResult = productRepository.save(customer);
            return productMapper.toDto(customerResult);
        }else {
            throw new NoSuchElementException("Product with id " + id + " not found");
        }
    }

    private Product getProduct(ProductDTO update, Product product) {
        if (update.getCode() != null) product.setCode(update.getCode());
        if(update.getName() != null) product.setName(update.getName());
        if(update.getDescription() != null) product.setDescription(update.getDescription());
        if(update.getImage() != null) product.setImage(update.getImage());
        if(update.getPrice() != null) product.setPrice(update.getPrice());
        if(update.getCategory() != null) product.setCategory(update.getCategory());
        if(update.getQuantity() != null) product.setQuantity(update.getQuantity());
        if(update.getInventoryStatus() != null) product.setInventoryStatus(update.getInventoryStatus());
        if(update.getRating() != null) product.setRating(update.getRating());
        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Product with id " + id + " not found")
        );
        productRepository.deleteById(id);
    }

    @Override
    public void deleteProducts(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isPresent()) {
            return productMapper.toDto(optionalProduct.get());
        }else {
            throw new NoSuchElementException("Customer with id " + id + " not found");
        }
    }

    @Override
    public PagingResponse<ProductDTO> getProducts(Specification<ProductDTO> spec, int page, int size, Sort sort) {
            return get(spec, PageRequest.of(page, size, sort));
    }

    public PagingResponse<ProductDTO> get(Specification<ProductDTO> spec, Pageable pageable) {
        Page<Product> page = productRepository.findAll(spec, pageable);
        List<ProductDTO> content = page.getContent().stream().map(productMapper::toDto).collect(Collectors.toList());
        return PagingResponse.<ProductDTO>builder()
                .page(page.getNumber() + 1)
                .total_results(page.getTotalElements())
                .size(page.getSize())
                .pageOffset(pageable.getOffset())
                .totalPages(page.getTotalPages() == 0 ? 1 : page.getTotalPages())
                .results(content)
                .build();
    }
}
