package com.codereview.service.llm;

import org.springframework.stereotype.Service;

@Service
public class LLMService {
    
    private final GeminiProvider geminiProvider;
    private final DeepSeekProvider deepSeekProvider;
    
    public LLMService(GeminiProvider geminiProvider, DeepSeekProvider deepSeekProvider) {
        this.geminiProvider = geminiProvider;
        this.deepSeekProvider = deepSeekProvider;
    }
    
    public String analyzeCode(String code, String language, String analysisType, String provider) {
        if ("deepseek".equalsIgnoreCase(provider)) {
            return deepSeekProvider.analyzeCode(code, language, analysisType);
        }
        return geminiProvider.analyzeCode(code, language, analysisType);
    }
    
    public String analyzeCodeWithGemini(String code, String language, String analysisType) {
        return geminiProvider.analyzeCode(code, language, analysisType);
    }
    
    public String analyzeCodeWithDeepSeek(String code, String language, String analysisType) {
        return deepSeekProvider.analyzeCode(code, language, analysisType);
    }
}
