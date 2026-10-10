package com.civicpulse.Civicpulse.dto;




import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StatusUpdateRequest(

    @NotBlank
    @Pattern(
        regexp = "Pending|In Progress|Resolved"
    )
    String status

) {}
