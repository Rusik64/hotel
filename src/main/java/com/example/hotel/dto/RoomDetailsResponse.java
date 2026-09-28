package com.example.hotel.dto;

import java.math.BigDecimal;

public record RoomDetailsResponse(

        Long roomNumber,
        BigDecimal dailyRoomPrice,
        String bookingInformation,
        Integer availablePlaces,
        Integer actualResidents

) {
}