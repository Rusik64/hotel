package com.example.hotel.controller;

import com.example.hotel.dto.RoomDetailsRequest;
import com.example.hotel.dto.RoomDetailsResponse;
import com.example.hotel.service.RoomDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-details")
@RequiredArgsConstructor
public class RoomDetailsController {

    private final RoomDetailsService roomDetailsService;

    @PostMapping("/room/{roomNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomDetailsResponse create(
            @PathVariable Long roomNumber,
            @Valid @RequestBody RoomDetailsRequest request
    ) {
        return roomDetailsService.create(
                roomNumber,
                request
        );
    }

    @GetMapping
    public List<RoomDetailsResponse> getAll() {
        return roomDetailsService.getAll();
    }

    @GetMapping("/{roomNumber}")
    public RoomDetailsResponse getByRoomNumber(
            @PathVariable Long roomNumber
    ) {
        return roomDetailsService.getByRoomNumber(
                roomNumber
        );
    }

    @GetMapping("/available")
    public List<RoomDetailsResponse> getWithAvailablePlaces() {
        return roomDetailsService.getWithAvailablePlaces();
    }

    @PutMapping("/{roomNumber}")
    public RoomDetailsResponse update(
            @PathVariable Long roomNumber,
            @Valid @RequestBody RoomDetailsRequest request
    ) {
        return roomDetailsService.update(
                roomNumber,
                request
        );
    }

    @PatchMapping("/{roomNumber}/booking")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBookingInformation(
            @PathVariable Long roomNumber,
            @RequestParam String bookingInformation
    ) {
        roomDetailsService.updateBookingInformation(
                roomNumber,
                bookingInformation
        );
    }

    @PatchMapping("/{roomNumber}/occupancy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOccupancyInformation(
            @PathVariable Long roomNumber,
            @RequestParam Integer availablePlaces,
            @RequestParam Integer actualResidents
    ) {
        roomDetailsService.updateOccupancyInformation(
                roomNumber,
                availablePlaces,
                actualResidents
        );
    }

    @DeleteMapping("/{roomNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long roomNumber
    ) {
        roomDetailsService.delete(roomNumber);
    }
}