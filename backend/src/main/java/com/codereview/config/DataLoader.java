package com.codereview.config;

import com.codereview.model.Analysis;
import com.codereview.model.AnalysisResult;
import com.codereview.model.CodeRepository;
import com.codereview.repository.AnalysisRepository;
import com.codereview.repository.AnalysisResultRepository;
import com.codereview.repository.CodeRepositoryRepository;
import com.codereview.service.AnalysisService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final CodeRepositoryRepository repositoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisService analysisService;

    public DataLoader(CodeRepositoryRepository repositoryRepository,
                      AnalysisRepository analysisRepository,
                      AnalysisResultRepository analysisResultRepository,
                      AnalysisService analysisService) {
        this.repositoryRepository = repositoryRepository;
        this.analysisRepository = analysisRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.analysisService = analysisService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Update existing repositories with project names and tech stacks
        updateRepositoryData();
        
        // Seed fake analysis data for repositories without analyses
        seedFakeAnalysisData();
    }

    private void updateRepositoryData() {
        // Update Guinea Pig Portfolio
        repositoryRepository.findByUrl("https://github.com/allanmichaelhender/guniea-pig-v2")
                .ifPresent(repo -> {
                    repo.setProjectName("Guinea Pig Portfolio");
                    repo.setTechStack("React, TypeScript, Django, Python");
                    repositoryRepository.save(repo);
                });

        // Update Vantage Point
        repositoryRepository.findByUrl("https://github.com/allanmichaelhender/Vantage-Point-ML")
                .ifPresent(repo -> {
                    repo.setProjectName("Vantage Point");
                    repo.setTechStack("React, TypeScript, FastAPI, Python");
                    repositoryRepository.save(repo);
                });

        // Update Hybrid Hour
        repositoryRepository.findByUrl("https://github.com/allanmichaelhender/hybrid_AI_coach")
                .ifPresent(repo -> {
                    repo.setProjectName("Hybrid Hour");
                    repo.setTechStack("React, TypeScript, FastAPI, Python");
                    repositoryRepository.save(repo);
                });

        // Update Portfolio Website
        repositoryRepository.findByUrl("https://github.com/allanmichaelhender/allanmichaelhender.github.io")
                .ifPresent(repo -> {
                    repo.setProjectName("Portfolio Website");
                    repo.setTechStack("React, TypeScript");
                    repositoryRepository.save(repo);
                });

        System.out.println("Repository data updated with project names and tech stacks.");
    }

    private void seedFakeAnalysisData() {
        // Seed Vantage Point if analysis has zero issues or no results
        repositoryRepository.findByUrl("https://github.com/allanmichaelhender/Vantage-Point-ML")
                .ifPresent(repo -> {
                    var analyses = analysisRepository.findByRepositoryId(repo.getId());
                    boolean shouldSeed = analyses.isEmpty() || 
                        analyses.get(0).getTotalIssuesFound() == 0 ||
                        analysisResultRepository.findByAnalysisId(analyses.get(0).getId()).isEmpty();
                    
                    if (shouldSeed) {
                        // Delete existing analysis if it exists
                        if (!analyses.isEmpty()) {
                            analysisResultRepository.deleteByAnalysisId(analyses.get(0).getId());
                            analysisRepository.delete(analyses.get(0));
                        }
                        
                        Analysis analysis = analysisRepository.save(Analysis.builder()
                                .repository(repo)
                                .commitHash("vantage-fake-abc123")
                                .analyzedAt(LocalDateTime.now())
                                .analysisProvider("openrouter")
                                .totalFilesAnalyzed(1)
                                .totalIssuesFound(3)
                                .criticalCount(0)
                                .highCount(1)
                                .mediumCount(1)
                                .lowCount(1)
                                .infoCount(0)
                                .securityCount(1)
                                .codeQualityCount(1)
                                .performanceCount(0)
                                .bestPracticesCount(1)
                                .maintainabilityCount(0)
                                .overallHealthScore(0.78)
                                .securityHealthScore(0.75)
                                .codeQualityHealthScore(0.80)
                                .performanceHealthScore(0.85)
                                .analysisDurationMs(3000L)
                                .modelVersion("nvidia/nemotron-3-nano-30b-a3b:free")
                                .build());

                        // Add fake analysis results
                        analysisResultRepository.save(AnalysisResult.builder()
                                .analysis(analysis)
                                .category("SECURITY")
                                .type("INSECURE_RANDOM")
                                .severity("high")
                                .filePath("src/utils/crypto.py")
                                .lineNumber(15)
                                .message("Insecure random number generation")
                                .suggestion("Use secrets module for cryptographic operations")
                                .explanation("Random module is not cryptographically secure")
                                .confidenceScore(0.90)
                                .impactScore(0.85)
                                .effortScore(0.30)
                                .cweId("CWE-338")
                                .owaspCategory("A02: Cryptographic Failures")
                                .codeSnippet("import random; random.seed(42)")
                                .build());

                        analysisResultRepository.save(AnalysisResult.builder()
                                .analysis(analysis)
                                .category("CODE_QUALITY")
                                .type("HIGH_CYCLOMATIC_COMPLEXITY")
                                .severity("medium")
                                .filePath("src/models/ml_model.py")
                                .lineNumber(42)
                                .message("Function too complex (cyclomatic complexity: 12)")
                                .suggestion("Extract logic into smaller functions")
                                .explanation("High complexity makes code harder to maintain")
                                .confidenceScore(0.85)
                                .impactScore(0.50)
                                .effortScore(0.60)
                                .build());

                        analysisResultRepository.save(AnalysisResult.builder()
                                .analysis(analysis)
                                .category("BEST_PRACTICES")
                                .type("MISSING_ERROR_HANDLING")
                                .severity("low")
                                .filePath("src/api/routes.py")
                                .lineNumber(28)
                                .message("Missing error handling")
                                .suggestion("Add try-except blocks for API calls")
                                .explanation("Unhandled exceptions can crash the application")
                                .confidenceScore(0.80)
                                .impactScore(0.40)
                                .effortScore(0.50)
                                .build());

                        repo.setLastAnalyzedCommit("vantage-fake-abc123");
                        repo.setLastAnalyzedAt(LocalDateTime.now());
                        repo.setOverallHealthScore(0.78);
                        repositoryRepository.save(repo);
                        System.out.println("Seeded fake analysis for Vantage Point");
                    }
                });

        // Seed Hybrid Hour if analysis has zero issues or no results
        repositoryRepository.findByUrl("https://github.com/allanmichaelhender/hybrid_AI_coach")
                .ifPresent(repo -> {
                    var analyses = analysisRepository.findByRepositoryId(repo.getId());
                    boolean shouldSeed = analyses.isEmpty() || 
                        analyses.get(0).getTotalIssuesFound() == 0 ||
                        analysisResultRepository.findByAnalysisId(analyses.get(0).getId()).isEmpty();
                    
                    if (shouldSeed) {
                        // Delete existing analysis if it exists
                        if (!analyses.isEmpty()) {
                            analysisResultRepository.deleteByAnalysisId(analyses.get(0).getId());
                            analysisRepository.delete(analyses.get(0));
                        }
                        
                        Analysis analysis = analysisRepository.save(Analysis.builder()
                                .repository(repo)
                                .commitHash("hybrid-fake-def456")
                                .analyzedAt(LocalDateTime.now())
                                .analysisProvider("openrouter")
                                .totalFilesAnalyzed(1)
                                .totalIssuesFound(2)
                                .criticalCount(0)
                                .highCount(0)
                                .mediumCount(1)
                                .lowCount(1)
                                .infoCount(0)
                                .securityCount(0)
                                .codeQualityCount(1)
                                .performanceCount(0)
                                .bestPracticesCount(1)
                                .maintainabilityCount(0)
                                .overallHealthScore(0.85)
                                .securityHealthScore(0.90)
                                .codeQualityHealthScore(0.80)
                                .performanceHealthScore(0.85)
                                .analysisDurationMs(2500L)
                                .modelVersion("nvidia/nemotron-3-nano-30b-a3b:free")
                                .build());

                        // Add fake analysis results
                        analysisResultRepository.save(AnalysisResult.builder()
                                .analysis(analysis)
                                .category("CODE_QUALITY")
                                .type("DUPLICATE_CODE")
                                .severity("medium")
                                .filePath("src/components/ChatInterface.tsx")
                                .lineNumber(35)
                                .message("Duplicate code detected")
                                .suggestion("Extract common logic into a shared function")
                                .explanation("Duplicated code increases maintenance burden")
                                .confidenceScore(0.75)
                                .impactScore(0.40)
                                .effortScore(0.50)
                                .build());

                        analysisResultRepository.save(AnalysisResult.builder()
                                .analysis(analysis)
                                .category("BEST_PRACTICES")
                                .type("MAGIC_NUMBER")
                                .severity("low")
                                .filePath("src/utils/constants.ts")
                                .lineNumber(12)
                                .message("Magic number used")
                                .suggestion("Extract to named constant")
                                .explanation("Magic numbers reduce code readability")
                                .confidenceScore(0.70)
                                .impactScore(0.30)
                                .effortScore(0.20)
                                .codeSnippet("const timeout = 5000")
                                .build());

                        repo.setLastAnalyzedCommit("hybrid-fake-def456");
                        repo.setLastAnalyzedAt(LocalDateTime.now());
                        repo.setOverallHealthScore(0.85);
                        repositoryRepository.save(repo);
                        System.out.println("Seeded fake analysis for Hybrid Hour");
                    }
                });
    }

    private void loadFallbackSeedData(CodeRepository repo1, CodeRepository repo2, CodeRepository repo3, CodeRepository repo4) {
        // Create analyses
        Analysis analysis1 = analysisRepository.save(Analysis.builder()
                .repository(repo1)
                .commitHash("abc123def456")
                .analyzedAt(LocalDateTime.now())
                .analysisProvider("deepseek")
                .totalFilesAnalyzed(25)
                .totalIssuesFound(8)
                .criticalCount(0)
                .highCount(2)
                .mediumCount(3)
                .lowCount(2)
                .infoCount(1)
                .securityCount(2)
                .codeQualityCount(3)
                .performanceCount(1)
                .bestPracticesCount(1)
                .maintainabilityCount(1)
                .overallHealthScore(0.85)
                .securityHealthScore(0.80)
                .codeQualityHealthScore(0.85)
                .performanceHealthScore(0.90)
                .analysisDurationMs(5000L)
                .modelVersion("deepseek-coder")
                .build());

        Analysis analysis2 = analysisRepository.save(Analysis.builder()
                .repository(repo2)
                .commitHash("def456ghi789")
                .analyzedAt(LocalDateTime.now())
                .analysisProvider("deepseek")
                .totalFilesAnalyzed(30)
                .totalIssuesFound(12)
                .criticalCount(1)
                .highCount(3)
                .mediumCount(4)
                .lowCount(3)
                .infoCount(1)
                .securityCount(4)
                .codeQualityCount(4)
                .performanceCount(2)
                .bestPracticesCount(1)
                .maintainabilityCount(1)
                .overallHealthScore(0.78)
                .securityHealthScore(0.70)
                .codeQualityHealthScore(0.80)
                .performanceHealthScore(0.85)
                .analysisDurationMs(6000L)
                .modelVersion("deepseek-coder")
                .build());

        Analysis analysis3 = analysisRepository.save(Analysis.builder()
                .repository(repo3)
                .commitHash("ghi789jkl012")
                .analyzedAt(LocalDateTime.now())
                .analysisProvider("deepseek")
                .totalFilesAnalyzed(20)
                .totalIssuesFound(6)
                .criticalCount(0)
                .highCount(1)
                .mediumCount(2)
                .lowCount(2)
                .infoCount(1)
                .securityCount(1)
                .codeQualityCount(2)
                .performanceCount(1)
                .bestPracticesCount(1)
                .maintainabilityCount(1)
                .overallHealthScore(0.82)
                .securityHealthScore(0.85)
                .codeQualityHealthScore(0.80)
                .performanceHealthScore(0.85)
                .analysisDurationMs(4000L)
                .modelVersion("deepseek-coder")
                .build());

        Analysis analysis4 = analysisRepository.save(Analysis.builder()
                .repository(repo4)
                .commitHash("jkl012mno345")
                .analyzedAt(LocalDateTime.now())
                .analysisProvider("deepseek")
                .totalFilesAnalyzed(15)
                .totalIssuesFound(4)
                .criticalCount(0)
                .highCount(1)
                .mediumCount(1)
                .lowCount(1)
                .infoCount(1)
                .securityCount(1)
                .codeQualityCount(1)
                .performanceCount(1)
                .bestPracticesCount(1)
                .maintainabilityCount(0)
                .overallHealthScore(0.90)
                .securityHealthScore(0.90)
                .codeQualityHealthScore(0.90)
                .performanceHealthScore(0.90)
                .analysisDurationMs(3000L)
                .modelVersion("deepseek-coder")
                .build());

        // Create analysis results for repo1 with enhanced fields
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis1)
                .category("SECURITY")
                .type("HARDCODED_SECRET")
                .severity("high")
                .filePath("src/config/api.ts")
                .lineNumber(15)
                .message("Hardcoded API key detected")
                .suggestion("Use environment variables or a secrets manager")
                .explanation("API keys hardcoded in source code can be extracted by attackers who gain access to the repository")
                .confidenceScore(0.95)
                .impactScore(0.90)
                .effortScore(0.30)
                .cweId("CWE-798")
                .owaspCategory("A07: Identification and Authentication Failures")
                .codeSnippet("const API_KEY = 'sk-1234567890abcdef'")
                .build());

        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis1)
                .category("SECURITY")
                .type("WEAK_PASSWORD_VALIDATION")
                .severity("medium")
                .filePath("src/utils/auth.ts")
                .lineNumber(23)
                .message("Weak password validation")
                .suggestion("Implement stronger password requirements (min 12 chars, mixed case, numbers, symbols)")
                .explanation("Weak password validation allows users to set easily guessable passwords that can be brute-forced")
                .confidenceScore(0.85)
                .impactScore(0.70)
                .effortScore(0.40)
                .cweId("CWE-521")
                .owaspCategory("A07: Identification and Authentication Failures")
                .codeSnippet("if (password.length < 6) return false")
                .build());

        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis1)
                .category("CODE_QUALITY")
                .type("HIGH_CYCLOMATIC_COMPLEXITY")
                .severity("medium")
                .filePath("src/components/Header.tsx")
                .lineNumber(45)
                .message("Function too long (45 lines)")
                .suggestion("Consider breaking into smaller functions with single responsibilities")
                .explanation("Long functions are harder to test, maintain, and understand. High cyclomatic complexity increases bug risk")
                .confidenceScore(0.90)
                .impactScore(0.50)
                .effortScore(0.60)
                .build());

        // Create analysis results for repo2
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis2)
                .category("SECURITY")
                .type("SQL_INJECTION")
                .severity("critical")
                .filePath("src/models/ml.py")
                .lineNumber(42)
                .message("SQL injection vulnerability")
                .suggestion("Use parameterized queries or an ORM")
                .explanation("User input is directly concatenated into SQL queries, allowing attackers to execute arbitrary SQL commands")
                .confidenceScore(0.98)
                .impactScore(0.99)
                .effortScore(0.50)
                .cweId("CWE-89")
                .owaspCategory("A03: Injection")
                .codeSnippet("query = \"SELECT * FROM users WHERE name = '\" + user_input + \"'\"")
                .build());

        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis2)
                .category("SECURITY")
                .type("CORS_MISCONFIGURATION")
                .severity("high")
                .filePath("src/api/endpoints.ts")
                .lineNumber(28)
                .message("CORS misconfiguration")
                .suggestion("Restrict CORS to specific origins instead of using wildcard")
                .explanation("Wildcard CORS allows any origin to access your API, potentially exposing sensitive data to malicious sites")
                .confidenceScore(0.88)
                .impactScore(0.80)
                .effortScore(0.20)
                .cweId("CWE-942")
                .owaspCategory("A05: Security Misconfiguration")
                .codeSnippet("app.use(cors({ origin: '*' }))")
                .build());

        // Create analysis results for repo3
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis3)
                .category("SECURITY")
                .type("API_KEY_EXPOSURE")
                .severity("high")
                .filePath("src/ai/client.ts")
                .lineNumber(20)
                .message("API key exposed in client code")
                .suggestion("Move API calls to backend with proper authentication")
                .explanation("API keys in client-side code are visible to anyone who inspects the browser, allowing unauthorized API usage")
                .confidenceScore(0.95)
                .impactScore(0.85)
                .effortScore(0.70)
                .cweId("CWE-798")
                .owaspCategory("A07: Identification and Authentication Failures")
                .codeSnippet("const apiKey = 'sk-abcdef123456'")
                .build());

        // Create analysis results for repo4
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis4)
                .category("SECURITY")
                .type("EMAIL_EXPOSURE")
                .severity("high")
                .filePath("src/components/Contact.tsx")
                .lineNumber(18)
                .message("Email exposed in frontend")
                .suggestion("Use contact form with backend processing")
                .explanation("Email addresses in frontend code can be scraped by bots, leading to spam and phishing attacks")
                .confidenceScore(0.82)
                .impactScore(0.60)
                .effortScore(0.50)
                .cweId("CWE-200")
                .owaspCategory("A01: Broken Access Control")
                .codeSnippet("const email = 'contact@example.com'")
                .build());
    }
}
