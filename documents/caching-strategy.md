# Caching Strategy

## Redis Configuration

**Host:** localhost (or redis container in Docker)
**Port:** 6379
**TTL:** 24 hours (86400 seconds)

## Cache Key Format

```
analysis:{repoUrl}:{commitHash}
```

Example:
```
analysis:https://github.com/owner/repo:abc123def456
```

## Cache Workflow

**Check Cache:**
1. Receive analysis request
2. Generate cache key from repo URL and commit hash
3. Check Redis for cached results
4. If found, return cached results
5. If not found, proceed with analysis

**Update Cache:**
1. Complete analysis
2. Serialize results to JSON
3. Store in Redis with 24-hour TTL
4. Return results to client

**Cache Invalidation:**
- Automatic expiration after 24 hours
- New commit hash = new cache key (old cache not used)
- Manual invalidation not implemented (rely on TTL)

## Benefits

- Reduces OpenRouter API calls
- Improves response time for cached repositories
- Lowers costs (fewer API requests)
- Handles rate limits gracefully
