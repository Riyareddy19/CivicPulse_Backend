package com.civicpulse.Civicpulse.repository;


import com.civicpulse.Civicpulse.model.CivicIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CivicIssueRepository
        extends JpaRepository<CivicIssue, Long> {

    long countByAreaIgnoreCaseAndCategoryIgnoreCase(
        String area, String category
    );

    List<CivicIssue> findByAreaIgnoreCaseOrderByPriorityScoreDesc(
        String area
    );

    List<CivicIssue> findAllByOrderByPriorityScoreDescCreatedAtDesc();

    List<CivicIssue> findByStatusIgnoreCaseOrderByPriorityScoreDesc(
        String status
    );
}