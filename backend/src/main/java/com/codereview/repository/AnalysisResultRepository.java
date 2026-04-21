package com.codereview.repository;

import com.codereview.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByAnalysisId(Long analysisId);
    List<AnalysisResult> findByAnalysisIdAndType(Long analysisId, String type);
    List<AnalysisResult> findByAnalysisIdAndSeverity(Long analysisId, String severity);
}
