package com.codereview.service.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekProvider implements LLMProvider {
    
    private final WebClient webClient;
    private final String apiKey;
    
    public DeepSeekProvider(@Value("${deepseek.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.deepseek.com/v1/chat/completions")
                .build();
    }
    
    @Override
    public String analyzeCode(String code, String language, String analysisType) {
        String prompt = buildPrompt(code, language, analysisType);
        
        try {
            String response = webClient.post()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of(
                            "model", "deepseek-coder",
                            "messages", List.of(
                                    Map.of("role", "user", "content", prompt)
                            )
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
        // Streaming not implemented for DeepSeek in MVP
        return List.of(analyzeCode(code, language, analysisType));
    }
    
    private String buildPrompt(String code, String language, String analysisType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following ").append(language).append(" code for ").append(analysisType).append(" issues.\n\n");
        prompt.append("Code:\n```\n").append(code).append("\n```\n\n");
        
        if (analysisType.equals("security")) {
            prompt.append("Focus on:\n");
            prompt.append("- Hardcoded secrets/API keys\n");
            prompt.append("- SQL injection vulnerabilities\n");
            prompt.append("- XSS vulnerabilities\n");
            prompt.append("- Authentication/authorization issues\n");
        } else if (analysisType.equals("quality")) {
            prompt.append("Focus on:\n");
            prompt.append("- Code complexity\n");
            prompt.append("- Function/method length\n");
            prompt.append("- Code duplication\n");
            prompt.append("- Maintainability issues\n");
        }
        
        prompt.append("\nProvide a JSON response with the following format:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"severity\": \"critical|high|medium|low|info\",\n");
        prompt.append("    \"message\": \"description of the issue\",\n");
        prompt.append("    \"suggestion\": \"how to fix it\",\n");
        prompt.append("    \"lineNumber\": line_number\n");
        prompt.append("  }\n");
        prompt.append("]\n");
        
        return prompt.toString();
    }
    
    private String extractContent(String response) {
        // Parse DeepSeek API response to extract the generated content
        if (response == null || response.isEmpty()) {
            return "No response from DeepSeek API";
        }
        
        try {
            int contentStart = response.indexOf("\"content\": \"");
            if (contentStart == -1) {
                return response;
            }
            contentStart += 12; // Length of "\"content\": \""
            
            int contentEnd = response.indexOf("\"", contentStart);
            if (contentEnd == -1) {
                return response.substring(contentStart);
            }
            
            return response.substring(contentStart, contentEnd)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        } catch (Exception e) {
            return response;
        }
    }
}
