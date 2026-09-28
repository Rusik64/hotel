package com.example.hotel.dto;

import java.time.LocalDate;

public record ClientResponse(

        Long id,
        String lastName,
        String firstName,
        String middleName,
        String identityDocument,
        String passportSeries,
        String passportNumber,
        LocalDate birthDate,
        String gender,
        String homeAddress,
        String phone

) {
}