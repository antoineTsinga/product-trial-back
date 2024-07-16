package com.shop.product.web;


import com.shop.product.api.dtos.PagingResponse;
import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.api.dtos.ProductPagingResponse;
import com.shop.product.api.service.IProductService;


import com.shop.product.exception.BusinessException;
import com.shop.product.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import jakarta.validation.Valid;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;



@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService iProductService;
    private final ImageUploadService imageUploadService;
    private static final List<String> VALID_SORT_FIELDS = Arrays.asList("id", "name", "code", "price", "category", "quantity", "rating");

    @Value("${admin-code}")
    private String adminCode;

    public ProductController(IProductService iProductService, ImageUploadService imageUploadService) {
        this.iProductService = iProductService;
        this.imageUploadService = imageUploadService;
    }
    private void validateSortParameter(Sort sort) {
        sort.stream().forEach(order -> {
            if (!VALID_SORT_FIELDS.contains(order.getProperty())) {
                throw new BusinessException("Invalid sort parameter: " + order.getProperty(), HttpStatus.BAD_REQUEST);
            }
        });
    }

    @Operation(
            summary = "Find Products using over 7 filters and sort options.",
            description = "Retrieve a paginated list of products with optional filtering and sorting capabilities."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = ProductPagingResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PagingResponse<ProductDTO>> productList(
            @Parameter(description = "Filter by product name starting with the specified string")
            @RequestParam(required = false) String name_startsWith,
            @Parameter(description = "Filter by product code starting with the specified string")
            @RequestParam(required = false) String code_startsWith,
            @Parameter(description = "Filter by product price equal to the specified value")
            @RequestParam(required = false) Double price_equals,
            @Parameter(description = "Filter by product rating equal to the specified value")
            @RequestParam(required = false) Integer rating_equals,
            @Parameter(description = "Filter by product quantity equal to the specified value")
            @RequestParam(required = false) Integer quantity_equals,
            @Parameter(description = "Filter by inventory status equal to the specified value")
            @RequestParam(required = false) String inventoryStatus_equals,
            @Parameter(description = "Filter by product category equal to the specified value")
            @RequestParam(required = false) String category_equals,

            @ParameterObject
            @And({
                    @Spec(path = "name", params = "name_contains", spec = LikeIgnoreCase.class),
                    @Spec(path = "code", params = "code_contains", spec = StartingWithIgnoreCase.class),
                    @Spec(path = "price", params = "price_equals", spec = Equal.class),
                    @Spec(path = "rating", params = "rating_equals", spec = Equal.class),
                    @Spec(path = "quantity", params = "quantity_equals", spec = Equal.class),
                    @Spec(path = "inventoryStatus", params = "inventoryStatus_equals", spec = Equal.class),
                    @Spec(path = "category", params = "category_equals", spec = Equal.class),
            }) Specification<ProductDTO> spec,
            @Parameter(description = "Sort criteria, example is 'name,asc'", schema =  @Schema(implementation = String.class))
            @SortDefault(sort = {"name"}, direction = Sort.Direction.ASC, caseSensitive = false) Sort sort,
            @Parameter(description = "Page number, starting from 1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "20") int size
    ){
        validateSortParameter(sort);
        PagingResponse<ProductDTO> pagingResponse =  iProductService.getProducts(spec, page, size, sort);
        return new ResponseEntity<>(pagingResponse, HttpStatus.OK);
    }


    @Operation(summary = "Find Product with specific id")
    @GetMapping("/{id}")
    public  ResponseEntity<ProductDTO> getCustomerById(@Parameter(description = "id of product to get") @PathVariable Long id){
        return new ResponseEntity<>(iProductService.getProductById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update all fields of a product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateCustomer(@Parameter(description = "id of product to be update") @PathVariable Long id,@Valid @RequestBody ProductDTO productDTO){
        return new ResponseEntity<>(iProductService.updateProduct(productDTO, id), HttpStatus.OK);
    }

    @Operation(summary = "Update fields of a product")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> patchProduct(@Parameter(description = "id of product to be update") @PathVariable Long id, @RequestBody ProductDTO productDTO){
        return new ResponseEntity<>(iProductService.partialUpdateProduct(productDTO, id), HttpStatus.OK);
    }


    @Operation(summary = "Create a product")
    @PostMapping("")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO){
        return new ResponseEntity<>(iProductService.createProduct(productDTO), HttpStatus.CREATED);
    }
    @Operation(summary = "Delete a products")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "id of product you want to delete") @PathVariable Long id){
        iProductService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a list of products")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteProducts(@Parameter(description = "list of ids to delete") @RequestParam List<Long> ids){
        iProductService.deleteProducts(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(summary = "Reset data base with admin code")
    @PostMapping("/admin/reset")
    public ResponseEntity<String> resetDatabase(@Parameter(description = "Admin secret code set on .env") @RequestParam String code) {
        if (!adminCode.equals(code)) {
            throw new BusinessException("Invalid admin code", HttpStatus.UNAUTHORIZED);
        }

        iProductService.resetDatabase();
        return new ResponseEntity<>("Database has been reset", HttpStatus.OK);
    }

    @Operation(summary = "Upload image of specific product")
    @PostMapping("/upload-image")
    public ResponseEntity<ProductDTO> uploadProductImage(@Parameter(description = "id of product to upload image") @RequestParam("id") Long productId,
                                                         @Parameter(description = "image file") @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageUploadService.uploadImage(file);
            ProductDTO updatedProduct = iProductService.updateProductImage(productId, imageUrl);
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get resource image product",
            description = "Get resource image product. Only available in local")
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "local")
    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@Parameter(description = "name of image to get")  @PathVariable String fileName) {
        Resource file = imageUploadService.loadImageAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


}
