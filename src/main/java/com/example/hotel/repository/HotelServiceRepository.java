package com.example.hotel.repository;

import com.example.hotel.repository.model.HotelService;
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
public interface HotelServiceRepository
        extends JpaRepository<HotelService, Long> {

    @Query("""
            SELECT hs
            FROM HotelService hs
            ORDER BY hs.id
            """)
    List<HotelService> findAllServices();

    @Query("""
            SELECT hs
            FROM HotelService hs
            WHERE hs.id = :id
            """)
    Optional<HotelService> findServiceById(
            @Param("id") Long id
    );

    @Query("""
            SELECT hs
            FROM HotelService hs
            WHERE LOWER(hs.serviceName) LIKE LOWER(
                CONCAT('%', :serviceName, '%')
            )
            ORDER BY hs.serviceName
            """)
    List<HotelService> searchServicesByName(
            @Param("serviceName") String serviceName
    );

    @Query("""
            SELECT hs
            FROM HotelService hs
            WHERE hs.price BETWEEN :minPrice AND :maxPrice
            ORDER BY hs.price
            """)
    List<HotelService> findServicesByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE HotelService hs
            SET hs.serviceName = :serviceName,
                hs.price = :price
            WHERE hs.id = :id
            """)
    int updateService(
            @Param("id") Long id,
            @Param("serviceName") String serviceName,
            @Param("price") BigDecimal price
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE HotelService hs
            SET hs.price = :price
            WHERE hs.id = :id
            """)
    int updateServicePrice(
            @Param("id") Long id,
            @Param("price") BigDecimal price
    );

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM HotelService hs
            WHERE hs.id = :id
            """)
    int deleteServiceById(
            @Param("id") Long id
    );
}