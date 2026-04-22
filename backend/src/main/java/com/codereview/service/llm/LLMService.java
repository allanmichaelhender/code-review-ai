package com.codereview.service.llm;

import org.springframework.stereotype.Service;

@Service
public class LLMService {
    
    private final GeminiProvider geminiProvider;
    private final DeepSeekProvider deepSeekProvider;
    private final OpenRouterProvider openRouterProvider;
    
    public LLMService(GeminiProvider geminiProvider, DeepSeekProvider deepSeekProvider, OpenRouterProvider openRouterProvider) {
        this.geminiProvider = geminiProvider;
        this.deepSeekProvider = deepSeekProvider;
        this.openRouterProvider = openRouterProvider;
    }
    
    public String analyzeCode(String code, String language, String analysisType, String provider) {
        if ("deepseek".equalsIgnoreCase(provider)) {
            return deepSeekProvider.analyzeCode(code, language, analysisType);
        } else if ("openrouter".equalsIgnoreCase(provider)) {
            return openRouterProvider.analyzeCode(code, language, analysisType);
        }
        return geminiProvider.analyzeCode(code, language, analysisType);
    }
    
    public String analyzeCodeWithGemini(String code, String language, String analysisType) {
        return geminiProvider.analyzeCode(code, language, analysisType);
    }
    
    public String analyzeCodeWithDeepSeek(String code, String language, String analysisType) {
        return deepSeekProvider.analyzeCode(code, language, analysisType);
    }
    
    public String analyzeCodeWithOpenRouter(String code, String language, String analysisType) {
        return openRouterProvider.analyzeCode(code, language, analysisType);
    }
}
