package com.example.hotel.dto;

import java.util.Map;

public record LoginResponse(String token, Map<String, Object> user) {}