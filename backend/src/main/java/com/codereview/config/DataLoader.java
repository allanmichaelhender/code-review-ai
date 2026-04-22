package com.codereview.config;

import com.codereview.model.Analysis;
import com.codereview.model.AnalysisResult;
import com.codereview.model.CodeRepository;
import com.codereview.repository.AnalysisRepository;
import com.codereview.repository.AnalysisResultRepository;
import com.codereview.repository.CodeRepositoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final CodeRepositoryRepository repositoryRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public DataLoader(CodeRepositoryRepository repositoryRepository,
                      AnalysisRepository analysisRepository,
                      AnalysisResultRepository analysisResultRepository) {
        this.repositoryRepository = repositoryRepository;
        this.analysisRepository = analysisRepository;
        this.analysisResultRepository = analysisResultRepository;
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
                .build());

        // Create analysis results for repo1
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis1)
                .type("security")
                .severity("high")
                .filePath("src/config/api.ts")
                .lineNumber(15)
                .message("Hardcoded API key detected")
                .suggestion("Use environment variables or a secrets manager")
                .build());

        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis1)
                .type("security")
                .severity("medium")
                .filePath("src/utils/auth.ts")
                .lineNumber(23)
                .message("Weak password validation")
                .suggestion("Implement stronger password requirements")
                .build());

        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis1)
                .type("quality")
                .severity("medium")
                .filePath("src/components/Header.tsx")
                .lineNumber(45)
                .message("Function too long (45 lines)")
                .suggestion("Consider breaking into smaller functions")
                .build());

        // Create analysis results for repo2
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis2)
                .type("security")
                .severity("critical")
                .filePath("src/models/ml.py")
                .lineNumber(42)
                .message("SQL injection vulnerability")
                .suggestion("Use parameterized queries")
                .build());

        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis2)
                .type("security")
                .severity("high")
                .filePath("src/api/endpoints.ts")
                .lineNumber(28)
                .message("CORS misconfiguration")
                .suggestion("Restrict CORS to specific origins")
                .build());

        // Create analysis results for repo3
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis3)
                .type("security")
                .severity("high")
                .filePath("src/ai/client.ts")
                .lineNumber(20)
                .message("API key exposed in client code")
                .suggestion("Move API calls to backend")
                .build());

        // Create analysis results for repo4
        analysisResultRepository.save(AnalysisResult.builder()
                .analysis(analysis4)
                .type("security")
                .severity("high")
                .filePath("src/components/Contact.tsx")
                .lineNumber(18)
                .message("Email exposed in frontend")
                .suggestion("Use contact form with backend processing")
                .build());
    }
}
