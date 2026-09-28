package com.example.hotel.repository;

import com.example.hotel.repository.model.RoomDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomDetailsRepository
        extends JpaRepository<RoomDetails, Long> {

    @Query("""
            SELECT rd
            FROM RoomDetails rd
            ORDER BY rd.roomNumber
            """)
    List<RoomDetails> findAllRoomDetails();

    @Query("""
            SELECT rd
            FROM RoomDetails rd
            LEFT JOIN FETCH rd.room
            WHERE rd.roomNumber = :roomNumber
            """)
    Optional<RoomDetails> findByRoomNumber(
            @Param("roomNumber") Long roomNumber
    );

    @Query("""
            SELECT rd
            FROM RoomDetails rd
            WHERE rd.availablePlaces > 0
            ORDER BY rd.roomNumber
            """)
    List<RoomDetails> findWithAvailablePlaces();

    @Modifying
    @Transactional
    @Query("""
            UPDATE RoomDetails rd
            SET rd.bookingInformation = :bookingInformation
            WHERE rd.roomNumber = :roomNumber
            """)
    int updateBookingInformation(
            @Param("roomNumber") Long roomNumber,
            @Param("bookingInformation") String bookingInformation
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE RoomDetails rd
            SET rd.availablePlaces = :availablePlaces,
                rd.actualResidents = :actualResidents
            WHERE rd.roomNumber = :roomNumber
            """)
    int updateOccupancyInformation(
            @Param("roomNumber") Long roomNumber,
            @Param("availablePlaces") Integer availablePlaces,
            @Param("actualResidents") Integer actualResidents
    );

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM RoomDetails rd
            WHERE rd.roomNumber = :roomNumber
            """)
    int deleteByRoomNumber(
            @Param("roomNumber") Long roomNumber
    );
}