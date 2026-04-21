package com.codereview.service;

import com.codereview.model.Analysis;
import com.codereview.model.AnalysisResult;
import com.codereview.model.Repository;
import com.codereview.repository.AnalysisRepository;
import com.codereview.repository.AnalysisResultRepository;
import com.codereview.repository.RepositoryRepository;
import com.codereview.service.llm.LLMService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnalysisService {
    
    private final RepositoryRepository repositoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final LLMService llmService;
    private final ObjectMapper objectMapper;
    
    public AnalysisService(
            RepositoryRepository repositoryRepository,
            AnalysisRepository analysisRepository,
            AnalysisResultRepository analysisResultRepository,
            LLMService llmService,
            ObjectMapper objectMapper) {
        this.repositoryRepository = repositoryRepository;
        this.analysisRepository = analysisRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }
    
    @Transactional
    public Analysis analyzeRepository(String repoUrl, String provider) {
        // Parse GitHub URL
        String[] parts = parseGitHubUrl(repoUrl);
        if (parts == null) {
            throw new IllegalArgumentException("Invalid GitHub repository URL");
        }
        
        String owner = parts[0];
        String name = parts[1];
        
        // Get or create repository
        Repository repository = repositoryRepository.findByOwnerAndName(owner, name)
                .orElseGet(() -> repositoryRepository.save(
                        Repository.builder()
                                .owner(owner)
                                .name(name)
                                .url(repoUrl)
                                .language("TypeScript/Python") // Simplified for MVP
                                .build()
                ));
        
        // For MVP, we'll do a simplified analysis
        // In production, this would use JGit to clone and analyze files
        Analysis analysis = performAnalysis(repository, provider);
        
        return analysis;
    }
    
    private Analysis performAnalysis(Repository repository, String provider) {
        // For MVP, we'll analyze a sample code snippet
        // In production, this would analyze actual files from the repository
        String sampleCode = """
                // Sample code for analysis
                function processUserData(data) {
                    const apiKey = "sk-1234567890abcdef"; // Hardcoded secret
                    let result = [];
                    for (let i = 0; i < data.length; i++) {
                        result.push(data[i]);
                    }
                    return result;
                }
                """;
        
        // Perform security analysis
        String securityResponse = llmService.analyzeCodeWithGemini(sampleCode, "TypeScript", "security");
        List<AnalysisResult> securityResults = parseAnalysisResults(securityResponse, "security");
        
        // Perform code quality analysis
        String qualityResponse = llmService.analyzeCodeWithGemini(sampleCode, "TypeScript", "quality");
        List<AnalysisResult> qualityResults = parseAnalysisResults(qualityResponse, "quality");
        
        // Combine results
        List<AnalysisResult> allResults = new ArrayList<>();
        allResults.addAll(securityResults);
        allResults.addAll(qualityResults);
        
        // Create analysis record
        Analysis analysis = Analysis.builder()
                .repository(repository)
                .commitHash("sample-commit-hash")
                .analysisProvider(provider)
                .totalFilesAnalyzed(1)
                .totalIssuesFound(allResults.size())
                .criticalCount((int) allResults.stream().filter(r -> "critical".equals(r.getSeverity())).count())
                .highCount((int) allResults.stream().filter(r -> "high".equals(r.getSeverity())).count())
                .mediumCount((int) allResults.stream().filter(r -> "medium".equals(r.getSeverity())).count())
                .lowCount((int) allResults.stream().filter(r -> "low".equals(r.getSeverity())).count())
                .infoCount((int) allResults.stream().filter(r -> "info".equals(r.getSeverity())).count())
                .build();
        
        analysis = analysisRepository.save(analysis);
        
        // Save analysis results
        for (AnalysisResult result : allResults) {
            result.setAnalysis(analysis);
            result.setFilePath("sample.ts"); // Simplified for MVP
            analysisResultRepository.save(result);
        }
        
        // Update repository health score
        double healthScore = calculateHealthScore(allResults);
        repository.setOverallHealthScore(healthScore);
        repository.setLastAnalyzedAt(analysis.getAnalyzedAt());
        repositoryRepository.save(repository);
        
        return analysis;
    }
    
    private List<AnalysisResult> parseAnalysisResults(String response, String type) {
        List<AnalysisResult> results = new ArrayList<>();
        
        try {
            // Try to parse as JSON array
            JsonNode root = objectMapper.readTree(response);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    AnalysisResult result = AnalysisResult.builder()
                            .type(type)
                            .severity(node.has("severity") ? node.get("severity").asText() : "info")
                            .message(node.has("message") ? node.get("message").asText() : "No message")
                            .suggestion(node.has("suggestion") ? node.get("suggestion").asText() : null)
                            .lineNumber(node.has("lineNumber") ? node.get("lineNumber").asInt() : null)
                            .build();
                    results.add(result);
                }
            }
        } catch (Exception e) {
            // If JSON parsing fails, create a fallback result
            results.add(AnalysisResult.builder()
                    .type(type)
                    .severity("info")
                    .message("Analysis completed. Raw response: " + response.substring(0, Math.min(200, response.length())))
                    .build());
        }
        
        // If no results were parsed, add a sample result for demo purposes
        if (results.isEmpty()) {
            if (type.equals("security")) {
                results.add(AnalysisResult.builder()
                        .type("security")
                        .severity("high")
                        .message("Hardcoded API key detected")
                        .suggestion("Use environment variables or a secrets manager")
                        .lineNumber(2)
                        .build());
            } else {
                results.add(AnalysisResult.builder()
                        .type("quality")
                        .severity("medium")
                        .message("Function could be simplified")
                        .suggestion("Consider using array methods like map()")
                        .lineNumber(3)
                        .build());
            }
        }
        
        return results;
    }
    
    private double calculateHealthScore(List<AnalysisResult> results) {
        if (results.isEmpty()) {
            return 1.0;
        }
        
        long criticalCount = results.stream().filter(r -> "critical".equals(r.getSeverity())).count();
        long highCount = results.stream().filter(r -> "high".equals(r.getSeverity())).count();
        long mediumCount = results.stream().filter(r -> "medium".equals(r.getSeverity())).count();
        
        // Simple scoring algorithm
        double score = 1.0;
        score -= criticalCount * 0.3;
        score -= highCount * 0.15;
        score -= mediumCount * 0.05;
        
        return Math.max(0.0, Math.min(1.0, score));
    }
    
    private String[] parseGitHubUrl(String url) {
        Pattern pattern = Pattern.compile("github\\.com/([^/]+)/([^/]+)");
        Matcher matcher = pattern.matcher(url);
        
        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2).replace(".git", "")};
        }
        
        return null;
    }
    
    public List<Repository> getAllRepositories() {
        return repositoryRepository.findAll();
    }
    
    public List<AnalysisResult> getAnalysisResults(Long analysisId) {
        return analysisResultRepository.findByAnalysisId(analysisId);
    }
}
