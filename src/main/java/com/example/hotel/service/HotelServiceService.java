package com.example.hotel.service;

import com.example.hotel.dto.HotelServiceRequest;
import com.example.hotel.dto.HotelServiceResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.HotelServiceRepository;
import com.example.hotel.repository.model.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceService {

    private final HotelServiceRepository hotelServiceRepository;

    @Transactional
    public HotelServiceResponse create(
            HotelServiceRequest request
    ) {

        HotelService hotelService = HotelService.builder()
                .serviceName(request.serviceName())
                .price(request.price())
                .build();

        HotelService savedService =
                hotelServiceRepository.save(hotelService);

        return toResponse(savedService);
    }

    public List<HotelServiceResponse> getAll() {

        return hotelServiceRepository.findAllServices()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public HotelServiceResponse getById(Long id) {

        return toResponse(findService(id));
    }

    public List<HotelServiceResponse> searchByName(
            String serviceName
    ) {

        return hotelServiceRepository
                .searchServicesByName(serviceName)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<HotelServiceResponse> getByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Минимальная стоимость не может быть больше максимальной"
            );
        }

        return hotelServiceRepository
                .findServicesByPriceRange(
                        minPrice,
                        maxPrice
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HotelServiceResponse update(
            Long id,
            HotelServiceRequest request
    ) {

        HotelService hotelService = findService(id);

        int updatedRows =
                hotelServiceRepository.updateService(
                        id,
                        request.serviceName(),
                        request.price()
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Услуга с ID " + id + " не найдена"
            );
        }

        hotelService.setServiceName(request.serviceName());
        hotelService.setPrice(request.price());

        return toResponse(hotelService);
    }

    @Transactional
    public void updatePrice(
            Long id,
            BigDecimal price
    ) {

        if (price.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Стоимость услуги должна быть больше 0"
            );
        }

        int updatedRows =
                hotelServiceRepository.updateServicePrice(
                        id,
                        price
                );

        if (updatedRows == 0) {
            throw new ResourceNotFoundException(
                    "Услуга с ID " + id + " не найдена"
            );
        }
    }

    @Transactional
    public void delete(Long id) {

        int deletedRows =
                hotelServiceRepository.deleteServiceById(id);

        if (deletedRows == 0) {
            throw new ResourceNotFoundException(
                    "Услуга с ID " + id + " не найдена"
            );
        }
    }

    private HotelService findService(Long id) {

        return hotelServiceRepository
                .findServiceById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Услуга с ID " + id + " не найдена"
                        )
                );
    }

    private HotelServiceResponse toResponse(
            HotelService hotelService
    ) {

        return new HotelServiceResponse(
                hotelService.getId(),
                hotelService.getServiceName(),
                hotelService.getPrice()
        );
    }
}