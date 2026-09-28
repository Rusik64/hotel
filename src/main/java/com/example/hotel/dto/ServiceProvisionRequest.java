package com.example.hotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ServiceProvisionRequest(

        @NotNull(message = "Идентификатор услуги обязателен")
        Long serviceId,

        @NotNull(message = "Дата оказания услуги обязательна")
        LocalDate provisionDate,

        @NotNull(message = "Количество обязательно")
        @Min(
                value = 1,
                message = "Количество должно быть не менее 1"
        )
        Integer quantity

) {
}