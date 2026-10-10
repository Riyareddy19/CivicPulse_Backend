package com.civicpulse.Civicpulse.dto;


import jakarta.validation.constraints.*;

public record IssueCreateRequest(

    @NotBlank
    @Size(max = 100)
    String reporterName,

    @NotBlank
    @Pattern(regexp = "(?i)Potholes|Streetlights|Garbage|Water leakage|Drainage")
    String category,

    @NotBlank
    @Size(max = 2000)
    String description,

    @NotBlank
    @Size(max = 100)
    String area,

    @Min(1)
    @Max(5)
    int severity

) {}