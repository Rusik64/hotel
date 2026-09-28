package com.example.hotel.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrationRequest(
        @NotNull(message = "Идентификатор номера обязателен")
        Long roomNumber,

        @NotNull(message = "Идентификатор клиента обязателен")
        Long clientId,

        @NotNull(message = "Дата прибытия обязательна")
        LocalDate arrivalDate,

        LocalDate departureDate
) { }