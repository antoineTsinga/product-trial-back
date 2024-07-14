package com.shop.product.web;


import com.shop.product.api.dtos.PagingResponse;
import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.api.dtos.ProductPagingResponse;
import com.shop.product.api.service.IProductService;

import com.shop.product.api.utils.SimpleQueryContext;
import com.shop.product.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.DependentRequired;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWithIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.utils.Converter;
import net.kaczmarzyk.spring.data.jpa.utils.QueryContext;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService iProductService;

    public ProductController(IProductService iProductService) {
        this.iProductService = iProductService;
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
                    @Spec(path = "name", params = "name_startsWith", spec = StartingWithIgnoreCase.class),
                    @Spec(path = "code", params = "code_startsWith", spec = StartingWithIgnoreCase.class),
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

        if (page < 1) {
            throw new BusinessException("Page index must not be less than 1", HttpStatus.NOT_FOUND);
        }

        PagingResponse<ProductDTO> pagingResponse =  iProductService.getProducts(spec, page - 1, size, sort);

        return new ResponseEntity<>(pagingResponse, HttpStatus.OK);
    }


    @Operation(summary = "Find Product with specific id")
    @GetMapping("/{id}")
    public  ResponseEntity<ProductDTO> getCustomerById(@Parameter(description = "id of product to get") @PathVariable Long id){
        return new ResponseEntity<>(iProductService.getProductById(id), HttpStatus.OK);
    }

    @Operation(summary = "Update all fields of a product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateCustomer(@Parameter(description = "id of product to be update") @PathVariable Long id, @RequestBody ProductDTO productDTO){
        return new ResponseEntity<>(iProductService.updateProduct(productDTO, id), HttpStatus.OK);
    }

    @Operation(summary = "Update fields of a product")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> patchProduct(@Parameter(description = "id of product to be update") @PathVariable Long id, @RequestBody ProductDTO productDTO){
        return new ResponseEntity<>(iProductService.partialUpdateProduct(productDTO, id), HttpStatus.OK);
    }


    @Operation(summary = "Create a product")
    @PostMapping("")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO){
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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleResourceNotFoundException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleResourceBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }



}
