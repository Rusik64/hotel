package com.example.hotel.controller;

import com.example.hotel.dto.HotelServiceRequest;
import com.example.hotel.dto.HotelServiceResponse;
import com.example.hotel.service.HotelServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class HotelServiceController {

    private final HotelServiceService hotelServiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelServiceResponse create(
            @Valid @RequestBody HotelServiceRequest request
    ) {
        return hotelServiceService.create(request);
    }

    @GetMapping
    public List<HotelServiceResponse> getAll() {
        return hotelServiceService.getAll();
    }

    @GetMapping("/{id}")
    public HotelServiceResponse getById(
            @PathVariable Long id
    ) {
        return hotelServiceService.getById(id);
    }

    @GetMapping("/search")
    public List<HotelServiceResponse> searchByName(
            @RequestParam String name
    ) {
        return hotelServiceService.searchByName(name);
    }

    @GetMapping("/search/price")
    public List<HotelServiceResponse> getByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return hotelServiceService.getByPriceRange(
                minPrice,
                maxPrice
        );
    }

    @PutMapping("/{id}")
    public HotelServiceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody HotelServiceRequest request
    ) {
        return hotelServiceService.update(id, request);
    }

    @PatchMapping("/{id}/price")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrice(
            @PathVariable Long id,
            @RequestParam BigDecimal price
    ) {
        hotelServiceService.updatePrice(id, price);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        hotelServiceService.delete(id);
    }
}