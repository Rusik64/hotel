package com.example.hotel.service;

import com.example.hotel.dto.ClientRequest;
import com.example.hotel.dto.ClientResponse;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repository.ClientRepository;
import com.example.hotel.repository.model.Client;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponse create(ClientRequest request) {

        Client client = Client.builder()
                .lastName(request.lastName())
                .firstName(request.firstName())
                .middleName(request.middleName())
                .identityDocument(request.identityDocument())
                .passportSeries(request.passportSeries())
                .passportNumber(request.passportNumber())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .homeAddress(request.homeAddress())
                .phone(request.phone())
                .build();

        Client savedClient = clientRepository.save(client);

        return toResponse(savedClient);
    }

    public List<ClientResponse> getAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClientResponse getById(Long id) {

        Client client = findClient(id);

        return toResponse(client);
    }

    public ClientResponse update(Long id, ClientRequest request) {

        Client client = findClient(id);

        client.setLastName(request.lastName());
        client.setFirstName(request.firstName());
        client.setMiddleName(request.middleName());
        client.setIdentityDocument(request.identityDocument());
        client.setPassportSeries(request.passportSeries());
        client.setPassportNumber(request.passportNumber());
        client.setBirthDate(request.birthDate());
        client.setGender(request.gender());
        client.setHomeAddress(request.homeAddress());
        client.setPhone(request.phone());

        Client updatedClient = clientRepository.save(client);

        return toResponse(updatedClient);
    }

    public void delete(Long id) {

        Client client = findClient(id);

        clientRepository.delete(client);
    }

    private Client findClient(Long id) {

        return clientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Клиент с ID " + id + " не найден"
                        )
                );
    }

    private ClientResponse toResponse(Client client) {

        return new ClientResponse(
                client.getId(),
                client.getLastName(),
                client.getFirstName(),
                client.getMiddleName(),
                client.getIdentityDocument(),
                client.getPassportSeries(),
                client.getPassportNumber(),
                client.getBirthDate(),
                client.getGender(),
                client.getHomeAddress(),
                client.getPhone()
        );
    }
}