package com.shop.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.product.entities.Product;
import com.shop.product.mapper.ProductMapper;
import com.shop.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;



@SpringBootApplication
public class ProductTrialApplication {

	private final ProductRepository productRepository;
	private static final Logger LOG = LoggerFactory.getLogger(ProductTrialApplication.class);

	public ProductTrialApplication(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(ProductTrialApplication.class, args);
	}
	@Bean
	ProductMapper customerMapper(){
		return ProductMapper.INSTANCE;
	}

	@Bean
	CommandLineRunner commandLineRunner(ProductRepository productRepository){
		return args -> {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Product>> productTypeReference = new TypeReference<>(){};
			InputStream productStream = TypeReference.class.getResourceAsStream("/fixtures/products.json");
			try {
				List<Product> products = mapper.readValue(productStream, productTypeReference);
				products.forEach((product -> {
					if(productRepository.findByCode(product.getCode()).isEmpty()){
						productRepository.save(product);
					}
				}));
			}catch (IOException e){
				LOG.error(e.getMessage());
			}
		};
	}
}
