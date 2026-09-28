package com.example.hotel.repository;

import com.example.hotel.repository.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    @Query("""
            SELECT r
            FROM Registration r
            LEFT JOIN FETCH r.client
            LEFT JOIN FETCH r.room
            ORDER BY r.id
            """)
    List<Registration> findAllRegistrations();

    @Query("""
        SELECT r
        FROM Registration r
        LEFT JOIN FETCH r.client
        LEFT JOIN FETCH r.room
        WHERE r.id = :id
        """)
    Optional<Registration> findRegistrationById(
            @Param("id") Long id
    );

    @Query("""
            SELECT r
            FROM Registration r
            LEFT JOIN FETCH r.client
            LEFT JOIN FETCH r.room
            WHERE r.client.id = :clientId
            ORDER BY r.arrivalDate DESC
            """)
    List<Registration> findRegistrationsByClientId(
            @Param("clientId") Long clientId
    );

    @Query("""
            SELECT r
            FROM Registration r
            LEFT JOIN FETCH r.client
            LEFT JOIN FETCH r.room
            WHERE r.room.roomNumber = :roomNumber
            ORDER BY r.arrivalDate DESC
            """)
    List<Registration> findRegistrationsByRoomNumber(
            @Param("roomNumber") Long roomNumber
    );

    @Query("""
            SELECT r
            FROM Registration r
            LEFT JOIN FETCH r.client
            LEFT JOIN FETCH r.room
            WHERE r.departureDate IS NULL
            """)
    List<Registration> findActiveRegistrations();

    @Query("""
            SELECT r
            FROM Registration r
            LEFT JOIN FETCH r.client
            LEFT JOIN FETCH r.room
            WHERE r.arrivalDate BETWEEN :startDate AND :endDate
            ORDER BY r.arrivalDate
            """)
    List<Registration> findRegistrationsByArrivalPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    @Modifying
    @Transactional
    @Query(value = """
            UPDATE Registration r
            SET r.departureDate = :departureDate
            WHERE r.id = :id
            """,
    nativeQuery = true)
    int updateDepartureDate(
            @Param("id") Long id,
            @Param("departureDate") LocalDate departureDate
    );

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM Registration r
            WHERE r.id = :id
            """)
    int deleteRegistrationById(
            @Param("id") Long id
    );

    @Query("SELECT r FROM Registration r WHERE r.room.roomNumber = :roomNumber " +
            "AND r.arrivalDate <= :departureDate " +
            "AND (r.departureDate IS NULL OR r.departureDate >= :arrivalDate)")
    List<Registration> findOverlappingRegistrations(
            @Param("roomNumber") Long roomNumber,
            @Param("arrivalDate") LocalDate arrivalDate,
            @Param("departureDate") LocalDate departureDate
    );
}