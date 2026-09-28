package com.example.hotel.dto;

import java.math.BigDecimal;

public record InvoiceItemResponse(

        Long id,

        Long paymentCardId,

        Long serviceProvisionId,

        Long serviceId,

        String serviceName,

        BigDecimal amount

) {
}