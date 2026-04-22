package com.codereview.controller;

import com.codereview.model.Analysis;
import com.codereview.model.AnalysisResult;
import com.codereview.model.CodeRepository;
import com.codereview.service.AnalysisService;
import com.codereview.service.ScheduledAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalysisController {
    
    private final AnalysisService analysisService;
    private final ScheduledAnalysisService scheduledAnalysisService;
    
    public AnalysisController(AnalysisService analysisService, ScheduledAnalysisService scheduledAnalysisService) {
        this.analysisService = analysisService;
        this.scheduledAnalysisService = scheduledAnalysisService;
    }
    
    @GetMapping("/repositories")
    public ResponseEntity<List<CodeRepository>> getRepositories() {
        List<CodeRepository> repositories = analysisService.getAllRepositories();
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

    @GetMapping("/analysis/by-repo")
    public ResponseEntity<?> getAnalysisByRepoUrl(@RequestParam String repo) {
        System.out.println("=== GET /api/analysis/by-repo ===");
        System.out.println("Repo URL: " + repo);
        try {
            System.out.println("Querying database for existing analysis...");
            var analysis = analysisService.getLatestAnalysisByRepoUrl(repo);
            System.out.println("Analysis found: " + analysis.isPresent());
            if (analysis.isEmpty()) {
                System.out.println("No existing analysis found, returning 404");
                return ResponseEntity.notFound().build();
            }
            List<AnalysisResult> results = analysisService.getAnalysisResults(analysis.get().getId());
            return ResponseEntity.ok(Map.of(
                    "analysisId", analysis.get().getId(),
                    "results", results,
                    "totalIssues", analysis.get().getTotalIssuesFound(),
                    "healthScore", analysis.get().getRepository().getOverallHealthScore()
            ));
        } catch (Exception e) {
            System.out.println("Error in /api/analysis/by-repo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/admin/trigger-analysis")
    public ResponseEntity<?> triggerAnalysis() {
        try {
            scheduledAnalysisService.triggerManualAnalysis();
            return ResponseEntity.ok(Map.of("message", "Manual analysis triggered successfully (analyzing all repositories)"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
