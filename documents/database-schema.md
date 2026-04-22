# Database Schema

## code_repository

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| owner | VARCHAR(255) | Repository owner |
| name | VARCHAR(255) | Repository name |
| project_name | VARCHAR(255) | Custom project name |
| tech_stack | VARCHAR(255) | Comma-separated tech stack |
| url | VARCHAR(500) | Repository URL (unique) |
| description | TEXT | Repository description |
| language | VARCHAR(50) | Primary language |
| stars | INTEGER | Star count |
| last_analyzed_commit | VARCHAR(100) | Last commit hash analysed |
| last_analyzed_at | TIMESTAMP | Last analysis timestamp |
| overall_health_score | DECIMAL(5,2) | Overall health score (0.0-1.0) |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp |

## analysis

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| repository_id | BIGINT | Foreign key to code_repository |
| commit_hash | VARCHAR(100) | Commit hash analysed |
| analyzed_at | TIMESTAMP | Analysis timestamp |
| analysis_provider | VARCHAR(50) | LLM provider used |
| total_files_analyzed | INTEGER | Number of files analysed |
| total_issues_found | INTEGER | Total issues found |
| critical_count | INTEGER | Critical issues count |
| high_count | INTEGER | High issues count |
| medium_count | INTEGER | Medium issues count |
| low_count | INTEGER | Low issues count |
| info_count | INTEGER | Info issues count |
| security_count | INTEGER | Security issues count |
| code_quality_count | INTEGER | Code quality issues count |
| performance_count | INTEGER | Performance issues count |
| best_practices_count | INTEGER | Best practices issues count |
| maintainability_count | INTEGER | Maintainability issues count |
| overall_health_score | DECIMAL(5,2) | Overall health score (0.0-1.0) |
| security_health_score | DECIMAL(5,2) | Security health score (0.0-1.0) |
| code_quality_health_score | DECIMAL(5,2) | Code quality health score (0.0-1.0) |
| performance_health_score | DECIMAL(5,2) | Performance health score (0.0-1.0) |
| analysis_duration_ms | BIGINT | Analysis duration in milliseconds |
| model_version | VARCHAR(100) | LLM model version used |

## analysis_result

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| analysis_id | BIGINT | Foreign key to analysis |
| category | VARCHAR(50) | Issue category (SECURITY, CODE_QUALITY, etc.) |
| type | VARCHAR(100) | Issue type (SQL_INJECTION, DUPLICATE_CODE, etc.) |
| severity | VARCHAR(20) | Severity (critical, high, medium, low, info) |
| file_path | VARCHAR(500) | File path where issue found |
| line_number | INTEGER | Line number where issue found |
| message | TEXT | Issue message |
| suggestion | TEXT | Suggested fix |
| explanation | TEXT | Detailed explanation |
| confidence_score | DECIMAL(5,2) | AI confidence score (0.0-1.0) |
| impact_score | DECIMAL(5,2) | Impact score (0.0-1.0) |
| effort_score | DECIMAL(5,2) | Effort to fix score (0.0-1.0) |
| cwe_id | VARCHAR(20) | CWE identifier (if applicable) |
| owasp_category | VARCHAR(100) | OWASP category (if applicable) |
| code_snippet | TEXT | Code snippet showing the issue |
