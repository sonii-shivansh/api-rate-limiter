package com.example.ratelimiterservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private int bucketCapacity = 10;
    private double refillRatePerMinute = 10.0;

    // Getters and Setters
    public int getBucketCapacity() {
        return bucketCapacity;
    }

    public void setBucketCapacity(int bucketCapacity) {
        this.bucketCapacity = bucketCapacity;
    }

    public double getRefillRatePerMinute() {
        return refillRatePerMinute;
    }

    public void setRefillRatePerMinute(double refillRatePerMinute) {
        this.refillRatePerMinute = refillRatePerMinute;
    }
}