package com.example.hotel.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaymentCardRequest(

        @NotNull(message = "Идентификатор регистрации обязателен")
        Long registrationId,

        @NotNull(message = "Количество оплаченных дней обязательно")
        @Min(
                value = 1,
                message = "Количество оплаченных дней должно быть не менее 1"
        )
        Integer paidDays,

        @NotNull(message = "Общая сумма обязательна")
        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = "Общая сумма должна быть больше 0"
        )
        @Digits( integer = 10, fraction = 2, message = "Общая сумма должна содержать не более 10 цифр до запятой и 2 после" )
        BigDecimal totalAmount,

        @NotBlank(message = "Статус оплаты обязателен")
        @Size(max = 50)
        String paymentStatus

) {
}