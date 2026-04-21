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
    private String type;
    
    @Column(nullable = false)
    private String severity;
    
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
    
    private String status;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "open";
        }
    }
}
