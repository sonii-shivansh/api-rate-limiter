package com.example.ratelimiterservice.controller;

import com.example.ratelimiterservice.service.InMemoryRateLimiterService;
import com.example.ratelimiterservice.service.RedisRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {

    private final RedisRateLimiterService rateLimiterService;

    public RateLimiterController(RedisRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/api/limited")
    public ResponseEntity<String> getLimitedResource(HttpServletRequest request) {
        // Use the client's IP address as the key for rate limiting
        String ipAddress = request.getRemoteAddr();

        if (rateLimiterService.isAllowed(ipAddress)) {
            // Return 200 OK if allowed
            return ResponseEntity.ok("Success! Resource accessed.");
        } else {
            // Return 429 Too Many Requests if denied
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Error: Too many requests. Please try again later.");
        }
    }
}
