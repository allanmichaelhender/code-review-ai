package com.codereview.controller;

import com.codereview.model.Analysis;
import com.codereview.model.AnalysisResult;
import com.codereview.model.Repository;
import com.codereview.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalysisController {
    
    private final AnalysisService analysisService;
    
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }
    
    @GetMapping("/repositories")
    public ResponseEntity<List<Repository>> getRepositories() {
        List<Repository> repositories = analysisService.getAllRepositories();
        return ResponseEntity.ok(repositories);
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeRepository(@RequestParam String repo, @RequestParam(defaultValue = "gemini") String provider) {
        try {
            Analysis analysis = analysisService.analyzeRepository(repo, provider);
            List<AnalysisResult> results = analysisService.getAnalysisResults(analysis.getId());
            
            return ResponseEntity.ok(Map.of(
                    "analysisId", analysis.getId(),
                    "results", results,
                    "totalIssues", analysis.getTotalIssuesFound(),
                    "healthScore", analysis.getRepository().getOverallHealthScore()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/analysis/{id}/results")
    public ResponseEntity<List<AnalysisResult>> getAnalysisResults(@PathVariable Long id) {
        List<AnalysisResult> results = analysisService.getAnalysisResults(id);
        return ResponseEntity.ok(results);
    }
}
