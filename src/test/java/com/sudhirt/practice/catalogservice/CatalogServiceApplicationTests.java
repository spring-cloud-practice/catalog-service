package com.sudhirt.practice.catalogservice;

import com.google.gson.Gson;
import com.sudhirt.practice.catalogservice.constant.ReservationStatus;
import com.sudhirt.practice.catalogservice.domain.Product;
import com.sudhirt.practice.catalogservice.domain.Reservation;
import com.sudhirt.practice.catalogservice.repository.ProductRepository;
import com.sudhirt.practice.catalogservice.repository.ReservationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CatalogServiceApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private List<Product> products;

    @Before
    public void load() throws Exception {
        if (products == null) {
            products = productRepository.findAll();
        }
    }

    @Test
    public void findAllProducts() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(products.size())));
    }

    @Test
    public void getProductById() throws Exception {
        Product product = products.get(15);
        mvc.perform(get("/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId())));
    }

    @Test
    @DirtiesContext
    public void testAddReservation() throws Exception {
        Product product = products.get(10);
        Reservation reservation = Reservation.builder().orderId("1234").quantity(product.getQuantity()).status(ReservationStatus.NEW).build();
        Gson gson = new Gson();
        String json = gson.toJson(reservation);
        // Create new reservation
        mvc.perform(post("/products/" + product.getId() + "/reservations").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
        // Confirm reservation is created successfully
        mvc.perform(get("/products/" + product.getId() + "/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        // Confirm product quantity is updated correctly
        mvc.perform(get("/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(0)));
    }

    @Test
    @DirtiesContext
    public void testConfirmReservation() throws Exception {
        Product product = products.get(10);
        Reservation reservation = Reservation.builder().orderId("1234").quantity(product.getQuantity()).status(ReservationStatus.NEW).build();
        Gson gson = new Gson();
        String reservationJson = gson.toJson(reservation);
        // Create new reservation
        mvc.perform(post("/products/" + product.getId() + "/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson))
                .andExpect(status().isOk());
        List<Reservation> reservations = reservationRepository.findByProduct(Product.builder().id(product.getId()).build());
        assertThat(reservations.size()).isEqualTo(1);
        // Confirm reservation
        Reservation confirmReservation = Reservation.builder().id(reservations.get(0).getId()).status(ReservationStatus.CONFIRMED).build();
        String confirmReservationJson = gson.toJson(confirmReservation);
        mvc.perform(put("/products/" + product.getId() + "/reservations/" + reservations.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(confirmReservationJson))
                .andExpect(status().isOk());
        reservations = reservationRepository.findByProduct(Product.builder().id(product.getId()).build());
        assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DirtiesContext
    public void testCancelReservation() throws Exception {
        Product product = products.get(10);
        Reservation reservation = Reservation.builder().orderId("1234").quantity(product.getQuantity()).status(ReservationStatus.NEW).build();
        Gson gson = new Gson();
        String reservationJson = gson.toJson(reservation);
        // Create new reservation
        mvc.perform(post("/products/" + product.getId() + "/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson))
                .andExpect(status().isOk());
        List<Reservation> reservations = reservationRepository.findByProduct(Product.builder().id(product.getId()).build());
        assertThat(reservations.size()).isEqualTo(1);
        // Cancel reservation
        Reservation cancelledReservation = Reservation.builder().id(reservations.get(0).getId()).status(ReservationStatus.CANCELLED).build();
        String cancelledReservationJson = gson.toJson(cancelledReservation);
        mvc.perform(put("/products/" + product.getId() + "/reservations/" + reservations.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cancelledReservationJson))
                .andExpect(status().isOk());
        reservations = reservationRepository.findByProduct(Product.builder().id(product.getId()).build());
        assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        // Confirm product quantity is updated correctly
        mvc.perform(get("/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(product.getQuantity().intValue())));
    }
}
