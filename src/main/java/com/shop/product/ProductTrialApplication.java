package com.shop.product;


import com.shop.product.mapper.ProductMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class ProductTrialApplication {


    public static void main(String[] args) {
		SpringApplication.run(ProductTrialApplication.class, args);
	}
	@Bean
	ProductMapper customerMapper(){
		return ProductMapper.INSTANCE;
	}

}
