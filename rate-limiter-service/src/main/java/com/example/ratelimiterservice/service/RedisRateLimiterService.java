package com.example.ratelimiterservice.service;

import com.example.ratelimiterservice.config.RateLimiterProperties;
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

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate, RedisScript<Long> redisScript, RateLimiterProperties rateLimiterProperties) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
        this.rateLimiterProperties = rateLimiterProperties;
    }

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
}