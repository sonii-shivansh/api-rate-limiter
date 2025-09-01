package com.example.ratelimiterservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class RedisRateLimiterService {

    // The maximum number of tokens a bucket can hold (burst capacity)
    private static final int BUCKET_CAPACITY = 10;
    // The rate at which tokens are added to the bucket (tokens per minute)
    private static final int REFILL_RATE = 10;

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String tokensKey = "rate:limiter:" + key + ":tokens";
        String timestampKey = "rate:limiter:" + key + ":timestamp";

        // Atomically get both values. We can improve this with a Lua script in a real system.
        String currentTokensStr = ops.get(tokensKey);
        String lastRefillTimestampStr = ops.get(timestampKey);

        long currentTokens = (currentTokensStr != null) ? Long.parseLong(currentTokensStr) : BUCKET_CAPACITY;
        long lastRefillTimestamp = (lastRefillTimestampStr != null) ? Long.parseLong(lastRefillTimestampStr) : Instant.now().getEpochSecond();
        long currentTime = Instant.now().getEpochSecond();

        // Calculate how many new tokens to add since the last request
        long timeElapsed = currentTime - lastRefillTimestamp;
        long newTokens = (timeElapsed * REFILL_RATE) / 60;

        // Add new tokens, but don't exceed the bucket's max capacity
        currentTokens = Math.min(currentTokens + newTokens, BUCKET_CAPACITY);

        // Update the timestamp to the current time
        ops.set(timestampKey, String.valueOf(currentTime), 65, TimeUnit.SECONDS); // Expire keys after 65s

        // If tokens are available, consume one and allow the request
        if (currentTokens >= 1) {
            ops.set(tokensKey, String.valueOf(currentTokens - 1), 65, TimeUnit.SECONDS); // Expire keys after 65s
            return true;
        } else {
            // If no tokens are available, deny the request
            ops.set(tokensKey, String.valueOf(currentTokens), 65, TimeUnit.SECONDS); // Still update the count (which is 0)
            return false;
        }
    }
}