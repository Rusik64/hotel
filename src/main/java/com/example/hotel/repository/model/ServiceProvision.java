package com.example.hotel.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_provisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProvision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_provision_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private HotelService hotelService;

    @Column(
            name = "provision_date",
            nullable = false
    )
    private LocalDate provisionDate;

    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;
    @OneToMany(
            mappedBy = "serviceProvision",
            fetch = FetchType.LAZY
    )
    private List<InvoiceItem> invoiceItems = new ArrayList<>();
}