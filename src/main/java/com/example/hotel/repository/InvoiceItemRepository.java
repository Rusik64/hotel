package com.example.hotel.repository;

import com.example.hotel.repository.model.InvoiceItem;
import com.example.hotel.repository.model.PaymentCard;
import com.example.hotel.repository.model.ServiceProvision;
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
public interface InvoiceItemRepository
        extends JpaRepository<InvoiceItem, Long> {

    @Query("""
            SELECT ii
            FROM InvoiceItem ii
            LEFT JOIN FETCH ii.paymentCard
            LEFT JOIN FETCH ii.serviceProvision sp
            LEFT JOIN FETCH sp.hotelService
            ORDER BY ii.id
            """)
    List<InvoiceItem> findAllInvoiceItems();

    @Query("""
            SELECT ii
            FROM InvoiceItem ii
            LEFT JOIN FETCH ii.paymentCard
            LEFT JOIN FETCH ii.serviceProvision sp
            LEFT JOIN FETCH sp.hotelService
            WHERE ii.id = :id
            """)
    Optional<InvoiceItem> findInvoiceItemById(
            @Param("id") Long id
    );

    @Query("""
            SELECT ii
            FROM InvoiceItem ii
            LEFT JOIN FETCH ii.paymentCard
            LEFT JOIN FETCH ii.serviceProvision sp
            LEFT JOIN FETCH sp.hotelService
            WHERE ii.paymentCard.id = :paymentCardId
            ORDER BY ii.id
            """)
    List<InvoiceItem> findByPaymentCardId(
            @Param("paymentCardId") Long paymentCardId
    );

    @Query("""
            SELECT ii
            FROM InvoiceItem ii
            LEFT JOIN FETCH ii.paymentCard
            LEFT JOIN FETCH ii.serviceProvision sp
            LEFT JOIN FETCH sp.hotelService
            WHERE ii.serviceProvision.id = :serviceProvisionId
            ORDER BY ii.id
            """)
    List<InvoiceItem> findByServiceProvisionId(
            @Param("serviceProvisionId")
            Long serviceProvisionId
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE InvoiceItem ii
        SET ii.paymentCard = :paymentCard,
            ii.serviceProvision = :serviceProvision,
            ii.amount = :amount
        WHERE ii.id = :id
        """)
    int updateInvoiceItem(
            @Param("id") Long id,
            @Param("paymentCard")
            PaymentCard paymentCard,
            @Param("serviceProvision")
            ServiceProvision serviceProvision,
            @Param("amount")
            BigDecimal amount
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE InvoiceItem ii
            SET ii.amount = :amount
            WHERE ii.id = :id
            """)
    int updateAmount(
            @Param("id") Long id,
            @Param("amount") java.math.BigDecimal amount
    );

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM InvoiceItem ii
            WHERE ii.id = :id
            """)
    int deleteInvoiceItemById(
            @Param("id") Long id
    );
}