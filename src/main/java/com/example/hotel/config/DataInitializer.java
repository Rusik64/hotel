package com.example.hotel.config;

import com.example.hotel.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!employeeRepository.existsEmployeeByUsername("admin")) {
            employeeRepository.insertEmployee(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "Главный",
                    "Администратор",
                    null,
                    "ADMIN",
                    true
            );
            System.out.println("✅ Создан администратор: admin / admin123");
        }
    }
}