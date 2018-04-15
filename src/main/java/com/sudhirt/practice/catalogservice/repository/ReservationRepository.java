package com.sudhirt.practice.catalogservice.repository;

import com.sudhirt.practice.catalogservice.domain.Product;
import com.sudhirt.practice.catalogservice.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    Optional<Reservation> findByIdAndProduct(String id, Product product);

    List<Reservation> findByProduct(Product product);
}
