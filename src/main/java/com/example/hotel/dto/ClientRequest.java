package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ClientRequest(
        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        @Pattern( regexp = "^[А-Яа-яЁёA-Za-zÀ-ÿ -]+$", message = "Фамилия может содержать только буквы, пробел и дефис" )
        String lastName,

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        @Pattern( regexp = "^[А-Яа-яЁёA-Za-zÀ-ÿ -]+$", message = "Имя может содержать только буквы, пробел и дефис" )
        String firstName,

        @Size(max = 100, message = "Отчество не должно превышать 100 символов")
        @Pattern( regexp = "^[А-Яа-яЁёA-Za-zÀ-ÿ -]*$", message = "Отчество может содержать только буквы, пробел и дефис" )
        String middleName,

        @Size(max = 100, message = "Название документа не должно превышать 100 символов")
        String identityDocument,

        @Size(max = 20, message = "Серия паспорта не должна превышать 20 символов")
        @Pattern( regexp = "^\\d*$", message = "Серия паспорта должна содержать только цифры" )
        String passportSeries,

        @Size(max = 20, message = "Номер паспорта не должен превышать 20 символов")
        @Pattern( regexp = "^\\d*$", message = "Номер паспорта должен содержать только цифры" )
        String passportNumber,

        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,
        @Size(max = 20, message = "Пол не должен превышать 20 символов")
        @Pattern( regexp = "^(Мужской|Женский)?$", message = "Допустимые значения: Мужской или Женский" )
        String gender,

        @Size(max = 255, message = "Адрес не должен превышать 255 символов")
        String homeAddress,

        @Size(max = 30, message = "Телефон не должен превышать 30 символов")
        @Pattern( regexp = "^\\+?[0-9()\\-\\s]*$", message = "Некорректный формат телефона" )
        String phone
) { }