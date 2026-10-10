package com.civicpulse.Civicpulse.dto;


public record DashboardStats(
    long totalIssues,
    long pending,
    long inProgress,
    long resolved,
    long highPriority,
    long mediumPriority,
    long lowPriority
) {}