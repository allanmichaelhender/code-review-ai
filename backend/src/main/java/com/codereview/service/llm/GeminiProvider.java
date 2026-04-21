package com.codereview.service.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiProvider implements LLMProvider {
    
    private final WebClient webClient;
    private final String apiKey;
    
    public GeminiProvider(@Value("${gemini.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent")
                .build();
    }
    
    @Override
    public String analyzeCode(String code, String language, String analysisType) {
        String prompt = buildPrompt(code, language, analysisType);
        
        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .bodyValue(Map.of(
                            "contents", List.of(
                                    Map.of("parts", List.of(
                                            Map.of("text", prompt)
                                    ))
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
        String prompt = buildPrompt(code, language, analysisType);
        List<String> results = new ArrayList<>();
        
        try {
            Flux<String> stream = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .bodyValue(Map.of(
                            "contents", List.of(
                                    Map.of("parts", List.of(
                                            Map.of("text", prompt)
                                    ))
                            )
                    ))
                    .retrieve()
                    .bodyToFlux(String.class);
            
            stream.subscribe(results::add);
            return results;
        } catch (Exception e) {
            results.add("Error analyzing code: " + e.getMessage());
            return results;
        }
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
        // Parse Gemini API response to extract the generated content
        // This is a simplified implementation
        if (response == null || response.isEmpty()) {
            return "No response from Gemini API";
        }
        
        try {
            // Extract the text from the Gemini response
            int textStart = response.indexOf("\"text\": \"");
            if (textStart == -1) {
                return response;
            }
            textStart += 9; // Length of "\"text\": \""
            
            int textEnd = response.indexOf("\"", textStart);
            if (textEnd == -1) {
                return response.substring(textStart);
            }
            
            return response.substring(textStart, textEnd)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        } catch (Exception e) {
            return response;
        }
    }
}
