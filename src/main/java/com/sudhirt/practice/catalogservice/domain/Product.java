package com.sudhirt.practice.catalogservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"reservations"})
public class Product {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Long quantity;
    @Column(nullable = false)
    private Double price;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<Reservation> reservations;

    public void addReservation(Reservation reservation) {
        if(reservations == null) {
            reservations = new ArrayList<>();
        }
        reservation.setProduct(this);
        reservations.add(reservation);
        this.quantity -= reservation.getQuantity();
    }
}
