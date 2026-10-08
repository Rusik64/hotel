package com.example.hotel.service;

import com.example.hotel.dto.RegistrationRequest;
import com.example.hotel.dto.RegistrationResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.ClientRepository;
import com.example.hotel.repository.RegistrationRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.model.Client;
import com.example.hotel.repository.model.Registration;
import com.example.hotel.repository.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final ClientRepository clientRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public RegistrationResponse create(
            RegistrationRequest request
    ) {

        validateDates(
                request.arrivalDate(),
                request.departureDate()
        );

        Client client = clientRepository
                .findClientById(request.clientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Клиент с ID "
                                        + request.clientId()
                                        + " не найден"
                        )
                );

        Room room = roomRepository
                .findRoomByNumber(request.roomNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Номер с ID "
                                        + request.roomNumber()
                                        + " не найден"
                        )
                );

        // ✅ Проверяем пересечение периодов бронирования
        checkOverlap(
                room.getRoomNumber(),
                request.arrivalDate(),
                request.departureDate(),
                null
        );

        Registration registration = Registration.builder()
                .client(client)
                .room(room)
                .arrivalDate(request.arrivalDate())
                .departureDate(request.departureDate())
                .build();

        Registration savedRegistration =
                registrationRepository.save(registration);

        // ✅ Помечаем номер как занятый только если клиент ещё не выехал
        if (request.departureDate() == null) {
            roomRepository.updateRoomStatus(
                    room.getRoomNumber(),
                    true
            );
        }

        return toResponse(savedRegistration);
    }

    public List<RegistrationResponse> getAll() {

        return registrationRepository.findAllRegistrations()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RegistrationResponse getById(Long id) {

        return toResponse(findRegistration(id));
    }

    public List<RegistrationResponse> getByClientId(
            Long clientId
    ) {

        return registrationRepository
                .findRegistrationsByClientId(clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RegistrationResponse> getByRoomNumber(
            Long roomNumber
    ) {

        return registrationRepository
                .findRegistrationsByRoomNumber(roomNumber)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RegistrationResponse> getActive() {

        return registrationRepository.findActiveRegistrations()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RegistrationResponse> getByArrivalPeriod(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Дата начала не может быть позже даты окончания"
            );
        }

        return registrationRepository
                .findRegistrationsByArrivalPeriod(
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RegistrationResponse update(
            Long id,
            RegistrationRequest request
    ) {

        validateDates(
                request.arrivalDate(),
                request.departureDate()
        );

        Registration registration = findRegistration(id);

        Client client = clientRepository
                .findClientById(request.clientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Клиент с ID "
                                        + request.clientId()
                                        + " не найден"
                        )
                );

        Room room = roomRepository
                .findRoomByNumber(request.roomNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Номер с ID "
                                        + request.roomNumber()
                                        + " не найден"
                        )
                );

        // ✅ Проверяем пересечение периодов (исключая текущую регистрацию)
        checkOverlap(
                room.getRoomNumber(),
                request.arrivalDate(),
                request.departureDate(),
                id
        );

        registration.setClient(client);
        registration.setRoom(room);
        registration.setArrivalDate(request.arrivalDate());
        registration.setDepartureDate(request.departureDate());

        Registration updatedRegistration =
                registrationRepository.save(registration);

        // ✅ Обновляем статус номера в зависимости от даты выезда
        if (request.departureDate() == null) {
            roomRepository.updateRoomStatus(
                    room.getRoomNumber(),
                    true
            );
        } else {
            roomRepository.updateRoomStatus(
                    room.getRoomNumber(),
                    false
            );
        }

        return toResponse(updatedRegistration);
    }

    @Transactional
    public void checkOut(
            Long id,
            LocalDate departureDate
    ) {

        Registration registration = findRegistration(id);

        if (registration.getDepartureDate() != null) {
            throw new IllegalStateException(
                    "Клиент по данной регистрации уже выселен"
            );
        }

        if (departureDate.isBefore(
                registration.getArrivalDate()
        )) {
            throw new IllegalArgumentException(
                    "Дата выезда не может быть раньше даты прибытия"
            );
        }

        int updatedRows =
                registrationRepository.updateDepartureDate(
                        id,
                        departureDate
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Регистрация с ID " + id + " не найдена"
            );
        }

        roomRepository.updateRoomStatus(
                registration.getRoom().getRoomNumber(),
                false
        );
    }

    @Transactional
    public void delete(Long id) {

        Registration registration = findRegistration(id);

        int deletedRows =
                registrationRepository
                        .deleteRegistrationById(id);

        if (deletedRows == 0) {
            throw new ResourceNotFoundException(
                    "Регистрация с ID " + id + " не найдена"
            );
        }

        if (registration.getDepartureDate() == null) {
            roomRepository.updateRoomStatus(
                    registration.getRoom().getRoomNumber(),
                    false
            );
        }
    }

    private Registration findRegistration(Long id) {

        return registrationRepository
                .findRegistrationById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Регистрация с ID "
                                        + id + " не найдена"
                        )
                );
    }

    private void validateDates(
            LocalDate arrivalDate,
            LocalDate departureDate
    ) {

        if (departureDate != null
                && departureDate.isBefore(arrivalDate)) {

            throw new IllegalArgumentException(
                    "Дата выезда не может быть раньше даты прибытия"
            );
        }
    }

    /**
     * Проверяет, не пересекается ли новый период бронирования
     * с существующими регистрациями для того же номера.
     *
     * @param roomNumber     номер комнаты
     * @param arrivalDate    дата заезда
     * @param departureDate  дата выезда (может быть null — тогда считается бесконечностью)
     * @param excludeId      ID регистрации, которую нужно исключить из проверки (при обновлении)
     */
    private void checkOverlap(
            Long roomNumber,  // ← Изменено с Integer на Long
            LocalDate arrivalDate,
            LocalDate departureDate,
            Long excludeId
    ) {

        LocalDate effectiveDeparture = departureDate != null
                ? departureDate
                : LocalDate.of(2099, 12, 31);

        List<Registration> overlapping = registrationRepository
                .findOverlappingRegistrations(
                        roomNumber,
                        arrivalDate,
                        effectiveDeparture
                );

        if (excludeId != null) {
            overlapping = overlapping.stream()
                    .filter(r -> !r.getId().equals(excludeId))
                    .toList();
        }

        if (!overlapping.isEmpty()) {
            throw new IllegalStateException(
                    "Номер " + roomNumber
                            + " уже забронирован на этот период"
            );
        }
    }

    private RegistrationResponse toResponse(
            Registration registration
    ) {

        return new RegistrationResponse(
                registration.getId(),
                registration.getRoom().getRoomNumber(),
                registration.getClient().getId(),
                registration.getArrivalDate(),
                registration.getDepartureDate()
        );
    }
}