package com.example.hotel.service;

import com.example.hotel.dto.RoomDetailsRequest;
import com.example.hotel.dto.RoomDetailsResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.RoomDetailsRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.model.Room;
import com.example.hotel.repository.model.RoomDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomDetailsService {

    private final RoomRepository roomRepository;
    private final RoomDetailsRepository roomDetailsRepository;

    @Transactional
    public RoomDetailsResponse create(
            Long roomNumber,
            RoomDetailsRequest request
    ) {

        Room room = roomRepository.findRoomByNumber(roomNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Номер с ID " + roomNumber + " не найден"
                        )
                );

        if (room.getDetails() != null) {
            throw new IllegalStateException(
                    "Для номера с ID " + roomNumber
                            + " сведения уже существуют"
            );
        }

        RoomDetails roomDetails = RoomDetails.builder()
                .room(room)
                .dailyRoomPrice(request.dailyRoomPrice())
                .bookingInformation(request.bookingInformation())
                .availablePlaces(request.availablePlaces())
                .actualResidents(request.actualResidents())
                .build();

        RoomDetails savedDetails = roomDetailsRepository.save(roomDetails);

        return toResponse(savedDetails);
    }

    public List<RoomDetailsResponse> getAll() {

        return roomDetailsRepository.findAllRoomDetails()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RoomDetailsResponse getByRoomNumber(Long roomNumber) {

        RoomDetails roomDetails = findRoomDetails(roomNumber);

        return toResponse(roomDetails);
    }

    public List<RoomDetailsResponse> getWithAvailablePlaces() {

        return roomDetailsRepository.findWithAvailablePlaces()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoomDetailsResponse update(
            Long roomNumber,
            RoomDetailsRequest request
    ) {

        RoomDetails roomDetails = findRoomDetails(roomNumber);

        roomDetails.setDailyRoomPrice(
                request.dailyRoomPrice()
        );
        roomDetails.setBookingInformation(
                request.bookingInformation()
        );
        roomDetails.setAvailablePlaces(
                request.availablePlaces()
        );
        roomDetails.setActualResidents(
                request.actualResidents()
        );

        RoomDetails updatedDetails =
                roomDetailsRepository.save(roomDetails);

        return toResponse(updatedDetails);
    }

    @Transactional
    public void updateBookingInformation(
            Long roomNumber,
            String bookingInformation
    ) {

        int updatedRows =
                roomDetailsRepository.updateBookingInformation(
                        roomNumber,
                        bookingInformation
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Сведения для номера с ID "
                            + roomNumber + " не найдены"
            );
        }
    }

    @Transactional
    public void updateOccupancyInformation(
            Long roomNumber,
            Integer availablePlaces,
            Integer actualResidents
    ) {

        int updatedRows =
                roomDetailsRepository.updateOccupancyInformation(
                        roomNumber,
                        availablePlaces,
                        actualResidents
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Сведения для номера с ID "
                            + roomNumber + " не найдены"
            );
        }
    }

    @Transactional
    public void delete(Long roomNumber) {

        int deletedRows =
                roomDetailsRepository.deleteByRoomNumber(
                        roomNumber
                );

        if (deletedRows == 0) {
            throw new ResourceNotFoundException(
                    "Сведения для номера с ID "
                            + roomNumber + " не найдены"
            );
        }
    }

    private RoomDetails findRoomDetails(Long roomNumber) {

        return roomDetailsRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Сведения для номера с ID "
                                        + roomNumber + " не найдены"
                        )
                );
    }

    private RoomDetailsResponse toResponse(
            RoomDetails roomDetails
    ) {

        return new RoomDetailsResponse(
                roomDetails.getRoomNumber(),
                roomDetails.getDailyRoomPrice(),
                roomDetails.getBookingInformation(),
                roomDetails.getAvailablePlaces(),
                roomDetails.getActualResidents()
        );
    }
}