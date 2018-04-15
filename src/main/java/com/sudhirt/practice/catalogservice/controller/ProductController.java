package com.sudhirt.practice.catalogservice.controller;

import com.sudhirt.practice.catalogservice.domain.Product;
import com.sudhirt.practice.catalogservice.exception.NotFoundException;
import com.sudhirt.practice.catalogservice.repository.ProductRepository;
import com.sudhirt.practice.catalogservice.constant.ReservationStatus;
import com.sudhirt.practice.catalogservice.domain.Reservation;
import com.sudhirt.practice.catalogservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/products")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public Product get(@PathVariable(name = "id", required = true) String id) {
        //Optional<Product> productHolder = productRepository.findOne(Example.of(Product.builder().id(id).build()));
        Optional<Product> productHolder = productRepository.findById(id);
        return productHolder.map(p -> {
            return p;
        }).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/products/{id}/reservations")
    public List<Reservation> getProductReservations(@PathVariable(name = "id", required = true) String id) {
        return reservationRepository.findByProduct(Product.builder().id(id).build());
    }

    @Transactional
    @PostMapping("/products/{id}/reservations")
    public Product reserve(@PathVariable(name = "id", required = true) String id,
                           @RequestBody @Valid Reservation reservation) {
        Optional<Product> productHolder = productRepository.findById(id);
        return productHolder.map(product -> {
            product.addReservation(reservation);
            productRepository.save(product);
            return product;
        }).orElseThrow(NotFoundException::new);
    }

    @Transactional
    @PutMapping("/products/{productId}/reservations/{reservationId}")
    public Reservation updateReservation(@PathVariable(name = "productId", required = true) String productId,
                                     @PathVariable(name = "reservationId", required = true) String reservationId,
                                     @RequestBody Reservation reservation) {
        Optional<Reservation> reservationHolder = reservationRepository.findByIdAndProduct(reservationId, Product.builder().id(productId).build());
        return reservationHolder.map(reservationEntity -> {
            reservationEntity.getProduct();
            reservationEntity.setStatus(reservation.getStatus());
            if (ReservationStatus.CANCELLED.equals(reservation.getStatus())) {
                reservationEntity.getProduct().setQuantity(reservationEntity.getProduct().getQuantity() + reservationEntity.getQuantity());
            }
            reservationRepository.save(reservationEntity);
            return reservationEntity;
        }).orElseThrow(NotFoundException::new);
    }

}
