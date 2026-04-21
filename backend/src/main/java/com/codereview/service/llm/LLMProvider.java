package com.codereview.service.llm;

import java.util.List;

public interface LLMProvider {
    String analyzeCode(String code, String language, String analysisType);
    List<String> analyzeCodeStreaming(String code, String language, String analysisType);
}
