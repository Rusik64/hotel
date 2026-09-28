package com.example.hotel.repository;

import com.example.hotel.repository.model.PaymentCard;
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
public interface PaymentCardRepository
        extends JpaRepository<PaymentCard, Long> {

    @Query("""
            SELECT pc
            FROM PaymentCard pc
            LEFT JOIN FETCH pc.registration
            ORDER BY pc.id
            """)
    List<PaymentCard> findAllPaymentCards();

    @Query("""
            SELECT pc
            FROM PaymentCard pc
            LEFT JOIN FETCH pc.registration
            WHERE pc.id = :id
            """)
    Optional<PaymentCard> findPaymentCardById(
            @Param("id") Long id
    );

    @Query("""
            SELECT pc
            FROM PaymentCard pc
            LEFT JOIN FETCH pc.registration
            WHERE pc.registration.id = :registrationId
            """)
    Optional<PaymentCard> findByRegistrationId(
            @Param("registrationId") Long registrationId
    );

    @Query("""
            SELECT pc
            FROM PaymentCard pc
            LEFT JOIN FETCH pc.registration
            WHERE pc.paymentStatus = :paymentStatus
            ORDER BY pc.id
            """)
    List<PaymentCard> findByPaymentStatus(
            @Param("paymentStatus") String paymentStatus
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE PaymentCard pc
            SET pc.paymentStatus = :paymentStatus
            WHERE pc.id = :id
            """)
    int updatePaymentStatus(
            @Param("id") Long id,
            @Param("paymentStatus") String paymentStatus
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE PaymentCard pc
            SET pc.paidDays = :paidDays,
                pc.totalAmount = :totalAmount,
                pc.paymentStatus = :paymentStatus
            WHERE pc.id = :id
            """)
    int updatePaymentCard(
            @Param("id") Long id,
            @Param("paidDays") Integer paidDays,
            @Param("totalAmount") BigDecimal totalAmount,
            @Param("paymentStatus") String paymentStatus
    );

    @Modifying
    @Transactional
    @Query("""
            DELETE FROM PaymentCard pc
            WHERE pc.id = :id
            """)
    int deletePaymentCardById(
            @Param("id") Long id
    );
}