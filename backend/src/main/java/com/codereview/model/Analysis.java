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
    private Repository repository;
    
    @Column(nullable = false)
    private String commitHash;
    
    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;
    
    private String analysisProvider;
    
    @Column(name = "total_files_analyzed")
    private Integer totalFilesAnalyzed;
    
    @Column(name = "total_issues_found")
    private Integer totalIssuesFound;
    
    private Integer criticalCount;
    private Integer highCount;
    private Integer mediumCount;
    private Integer lowCount;
    private Integer infoCount;
    
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
