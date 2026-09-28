package com.example.hotel.controller;

import com.example.hotel.dto.ClientRequest;
import com.example.hotel.dto.ClientResponse;
import com.example.hotel.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(
            @Valid @RequestBody ClientRequest request
    ) {
        return clientService.create(request);
    }

    @GetMapping
    public List<ClientResponse> getAll() {
        return clientService.getAll();
    }

    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PutMapping("/{id}")
    public ClientResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request
    ) {
        return clientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }
}