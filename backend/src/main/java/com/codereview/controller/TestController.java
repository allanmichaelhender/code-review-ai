package com.codereview.controller;

import com.codereview.service.GitService;
import com.codereview.service.llm.OpenRouterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private GitService gitService;

    @Autowired
    private OpenRouterProvider openRouterProvider;

    @PostMapping("/analyze")
    public Map<String, Object> testAnalysis(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String repoUrl = request.get("repoUrl");

        try {
            // Step 1: Clone repository
            String tempDir = "/tmp/test-repo-" + System.currentTimeMillis();
            System.out.println("=== TEST: Cloning repository ===");
            System.out.println("Repo URL: " + repoUrl);
            System.out.println("Temp dir: " + tempDir);

            List<Path> codeFiles = gitService.cloneRepository(repoUrl, tempDir);
            System.out.println("=== TEST: File collection complete ===");
            System.out.println("Total files found: " + codeFiles.size());

            result.put("filesCollected", codeFiles.size());
            result.put("files", codeFiles.stream()
                    .limit(10)
                    .map(p -> p.toString())
                    .toList());

            if (codeFiles.isEmpty()) {
                result.put("error", "No files found in repository");
                return result;
            }

            // Step 2: Analyze first file with OpenRouter
            // Note: Files are already deleted by GitService, so we can't read them
            // For now, just return the file collection results
            result.put("success", "File collection working. OpenRouter analysis skipped because files are deleted after collection.");
            result.put("note", "Need to modify GitService to keep files for analysis or read content during collection.");

            return result;

        } catch (Exception e) {
            System.out.println("=== TEST: Error ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            result.put("error", e.getMessage());
            result.put("stackTrace", e.getStackTrace());
            return result;
        }
    }

    private String detectLanguage(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "java" -> "Java";
            case "js", "jsx" -> "JavaScript";
            case "ts", "tsx" -> "TypeScript";
            case "py" -> "Python";
            case "go" -> "Go";
            case "rs" -> "Rust";
            case "cpp", "cc", "cxx" -> "C++";
            case "c" -> "C";
            case "cs" -> "C#";
            case "rb" -> "Ruby";
            case "php" -> "PHP";
            case "swift" -> "Swift";
            case "kt" -> "Kotlin";
            case "scala" -> "Scala";
            case "html" -> "HTML";
            case "css" -> "CSS";
            case "json" -> "JSON";
            case "xml" -> "XML";
            case "yaml", "yml" -> "YAML";
            case "md" -> "Markdown";
            default -> "Unknown";
        };
    }
}
