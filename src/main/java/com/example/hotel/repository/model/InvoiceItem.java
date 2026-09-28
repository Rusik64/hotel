package com.example.hotel.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_card_id",
            nullable = false
    )
    private PaymentCard paymentCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_provision_id",
            nullable = false
    )
    private ServiceProvision serviceProvision;

    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;
}