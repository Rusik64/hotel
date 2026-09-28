package com.example.hotel.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetails {

    @Id
    @Column(name = "room_number")
    private Long roomNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "room_number")
    private Room room;

    @Column(
            name = "daily_room_price",
            precision = 10,
            scale = 2
    )
    private BigDecimal dailyRoomPrice;

    @Column(name = "booking_information", length = 500)
    private String bookingInformation;

    @Column(name = "available_places")
    private Integer availablePlaces;

    @Column(name = "actual_residents")
    private Integer actualResidents;
}