package com.shop.product.service;

import com.shop.product.api.dtos.PagingResponse;
import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.api.service.IProductService;
import com.shop.product.entities.Product;
import com.shop.product.mapper.ProductMapper;
import com.shop.product.repository.ProductRepository;
import com.shop.product.exception.BusinessException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsByCode(productDTO.getCode())) {
            throw new BusinessException("Product with code " + productDTO.getCode() + " already exists", HttpStatus.CONFLICT);
        }

        validateProductData(productDTO);

        Product product = productMapper.toEntity(productDTO);
        Product productResult = productRepository.save(product);
        return productMapper.toDto(productResult);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO, Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));

        if (!existingProduct.getCode().equals(productDTO.getCode()) &&
                productRepository.existsByCode(productDTO.getCode())) {
            throw new BusinessException("Product with code " + productDTO.getCode() + " already exists", HttpStatus.CONFLICT);
        }

        validateProductData(productDTO);

        Product updatedProduct = productMapper.toEntity(productDTO);
        updatedProduct.setId(id);
        Product productResult = productRepository.save(updatedProduct);
        return productMapper.toDto(productResult);
    }

    @Override
    @Transactional
    public ProductDTO partialUpdateProduct(ProductDTO productDTO, Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));

        if (productDTO.getCode() != null && !existingProduct.getCode().equals(productDTO.getCode()) &&
                productRepository.existsByCode(productDTO.getCode())) {
            throw new BusinessException("Product with code " + productDTO.getCode() + " already exists", HttpStatus.CONFLICT);
        }

        Product updatedProduct = getProduct(productDTO, existingProduct);
        validateProductData(productMapper.toDto(updatedProduct));

        Product productResult = productRepository.save(updatedProduct);
        return productMapper.toDto(productResult);
    }

    private Product getProduct(ProductDTO update, Product product) {
        if (update.getCode() != null) product.setCode(update.getCode());
        if (update.getName() != null) product.setName(update.getName());
        if (update.getDescription() != null) product.setDescription(update.getDescription());
        if (update.getImage() != null) product.setImage(update.getImage());
        if (update.getPrice() != null) product.setPrice(update.getPrice());
        if (update.getCategory() != null) product.setCategory(update.getCategory());
        if (update.getQuantity() != null) product.setQuantity(update.getQuantity());
        if (update.getInventoryStatus() != null) product.setInventoryStatus(update.getInventoryStatus());
        if (update.getRating() != null) product.setRating(update.getRating());
        return product;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteProducts(List<Long> ids) {
        List<Product> productsToDelete = productRepository.findAllById(ids);
        if (productsToDelete.size() != ids.size()) {
            throw new BusinessException("Some products to delete were not found", HttpStatus.NOT_FOUND);
        }
        productRepository.deleteAllById(ids);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
    }

    @Override
    public PagingResponse<ProductDTO> getProducts(Specification<ProductDTO> spec, int page, int size, Sort sort) {
        if (page < 1) {
            throw new BusinessException("Page index must not be less than 1", HttpStatus.BAD_REQUEST);
        }
        if (size < 1) {
            throw new BusinessException("Page size must not be less than 1", HttpStatus.BAD_REQUEST);
        }
        return get(spec, PageRequest.of(page - 1, size, sort));
    }

    private PagingResponse<ProductDTO> get(Specification<ProductDTO> spec, Pageable pageable) {
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

    private void validateProductData(ProductDTO productDTO) {
        if (productDTO.getPrice() != null && productDTO.getPrice() < 0) {
            throw new BusinessException("Product price cannot be negative", HttpStatus.BAD_REQUEST);
        }
        if (productDTO.getQuantity() != null && productDTO.getQuantity() < 0) {
            throw new BusinessException("Product quantity cannot be negative", HttpStatus.BAD_REQUEST);
        }
        if (productDTO.getRating() != null && (productDTO.getRating() < 0 || productDTO.getRating() > 5)) {
            throw new BusinessException("Product rating must be between 0 and 5", HttpStatus.BAD_REQUEST);
        }
        // Add more validations as needed
    }
}