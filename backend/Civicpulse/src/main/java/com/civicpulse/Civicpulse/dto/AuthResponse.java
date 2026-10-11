
package com.civicpulse.Civicpulse.dto;

public record AuthResponse(
    Long id,
    String fullName,
    String email,
    String role
) {}
