package com.example.hotel.service;

import com.example.hotel.dto.EmployeeRequest;
import com.example.hotel.dto.EmployeeResponse;
import com.example.hotel.repository.EmployeeRepository;
import com.example.hotel.repository.model.Employee;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAllEmployees().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));
        return convertToResponse(employee);
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsEmployeeByUsername(request.username())) {
            throw new RuntimeException("Сотрудник с таким логином уже существует");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        employeeRepository.insertEmployee(
                request.username(),
                encodedPassword,
                request.firstName(),
                request.lastName(),
                request.middleName(),
                request.role(),
                request.active() != null ? request.active() : true
        );

        Employee created = employeeRepository.findEmployeeByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Ошибка при создании сотрудника"));

        return convertToResponse(created);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        if (!employee.getUsername().equals(request.username())
                && employeeRepository.existsEmployeeByUsername(request.username())) {
            throw new RuntimeException("Сотрудник с таким логином уже существует");
        }

        String password = employee.getPassword();
        if (request.password() != null && !request.password().isEmpty()) {
            password = passwordEncoder.encode(request.password());
        }

        employeeRepository.updateEmployee(
                id,
                request.username(),
                password,
                request.firstName(),
                request.lastName(),
                request.middleName(),
                request.role(),
                request.active() != null ? request.active() : employee.getActive()
        );

        Employee updated = employeeRepository.findEmployeeById(id)
                .orElseThrow(() -> new RuntimeException("Ошибка при обновлении сотрудника"));

        return convertToResponse(updated);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsEmployeeById(id)) {
            throw new RuntimeException("Сотрудник не найден");
        }
        employeeRepository.deleteEmployeeById(id);
    }

    private EmployeeResponse convertToResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getUsername(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getMiddleName(),
                employee.getRole(),
                employee.getActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}