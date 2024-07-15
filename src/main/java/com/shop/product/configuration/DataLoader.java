package com.shop.product.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.shop.product.entities.Product;
import com.shop.product.repository.ProductRepository;

import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    private final ProductRepository repository;
    private final ObjectMapper mapper;

    @Autowired
    private Environment env;

    public DataLoader(ProductRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            if (repository.count() > 0) return;
            loadInitialData();
        };
    }

    public void loadInitialData() throws Exception {
        InputStream inputStream = new ClassPathResource("fixtures/products.json").getInputStream();
        List<Product> products = Arrays.asList(mapper.readValue(inputStream, Product[].class));
        List<Product> productsResult = repository.saveAll(products);
        LOG.info("Database has been initialized with {} products.", productsResult.size());
    }


    @Transactional
    public void resetDatabase() {
        String tableName = "product";

        try {
            String dbType = env.getProperty("spring.datasource.url");

            if(dbType == null) return;
            if (dbType.contains("h2")) {
                repository.deleteAllProducts();
                repository.resetIdSequenceH2();
            } else if (dbType.contains("postgresql")) {
                repository.truncateTable();
                repository.resetIdSequencePostgres();
            } else {
                repository.deleteAllProducts();
            }

            LOG.info("Table {} has been reset.", tableName);

            loadInitialData();

            LOG.info("Database has been reset.");
        } catch (Exception e) {
            LOG.error("Error resetting database: {}", e.getMessage());
            throw new RuntimeException("Failed to reset database", e);
        }
    }

}