package com.example.hotel.controller;

import com.example.hotel.dto.EmployeePrincipal;
import com.example.hotel.dto.LoginRequest;
import com.example.hotel.dto.LoginResponse;
import com.example.hotel.repository.EmployeeRepository;
import com.example.hotel.repository.model.Employee;
import com.example.hotel.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Employee employee = employeeRepository
                .findEmployeeByUsername(request.username())
                .orElse(null);

        if (employee == null ||
                !passwordEncoder.matches(request.password(), employee.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Неверный логин или пароль"));
        }

        if (!employee.getActive()) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Учётная запись заблокирована"));
        }

        String token = jwtUtils.generateToken(
                employee.getUsername(),
                employee.getRole(),
                employee.getId()
        );

        Map<String, Object> user = new HashMap<>();
        user.put("id", employee.getId());
        user.put("username", employee.getUsername());
        user.put("firstName", employee.getFirstName());
        user.put("lastName", employee.getLastName());
        user.put("role", employee.getRole());

        return ResponseEntity.ok(new LoginResponse(token, user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        EmployeePrincipal principal =
                (EmployeePrincipal) authentication.getPrincipal();

        Employee employee = employeeRepository
                .findEmployeeById(principal.id())
                .orElse(null);

        if (employee == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Пользователь не найден"));
        }

        Map<String, Object> user = new HashMap<>();
        user.put("id", employee.getId());
        user.put("username", employee.getUsername());
        user.put("firstName", employee.getFirstName());
        user.put("lastName", employee.getLastName());
        user.put("role", employee.getRole());

        return ResponseEntity.ok(user);
    }
}