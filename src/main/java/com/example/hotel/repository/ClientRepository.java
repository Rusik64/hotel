package com.example.hotel.repository;

import com.example.hotel.repository.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("""
            SELECT c
            FROM Client c
            ORDER BY c.id
            """)
    List<Client> findAllClients();

    @Query("""
            SELECT c
            FROM Client c
            WHERE c.id = :id
            """)
    Optional<Client> findClientById(
            @Param("id") Long id
    );
}