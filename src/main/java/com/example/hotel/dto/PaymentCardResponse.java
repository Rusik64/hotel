package com.example.hotel.dto;

import java.math.BigDecimal;

public record PaymentCardResponse(

        Long id,
        Long registrationId,
        Integer paidDays,
        BigDecimal totalAmount,
        String paymentStatus

) {
}