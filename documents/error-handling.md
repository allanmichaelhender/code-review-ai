# Error Handling

## Backend Error Handling

**AnalysisService:**
- Try-catch blocks around LLM API calls
- Retry logic with exponential backoff for rate limits
- Error logging with context
- Graceful degradation (return empty results on failure)

**GitService:**
- Handles Git clone failures
- Validates repository URL format
- Cleans up temporary directories on error
- Logs specific Git errors

**ScheduledAnalysisService:**
- Per-repository error tracking
- Continues with next repository on failure
- Logs comprehensive error summary
- No single repository failure stops entire job

## Frontend Error Handling

**Landing.tsx:**
- Try-catch around repository fetch
- Error state display
- Loading state during fetch

**Demo.tsx:**
- Try-catch around analysis fetch
- Error message display
- Loading state during analysis
- Timeout handling (5-minute proxy timeout)

## Common Error Scenarios

**Rate Limit (429):**
- Backend: Exponential backoff retry (up to 5 attempts)
- Frontend: Timeout after 5 minutes
- User: Error message displayed

**Invalid Repository URL:**
- Backend: 400 Bad Request
- Frontend: Validation before API call

**Repository Not Found:**
- Backend: 404 Not Found
- Frontend: Error message displayed

**LLM API Failure:**
- Backend: Logs error, returns empty results
- Frontend: Displays "No analysis found" message
