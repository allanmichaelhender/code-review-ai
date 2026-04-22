package com.codereview.service;

import com.codereview.model.Analysis;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String ANALYSIS_CACHE_PREFIX = "analysis:";
    private static final long CACHE_TTL_HOURS = 24; // Cache for 24 hours
    
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void cacheAnalysis(String repoUrl, String commitHash, Analysis analysis) {
        String cacheKey = buildCacheKey(repoUrl, commitHash);
        try {
            redisTemplate.opsForValue().set(cacheKey, analysis, CACHE_TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            // Log error but don't fail the analysis
            System.err.println("Failed to cache analysis: " + e.getMessage());
        }
    }
    
    public Analysis getCachedAnalysis(String repoUrl, String commitHash) {
        String cacheKey = buildCacheKey(repoUrl, commitHash);
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof Analysis) {
                return (Analysis) cached;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Failed to retrieve cached analysis: " + e.getMessage());
            return null;
        }
    }
    
    public void invalidateCache(String repoUrl, String commitHash) {
        String cacheKey = buildCacheKey(repoUrl, commitHash);
        try {
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            System.err.println("Failed to invalidate cache: " + e.getMessage());
        }
    }
    
    public void invalidateAllCacheForRepo(String repoUrl) {
        try {
            // Delete all keys matching the pattern
            String pattern = ANALYSIS_CACHE_PREFIX + sanitizeKey(repoUrl) + ":*";
            redisTemplate.delete(redisTemplate.keys(pattern));
        } catch (Exception e) {
            System.err.println("Failed to invalidate all cache for repo: " + e.getMessage());
        }
    }
    
    private String buildCacheKey(String repoUrl, String commitHash) {
        return ANALYSIS_CACHE_PREFIX + sanitizeKey(repoUrl) + ":" + commitHash;
    }
    
    private String sanitizeKey(String key) {
        // Replace special characters that are not allowed in Redis keys
        return key.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }
}
