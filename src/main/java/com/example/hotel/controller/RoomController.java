package com.example.hotel.controller;

import com.example.hotel.dto.RoomRequest;
import com.example.hotel.dto.RoomResponse;
import com.example.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(
            @Valid @RequestBody RoomRequest request
    ) {
        return roomService.create(request);
    }

    @GetMapping
    public List<RoomResponse> getAll() {
        return roomService.getAll();
    }

    @GetMapping("/{roomNumber}")
    public RoomResponse getByNumber(
            @PathVariable Long roomNumber
    ) {
        return roomService.getByNumber(roomNumber);
    }

    @GetMapping("/available")
    public List<RoomResponse> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    @GetMapping("/search/type")
    public List<RoomResponse> getByType(
            @RequestParam String roomType
    ) {
        return roomService.getByType(roomType);
    }

    @GetMapping("/search/price")
    public List<RoomResponse> getByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return roomService.getByPriceRange(
                minPrice,
                maxPrice
        );
    }

    @PutMapping("/{roomNumber}")
    public RoomResponse update(
            @PathVariable Long roomNumber,
            @Valid @RequestBody RoomRequest request
    ) {
        return roomService.update(roomNumber, request);
    }

    @PatchMapping("/{roomNumber}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(
            @PathVariable Long roomNumber,
            @RequestParam Boolean occupied
    ) {
        roomService.updateStatus(roomNumber, occupied);
    }

    @DeleteMapping("/{roomNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long roomNumber
    ) {
        roomService.delete(roomNumber);
    }
}