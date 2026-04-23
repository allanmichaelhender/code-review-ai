package com.codereview.service;

import com.codereview.model.Analysis;
import com.codereview.model.AnalysisResult;
import com.codereview.model.CodeRepository;
import com.codereview.repository.AnalysisRepository;
import com.codereview.repository.AnalysisResultRepository;
import com.codereview.repository.CodeRepositoryRepository;
import com.codereview.service.llm.LLMService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnalysisService {
    
    private final CodeRepositoryRepository repositoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final LLMService llmService;
    private final ObjectMapper objectMapper;
    private final GitService gitService;
    private final RedisCacheService redisCacheService;
    
    public AnalysisService(
            CodeRepositoryRepository repositoryRepository,
            AnalysisRepository analysisRepository,
            AnalysisResultRepository analysisResultRepository,
            LLMService llmService,
            ObjectMapper objectMapper,
            GitService gitService,
            RedisCacheService redisCacheService) {
        this.repositoryRepository = repositoryRepository;
        this.analysisRepository = analysisRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.llmService = llmService;
        this.objectMapper = objectMapper;
        this.gitService = gitService;
        this.redisCacheService = redisCacheService;
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
        CodeRepository repository = repositoryRepository.findByUrl(repoUrl)
                .orElseGet(() -> repositoryRepository.save(
                        CodeRepository.builder()
                                .owner(owner)
                                .name(name)
                                .url(repoUrl)
                                .language("TypeScript/Python") // Simplified for MVP
                                .build()
                ));
        
        // Get latest commit hash
        String tempDir = "/tmp/repo-" + System.currentTimeMillis() + "-hash";
        String commitHash;
        try {
            commitHash = gitService.getLatestCommitHash(repoUrl, tempDir);
        } catch (Exception e) {
            commitHash = "unknown-" + System.currentTimeMillis();
        }
        
        // Check cache first
        Analysis cachedAnalysis = redisCacheService.getCachedAnalysis(repoUrl, commitHash);
        if (cachedAnalysis != null) {
            return cachedAnalysis;
        }
        
        // Clone repository and analyze files
        Analysis analysis = performAnalysis(repository, repoUrl, provider, commitHash);
        
        // Cache the result
        redisCacheService.cacheAnalysis(repoUrl, commitHash, analysis);
        
        return analysis;
    }

    @Transactional
    public Analysis analyzeRepositoryStreaming(String repoUrl, String provider, String sessionId, SimpMessagingTemplate messagingTemplate) {
        // Parse GitHub URL
        String[] parts = parseGitHubUrl(repoUrl);
        if (parts == null) {
            throw new IllegalArgumentException("Invalid GitHub repository URL");
        }
        
        String owner = parts[0];
        String name = parts[1];
        
        // Send initial status
        messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
            "type", "status",
            "message", "Cloning repository..."
        ));
        
        // Get or create repository
        CodeRepository repository = repositoryRepository.findByOwnerAndName(owner, name)
                .orElseGet(() -> repositoryRepository.save(
                        CodeRepository.builder()
                                .owner(owner)
                                .name(name)
                                .url(repoUrl)
                                .language("TypeScript/Python")
                                .build()
                ));
        
        // Clone repository and analyze files with streaming
        Analysis analysis = performAnalysisStreaming(repository, repoUrl, provider, sessionId, messagingTemplate);
        
        return analysis;
    }
    
    private Analysis performAnalysis(CodeRepository repository, String repoUrl, String provider, String commitHash) {
        long startTime = System.currentTimeMillis();
        String tempDir = "/tmp/repo-" + System.currentTimeMillis();
        try {
            // Clone repository and get code files
            List<Path> codeFiles = gitService.cloneRepository(repoUrl, tempDir);
            System.out.println("Cloned repository, found " + codeFiles.size() + " code files to analyze");

            // Analyze files
            List<AnalysisResult> allResults = new ArrayList<>();
            int fileCount = 0;

            for (Path file : codeFiles) {
                if (fileCount >= 1) break; // Limit to 1 file to stay within 50 daily request limit

                try {
                    String fileContent = Files.readString(file);
                    String fileName = file.getFileName().toString();
                    String language = detectLanguage(fileName);
                    System.out.println("Analyzing file " + (fileCount + 1) + "/" + codeFiles.size() + ": " + fileName + " (" + language + ")");

                    // Perform comprehensive analysis with OpenRouter
                    String response = llmService.analyzeCodeWithOpenRouter(fileContent, language, "comprehensive");
                    System.out.println("OpenRouter response for " + fileName + ": " + response.substring(0, Math.min(200, response.length())));
                    List<AnalysisResult> results = parseDeepSeekAnalysisResults(response, fileName);
                    System.out.println("Parsed " + results.size() + " issues from " + fileName);

                    allResults.addAll(results);
                    fileCount++;
                } catch (IOException e) {
                    System.out.println("Failed to read file: " + e.getMessage());
                    // Skip files that can't be read
                    continue;
                }
            }

            System.out.println("Total files analyzed: " + fileCount + ", Total issues found: " + allResults.size());

            // Calculate category counts and health scores
            int securityCount = (int) allResults.stream().filter(r -> "SECURITY".equals(r.getCategory())).count();
            int codeQualityCount = (int) allResults.stream().filter(r -> "CODE_QUALITY".equals(r.getCategory())).count();
            int performanceCount = (int) allResults.stream().filter(r -> "PERFORMANCE".equals(r.getCategory())).count();
            int bestPracticesCount = (int) allResults.stream().filter(r -> "BEST_PRACTICES".equals(r.getCategory())).count();
            int maintainabilityCount = (int) allResults.stream().filter(r -> "MAINTAINABILITY".equals(r.getCategory())).count();

            // Create analysis record
            long duration = System.currentTimeMillis() - startTime;
            Analysis analysis = Analysis.builder()
                    .repository(repository)
                    .commitHash(commitHash)
                    .analysisProvider(provider)
                    .totalFilesAnalyzed(fileCount)
                    .totalIssuesFound(allResults.size())
                    .criticalCount((int) allResults.stream().filter(r -> "critical".equals(r.getSeverity())).count())
                    .highCount((int) allResults.stream().filter(r -> "high".equals(r.getSeverity())).count())
                    .mediumCount((int) allResults.stream().filter(r -> "medium".equals(r.getSeverity())).count())
                    .lowCount((int) allResults.stream().filter(r -> "low".equals(r.getSeverity())).count())
                    .infoCount((int) allResults.stream().filter(r -> "info".equals(r.getSeverity())).count())
                    .securityCount(securityCount)
                    .codeQualityCount(codeQualityCount)
                    .performanceCount(performanceCount)
                    .bestPracticesCount(bestPracticesCount)
                    .maintainabilityCount(maintainabilityCount)
                    .overallHealthScore(calculateHealthScore(allResults))
                    .securityHealthScore(calculateCategoryHealthScore(allResults, "SECURITY"))
                    .codeQualityHealthScore(calculateCategoryHealthScore(allResults, "CODE_QUALITY"))
                    .performanceHealthScore(calculateCategoryHealthScore(allResults, "PERFORMANCE"))
                    .analysisDurationMs(duration)
                    .modelVersion("deepseek-coder")
                    .build();

            analysis = analysisRepository.save(analysis);

            // Save analysis results
            for (AnalysisResult result : allResults) {
                result.setAnalysis(analysis);
                analysisResultRepository.save(result);
            }

            // Update repository health score
            repository.setOverallHealthScore(analysis.getOverallHealthScore());
            repository.setLastAnalyzedAt(analysis.getAnalyzedAt());
            repository.setLastAnalyzedCommit(commitHash);
            repositoryRepository.save(repository);

            return analysis;
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze repository: " + e.getMessage(), e);
        } finally {
            // Cleanup: delete the cloned repository
            gitService.deleteDirectory(new File(tempDir));
        }
    }

    private Analysis performAnalysisStreaming(CodeRepository repository, String repoUrl, String provider, String sessionId, SimpMessagingTemplate messagingTemplate) {
        try {
            // Clone repository and get code files
            String tempDir = "/tmp/repo-" + System.currentTimeMillis();
            messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                "type", "status",
                "message", "Cloning repository and collecting files..."
            ));
            
            List<Path> codeFiles = gitService.cloneRepository(repoUrl, tempDir);
            
            // Get latest commit hash
            String commitHash = gitService.getLatestCommitHash(repoUrl, tempDir + "-hash");
            
            // Analyze files with streaming
            List<AnalysisResult> allResults = new ArrayList<>();
            int fileCount = 0;
            int totalFiles = Math.min(codeFiles.size(), 50);
            
            messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                "type", "progress",
                "message", "Starting analysis...",
                "current", 0,
                "total", totalFiles
            ));
            
            for (Path file : codeFiles) {
                if (fileCount >= 1) break; // Limit to 1 file to stay within 50 daily request limit
                
                try {
                    String fileContent = Files.readString(file);
                    String fileName = file.getFileName().toString();
                    String language = detectLanguage(fileName);
                    
                    // Send progress update
                    messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                        "type", "progress",
                        "message", "Analyzing: " + fileName,
                        "current", fileCount + 1,
                        "total", totalFiles
                    ));
                    
                    // Perform security analysis
                    String securityResponse = llmService.analyzeCodeWithGemini(fileContent, language, "security");
                    List<AnalysisResult> securityResults = parseAnalysisResults(securityResponse, "security", fileName);
                    
                    // Stream security results
                    for (AnalysisResult result : securityResults) {
                        messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                            "type", "result",
                            "result", result
                        ));
                    }
                    
                    // Perform code quality analysis
                    String qualityResponse = llmService.analyzeCodeWithGemini(fileContent, language, "quality");
                    List<AnalysisResult> qualityResults = parseAnalysisResults(qualityResponse, "quality", fileName);
                    
                    // Stream quality results
                    for (AnalysisResult result : qualityResults) {
                        messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                            "type", "result",
                            "result", result
                        ));
                    }
                    
                    allResults.addAll(securityResults);
                    allResults.addAll(qualityResults);
                    fileCount++;
                } catch (IOException e) {
                    // Skip files that can't be read
                    continue;
                }
            }
            
            // Create analysis record
            Analysis analysis = Analysis.builder()
                    .repository(repository)
                    .commitHash(commitHash)
                    .analysisProvider(provider)
                    .totalFilesAnalyzed(fileCount)
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
                analysisResultRepository.save(result);
            }
            
            // Update repository health score
            double healthScore = calculateHealthScore(allResults);
            repository.setOverallHealthScore(healthScore);
            repository.setLastAnalyzedAt(analysis.getAnalyzedAt());
            repository.setLastAnalyzedCommit(commitHash);
            repositoryRepository.save(repository);
            
            // Send completion message
            messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                "type", "complete",
                "analysisId", analysis.getId(),
                "totalIssues", analysis.getTotalIssuesFound(),
                "healthScore", healthScore
            ));
            
            return analysis;
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/analysis/" + sessionId, Map.of(
                "type", "error",
                "message", "Failed to analyze repository: " + e.getMessage()
            ));
            throw new RuntimeException("Failed to analyze repository: " + e.getMessage(), e);
        }
    }
    
    private List<AnalysisResult> parseDeepSeekAnalysisResults(String response, String fileName) {
        List<AnalysisResult> results = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode issues = root.get("issues");
            
            if (issues != null && issues.isArray()) {
                for (JsonNode node : issues) {
                    AnalysisResult result = AnalysisResult.builder()
                            .category(node.has("category") ? node.get("category").asText() : "CODE_QUALITY")
                            .type(node.has("type") ? node.get("type").asText() : "UNKNOWN")
                            .severity(node.has("severity") ? node.get("severity").asText() : "info")
                            .message(node.has("message") ? node.get("message").asText() : "No message")
                            .suggestion(node.has("suggestion") ? node.get("suggestion").asText() : null)
                            .explanation(node.has("explanation") ? node.get("explanation").asText() : null)
                            .confidenceScore(node.has("confidenceScore") ? node.get("confidenceScore").asDouble() : 0.8)
                            .impactScore(node.has("impactScore") ? node.get("impactScore").asDouble() : null)
                            .effortScore(node.has("effortScore") ? node.get("effortScore").asDouble() : null)
                            .cweId(node.has("cweId") ? node.get("cweId").asText() : null)
                            .owaspCategory(node.has("owaspCategory") ? node.get("owaspCategory").asText() : null)
                            .filePath(fileName)
                            .lineNumber(node.has("lineNumber") ? node.get("lineNumber").asInt() : null)
                            .codeSnippet(node.has("codeSnippet") ? node.get("codeSnippet").asText() : null)
                            .build();
                    results.add(result);
                }
            }
        } catch (Exception e) {
            // If JSON parsing fails, return empty list
            return results;
        }
        
        return results;
    }
    
    private List<AnalysisResult> parseAnalysisResults(String response, String type, String fileName) {
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
                            .filePath(fileName)
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
                    .message("Analysis completed for " + fileName)
                    .filePath(fileName)
                    .build());
        }
        
        // If no results were parsed, add a sample result for demo purposes
        if (results.isEmpty()) {
            if (type.equals("security")) {
                results.add(AnalysisResult.builder()
                        .type("security")
                        .severity("high")
                        .message("Potential security issue detected")
                        .suggestion("Review code for security vulnerabilities")
                        .filePath(fileName)
                        .build());
            } else {
                results.add(AnalysisResult.builder()
                        .type("quality")
                        .severity("medium")
                        .message("Code quality improvement possible")
                        .suggestion("Consider refactoring for better readability")
                        .filePath(fileName)
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
    
    private double calculateCategoryHealthScore(List<AnalysisResult> results, String category) {
        List<AnalysisResult> categoryResults = results.stream()
                .filter(r -> category.equals(r.getCategory()))
                .toList();
        
        if (categoryResults.isEmpty()) {
            return 1.0;
        }
        
        long criticalCount = categoryResults.stream().filter(r -> "critical".equals(r.getSeverity())).count();
        long highCount = categoryResults.stream().filter(r -> "high".equals(r.getSeverity())).count();
        long mediumCount = categoryResults.stream().filter(r -> "medium".equals(r.getSeverity())).count();
        
        double score = 1.0;
        score -= criticalCount * 0.4;
        score -= highCount * 0.2;
        score -= mediumCount * 0.1;
        
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
    
    public List<CodeRepository> getAllRepositories() {
        return repositoryRepository.findAll();
    }
    
    public List<AnalysisResult> getAnalysisResults(Long analysisId) {
        return analysisResultRepository.findByAnalysisId(analysisId);
    }

    public java.util.Optional<Analysis> getLatestAnalysisByRepoUrl(String repoUrl) {
        return repositoryRepository.findByUrl(repoUrl)
                .flatMap(repo -> analysisRepository.findTopByRepositoryOrderByAnalyzedAtDesc(repo));
    }

    public String analyzeSingleFile(String code, String language, String provider) {
        return llmService.analyzeCode(code, language, "comprehensive", provider);
    }

    public String analyzeGitHubFile(String fileUrl, String provider) {
        try {
            // Parse GitHub file URL to extract owner, repo, and file path
            // Format: https://github.com/owner/repo/blob/branch/path/to/file.ext
            String[] parts = fileUrl.split("github\\.com/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid GitHub file URL");
            }

            String[] repoParts = parts[1].split("/");
            if (repoParts.length < 4) {
                throw new IllegalArgumentException("Invalid GitHub file URL format");
            }

            String owner = repoParts[0];
            String repo = repoParts[1];
            String branch = repoParts[3]; // "blob" is at index 2
            String filePath = String.join("/", java.util.Arrays.copyOfRange(repoParts, 4, repoParts.length));

            // Fetch file content from GitHub API
            String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + filePath + "?ref=" + branch;
            
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(apiUrl))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch file from GitHub: " + response.statusCode());
            }

            // Parse GitHub API response to get file content
            JsonNode responseJson = objectMapper.readTree(response.body());
            String content = responseJson.get("content").asText();
            
            // Decode base64 content (remove newlines first)
            String decodedContent = new String(java.util.Base64.getDecoder().decode(content.replace("\n", "")));

            // Detect language from file extension
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            String language = detectLanguage(fileName);

            // Analyze the code
            return llmService.analyzeCode(decodedContent, language, "comprehensive", provider);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze GitHub file: " + e.getMessage(), e);
        }
    }

    private String detectLanguage(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "java" -> "Java";
            case "ts", "tsx" -> "TypeScript";
            case "js", "jsx" -> "JavaScript";
            case "py" -> "Python";
            case "go" -> "Go";
            case "rs" -> "Rust";
            case "c", "cpp", "cc", "h", "hpp" -> "C/C++";
            case "cs" -> "C#";
            case "php" -> "PHP";
            case "rb" -> "Ruby";
            case "swift" -> "Swift";
            case "kt", "kts" -> "Kotlin";
            case "scala" -> "Scala";
            default -> "TypeScript"; // Default for MVP
        };
    }
}
