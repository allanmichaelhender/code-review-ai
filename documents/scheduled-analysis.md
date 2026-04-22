# Scheduled Analysis System

## Purpose

Automatically analyze repositories on a schedule to keep analysis results up-to-date without manual intervention.

## Schedule Configuration

**Cron Expression:** `0 0 0 ? * MON-FRI`
- Runs every day at midnight (UTC)
- Monday through Friday only
- Weekends excluded to avoid rate limits

## Distribution Strategy

Repositories are distributed across 5 weekdays based on repository ID:
```java
int dayOfWeek = (repositoryId % 5) + 1; // 1=Monday, 2=Tuesday, etc.
```

This ensures:
- Even distribution of API calls across days
- No single day has too many analyses
- Respects OpenRouter rate limits (50 requests/day free tier)

## Implementation

**Service:** `ScheduledAnalysisService.java`
**Annotation:** `@Scheduled(cron = "0 0 0 ? * MON-FRI")`

**Workflow:**
1. At midnight, check current day of week
2. Fetch repositories where `repositoryId % 5 == currentDay`
3. For each repository:
   - Clone repository
   - Analyze with OpenRouter
   - Save results
   - Update metadata
   - Handle errors with logging
4. Log summary of completed analyses

## Manual Trigger

**Endpoint:** `POST /api/admin/trigger-analysis`
- Analyzes ALL repositories (not just today's subset)
- Used for testing and manual updates
- Returns summary of analysis results

## Error Handling

- Per-repository error tracking
- Logs errors but continues with next repository
- Retry logic with exponential backoff for rate limits
- Comprehensive logging for debugging
