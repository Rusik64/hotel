package com.example.hotel.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HotelServiceRequest(
        @NotBlank(message = "Название услуги обязательно")
        @Size( max = 150, message = "Название услуги не должно превышать 150 символов" )
        String serviceName,

        @NotNull(message = "Стоимость услуги обязательна")
        @DecimalMin( value = "0.0", inclusive = false, message = "Стоимость услуги должна быть больше 0" )
        @Digits( integer = 10, fraction = 2, message = "Стоимость услуги должна содержать не более 10 цифр до запятой и 2 после" )
        BigDecimal price
) { }