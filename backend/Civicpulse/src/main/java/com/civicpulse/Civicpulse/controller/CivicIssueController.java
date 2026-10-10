package com.civicpulse.Civicpulse.controller;


import com.civicpulse.Civicpulse.dto.DashboardStats;
import com.civicpulse.Civicpulse.dto.IssueCreateRequest;
import com.civicpulse.Civicpulse.dto.StatusUpdateRequest;
import com.civicpulse.Civicpulse.model.CivicIssue;
import com.civicpulse.Civicpulse.service.CivicIssueService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class CivicIssueController {

    private final CivicIssueService service;

    public CivicIssueController(CivicIssueService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CivicIssue createIssue(
        @Valid @RequestBody IssueCreateRequest request
    ) {
        return service.createIssue(request);
    }

    @GetMapping
    public List<CivicIssue> getAllIssues(
        @RequestParam(required = false) String status
    ) {
        if (status != null && !status.isBlank()) {
            return service.getIssuesByStatus(status);
        }
        return service.getAllIssues();
    }

    @GetMapping("/{id}")
    public CivicIssue getIssueById(@PathVariable Long id) {
        return service.getIssueById(id);
    }

    @GetMapping("/area/{area}")
    public List<CivicIssue> getIssuesByArea(
        @PathVariable String area
    ) {
        return service.getIssuesByArea(area);
    }

    @PatchMapping("/{id}/status")
    public CivicIssue updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody StatusUpdateRequest request
    ) {
        return service.updateStatus(id, request.status());
    }

    @GetMapping("/stats")
    public DashboardStats getDashboardStats() {
        return service.getDashboardStats();
    }
}