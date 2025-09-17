# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Your next feature here!

---

## [1.0.0] - 2025-09-17

### Added
- **Initial Release** of the High-Throughput API Rate Limiter.
- **API Gateway Service:** Single entry point for all client requests, built with Spring Cloud Gateway.
- **Rate Limiter Service:** Core rate-limiting logic using a Token Bucket algorithm implemented with a Redis Lua script.
- **Product Service:** A mock backend service to represent a protected resource.
- **Redis Integration:** Used for distributed, high-speed state management of token buckets.
- **Resilience4j Integration:** Circuit Breaker and Retry patterns to handle Redis connection failures gracefully.
- **Docker Support:** Fully containerized with Docker and orchestrated with Docker Compose for easy deployment.
- **CI/CD:** GitHub Actions for automated building, testing, and performance testing.
- **Load Testing:** k6 script for performance benchmarking.
