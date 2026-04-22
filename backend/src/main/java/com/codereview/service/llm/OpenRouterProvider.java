package com.codereview.service.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterProvider implements LLMProvider {
    
    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    
    public OpenRouterProvider(@Value("${openrouter.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.builder()
                .baseUrl("https://openrouter.ai/api/v1/chat/completions")
                .build();
    }
    
    @Override
    public String analyzeCode(String code, String language, String analysisType) {
        String prompt = buildPrompt(code, language, analysisType);
        
        try {
            String response = webClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", "http://localhost:8080")
                    .header("X-Title", "Code Review AI")
                    .bodyValue(Map.of(
                            "model", "nvidia/nemotron-3-nano-30b-a3b:free",
                            "messages", List.of(
                                    Map.of("role", "user", "content", prompt)
                            ),
                            "temperature", 0.7
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return extractContent(response);
        } catch (Exception e) {
            return "Error analyzing code: " + e.getMessage();
        }
    }
    
    @Override
    public List<String> analyzeCodeStreaming(String code, String language, String analysisType) {
        // Streaming not implemented for OpenRouter in MVP
        return List.of(analyzeCode(code, language, analysisType));
    }
    
    private String buildPrompt(String code, String language, String analysisType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert code reviewer. Analyze the following ").append(language).append(" code for potential issues.\n\n");
        prompt.append("Code:\n```\n").append(code).append("\n```\n\n");
        
        prompt.append("Analyze the code for issues in these categories:\n");
        prompt.append("1. SECURITY: SQL injection, XSS, hardcoded secrets, auth issues, input validation\n");
        prompt.append("2. CODE_QUALITY: Complexity, duplication, maintainability, code smells\n");
        prompt.append("3. PERFORMANCE: Inefficient algorithms, resource leaks, performance bottlenecks\n");
        prompt.append("4. BEST_PRACTICES: Language-specific best practices, design patterns\n");
        prompt.append("5. MAINTAINABILITY: Documentation, naming conventions, code organization\n\n");
        
        prompt.append("For each issue found, provide:\n");
        prompt.append("- category: One of SECURITY, CODE_QUALITY, PERFORMANCE, BEST_PRACTICES, MAINTAINABILITY\n");
        prompt.append("- type: Specific issue type (e.g., SQL_INJECTION, HIGH_CYCLOMATIC_COMPLEXITY)\n");
        prompt.append("- severity: critical, high, medium, low, or info\n");
        prompt.append("- message: Clear description of the issue\n");
        prompt.append("- suggestion: How to fix the issue\n");
        prompt.append("- explanation: Why this is an issue and potential impact\n");
        prompt.append("- confidenceScore: 0.0 to 1.0 confidence in this analysis\n");
        prompt.append("- impactScore: 0.0 to 1.0 potential impact if not fixed\n");
        prompt.append("- effortScore: 0.0 to 1.0 effort required to fix\n");
        prompt.append("- cweId: CWE identifier if applicable (e.g., CWE-89 for SQL injection)\n");
        prompt.append("- owaspCategory: OWASP category if applicable (e.g., A03: Injection)\n");
        prompt.append("- lineNumber: Line number where issue occurs\n");
        prompt.append("- codeSnippet: Relevant code snippet\n\n");
        
        prompt.append("Return ONLY a valid JSON object with this exact structure:\n");
        prompt.append("{\n");
        prompt.append("  \"issues\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"SECURITY\",\n");
        prompt.append("      \"type\": \"SQL_INJECTION\",\n");
        prompt.append("      \"severity\": \"critical\",\n");
        prompt.append("      \"message\": \"description\",\n");
        prompt.append("      \"suggestion\": \"fix suggestion\",\n");
        prompt.append("      \"explanation\": \"detailed explanation\",\n");
        prompt.append("      \"confidenceScore\": 0.9,\n");
        prompt.append("      \"impactScore\": 0.95,\n");
        prompt.append("      \"effortScore\": 0.3,\n");
        prompt.append("      \"cweId\": \"CWE-89\",\n");
        prompt.append("      \"owaspCategory\": \"A03: Injection\",\n");
        prompt.append("      \"lineNumber\": 42,\n");
        prompt.append("      \"codeSnippet\": \"code snippet\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        
        prompt.append("If no issues are found, return {\"issues\": []}.\n");
        
        return prompt.toString();
    }
    
    private String extractContent(String response) {
        if (response == null || response.isEmpty()) {
            return "{\"issues\": []}";
        }
        
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        String contentStr = content.asText();
                        // Clean up any markdown code blocks
                        contentStr = contentStr.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
                        return contentStr;
                    }
                }
            }
            return "{\"issues\": []}";
        } catch (Exception e) {
            // Fallback to simple string extraction if JSON parsing fails
            try {
                int contentStart = response.indexOf("\"content\": \"");
                if (contentStart == -1) {
                    return "{\"issues\": []}";
                }
                contentStart += 12;
                
                int contentEnd = response.indexOf("\"", contentStart);
                if (contentEnd == -1) {
                    return response.substring(contentStart);
                }
                
                String content = response.substring(contentStart, contentEnd)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
                
                // Clean up markdown
                content = content.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
                return content;
            } catch (Exception ex) {
                return "{\"issues\": []}";
            }
        }
    }
}
