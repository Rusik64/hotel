package com.example.hotel.service;

import com.example.hotel.dto.ServiceProvisionRequest;
import com.example.hotel.dto.ServiceProvisionResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.HotelServiceRepository;
import com.example.hotel.repository.ServiceProvisionRepository;
import com.example.hotel.repository.model.HotelService;
import com.example.hotel.repository.model.ServiceProvision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceProvisionService {

    private final ServiceProvisionRepository serviceProvisionRepository;
    private final HotelServiceRepository hotelServiceRepository;

    @Transactional
    public ServiceProvisionResponse create(
            ServiceProvisionRequest request
    ) {

        HotelService hotelService = findHotelService(
                request.serviceId()
        );

        ServiceProvision serviceProvision =
                ServiceProvision.builder()
                        .hotelService(hotelService)
                        .provisionDate(request.provisionDate())
                        .quantity(request.quantity())
                        .build();

        ServiceProvision savedProvision =
                serviceProvisionRepository.save(serviceProvision);

        return toResponse(savedProvision);
    }

    public List<ServiceProvisionResponse> getAll() {

        return serviceProvisionRepository
                .findAllServiceProvisions()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServiceProvisionResponse getById(Long id) {

        return toResponse(findServiceProvision(id));
    }

    public List<ServiceProvisionResponse> getByServiceId(
            Long serviceId
    ) {

        return serviceProvisionRepository
                .findByServiceId(serviceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServiceProvisionResponse> getByDatePeriod(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Дата начала не может быть позже даты окончания"
            );
        }

        return serviceProvisionRepository
                .findByDatePeriod(startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ServiceProvisionResponse update(
            Long id,
            ServiceProvisionRequest request
    ) {

        ServiceProvision serviceProvision =
                findServiceProvision(id);

        HotelService hotelService =
                findHotelService(request.serviceId());

        int updatedRows =
                serviceProvisionRepository
                        .updateServiceProvision(
                                id,
                                hotelService,
                                request.provisionDate(),
                                request.quantity()
                        );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Факт оказания услуги с ID "
                            + id + " не найден"
            );
        }

        serviceProvision.setHotelService(hotelService);
        serviceProvision.setProvisionDate(
                request.provisionDate()
        );
        serviceProvision.setQuantity(request.quantity());

        return toResponse(serviceProvision);
    }

    @Transactional
    public void updateQuantity(
            Long id,
            Integer quantity
    ) {

        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Количество должно быть не менее 1"
            );
        }

        int updatedRows =
                serviceProvisionRepository
                        .updateQuantity(id, quantity);

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Факт оказания услуги с ID "
                            + id + " не найден"
            );
        }
    }

    @Transactional
    public void delete(Long id) {

        int deletedRows =
                serviceProvisionRepository
                        .deleteServiceProvisionById(id);

        if (deletedRows == 0) {
            throw new ResourceNotFoundException(
                    "Факт оказания услуги с ID "
                            + id + " не найден"
            );
        }
    }

    private ServiceProvision findServiceProvision(Long id) {

        return serviceProvisionRepository
                .findServiceProvisionById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Факт оказания услуги с ID "
                                        + id + " не найден"
                        )
                );
    }

    private HotelService findHotelService(Long id) {

        return hotelServiceRepository
                .findServiceById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Услуга с ID "
                                        + id + " не найдена"
                        )
                );
    }

    private ServiceProvisionResponse toResponse(
            ServiceProvision serviceProvision
    ) {

        return new ServiceProvisionResponse(
                serviceProvision.getId(),
                serviceProvision.getHotelService().getId(),
                serviceProvision.getHotelService().getServiceName(),
                serviceProvision.getProvisionDate(),
                serviceProvision.getQuantity()
        );
    }
}