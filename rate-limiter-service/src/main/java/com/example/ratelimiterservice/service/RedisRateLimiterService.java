package com.example.ratelimiterservice.service;

import com.example.ratelimiterservice.config.RateLimiterProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
public class RedisRateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> redisScript;
    private final RateLimiterProperties rateLimiterProperties;

    private static final Logger log = LoggerFactory.getLogger(RedisRateLimiterService.class);

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate, RedisScript<Long> redisScript, RateLimiterProperties rateLimiterProperties) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
        this.rateLimiterProperties = rateLimiterProperties;
    }

    @CircuitBreaker(name = "redis", fallbackMethod = "fallbackIsAllowed")
    @Retry(name = "redis")
    public boolean isAllowed(String key) {

        String userKey = "rate:limiter:" + key;
        long currentTime = Instant.now().getEpochSecond();
        long requestedTokens = 1L;
        double refillRatePerSecond = rateLimiterProperties.getRefillRatePerMinute() / 60.0;

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(userKey),
                String.valueOf(rateLimiterProperties.getBucketCapacity()),
                String.valueOf(refillRatePerSecond),
                String.valueOf(currentTime),
                String.valueOf(requestedTokens)
        );

        return result == 1L;
    }

    public boolean fallbackIsAllowed(String key, Throwable t) {
        log.error("Circuit breaker is open for key: {}. Fallback enabled due to: {}", key, t.getMessage());
        return true;
    }

}