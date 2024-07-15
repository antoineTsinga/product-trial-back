package com.shop.product.configuration;
import com.shop.product.ProductTrialApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.product.entities.Product;
import com.shop.product.repository.ProductRepository;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ProductTrialApplication.class);
    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("fixtures/products.json").getInputStream();
            List<Product> products = Arrays.asList(mapper.readValue(inputStream, Product[].class));

            List<Product> productsResult = repository.saveAll(products);

            LOG.info("Database has been initialized with {} products.", productsResult.size());
        };
    }
}