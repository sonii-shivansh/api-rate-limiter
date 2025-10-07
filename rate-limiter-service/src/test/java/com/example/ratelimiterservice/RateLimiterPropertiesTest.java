
package com.example.ratelimiterservice;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.ratelimiterservice.config.RateLimiterProperties;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
 class RateLimiterPropertiesTest {
    @Autowired
    private RateLimiterProperties properties;
    @Test
    void testDefaultValues(){
        assertEquals(10, properties.getBucketCapacity());
        assertEquals(10.0, properties.getRefillRatePerMinute());


    }
    @SpringBootTest
    @TestPropertySource(properties = {
        "rate.limiter.bucket-capacity=200",
        "rate.limiter.refill-rate-per-minute=120"
    })
    static class OverriddenPropertiesTest {
        @Autowired
        private RateLimiterProperties overriddenProps;
        @Test
        void testOverriddenValues() {
            assertEquals(200, overriddenProps.getBucketCapacity());
            assertEquals(120.0, overriddenProps.getRefillRatePerMinute());
        }

    }

    
}
