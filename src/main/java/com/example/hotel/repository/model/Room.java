package com.example.hotel.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_number")
    private Long roomNumber;

    @Column(name = "room_type", nullable = false, length = 100)
    private String roomType;

    @Column(name = "is_occupied", nullable = false)
    private Boolean occupied;

    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(
            name = "room_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal roomPrice;
    @OneToOne(mappedBy = "room", fetch = FetchType.LAZY)
    private RoomDetails details;
}