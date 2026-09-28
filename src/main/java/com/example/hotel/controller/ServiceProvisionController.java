package com.example.hotel.controller;

import com.example.hotel.dto.ServiceProvisionRequest;
import com.example.hotel.dto.ServiceProvisionResponse;
import com.example.hotel.service.ServiceProvisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/service-provisions")
@RequiredArgsConstructor
public class ServiceProvisionController {

    private final ServiceProvisionService serviceProvisionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceProvisionResponse create(
            @Valid @RequestBody ServiceProvisionRequest request
    ) {
        return serviceProvisionService.create(request);
    }

    @GetMapping
    public List<ServiceProvisionResponse> getAll() {
        return serviceProvisionService.getAll();
    }

    @GetMapping("/{id}")
    public ServiceProvisionResponse getById(
            @PathVariable Long id
    ) {
        return serviceProvisionService.getById(id);
    }

    @GetMapping("/service/{serviceId}")
    public List<ServiceProvisionResponse> getByServiceId(
            @PathVariable Long serviceId
    ) {
        return serviceProvisionService
                .getByServiceId(serviceId);
    }

    @GetMapping("/search/date")
    public List<ServiceProvisionResponse> getByDatePeriod(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return serviceProvisionService.getByDatePeriod(
                startDate,
                endDate
        );
    }

    @PutMapping("/{id}")
    public ServiceProvisionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceProvisionRequest request
    ) {
        return serviceProvisionService.update(
                id,
                request
        );
    }

    @PatchMapping("/{id}/quantity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        serviceProvisionService.updateQuantity(
                id,
                quantity
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        serviceProvisionService.delete(id);
    }
}