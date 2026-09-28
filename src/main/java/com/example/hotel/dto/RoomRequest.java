package com.example.hotel.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RoomRequest(

        @NotBlank(message = "Тип номера обязателен")
        @Size(max = 100)
        String roomType,

        @NotNull(message = "Статус номера обязателен")
        Boolean occupied,

        @NotNull(message = "Этаж обязателен")
        @Min(value = 1, message = "Количество комнат должно быть не менее 1")
        Integer numberOfRooms,

        @Min(value = 1, message = "Этаж должен быть не менее 1")
        Integer floor,

        @Size(max = 30)
        @Pattern( regexp = "^\\+?[0-9()\\-\\s]*$",
                message = "Некорректный формат телефона"
        )
        String phone,

        @NotNull(message = "Стоимость номера обязательна")
        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = "Стоимость должна быть больше 0"
        )
        @Digits( integer = 8, fraction = 2,
                message = "Стоимость должна содержать не более 8 цифр до запятой и 2 после"
        )
        BigDecimal roomPrice
) {
}