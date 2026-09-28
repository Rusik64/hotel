package com.example.hotel.repository;

import com.example.hotel.repository.model.HotelService;
import com.example.hotel.repository.model.ServiceProvision;
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
public interface ServiceProvisionRepository
        extends JpaRepository<ServiceProvision, Long> {

    @Query("""
            SELECT sp
            FROM ServiceProvision sp
            LEFT JOIN FETCH sp.hotelService
            ORDER BY sp.id
            """)
    List<ServiceProvision> findAllServiceProvisions();

    @Query("""
            SELECT sp
            FROM ServiceProvision sp
            LEFT JOIN FETCH sp.hotelService
            WHERE sp.id = :id
            """)
    Optional<ServiceProvision> findServiceProvisionById(
            @Param("id") Long id
    );

    @Query("""
            SELECT sp
            FROM ServiceProvision sp
            LEFT JOIN FETCH sp.hotelService
            WHERE sp.hotelService.id = :serviceId
            ORDER BY sp.provisionDate DESC
            """)
    List<ServiceProvision> findByServiceId(
            @Param("serviceId") Long serviceId
    );

    @Query("""
            SELECT sp
            FROM ServiceProvision sp
            LEFT JOIN FETCH sp.hotelService
            WHERE sp.provisionDate BETWEEN :startDate AND :endDate
            ORDER BY sp.provisionDate
            """)
    List<ServiceProvision> findByDatePeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE ServiceProvision sp
        SET sp.hotelService = :hotelService,
            sp.provisionDate = :provisionDate,
            sp.quantity = :quantity
        WHERE sp.id = :id
        """)
    int updateServiceProvision(
            @Param("id") Long id,
            @Param("hotelService") HotelService hotelService,
            @Param("provisionDate") LocalDate provisionDate,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE ServiceProvision sp
            SET sp.quantity = :quantity
            WHERE sp.id = :id
            """)
    int updateQuantity(
            @Param("id") Long id,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM ServiceProvision sp
            WHERE sp.id = :id
            """)
    int deleteServiceProvisionById(
            @Param("id") Long id
    );
}