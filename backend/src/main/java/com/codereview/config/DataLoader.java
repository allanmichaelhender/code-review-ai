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
        // Only load data if repositories table is empty
        if (repositoryRepository.count() == 0) {
            loadSeedData();
        }
    }

    private void loadSeedData() {
        // Create repositories
        CodeRepository repo1 = repositoryRepository.save(CodeRepository.builder()
                .owner("allanmichaelhender")
                .name("guniea-pig-v2")
                .url("https://github.com/allanmichaelhender/guniea-pig-v2")
                .description("Guinea Pig Portfolio - Python + React + TS")
                .language("TypeScript")
                .stars(0)
                .overallHealthScore(0.85)
                .build());

        CodeRepository repo2 = repositoryRepository.save(CodeRepository.builder()
                .owner("allanmichaelhender")
                .name("Vantage-Point-ML")
                .url("https://github.com/allanmichaelhender/Vantage-Point-ML")
                .description("Vantage Point - Python + React + TS")
                .language("TypeScript")
                .stars(0)
                .overallHealthScore(0.78)
                .build());

        CodeRepository repo3 = repositoryRepository.save(CodeRepository.builder()
                .owner("allanmichaelhender")
                .name("hybrid_AI_coach")
                .url("https://github.com/allanmichaelhender/hybrid_AI_coach")
                .description("Hybrid Hour - Python + React + TS")
                .language("TypeScript")
                .stars(0)
                .overallHealthScore(0.82)
                .build());

        CodeRepository repo4 = repositoryRepository.save(CodeRepository.builder()
                .owner("allanmichaelhender")
                .name("allanmichaelhender.github.io")
                .url("https://github.com/allanmichaelhender/allanmichaelhender.github.io")
                .description("Portfolio Website - React + TS")
                .language("TypeScript")
                .stars(0)
                .overallHealthScore(0.90)
                .build());

        // Don't load seed data - force real analysis on first use
        System.out.println("Database initialized. Seed data disabled - real analysis will be performed on first request.");
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
