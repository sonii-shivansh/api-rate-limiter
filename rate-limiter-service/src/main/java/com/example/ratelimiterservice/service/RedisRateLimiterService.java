package com.example.ratelimiterservice.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
public class RedisRateLimiterService {

    private static final int BUCKET_CAPACITY = 10;
    private static final double REFILL_RATE_PER_MINUTE = 10.0;

    private static final double REFILL_RATE_PER_SECOND = REFILL_RATE_PER_MINUTE / 60.0;

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> redisScript;

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate, RedisScript<Long> redisScript) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
    }

    public boolean isAllowed(String key) {

        String userKey = "rate:limiter:" + key;
        long currentTime = Instant.now().getEpochSecond();
        long requestedTokens = 1L;

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(userKey),
                String.valueOf(BUCKET_CAPACITY),
                String.valueOf(REFILL_RATE_PER_SECOND),
                String.valueOf(currentTime),
                String.valueOf(requestedTokens)
        );

        return result == 1L;

    }
}