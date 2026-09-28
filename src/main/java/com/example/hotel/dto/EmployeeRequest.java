package com.example.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeRequest (
        @NotBlank(message = "Логин обязателен")
        @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только латинские буквы, цифры и подчеркивание")
        String username,

        @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
        String password,

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-zÀ-ÿ -]+$", message = "Имя может содержать только буквы, пробел и дефис")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-zÀ-ÿ -]+$", message = "Фамилия может содержать только буквы, пробел и дефис")
        String lastName,

        @Size(max = 100, message = "Отчество не должно превышать 100 символов")
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-zÀ-ÿ -]*$", message = "Отчество может содержать только буквы, пробел и дефис")
        String middleName,

        @NotBlank(message = "Роль обязательна")
        @Pattern(regexp = "^(ADMIN|MANAGER|RECEPTIONIST)$", message = "Роль должна быть: ADMIN, MANAGER или RECEPTIONIST")
        String role,

        Boolean active
) {}
