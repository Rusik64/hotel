package com.example.hotel.service;

import com.example.hotel.dto.PaymentCardRequest;
import com.example.hotel.dto.PaymentCardResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.PaymentCardRepository;
import com.example.hotel.repository.RegistrationRepository;
import com.example.hotel.repository.model.PaymentCard;
import com.example.hotel.repository.model.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final RegistrationRepository registrationRepository;

    @Transactional
    public PaymentCardResponse create(
            PaymentCardRequest request
    ) {

        Registration registration =
                registrationRepository
                        .findRegistrationById(
                                request.registrationId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Регистрация с ID "
                                                + request.registrationId()
                                                + " не найдена"
                                )
                        );

        if (paymentCardRepository
                .findByRegistrationId(
                        request.registrationId()
                )
                .isPresent()) {

            throw new IllegalStateException(
                    "Для регистрации с ID "
                            + request.registrationId()
                            + " расчётная карточка уже существует"
            );
        }

        PaymentCard paymentCard = PaymentCard.builder()
                .registration(registration)
                .paidDays(request.paidDays())
                .totalAmount(request.totalAmount())
                .paymentStatus(request.paymentStatus())
                .build();

        PaymentCard savedPaymentCard =
                paymentCardRepository.save(paymentCard);

        return toResponse(savedPaymentCard);
    }

    public List<PaymentCardResponse> getAll() {

        return paymentCardRepository
                .findAllPaymentCards()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentCardResponse getById(Long id) {

        return toResponse(findPaymentCard(id));
    }

    public PaymentCardResponse getByRegistrationId(
            Long registrationId
    ) {

        PaymentCard paymentCard =
                paymentCardRepository
                        .findByRegistrationId(registrationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Расчётная карточка для регистрации с ID "
                                                + registrationId
                                                + " не найдена"
                                )
                        );

        return toResponse(paymentCard);
    }

    public List<PaymentCardResponse> getByPaymentStatus(
            String paymentStatus
    ) {

        return paymentCardRepository
                .findByPaymentStatus(paymentStatus)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PaymentCardResponse update(
            Long id,
            PaymentCardRequest request
    ) {

        PaymentCard paymentCard = findPaymentCard(id);

        int updatedRows =
                paymentCardRepository.updatePaymentCard(
                        id,
                        request.paidDays(),
                        request.totalAmount(),
                        request.paymentStatus()
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Расчётная карточка с ID "
                            + id + " не найдена"
            );
        }

        paymentCard.setPaidDays(request.paidDays());
        paymentCard.setTotalAmount(request.totalAmount());
        paymentCard.setPaymentStatus(
                request.paymentStatus()
        );

        return toResponse(paymentCard);
    }

    @Transactional
    public void updatePaymentStatus(
            Long id,
            String paymentStatus
    ) {

        int updatedRows =
                paymentCardRepository.updatePaymentStatus(
                        id,
                        paymentStatus
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Расчётная карточка с ID "
                            + id + " не найдена"
            );
        }
    }

    @Transactional
    public void delete(Long id) {

        int deletedRows =
                paymentCardRepository
                        .deletePaymentCardById(id);

        if (deletedRows == 0) {
            throw new ResourceNotFoundException(
                    "Расчётная карточка с ID "
                            + id + " не найдена"
            );
        }
    }

    private PaymentCard findPaymentCard(Long id) {

        return paymentCardRepository
                .findPaymentCardById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Расчётная карточка с ID "
                                        + id + " не найдена"
                        )
                );
    }

    private PaymentCardResponse toResponse(
            PaymentCard paymentCard
    ) {

        return new PaymentCardResponse(
                paymentCard.getId(),
                paymentCard.getRegistration().getId(),
                paymentCard.getPaidDays(),
                paymentCard.getTotalAmount(),
                paymentCard.getPaymentStatus()
        );
    }
}