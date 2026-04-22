package com.codereview.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private CodeRepository repository;
    
    @Column(nullable = false)
    private String commitHash;
    
    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;
    
    private String analysisProvider;
    
    @Column(name = "total_files_analyzed")
    private Integer totalFilesAnalyzed;
    
    @Column(name = "total_issues_found")
    private Integer totalIssuesFound;
    
    // Severity counts
    private Integer criticalCount;
    private Integer highCount;
    private Integer mediumCount;
    private Integer lowCount;
    private Integer infoCount;
    
    // Category counts
    @Column(name = "security_count")
    private Integer securityCount;
    
    @Column(name = "code_quality_count")
    private Integer codeQualityCount;
    
    @Column(name = "performance_count")
    private Integer performanceCount;
    
    @Column(name = "best_practices_count")
    private Integer bestPracticesCount;
    
    @Column(name = "maintainability_count")
    private Integer maintainabilityCount;
    
    // Health scores (0.0 to 1.0)
    @Column(name = "overall_health_score")
    private Double overallHealthScore;
    
    @Column(name = "security_health_score")
    private Double securityHealthScore;
    
    @Column(name = "code_quality_health_score")
    private Double codeQualityHealthScore;
    
    @Column(name = "performance_health_score")
    private Double performanceHealthScore;
    
    // Analysis metadata
    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs;
    
    @Column(name = "tokens_used")
    private Long tokensUsed;
    
    @Column(name = "model_version")
    private String modelVersion;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (analyzedAt == null) {
            analyzedAt = LocalDateTime.now();
        }
    }
}
