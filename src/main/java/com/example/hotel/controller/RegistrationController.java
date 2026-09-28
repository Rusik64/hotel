package com.example.hotel.controller;

import com.example.hotel.dto.RegistrationRequest;
import com.example.hotel.dto.RegistrationResponse;
import com.example.hotel.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse create(
            @Valid @RequestBody RegistrationRequest request
    ) {
        return registrationService.create(request);
    }

    @GetMapping
    public List<RegistrationResponse> getAll() {
        return registrationService.getAll();
    }

    @GetMapping("/{id}")
    public RegistrationResponse getById(
            @PathVariable Long id
    ) {
        return registrationService.getById(id);
    }

    @GetMapping("/client/{clientId}")
    public List<RegistrationResponse> getByClientId(
            @PathVariable Long clientId
    ) {
        return registrationService.getByClientId(clientId);
    }

    @GetMapping("/room/{roomNumber}")
    public List<RegistrationResponse> getByRoomNumber(
            @PathVariable Long roomNumber
    ) {
        return registrationService.getByRoomNumber(roomNumber);
    }

    @GetMapping("/active")
    public List<RegistrationResponse> getActive() {
        return registrationService.getActive();
    }

    @GetMapping("/search/arrival")
    public List<RegistrationResponse> getByArrivalPeriod(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return registrationService.getByArrivalPeriod(
                startDate,
                endDate
        );
    }

    @PutMapping("/{id}")
    public RegistrationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RegistrationRequest request
    ) {
        return registrationService.update(id, request);
    }

    @PatchMapping("/{id}/checkout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkOut(
            @PathVariable Long id,
            @RequestParam LocalDate departureDate
    ) {
        registrationService.checkOut(
                id,
                departureDate
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        registrationService.delete(id);
    }
}