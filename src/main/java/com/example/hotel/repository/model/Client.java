package com.example.hotel.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 100, name = "last_name")
    private String lastName;

    @Column(nullable = false, length = 100, name = "first_name")
    private String firstName;

    @Column(length = 100, name = "middle_name")
    private String middleName;

    @Column(length = 100)
    private String identityDocument;

    @Column(length = 20)
    private String passportSeries;

    @Column(length = 20)
    private String passportNumber;

    private LocalDate birthDate;

    @Column(length = 20)
    private String gender;

    @Column(length = 255)
    private String homeAddress;

    @Column(length = 30)
    private String phone;
}