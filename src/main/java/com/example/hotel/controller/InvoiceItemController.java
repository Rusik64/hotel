package com.example.hotel.controller;

import com.example.hotel.dto.InvoiceItemRequest;
import com.example.hotel.dto.InvoiceItemResponse;
import com.example.hotel.service.InvoiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/invoice-items")
@RequiredArgsConstructor
public class InvoiceItemController {

    private final InvoiceItemService invoiceItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceItemResponse create(
            @Valid @RequestBody InvoiceItemRequest request
    ) {
        return invoiceItemService.create(request);
    }

    @GetMapping
    public List<InvoiceItemResponse> getAll() {
        return invoiceItemService.getAll();
    }

    @GetMapping("/{id}")
    public InvoiceItemResponse getById(
            @PathVariable Long id
    ) {
        return invoiceItemService.getById(id);
    }

    @GetMapping("/payment-card/{paymentCardId}")
    public List<InvoiceItemResponse>
    getByPaymentCardId(
            @PathVariable Long paymentCardId
    ) {
        return invoiceItemService
                .getByPaymentCardId(paymentCardId);
    }

    @GetMapping(
            "/service-provision/{serviceProvisionId}"
    )
    public List<InvoiceItemResponse>
    getByServiceProvisionId(
            @PathVariable Long serviceProvisionId
    ) {
        return invoiceItemService
                .getByServiceProvisionId(
                        serviceProvisionId
                );
    }

    @PutMapping("/{id}")
    public InvoiceItemResponse update(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceItemRequest request
    ) {
        return invoiceItemService.update(
                id,
                request
        );
    }

    @PatchMapping("/{id}/amount")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAmount(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        invoiceItemService.updateAmount(
                id,
                amount
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        invoiceItemService.delete(id);
    }
}