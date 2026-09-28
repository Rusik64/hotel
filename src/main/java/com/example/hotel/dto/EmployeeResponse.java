package com.example.hotel.dto;

import java.time.LocalDateTime;

public record EmployeeResponse(

    Long id,
    String username,
    String firstName,
    String lastName,
    String middleName,
    String role,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}