package com.example.hotel.dto;

import java.time.LocalDate;

public record RegistrationResponse(

        Long id,
        Long roomNumber,
        Long clientId,
        LocalDate arrivalDate,
        LocalDate departureDate

) {
}