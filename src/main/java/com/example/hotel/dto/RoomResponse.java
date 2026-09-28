package com.example.hotel.dto;

import java.math.BigDecimal;

public record RoomResponse(

        Long roomNumber,
        String roomType,
        Boolean occupied,
        Integer numberOfRooms,
        Integer floor,
        String phone,
        BigDecimal roomPrice

) {
}