package com.shop.product.mapper;
import com.shop.product.api.dtos.ProductDTO;
import com.shop.product.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "name", target = "name")
    Product toEntity(ProductDTO productDTO);
    @Mapping(source = "name", target = "name")
    ProductDTO toDto(Product product);

}
