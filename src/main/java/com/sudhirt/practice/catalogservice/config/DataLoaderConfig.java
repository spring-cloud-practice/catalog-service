package com.sudhirt.practice.catalogservice.config;

import com.sudhirt.practice.catalogservice.domain.Product;
import com.sudhirt.practice.catalogservice.repository.ProductRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Configuration
@Profile("!test")
public class DataLoaderConfig {

    @Bean
    ApplicationRunner init(ProductRepository productRepository) {
        return args -> productRepository.saveAll(products());
    }

    private Collection<Product> products() {
        List<Product> products = new ArrayList<>();
        Product product;
        final Random random = new Random();
        IntStream.range(0, 30).boxed().forEach(i -> {
            products.add(Product.builder()
                    .name("Product " + i)
                    .description("Product Description " + i)
                    .quantity(Long.valueOf(random.nextInt(10000)))
                    .price(Double.valueOf(random.nextInt(2000)))
                    .build());
        });
        return products;
    }
}
