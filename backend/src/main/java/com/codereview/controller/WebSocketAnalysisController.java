package com.codereview.controller;

import com.codereview.model.AnalysisResult;
import com.codereview.service.AnalysisService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketAnalysisController {

    private final AnalysisService analysisService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketAnalysisController(AnalysisService analysisService, SimpMessagingTemplate messagingTemplate) {
        this.analysisService = analysisService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/analyze")
    @SendTo("/topic/analysis")
    public Map<String, Object> handleAnalysisRequest(Map<String, String> request) {
        String repoUrl = request.get("repo");
        String provider = request.getOrDefault("provider", "gemini");
        String sessionId = request.get("sessionId");

        try {
            var analysis = analysisService.analyzeRepositoryStreaming(repoUrl, provider, sessionId, messagingTemplate);
            var results = analysisService.getAnalysisResults(analysis.getId());

            return Map.of(
                "type", "complete",
                "analysisId", analysis.getId(),
                "results", results,
                "totalIssues", analysis.getTotalIssuesFound(),
                "healthScore", analysis.getRepository().getOverallHealthScore()
            );
        } catch (Exception e) {
            return Map.of(
                "type", "error",
                "message", e.getMessage()
            );
        }
    }
}
