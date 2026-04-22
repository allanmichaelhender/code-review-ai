package com.codereview.service;

import com.codereview.model.CodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledAnalysisService.class);
    
    private final AnalysisService analysisService;
    
    public ScheduledAnalysisService(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }
    
    /**
     * Scheduled job to analyze a subset of repositories daily
     * Runs every weekday at midnight (0 0 0 ? * MON-FRI)
     * Distributes repositories across weekdays to avoid API rate limits
     */
    @Scheduled(cron = "0 0 0 ? * MON-FRI")
    public void analyzeRepositoriesDaily() {
        logger.info("=== Starting daily repository analysis ===");
        
        try {
            List<CodeRepository> repositories = analysisService.getAllRepositories();
            logger.info("Found {} total repositories in database", repositories.size());
            
            // Get current day of week (1=Monday, 5=Friday)
            DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
            int dayValue = currentDay.getValue(); // 1=Monday, 2=Tuesday, ..., 5=Friday, 6=Saturday, 7=Sunday
            
            // Only run on weekdays (Monday=1 through Friday=5)
            if (dayValue > 5) {
                logger.info("Today is {}, skipping analysis (weekdays only)", currentDay);
                return;
            }
            
            // Filter repositories assigned to today (modulo 5 based on ID)
            List<CodeRepository> todaysRepositories = repositories.stream()
                    .filter(repo -> (repo.getId() % 5) == (dayValue - 1))
                    .collect(Collectors.toList());
            
            logger.info("Found {} repositories assigned to {} (day {} of 5)", 
                    todaysRepositories.size(), currentDay, dayValue);
            
            if (todaysRepositories.isEmpty()) {
                logger.info("No repositories to analyze today");
                return;
            }
            
            int successCount = 0;
            int failureCount = 0;
            
            for (CodeRepository repo : todaysRepositories) {
                try {
                    logger.info("Analyzing repository: {} (ID: {}, Day: {})", 
                            repo.getName(), repo.getId(), currentDay);
                    analysisService.analyzeRepository(repo.getUrl(), "openrouter");
                    successCount++;
                    logger.info("Successfully analyzed: {}", repo.getName());
                } catch (Exception e) {
                    failureCount++;
                    logger.error("Failed to analyze repository: {} - Error: {}", 
                            repo.getName(), e.getMessage(), e);
                }
            }
            
            logger.info("=== Daily analysis complete for {} ===", currentDay);
            logger.info("Success: {}, Failures: {}", successCount, failureCount);
            
        } catch (Exception e) {
            logger.error("Error during daily analysis: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Manual trigger for testing (analyzes all repositories regardless of day)
     */
    public void triggerManualAnalysis() {
        logger.info("=== Manual analysis triggered (analyzing all repositories) ===");
        
        try {
            List<CodeRepository> repositories = analysisService.getAllRepositories();
            logger.info("Found {} repositories to analyze", repositories.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (CodeRepository repo : repositories) {
                try {
                    logger.info("Analyzing repository: {} ({})", repo.getName(), repo.getUrl());
                    analysisService.analyzeRepository(repo.getUrl(), "openrouter");
                    successCount++;
                    logger.info("Successfully analyzed: {}", repo.getName());
                } catch (Exception e) {
                    failureCount++;
                    logger.error("Failed to analyze repository: {} - Error: {}", repo.getName(), e.getMessage(), e);
                }
            }
            
            logger.info("=== Manual analysis complete ===");
            logger.info("Success: {}, Failures: {}", successCount, failureCount);
            
        } catch (Exception e) {
            logger.error("Error during manual analysis: {}", e.getMessage(), e);
        }
    }
}
