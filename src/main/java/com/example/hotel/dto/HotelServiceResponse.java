package com.example.hotel.dto;

import java.math.BigDecimal;

public record HotelServiceResponse(

        Long id,
        String serviceName,
        BigDecimal price

) {
}