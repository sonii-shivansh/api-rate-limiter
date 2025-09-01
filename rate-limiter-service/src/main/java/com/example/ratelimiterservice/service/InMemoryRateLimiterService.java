package com.example.ratelimiterservice.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRateLimiterService {

    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long TIME_WINDOW_IN_SECONDS = 60;

    // A map to store request timestamps for each user (identified by a key, e.g., IP address)
    // We use ConcurrentHashMap because it's thread-safe for simultaneous requests
    private final Map<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();


    public synchronized boolean isAllowed(String key) {
        long currentTime = Instant.now().getEpochSecond();

        // Get the list of timestamps for the user, or create a new one
        List<Long> timestamps = requestTimestamps.computeIfAbsent(key, k -> new LinkedList<>());

        // Remove timestamps that are older than our time window
        timestamps.removeIf(ts -> ts < currentTime - TIME_WINDOW_IN_SECONDS);

        // If the number of requests in the window is less than the max, allow it
        if (timestamps.size() < MAX_REQUESTS_PER_MINUTE) {
            timestamps.add(currentTime); // Record the new request timestamp
            requestTimestamps.put(key, timestamps);
            return true;
        }

        // Otherwise, deny the request
        return false;
    }
}