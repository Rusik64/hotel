package com.example.hotel.controller;

import com.example.hotel.dto.PaymentCardRequest;
import com.example.hotel.dto.PaymentCardResponse;
import com.example.hotel.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentCardResponse create(
            @Valid @RequestBody PaymentCardRequest request
    ) {
        return paymentCardService.create(request);
    }

    @GetMapping
    public List<PaymentCardResponse> getAll() {
        return paymentCardService.getAll();
    }

    @GetMapping("/{id}")
    public PaymentCardResponse getById(
            @PathVariable Long id
    ) {
        return paymentCardService.getById(id);
    }

    @GetMapping("/registration/{registrationId}")
    public PaymentCardResponse getByRegistrationId(
            @PathVariable Long registrationId
    ) {
        return paymentCardService
                .getByRegistrationId(registrationId);
    }

    @GetMapping("/search/status")
    public List<PaymentCardResponse> getByPaymentStatus(
            @RequestParam String paymentStatus
    ) {
        return paymentCardService
                .getByPaymentStatus(paymentStatus);
    }

    @PutMapping("/{id}")
    public PaymentCardResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardRequest request
    ) {
        return paymentCardService.update(
                id,
                request
        );
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus
    ) {
        paymentCardService.updatePaymentStatus(
                id,
                paymentStatus
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        paymentCardService.delete(id);
    }
}