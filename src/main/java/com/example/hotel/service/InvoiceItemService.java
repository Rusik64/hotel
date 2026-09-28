package com.example.hotel.service;

import com.example.hotel.dto.InvoiceItemRequest;
import com.example.hotel.dto.InvoiceItemResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.InvoiceItemRepository;
import com.example.hotel.repository.PaymentCardRepository;
import com.example.hotel.repository.ServiceProvisionRepository;
import com.example.hotel.repository.model.InvoiceItem;
import com.example.hotel.repository.model.PaymentCard;
import com.example.hotel.repository.model.ServiceProvision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;

    private final PaymentCardRepository paymentCardRepository;

    private final ServiceProvisionRepository
            serviceProvisionRepository;

    @Transactional
    public InvoiceItemResponse create(
            InvoiceItemRequest request
    ) {

        PaymentCard paymentCard =
                findPaymentCard(
                        request.paymentCardId()
                );

        ServiceProvision serviceProvision =
                findServiceProvision(
                        request.serviceProvisionId()
                );

        InvoiceItem invoiceItem =
                InvoiceItem.builder()
                        .paymentCard(paymentCard)
                        .serviceProvision(serviceProvision)
                        .amount(request.amount())
                        .build();

        InvoiceItem savedInvoiceItem =
                invoiceItemRepository.save(invoiceItem);

        return toResponse(savedInvoiceItem);
    }

    public List<InvoiceItemResponse> getAll() {

        return invoiceItemRepository
                .findAllInvoiceItems()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InvoiceItemResponse getById(Long id) {

        return toResponse(findInvoiceItem(id));
    }

    public List<InvoiceItemResponse> getByPaymentCardId(
            Long paymentCardId
    ) {

        return invoiceItemRepository
                .findByPaymentCardId(paymentCardId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<InvoiceItemResponse>
    getByServiceProvisionId(
            Long serviceProvisionId
    ) {

        return invoiceItemRepository
                .findByServiceProvisionId(
                        serviceProvisionId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public InvoiceItemResponse update(
            Long id,
            InvoiceItemRequest request
    ) {

        InvoiceItem invoiceItem =
                findInvoiceItem(id);

        PaymentCard paymentCard =
                findPaymentCard(
                        request.paymentCardId()
                );

        ServiceProvision serviceProvision =
                findServiceProvision(
                        request.serviceProvisionId()
                );

        int updatedRows =
                invoiceItemRepository.updateInvoiceItem(
                        id,
                        paymentCard,
                        serviceProvision,
                        request.amount()
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Позиция счёта с ID "
                            + id + " не найдена"
            );
        }

        invoiceItem.setPaymentCard(paymentCard);
        invoiceItem.setServiceProvision(
                serviceProvision
        );
        invoiceItem.setAmount(request.amount());

        return toResponse(invoiceItem);
    }

    @Transactional
    public void updateAmount(
            Long id,
            BigDecimal amount
    ) {

        if (amount == null
                || amount.signum() <= 0) {

            throw new IllegalArgumentException(
                    "Сумма должна быть больше 0"
            );
        }

        int updatedRows =
                invoiceItemRepository.updateAmount(
                        id,
                        amount
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Позиция счёта с ID "
                            + id + " не найдена"
            );
        }
    }

    @Transactional
    public void delete(Long id) {

        int deletedRows =
                invoiceItemRepository
                        .deleteInvoiceItemById(id);

        if (deletedRows == 0) {
            throw new ResourceNotFoundException(
                    "Позиция счёта с ID "
                            + id + " не найдена"
            );
        }
    }

    private InvoiceItem findInvoiceItem(Long id) {

        return invoiceItemRepository
                .findInvoiceItemById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Позиция счёта с ID "
                                        + id + " не найдена"
                        )
                );
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

    private ServiceProvision findServiceProvision(
            Long id
    ) {

        return serviceProvisionRepository
                .findServiceProvisionById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Факт оказания услуги с ID "
                                        + id + " не найден"
                        )
                );
    }

    private InvoiceItemResponse toResponse(
            InvoiceItem invoiceItem
    ) {

        ServiceProvision serviceProvision =
                invoiceItem.getServiceProvision();

        return new InvoiceItemResponse(
                invoiceItem.getId(),
                invoiceItem.getPaymentCard().getId(),
                serviceProvision.getId(),
                serviceProvision.getHotelService().getId(),
                serviceProvision.getHotelService()
                        .getServiceName(),
                invoiceItem.getAmount()
        );
    }
}