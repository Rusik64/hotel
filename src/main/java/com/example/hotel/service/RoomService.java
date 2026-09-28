package com.example.hotel.service;

import com.example.hotel.dto.RoomRequest;
import com.example.hotel.dto.RoomResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponse create(RoomRequest request) {

        Room room = Room.builder()
                .roomType(request.roomType())
                .occupied(request.occupied())
                .numberOfRooms(request.numberOfRooms())
                .floor(request.floor())
                .phone(request.phone())
                .roomPrice(request.roomPrice())
                .build();

        Room savedRoom = roomRepository.save(room);

        return toResponse(savedRoom);
    }

    public List<RoomResponse> getAll() {

        return roomRepository.findAllRooms()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoomResponse getByNumber(Long roomNumber) {

        return roomRepository.findRoomByNumber(roomNumber)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Номер с ID " + roomNumber + " не найден"
                        )
                );
    }

    public List<RoomResponse> getAvailableRooms() {

        return roomRepository.findAllAvailableRooms()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RoomResponse> getByType(String roomType) {

        return roomRepository.findRoomsByType(roomType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RoomResponse> getByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        return roomRepository.findRoomsByPriceRange(
                        minPrice,
                        maxPrice
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoomResponse update(
            Long roomNumber,
            RoomRequest request
    ) {

        Room room = findRoom(roomNumber);

        room.setRoomType(request.roomType());
        room.setOccupied(request.occupied());
        room.setNumberOfRooms(request.numberOfRooms());
        room.setFloor(request.floor());
        room.setPhone(request.phone());
        room.setRoomPrice(request.roomPrice());

        Room updatedRoom = roomRepository.save(room);

        return toResponse(updatedRoom);
    }

    @Transactional
    public void updateStatus(
            Long roomNumber,
            Boolean occupied
    ) {

        int updatedRows = roomRepository.updateRoomStatus(
                roomNumber,
                occupied
        );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Номер с ID " + roomNumber + " не найден"
            );
        }
    }

    @Transactional
    public void delete(Long roomNumber) {

        Room room = findRoom(roomNumber);

        roomRepository.delete(room);
    }

    private Room findRoom(Long roomNumber) {

        return roomRepository.findRoomByNumber(roomNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Номер с ID " + roomNumber + " не найден"
                        )
                );
    }

    private RoomResponse toResponse(Room room) {

        return new RoomResponse(
                room.getRoomNumber(),
                room.getRoomType(),
                room.getOccupied(),
                room.getNumberOfRooms(),
                room.getFloor(),
                room.getPhone(),
                room.getRoomPrice()
        );
    }
}