package com.civicpulse.Civicpulse.service;

import com.civicpulse.Civicpulse.dto.DashboardStats;
import com.civicpulse.Civicpulse.dto.IssueCreateRequest;
import com.civicpulse.Civicpulse.model.CivicIssue;
import com.civicpulse.Civicpulse.repository.CivicIssueRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class CivicIssueService {

    private final CivicIssueRepository repository;

    public CivicIssueService(CivicIssueRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CivicIssue createIssue(IssueCreateRequest request) {

        String category = normalizeCategory(request.category());

        String area = request.area().trim();

        long existingReports =
            repository.countByAreaIgnoreCaseAndCategoryIgnoreCase(
                area, category
            );

        int duplicateCount =
            (int) Math.min(existingReports, 1000);

        // Automatically determine locality impact
        int populationFactor =
            getPopulationFactor(area);

        int score =
            (request.severity() * 20)
            + Math.min(duplicateCount * 10, 30)
            + populationFactor;

        String priorityLevel;

        if (score >= 80) {
            priorityLevel = "High";
        } else if (score >= 50) {
            priorityLevel = "Medium";
        } else {
            priorityLevel = "Low";
        }

        CivicIssue issue = new CivicIssue();

        issue.setReporterName(
            request.reporterName().trim()
        );

        issue.setCategory(category);

        issue.setDescription(
            request.description().trim()
        );

        issue.setArea(area);

        issue.setSeverity(
            request.severity()
        );

        issue.setDuplicateCount(
            duplicateCount
        );

        issue.setPopulationFactor(
            populationFactor
        );

        issue.setPriorityScore(score);

        issue.setPriorityLevel(
            priorityLevel
        );

        issue.setDepartment(
            assignDepartment(category)
        );

        issue.setStatus("Pending");

        return repository.save(issue);
    }

    // Automatically assigns impact factor based on locality
    private int getPopulationFactor(String area) {

        return switch (
            area.trim().toLowerCase(Locale.ROOT)
        ) {

            case "kukatpally" -> 20;

            case "madhapur" -> 15;

            case "gachibowli" -> 15;

            case "banjara hills" -> 10;

            case "secunderabad" -> 10;

            default -> 5;
        };
    }

    private String normalizeCategory(String category) {

        return switch (
            category.trim().toLowerCase(Locale.ROOT)
        ) {

            case "potholes" -> "Potholes";

            case "streetlights" -> "Streetlights";

            case "garbage" -> "Garbage";

            case "water leakage" -> "Water leakage";

            case "drainage" -> "Drainage";

            default -> throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid category"
            );
        };
    }

    private String assignDepartment(String category) {

        return switch (category) {

            case "Potholes" ->
                "Roads Department";

            case "Streetlights" ->
                "Electrical Department";

            case "Garbage" ->
                "Sanitation Department";

            case "Water leakage" ->
                "Water Supply Department";

            case "Drainage" ->
                "Drainage Department";

            default ->
                "General Department";
        };
    }

    public List<CivicIssue> getAllIssues() {

        return repository
            .findAllByOrderByPriorityScoreDescCreatedAtDesc();
    }

    public CivicIssue getIssueById(Long id) {

        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Issue not found"
            ));
    }

    public List<CivicIssue> getIssuesByArea(String area) {

        return repository
            .findByAreaIgnoreCaseOrderByPriorityScoreDesc(area);
    }

    public List<CivicIssue> getIssuesByStatus(String status) {

        if (!List.of(
                "Pending",
                "In Progress",
                "Resolved"
            ).contains(status)) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid status"
            );
        }

        return repository
            .findByStatusIgnoreCaseOrderByPriorityScoreDesc(
                status
            );
    }

    @Transactional
    public CivicIssue updateStatus(
        Long id,
        String status
    ) {

        CivicIssue issue = getIssueById(id);

        issue.setStatus(status);

        return repository.save(issue);
    }

    public DashboardStats getDashboardStats() {

        List<CivicIssue> issues =
            repository.findAll();

        long total = issues.size();

        long pending = 0;
        long inProgress = 0;
        long resolved = 0;

        long high = 0;
        long medium = 0;
        long low = 0;

        for (CivicIssue issue : issues) {

            switch (issue.getStatus()) {

                case "Pending" ->
                    pending++;

                case "In Progress" ->
                    inProgress++;

                case "Resolved" ->
                    resolved++;
            }

            switch (issue.getPriorityLevel()) {

                case "High" ->
                    high++;

                case "Medium" ->
                    medium++;

                case "Low" ->
                    low++;
            }
        }

        return new DashboardStats(
            total,
            pending,
            inProgress,
            resolved,
            high,
            medium,
            low
        );
    }
}