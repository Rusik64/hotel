package com.example.hotel.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RoomDetailsRequest(

        @NotNull(message = "Стоимость проживания обязательна")
        @DecimalMin(
                value = "0.0",
                inclusive = false,
                message = "Стоимость должна быть больше 0"
        )
        @Digits( integer = 8,
                fraction = 2,
                message = "Стоимость должна содержать не более 8 цифр до запятой и 2 после" )
        BigDecimal dailyRoomPrice,

        @Size(max = 500)
        String bookingInformation,

        @NotNull(message = "Количество свободных мест обязательно")
        @Min(
                value = 0,
                message = "Количество свободных мест не может быть отрицательным"
        )
        Integer availablePlaces,

        @NotNull(message = "Количество проживающих обязательно")
        @Min(
                value = 0,
                message = "Количество проживающих не может быть отрицательным"
        )
        Integer actualResidents

) {
}