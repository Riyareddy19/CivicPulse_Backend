package com.civicpulse.Civicpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "civic_issues")

public class CivicIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String reporterName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String area;

    @Column(nullable = false)
    private int severity;

    @Column(nullable = false)
    private int duplicateCount;

    @Column(nullable = false)
    private int populationFactor;

    @Column(nullable = false)
    private int priorityScore;

    @Column(nullable = false, length = 20)
    private String priorityLevel;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "Pending";
        }
    }

    public CivicIssue() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getArea() { return area; }
    public void setArea(String area) {
        this.area = area;
    }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getDuplicateCount() { return duplicateCount; }
    public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public int getPopulationFactor() { return populationFactor; }
    public void setPopulationFactor(int populationFactor) {
        this.populationFactor = populationFactor;
    }

    public int getPriorityScore() { return priorityScore; }
    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
