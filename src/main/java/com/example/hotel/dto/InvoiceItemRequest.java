package com.example.hotel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceItemRequest(
        @NotNull( message = "Идентификатор расчётной карточки обязателен" )
        Long paymentCardId,

        @NotNull( message = "Идентификатор факта оказания услуги обязателен" )
        Long serviceProvisionId,

        @NotNull(message = "Сумма обязательна")
        @DecimalMin( value = "0.0", inclusive = false, message = "Сумма должна быть больше 0" )
        @Digits( integer = 10, fraction = 2, message = "Сумма должна содержать не более 10 цифр до запятой и 2 после" )
        BigDecimal amount
) { }