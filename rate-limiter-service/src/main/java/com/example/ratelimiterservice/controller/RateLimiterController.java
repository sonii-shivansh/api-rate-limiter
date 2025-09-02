package com.example.ratelimiterservice.controller;

import com.example.ratelimiterservice.service.RedisRateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class RateLimiterController {

    private final RedisRateLimiterService rateLimiterService;
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(RateLimiterController.class);

    public RateLimiterController(RedisRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
        this.webClient = WebClient.builder().baseUrl("http://product-service:8081").build();
    }

    @GetMapping("/api/products")
    public Mono<ResponseEntity<String>> getLimitedResource(ServerWebExchange exchange) {
        // Use the client's IP address as the key for rate limiting
        String ipAddress = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
        log.info("Received request for /api/products from IP: {}", ipAddress);

        if (rateLimiterService.isAllowed(ipAddress)) {
            // If allowed, forward the request to the product-service
            log.info("Request allowed for IP: {}. Forwarding to product-service.", ipAddress);
            return webClient.get()
                    .uri("/products")
                    .retrieve()
                    .toEntity(String.class);
        } else {
            // If denied, return 429 Too Many Requests immediately
            log.warn("Request rate limited for IP: {}", ipAddress);
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Error: Too many requests. Please try again later."));
        }
    }
}
