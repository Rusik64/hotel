package com.example.hotel.dto;

import java.time.LocalDate;

public record ServiceProvisionResponse(

        Long id,
        Long serviceId,
        String serviceName,
        LocalDate provisionDate,
        Integer quantity

) {
}