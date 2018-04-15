package com.sudhirt.practice.catalogservice.repository;

import com.sudhirt.practice.catalogservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
