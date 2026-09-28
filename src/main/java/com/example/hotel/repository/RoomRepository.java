package com.example.hotel.repository;

import com.example.hotel.repository.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
            SELECT r
            FROM Room r
            ORDER BY r.roomNumber
            """)
    List<Room> findAllRooms();

    @Query("""
            SELECT r
            FROM Room r
            WHERE r.roomNumber = :roomNumber
            """)
    Optional<Room> findRoomByNumber(
            @Param("roomNumber") Long roomNumber
    );

    @Query("""
            SELECT r
            FROM Room r
            WHERE r.occupied = false
            ORDER BY r.roomNumber
            """)
    List<Room> findAllAvailableRooms();

    @Query("""
            SELECT r
            FROM Room r
            WHERE r.roomType = :roomType
            ORDER BY r.roomNumber
            """)
    List<Room> findRoomsByType(
            @Param("roomType") String roomType
    );

    @Query("""
            SELECT r
            FROM Room r
            WHERE r.roomPrice BETWEEN :minPrice AND :maxPrice
            ORDER BY r.roomPrice
            """)
    List<Room> findRoomsByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE Room r
            SET r.occupied = :occupied
            WHERE r.roomNumber = :roomNumber
            """)
    int updateRoomStatus(
            @Param("roomNumber") Long roomNumber,
            @Param("occupied") Boolean occupied
    );
}