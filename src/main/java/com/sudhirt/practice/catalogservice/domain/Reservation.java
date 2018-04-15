package com.sudhirt.practice.catalogservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sudhirt.practice.catalogservice.constant.ReservationStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class Reservation {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;
    @NotNull
    @Column(nullable = false)
    private String orderId;
    @NotNull
    @Column(nullable = false)
    private Long quantity;
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
