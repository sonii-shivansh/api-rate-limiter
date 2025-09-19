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
import java.util.UUID;

@RestController
public class RateLimiterController {

    private final RedisRateLimiterService rateLimiterService;
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(RateLimiterController.class);

    /**
     * Constructs a RateLimiterController with a given RedisrateLimterSERVICE
     */
    public RateLimiterController(RedisRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
        this.webClient = WebClient.builder().baseUrl("http://product-service:8081").build();
    }

    /**
     * Handles GET requests to "/api/products" and applies rate limiting based on
     * the client's IP address.
     * <p>
     * If the client is allowed, forwards the request to the product-service.
     * If the client exceeds the rate limit, responds with HTTP 429 Too Many
     * Requests.
     *
     * @param exchange the ServerWebExchange containing request and response
     *                 information
     * @return a Mono wrapping ResponseEntity with either the product data or an
     *         error message
     */
    @GetMapping("/api/products")
    public Mono<ResponseEntity<String>> getLimitedResource(ServerWebExchange exchange) {
        // Generate a unique request ID for tracing
        String requestId = UUID.randomUUID().toString();

        // Use the client's IP address as the key for rate limiting
        String ipAddress = Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                .getAddress().getHostAddress();

        log.info("[RequestID: {}] Received request for /api/products from IP: {}", requestId, ipAddress);

        if (rateLimiterService.isAllowed(ipAddress)) {
            // If allowed, forward the request to the product-service
            log.info("[RequestID: {}] Request allowed for IP: {}. Forwarding to product-service.", requestId,
                    ipAddress);

            return webClient.get()
                    .uri("/products")
                    .retrieve()
                    .toEntity(String.class)
                    .doOnSuccess(response -> log.info(
                            "[RequestID: {}] Successfully fetched response from product-service for IP: {}. Status: {}",
                            requestId, ipAddress, response.getStatusCode()))
                    .doOnError(error -> log.error(
                            "[RequestID: {}] Error fetching response from product-service for IP: {}. Error: {}",
                            requestId, ipAddress, error.getMessage()));
        } else {
            // If denied, return 429 Too Many Requests immediately
            log.warn("[RequestID: {}] Request rate limited for IP: {}", requestId, ipAddress);
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Error: Too many requests. Please try again later."));
        }
    }
}
