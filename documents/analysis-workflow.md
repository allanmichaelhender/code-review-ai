# Analysis Workflow

## Analysis Process Flow

```
1. User submits repository URL
   ↓
2. GitService clones repository (shallow, depth=1)
   ↓
3. File filtering excludes non-code files
   ↓
4. Files limited to 1 (MVP constraint)
   ↓
5. File content read into memory
   ↓
6. LLMService sends file to OpenRouter API
   ↓
7. NVIDIA Nemotron-3 Nano 30B analyzes code
   ↓
8. Analysis results parsed and structured
   ↓
9. Results saved to PostgreSQL
   ↓
10. Results cached in Redis (24-hour TTL)
   ↓
11. Repository metadata updated (lastAnalyzedCommit, healthScore)
   ↓
12. Temporary cloned repository deleted
```

## Analysis Request Structure

**Prompt to LLM:**
```
Analyze the following code for security vulnerabilities, code quality issues, 
performance problems, and best practices violations. Provide results in JSON format.

Categories:
- SECURITY: SQL injection, XSS, hardcoded secrets, authentication issues
- CODE_QUALITY: Complexity, duplication, maintainability
- PERFORMANCE: Inefficient algorithms, resource leaks
- BEST_PRACTICES: Language-specific conventions
- MAINTAINABILITY: Documentation, naming conventions

For each issue, provide:
- category
- type
- severity (critical, high, medium, low, info)
- filePath
- lineNumber
- message
- suggestion
- explanation
- confidenceScore (0.0-1.0)
- impactScore (0.0-1.0)
- effortScore (0.0-1.0)
- cweId (if applicable)
- owaspCategory (if applicable)
- codeSnippet
```

## Health Score Calculation

Health scores are calculated by the LLM based on:
- Severity of issues found
- Number of issues per category
- Confidence scores of findings
- Impact and effort scores

Formula (simplified):
```
category_health = 1.0 - (weighted_issue_sum / max_possible_weight)
overall_health = average(category_health_scores)
```
