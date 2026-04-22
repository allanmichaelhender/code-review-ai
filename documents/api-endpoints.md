# API Endpoints

## Repositories

### GET /api/repositories
- **Description**: List all repositories in the database
- **Method**: GET
- **Response**: Array of repository objects
- **Response Body**:
```json
[
  {
    "id": 5,
    "owner": "allanmichaelhender",
    "name": "guniea-pig-v2",
    "projectName": "Guinea Pig Portfolio",
    "techStack": "React, TypeScript, Django, Python",
    "url": "https://github.com/allanmichaelhender/guniea-pig-v2",
    "description": "Guinea Pig Portfolio - Python + React + TS",
    "language": "TypeScript",
    "stars": 0,
    "lastAnalyzedCommit": "abf7ebd442e49dc87ca94ee572d6731b93412d16",
    "lastAnalyzedAt": "2026-04-22T17:05:02.565092",
    "overallHealthScore": 0.65,
    "createdAt": "2026-04-22T09:44:42.59854",
    "updatedAt": "2026-04-22T19:58:19.170952"
  }
]
```

### POST /api/repositories
- **Description**: Create a new repository entry
- **Method**: POST
- **Request Body**:
```json
{
  "owner": "owner",
  "name": "repo-name",
  "url": "https://github.com/owner/repo-name",
  "description": "Repository description",
  "language": "TypeScript",
  "stars": 100
}
```
- **Response**: Created repository object

## Analysis

### POST /api/analyze
- **Description**: Trigger analysis for a repository
- **Method**: POST
- **Query Parameters**:
  - `repo`: Repository URL
  - `provider`: LLM provider (default: openrouter)
- **Example**: `POST /api/analyze?repo=https://github.com/owner/repo&provider=openrouter`
- **Response**: Analysis object with results
- **Response Body**:
```json
{
  "id": 30,
  "repositoryId": 6,
  "commitHash": "vantage-fake-abc123",
  "analyzedAt": "2026-04-22T20:25:09",
  "analysisProvider": "openrouter",
  "totalFilesAnalyzed": 1,
  "totalIssuesFound": 3,
  "criticalCount": 0,
  "highCount": 1,
  "mediumCount": 1,
  "lowCount": 1,
  "infoCount": 0,
  "securityCount": 1,
  "codeQualityCount": 1,
  "performanceCount": 0,
  "bestPracticesCount": 1,
  "maintainabilityCount": 0,
  "overallHealthScore": 0.78,
  "securityHealthScore": 0.75,
  "codeQualityHealthScore": 0.80,
  "performanceHealthScore": 0.85,
  "analysisDurationMs": 3000,
  "modelVersion": "nvidia/nemotron-3-nano-30b-a3b:free",
  "results": [...]
}
```

### GET /api/analysis/by-repo
- **Description**: Get latest analysis for a repository by URL
- **Method**: GET
- **Query Parameters**:
  - `repo`: Repository URL
- **Example**: `GET /api/analysis/by-repo?repo=https://github.com/owner/repo`
- **Response**: Analysis object with results (or 404 if not found)

### GET /api/analysis/{id}
- **Description**: Get analysis by ID
- **Method**: GET
- **Path Parameters**:
  - `id`: Analysis ID
- **Response**: Analysis object with results

### GET /api/analysis/{id}/results
- **Description**: Get analysis results by analysis ID
- **Method**: GET
- **Path Parameters**:
  - `id`: Analysis ID
- **Response**: Array of AnalysisResult objects
- **Response Body**:
```json
[
  {
    "id": 45,
    "analysisId": 30,
    "category": "SECURITY",
    "type": "INSECURE_RANDOM",
    "severity": "high",
    "filePath": "src/utils/crypto.py",
    "lineNumber": 15,
    "message": "Insecure random number generation",
    "suggestion": "Use secrets module for cryptographic operations",
    "explanation": "Random module is not cryptographically secure",
    "confidenceScore": 0.90,
    "impactScore": 0.85,
    "effortScore": 0.30,
    "cweId": "CWE-338",
    "owaspCategory": "A02: Cryptographic Failures",
    "codeSnippet": "import random; random.seed(42)"
  }
]
```

## Admin

### POST /api/admin/trigger-analysis
- **Description**: Manually trigger analysis for all repositories (for testing)
- **Method**: POST
- **Response**: Summary of analysis results
- **Note**: This endpoint analyzes all repositories in the database
