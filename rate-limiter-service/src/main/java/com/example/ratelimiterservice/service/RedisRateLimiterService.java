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

    /**
     * Constructs a RedisRateLimiterService with the given RedisTemplate,
     * RedisScript, and rate limiter properties.
     * 
     * @param redisTemplate         the Redis template used for executing Redis
     *                              operation
     * @param redisScript           the Redis script used for rate limiting logic
     * @param rateLimiterProperties configuration properties for rate limiting
     *                              (bucket size, refill rate)
     */

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate, RedisScript<Long> redisScript,
            RateLimiterProperties rateLimiterProperties) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
        this.rateLimiterProperties = rateLimiterProperties;
    }

    /**
     * checks if the given key (e.g client IP) is allowed to make a request using
     * the default of 1 token
     * 
     * @param key the key representing a client (forexample IP address)
     * @return true if allowed, false if the rate limit has been exceeded
     */

    public boolean isAllowed(String key) {
        return isAllowed(key, rateLimiterProperties.getRequestCost());
    }

    /**
     * checks if a given key is allowed to make a request for a specified number of
     * tokens
     * <p>
     * This method uses a Redis script to enforce rate limits and is decorated with
     * a circuitbreaker and retry
     * 
     * @param key             the key representing a client (forexample IP address)
     * @param requestedTokens the number of tokens requested for this operation
     * @return true if allowed, false if the rate limit has been exceeded
     */

    @CircuitBreaker(name = "redis", fallbackMethod = "fallbackIsAllowed")
    @Retry(name = "redis")
    public boolean isAllowed(String key, long requestedTokens) {

        String userKey = "rate:limiter:" + key;
        long currentTime = Instant.now().getEpochSecond();
        double refillRatePerSecond = rateLimiterProperties.getRefillRatePerMinute() / 60.0;

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(userKey),
                String.valueOf(rateLimiterProperties.getBucketCapacity()),
                String.valueOf(refillRatePerSecond),
                String.valueOf(currentTime),
                String.valueOf(requestedTokens));

        return result == 1L;
    }

    /**
     * Fallback methos used by the circuitBreaker when Redis is unavailable or an
     * error occurs
     * <p>
     * Always return true toi allow the request when the circuit is open
     * 
     * @param key the key representing a client (forexample IP address)
     * @param t   the Throwable that triggered the fallback
     * @return true to allow the request despite the failure
     */

    public boolean fallbackIsAllowed(String key, Throwable t) {
        log.error("Circuit breaker is open for key: {}. Fallback enabled due to: {}", key, t.getMessage());
        return true;
    }

}