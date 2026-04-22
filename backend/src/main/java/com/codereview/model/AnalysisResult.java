package com.codereview.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;
    
    @Column(nullable = false)
    private String category; // SECURITY, CODE_QUALITY, PERFORMANCE, BEST_PRACTICES, MAINTAINABILITY
    
    @Column(nullable = false)
    private String type; // Specific issue type (e.g., SQL_INJECTION, HIGH_CYCLOMATIC_COMPLEXITY)
    
    @Column(nullable = false)
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW, INFO
    
    @Column(nullable = false)
    private String filePath;
    
    private Integer lineNumber;
    
    @Column(name = "end_line_number")
    private Integer endLineNumber;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(columnDefinition = "TEXT")
    private String suggestion;
    
    @Column(columnDefinition = "TEXT")
    private String codeSnippet;
    
    @Column(columnDefinition = "TEXT")
    private String explanation; // Detailed explanation of why this is an issue
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // 0.0 to 1.0, confidence in the analysis
    
    @Column(name = "impact_score")
    private Double impactScore; // 0.0 to 1.0, potential impact if not fixed
    
    @Column(name = "effort_score")
    private Double effortScore; // 0.0 to 1.0, effort required to fix
    
    @Column(name = "cwe_id")
    private String cweId; // CWE identifier for security issues
    
    @Column(name = "owasp_category")
    private String owaspCategory; // OWASP category for security issues
    
    private String status; // open, resolved, ignored, false_positive
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "open";
        }
        if (confidenceScore == null) {
            confidenceScore = 0.8; // Default confidence
        }
    }
}
