package com.example.hotel.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "registration_id",
            nullable = false,
            unique = true
    )
    private Registration registration;

    @Column(
            name = "paid_days",
            nullable = false
    )
    private Integer paidDays;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Column(
            name = "payment_status",
            nullable = false,
            length = 50
    )
    private String paymentStatus;
    @OneToMany(
            mappedBy = "paymentCard",
            fetch = FetchType.LAZY
    )
    private List<InvoiceItem> invoiceItems = new ArrayList<>();
}